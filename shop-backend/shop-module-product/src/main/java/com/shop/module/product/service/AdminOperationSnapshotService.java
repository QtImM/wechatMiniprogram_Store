package com.shop.module.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.common.exception.ServerException;
import com.shop.module.product.dal.dataobject.AdminOperationSnapshotDO;
import com.shop.module.product.dal.dataobject.ContentBannerDO;
import com.shop.module.product.dal.dataobject.ContentBrandDO;
import com.shop.module.product.dal.dataobject.ContentChannelDO;
import com.shop.module.product.dal.dataobject.ContentTopicDO;
import com.shop.module.product.dal.dataobject.ContentTopicProductDO;
import com.shop.module.product.dal.dataobject.ProductSkuDO;
import com.shop.module.product.dal.dataobject.ProductSpuDO;
import com.shop.module.product.dal.mysql.AdminOperationSnapshotMapper;
import com.shop.module.product.dal.mysql.ContentBannerMapper;
import com.shop.module.product.dal.mysql.ContentBrandMapper;
import com.shop.module.product.dal.mysql.ContentChannelMapper;
import com.shop.module.product.dal.mysql.ContentTopicMapper;
import com.shop.module.product.dal.mysql.ContentTopicProductMapper;
import com.shop.module.product.dal.mysql.ProductSkuMapper;
import com.shop.module.product.dal.mysql.ProductSpuMapper;
import com.shop.module.product.vo.AdminOperationSnapshotRespVO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminOperationSnapshotService {

    public static final String SCOPE_PRODUCT = "PRODUCT";
    public static final String SCOPE_CONTENT = "CONTENT";
    public static final String SCENE_PRODUCT = "PRODUCT";
    public static final String SCENE_BANNER = "BANNER";
    public static final String SCENE_CHANNEL = "CHANNEL";
    public static final String SCENE_BRAND = "BRAND";
    public static final String SCENE_TOPIC = "TOPIC";
    public static final String SCENE_TOPIC_PRODUCTS = "TOPIC_PRODUCTS";
    private static final int MAX_LIST_LIMIT = 10;
    private static final TypeReference<List<ProductSnapshotBundle>> PRODUCT_SNAPSHOT_LIST =
            new TypeReference<>() {
            };
    private static final TypeReference<List<Long>> LONG_LIST_TYPE = new TypeReference<>() {
    };

    private final AdminOperationSnapshotMapper snapshotMapper;
    private final ProductSpuMapper productSpuMapper;
    private final ProductSkuMapper productSkuMapper;
    private final ContentBannerMapper bannerMapper;
    private final ContentChannelMapper channelMapper;
    private final ContentBrandMapper brandMapper;
    private final ContentTopicMapper topicMapper;
    private final ContentTopicProductMapper topicProductMapper;
    private final ProductAdminService productAdminService;
    private final ContentAdminService contentAdminService;
    private final ObjectMapper objectMapper;

    public OperationCapture captureProducts(Collection<Long> spuIds) {
        List<Long> normalizedIds = normalizeIds(spuIds);
        if (normalizedIds.isEmpty()) {
            return new OperationCapture("", List.of(), "[]", 0);
        }
        List<ProductSnapshotBundle> bundles = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (Long spuId : normalizedIds) {
            ProductSpuDO spu = productSpuMapper.selectById(spuId);
            if (spu == null) {
                continue;
            }
            ProductSnapshotBundle bundle = new ProductSnapshotBundle();
            bundle.setSpu(copySpu(spu));
            bundle.setSkus(productSkuMapper.selectList(new LambdaQueryWrapper<ProductSkuDO>()
                            .eq(ProductSkuDO::getSpuId, spuId)
                            .orderByAsc(ProductSkuDO::getId))
                    .stream()
                    .map(this::copySku)
                    .toList());
            bundles.add(bundle);
            names.add(spu.getName());
        }
        return new OperationCapture(buildEntityName(names, "商品"), bundles.stream()
                .map(bundle -> bundle.getSpu().getId())
                .filter(Objects::nonNull)
                .toList(), toJson(bundles), bundles.size());
    }

    public OperationCapture filterProducts(OperationCapture capture, Collection<Long> targetIds) {
        if (capture == null || capture.snapshotJson() == null || capture.snapshotJson().isBlank()) {
            return new OperationCapture("", List.of(), "[]", 0);
        }
        Set<Long> targetSet = new LinkedHashSet<>(normalizeIds(targetIds));
        if (targetSet.isEmpty()) {
            return new OperationCapture("", List.of(), "[]", 0);
        }
        List<ProductSnapshotBundle> bundles = parseProductBundles(capture.snapshotJson()).stream()
                .filter(bundle -> bundle.getSpu() != null && targetSet.contains(bundle.getSpu().getId()))
                .toList();
        List<String> names = bundles.stream()
                .map(bundle -> bundle.getSpu() == null ? "" : bundle.getSpu().getName())
                .filter(name -> name != null && !name.isBlank())
                .toList();
        return new OperationCapture(buildEntityName(names, "商品"),
                bundles.stream().map(bundle -> bundle.getSpu().getId()).toList(),
                toJson(bundles), bundles.size());
    }

    public OperationCapture captureBanner(Long id) {
        return captureSingleContent(SCENE_BANNER, id, bannerMapper.selectById(id), value -> value == null ? "" : value.getTitle());
    }

    public OperationCapture captureChannel(Long id) {
        return captureSingleContent(SCENE_CHANNEL, id, channelMapper.selectById(id), value -> value == null ? "" : value.getName());
    }

    public OperationCapture captureBrand(Long id) {
        return captureSingleContent(SCENE_BRAND, id, brandMapper.selectById(id), value -> value == null ? "" : value.getName());
    }

    public OperationCapture captureTopic(Long id) {
        return captureSingleContent(SCENE_TOPIC, id, topicMapper.selectById(id), value -> value == null ? "" : value.getTitle());
    }

    public OperationCapture captureTopicProducts(Long topicId) {
        ContentTopicDO topic = topicMapper.selectById(topicId);
        if (topic == null) {
            return new OperationCapture("", List.of(), "null", 0);
        }
        TopicProductsSnapshot snapshot = new TopicProductsSnapshot();
        snapshot.setTopicId(topicId);
        snapshot.setTopicTitle(topic.getTitle());
        snapshot.setSpuIds(topicProductMapper.selectList(new LambdaQueryWrapper<ContentTopicProductDO>()
                        .eq(ContentTopicProductDO::getTopicId, topicId)
                        .orderByDesc(ContentTopicProductDO::getSort)
                        .orderByAsc(ContentTopicProductDO::getId))
                .stream()
                .map(ContentTopicProductDO::getSpuId)
                .toList());
        return new OperationCapture(topic.getTitle() + " 关联商品", List.of(topicId), toJson(snapshot), 1);
    }

    public void recordProductOperation(String operationLabel, Long operatorAdminId,
                                       OperationCapture before, OperationCapture after) {
        saveSnapshot(SCOPE_PRODUCT, SCENE_PRODUCT, operationLabel, operatorAdminId, before, after);
    }

    public void recordContentOperation(String sceneCode, String operationLabel, Long operatorAdminId,
                                       OperationCapture before, OperationCapture after) {
        saveSnapshot(SCOPE_CONTENT, sceneCode, operationLabel, operatorAdminId, before, after);
    }

    public List<AdminOperationSnapshotRespVO> listRecentProductSnapshots(int limit) {
        return listRecentSnapshots(List.of(SCENE_PRODUCT), limit);
    }

    public List<AdminOperationSnapshotRespVO> listRecentContentSnapshots(int limit) {
        return listRecentSnapshots(List.of(SCENE_BANNER, SCENE_CHANNEL, SCENE_BRAND, SCENE_TOPIC, SCENE_TOPIC_PRODUCTS), limit);
    }

    @Transactional(rollbackFor = Exception.class)
    public void rollbackProductSnapshot(Long snapshotId, Long adminId) {
        AdminOperationSnapshotDO snapshot = requireSnapshot(snapshotId, Set.of(SCENE_PRODUCT));
        ensureLatestSnapshot(snapshot);
        List<ProductSnapshotBundle> before = parseProductBundles(snapshot.getBeforeSnapshot());
        List<ProductSnapshotBundle> after = parseProductBundles(snapshot.getAfterSnapshot());
        Map<Long, ProductSnapshotBundle> beforeMap = toProductMap(before);
        Map<Long, ProductSnapshotBundle> afterMap = toProductMap(after);
        for (ProductSnapshotBundle bundle : before) {
            if (bundle.getSpu() == null || bundle.getSpu().getId() == null) {
                continue;
            }
            productAdminService.restoreProductSnapshot(bundle.getSpu(), bundle.getSkus(), adminId, "预览中心一键回退");
        }
        for (Map.Entry<Long, ProductSnapshotBundle> entry : afterMap.entrySet()) {
            if (beforeMap.containsKey(entry.getKey())) {
                continue;
            }
            productAdminService.deleteProductForRollback(entry.getKey());
        }
        markRolledBack(snapshotId, adminId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void rollbackContentSnapshot(Long snapshotId, Long adminId) {
        AdminOperationSnapshotDO snapshot = requireSnapshot(snapshotId,
                Set.of(SCENE_BANNER, SCENE_CHANNEL, SCENE_BRAND, SCENE_TOPIC, SCENE_TOPIC_PRODUCTS));
        ensureLatestSnapshot(snapshot);
        switch (snapshot.getSceneCode()) {
            case SCENE_BANNER -> rollbackBanner(snapshot);
            case SCENE_CHANNEL -> rollbackChannel(snapshot);
            case SCENE_BRAND -> rollbackBrand(snapshot);
            case SCENE_TOPIC -> rollbackTopic(snapshot);
            case SCENE_TOPIC_PRODUCTS -> rollbackTopicProducts(snapshot);
            default -> throw new ServerException(404, "该操作暂不支持回退");
        }
        markRolledBack(snapshotId, adminId);
    }

    private <T> OperationCapture captureSingleContent(String sceneCode, Long id, T entity,
                                                      java.util.function.Function<T, String> nameGetter) {
        if (id == null || entity == null) {
            return new OperationCapture("", List.of(), "null", 0);
        }
        String name = nameGetter.apply(entity);
        return new OperationCapture(name == null ? sceneCode : name, List.of(id), toJson(entity), 1);
    }

    private void saveSnapshot(String scopeCode, String sceneCode, String operationLabel, Long operatorAdminId,
                              OperationCapture before, OperationCapture after) {
        OperationCapture safeBefore = before == null ? new OperationCapture("", List.of(), "null", 0) : before;
        OperationCapture safeAfter = after == null ? new OperationCapture("", List.of(), "null", 0) : after;
        List<Long> entityIds = !safeAfter.entityIds().isEmpty() ? safeAfter.entityIds() : safeBefore.entityIds();
        int itemCount = Math.max(safeBefore.itemCount(), safeAfter.itemCount());
        if (entityIds.isEmpty() && itemCount == 0) {
            return;
        }
        AdminOperationSnapshotDO snapshot = new AdminOperationSnapshotDO();
        snapshot.setScopeCode(scopeCode);
        snapshot.setSceneCode(sceneCode);
        snapshot.setOperationLabel(operationLabel);
        snapshot.setEntityName(!safeAfter.entityName().isBlank() ? safeAfter.entityName() : safeBefore.entityName());
        snapshot.setEntityIdsJson(toJson(entityIds));
        snapshot.setItemCount(itemCount);
        snapshot.setOperatorAdminId(operatorAdminId == null ? 0L : operatorAdminId);
        snapshot.setBeforeSnapshot(safeBefore.snapshotJson());
        snapshot.setAfterSnapshot(safeAfter.snapshotJson());
        snapshot.setRolledBack(0);
        snapshotMapper.insert(snapshot);
    }

    private List<AdminOperationSnapshotRespVO> listRecentSnapshots(List<String> sceneCodes, int limit) {
        return snapshotMapper.selectList(new LambdaQueryWrapper<AdminOperationSnapshotDO>()
                        .in(AdminOperationSnapshotDO::getSceneCode, sceneCodes)
                        .eq(AdminOperationSnapshotDO::getRolledBack, 0)
                        .orderByDesc(AdminOperationSnapshotDO::getId)
                        .last("LIMIT " + normalizeLimit(limit)))
                .stream()
                .map(this::toResp)
                .toList();
    }

    private AdminOperationSnapshotRespVO toResp(AdminOperationSnapshotDO source) {
        AdminOperationSnapshotRespVO item = new AdminOperationSnapshotRespVO();
        item.setId(source.getId());
        item.setScopeCode(source.getScopeCode());
        item.setSceneCode(source.getSceneCode());
        item.setOperationLabel(source.getOperationLabel());
        item.setEntityName(source.getEntityName());
        item.setItemCount(source.getItemCount());
        item.setRolledBack(source.getRolledBack());
        item.setCreateTime(source.getCreateTime());
        return item;
    }

    private AdminOperationSnapshotDO requireSnapshot(Long snapshotId, Set<String> allowedScenes) {
        AdminOperationSnapshotDO snapshot = snapshotId == null ? null : snapshotMapper.selectById(snapshotId);
        if (snapshot == null || snapshot.getDeleted() != null && snapshot.getDeleted()) {
            throw new ServerException(404, "回退记录不存在");
        }
        if (!allowedScenes.contains(snapshot.getSceneCode())) {
            throw new ServerException(404, "回退记录类型不正确");
        }
        if (Integer.valueOf(1).equals(snapshot.getRolledBack())) {
            throw new ServerException(400, "这条操作已经回退过了");
        }
        return snapshot;
    }

    private void ensureLatestSnapshot(AdminOperationSnapshotDO snapshot) {
        Set<Long> currentIds = new LinkedHashSet<>(parseLongList(snapshot.getEntityIdsJson()));
        if (currentIds.isEmpty()) {
            return;
        }
        List<AdminOperationSnapshotDO> newerSnapshots = snapshotMapper.selectList(new LambdaQueryWrapper<AdminOperationSnapshotDO>()
                .eq(AdminOperationSnapshotDO::getSceneCode, snapshot.getSceneCode())
                .eq(AdminOperationSnapshotDO::getRolledBack, 0)
                .gt(AdminOperationSnapshotDO::getId, snapshot.getId())
                .orderByAsc(AdminOperationSnapshotDO::getId));
        for (AdminOperationSnapshotDO newer : newerSnapshots) {
            Set<Long> newerIds = new LinkedHashSet<>(parseLongList(newer.getEntityIdsJson()));
            newerIds.retainAll(currentIds);
            if (!newerIds.isEmpty()) {
                throw new ServerException(409, "该对象已有更新的后续操作，请先回退最近的一次");
            }
        }
    }

    private void rollbackBanner(AdminOperationSnapshotDO snapshot) {
        ContentBannerDO before = parseObject(snapshot.getBeforeSnapshot(), ContentBannerDO.class);
        ContentBannerDO after = parseObject(snapshot.getAfterSnapshot(), ContentBannerDO.class);
        if (before == null) {
            if (after != null && after.getId() != null) {
                contentAdminService.deleteBannerForRollback(after.getId());
            }
            return;
        }
        contentAdminService.restoreBannerSnapshot(before);
    }

    private void rollbackChannel(AdminOperationSnapshotDO snapshot) {
        ContentChannelDO before = parseObject(snapshot.getBeforeSnapshot(), ContentChannelDO.class);
        ContentChannelDO after = parseObject(snapshot.getAfterSnapshot(), ContentChannelDO.class);
        if (before == null) {
            if (after != null && after.getId() != null) {
                contentAdminService.deleteChannelForRollback(after.getId());
            }
            return;
        }
        contentAdminService.restoreChannelSnapshot(before);
    }

    private void rollbackBrand(AdminOperationSnapshotDO snapshot) {
        ContentBrandDO before = parseObject(snapshot.getBeforeSnapshot(), ContentBrandDO.class);
        ContentBrandDO after = parseObject(snapshot.getAfterSnapshot(), ContentBrandDO.class);
        if (before == null) {
            if (after != null && after.getId() != null) {
                contentAdminService.deleteBrandForRollback(after.getId());
            }
            return;
        }
        contentAdminService.restoreBrandSnapshot(before);
    }

    private void rollbackTopic(AdminOperationSnapshotDO snapshot) {
        ContentTopicDO before = parseObject(snapshot.getBeforeSnapshot(), ContentTopicDO.class);
        ContentTopicDO after = parseObject(snapshot.getAfterSnapshot(), ContentTopicDO.class);
        if (before == null) {
            if (after != null && after.getId() != null) {
                contentAdminService.deleteTopicForRollback(after.getId());
            }
            return;
        }
        contentAdminService.restoreTopicSnapshot(before);
    }

    private void rollbackTopicProducts(AdminOperationSnapshotDO snapshot) {
        TopicProductsSnapshot before = parseObject(snapshot.getBeforeSnapshot(), TopicProductsSnapshot.class);
        TopicProductsSnapshot after = parseObject(snapshot.getAfterSnapshot(), TopicProductsSnapshot.class);
        if (before == null) {
            if (after == null || after.getTopicId() == null) {
                throw new ServerException(400, "专题关联快照缺失");
            }
            contentAdminService.restoreTopicProductsSnapshot(after.getTopicId(), List.of());
            return;
        }
        contentAdminService.restoreTopicProductsSnapshot(before.getTopicId(), before.getSpuIds());
    }

    private void markRolledBack(Long snapshotId, Long adminId) {
        snapshotMapper.update(null, new LambdaUpdateWrapper<AdminOperationSnapshotDO>()
                .eq(AdminOperationSnapshotDO::getId, snapshotId)
                .set(AdminOperationSnapshotDO::getRolledBack, 1)
                .set(AdminOperationSnapshotDO::getRollbackTime, LocalDateTime.now())
                .set(AdminOperationSnapshotDO::getRollbackAdminId, adminId));
    }

    private Map<Long, ProductSnapshotBundle> toProductMap(List<ProductSnapshotBundle> bundles) {
        Map<Long, ProductSnapshotBundle> result = new LinkedHashMap<>();
        for (ProductSnapshotBundle bundle : bundles) {
            if (bundle.getSpu() != null && bundle.getSpu().getId() != null) {
                result.put(bundle.getSpu().getId(), bundle);
            }
        }
        return result;
    }

    private List<ProductSnapshotBundle> parseProductBundles(String json) {
        if (json == null || json.isBlank() || "null".equalsIgnoreCase(json.trim())) {
            return List.of();
        }
        try {
            List<ProductSnapshotBundle> bundles = objectMapper.readValue(json, PRODUCT_SNAPSHOT_LIST);
            return bundles == null ? List.of() : bundles;
        } catch (Exception exception) {
            throw new ServerException(500, "商品回退快照解析失败");
        }
    }

    private List<Long> parseLongList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            List<Long> result = objectMapper.readValue(json, LONG_LIST_TYPE);
            return result == null ? List.of() : result.stream()
                    .filter(Objects::nonNull)
                    .sorted(Comparator.naturalOrder())
                    .toList();
        } catch (Exception exception) {
            return List.of();
        }
    }

    private <T> T parseObject(String json, Class<T> type) {
        if (json == null || json.isBlank() || "null".equalsIgnoreCase(json.trim())) {
            return null;
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception exception) {
            throw new ServerException(500, "回退快照解析失败");
        }
    }

    private ProductSpuDO copySpu(ProductSpuDO source) {
        ProductSpuDO target = new ProductSpuDO();
        target.setId(source.getId());
        target.setCategoryId(source.getCategoryId());
        target.setName(source.getName());
        target.setKeyword(source.getKeyword());
        target.setIntroduction(source.getIntroduction());
        target.setDescription(source.getDescription());
        target.setPicUrl(source.getPicUrl());
        target.setSliderPicUrls(source.getSliderPicUrls());
        target.setVideoUrl(source.getVideoUrl());
        target.setType(source.getType());
        target.setPrice(source.getPrice());
        target.setMarketPrice(source.getMarketPrice());
        target.setStock(source.getStock());
        target.setSalesCount(source.getSalesCount());
        target.setSort(source.getSort());
        target.setStatus(source.getStatus());
        return target;
    }

    private ProductSkuDO copySku(ProductSkuDO source) {
        ProductSkuDO target = new ProductSkuDO();
        target.setId(source.getId());
        target.setSpuId(source.getSpuId());
        target.setSkuCode(source.getSkuCode());
        target.setProperties(source.getProperties());
        target.setPrice(source.getPrice());
        target.setMarketPrice(source.getMarketPrice());
        target.setStock(source.getStock());
        target.setWarningStock(source.getWarningStock());
        target.setPicUrl(source.getPicUrl());
        target.setWeight(source.getWeight());
        target.setVolume(source.getVolume());
        return target;
    }

    private List<Long> normalizeIds(Collection<Long> ids) {
        if (ids == null) {
            return List.of();
        }
        return ids.stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .sorted()
                .toList();
    }

    private String buildEntityName(List<String> names, String fallbackLabel) {
        List<String> validNames = names.stream()
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .toList();
        if (validNames.isEmpty()) {
            return fallbackLabel;
        }
        if (validNames.size() == 1) {
            return validNames.getFirst();
        }
        return validNames.getFirst() + " 等 " + validNames.size() + " 项";
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return 5;
        }
        return Math.min(limit, MAX_LIST_LIMIT);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new ServerException(500, "快照序列化失败");
        }
    }

    public record OperationCapture(String entityName, List<Long> entityIds, String snapshotJson, int itemCount) {
    }

    @Data
    public static class ProductSnapshotBundle {
        private ProductSpuDO spu;
        private List<ProductSkuDO> skus = new ArrayList<>();
    }

    @Data
    public static class TopicProductsSnapshot {
        private Long topicId;
        private String topicTitle;
        private List<Long> spuIds = new ArrayList<>();
    }
}
