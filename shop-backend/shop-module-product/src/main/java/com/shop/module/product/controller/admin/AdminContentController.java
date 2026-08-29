package com.shop.module.product.controller.admin;

import com.shop.common.pojo.CommonResult;
import com.shop.framework.security.SecurityUtils;
import com.shop.module.product.dal.dataobject.*;
import com.shop.module.product.service.AdminOperationSnapshotService;
import com.shop.module.product.service.ContentAdminService;
import com.shop.module.product.vo.AdminOperationSnapshotRespVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理后台 — 内容管理（Banner / 频道 / 品牌 / 专题）
 */
@RestController
@RequestMapping("/admin-api/content")
@RequiredArgsConstructor
public class AdminContentController {

    private final ContentAdminService contentAdminService;
    private final AdminOperationSnapshotService operationSnapshotService;

    // ==================== Banner ====================

    @GetMapping("/banner/list")
    public CommonResult<List<ContentBannerDO>> bannerList() {
        return CommonResult.success(contentAdminService.bannerList());
    }

    @PostMapping("/banner/create")
    public CommonResult<Boolean> createBanner(@RequestBody ContentBannerDO banner) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        Long id = contentAdminService.createBanner(banner);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_BANNER, "新建 Banner",
                adminId, operationSnapshotService.captureBanner(null), operationSnapshotService.captureBanner(id));
        return CommonResult.success(true);
    }

    @PutMapping("/banner/update")
    public CommonResult<Boolean> updateBanner(@RequestBody ContentBannerDO banner) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        var before = operationSnapshotService.captureBanner(banner == null ? null : banner.getId());
        contentAdminService.updateBanner(banner);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_BANNER, "更新 Banner",
                adminId, before, operationSnapshotService.captureBanner(banner == null ? null : banner.getId()));
        return CommonResult.success(true);
    }

    @DeleteMapping("/banner/delete")
    public CommonResult<Boolean> deleteBanner(@RequestParam Long id) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        var before = operationSnapshotService.captureBanner(id);
        contentAdminService.deleteBanner(id);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_BANNER, "删除 Banner",
                adminId, before, operationSnapshotService.captureBanner(id));
        return CommonResult.success(true);
    }

    @PutMapping("/banner/update-status")
    public CommonResult<Boolean> updateBannerStatus(@RequestParam Long id, @RequestParam Integer status) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        var before = operationSnapshotService.captureBanner(id);
        contentAdminService.updateBannerStatus(id, status);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_BANNER, "切换 Banner 状态",
                adminId, before, operationSnapshotService.captureBanner(id));
        return CommonResult.success(true);
    }

    // ==================== 频道 ====================

    @GetMapping("/channel/list")
    public CommonResult<List<ContentChannelDO>> channelList() {
        return CommonResult.success(contentAdminService.channelList());
    }

    @PostMapping("/channel/create")
    public CommonResult<Boolean> createChannel(@RequestBody ContentChannelDO channel) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        Long id = contentAdminService.createChannel(channel);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_CHANNEL, "新建频道",
                adminId, operationSnapshotService.captureChannel(null), operationSnapshotService.captureChannel(id));
        return CommonResult.success(true);
    }

    @PutMapping("/channel/update")
    public CommonResult<Boolean> updateChannel(@RequestBody ContentChannelDO channel) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        var before = operationSnapshotService.captureChannel(channel == null ? null : channel.getId());
        contentAdminService.updateChannel(channel);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_CHANNEL, "更新频道",
                adminId, before, operationSnapshotService.captureChannel(channel == null ? null : channel.getId()));
        return CommonResult.success(true);
    }

    @DeleteMapping("/channel/delete")
    public CommonResult<Boolean> deleteChannel(@RequestParam Long id) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        var before = operationSnapshotService.captureChannel(id);
        contentAdminService.deleteChannel(id);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_CHANNEL, "删除频道",
                adminId, before, operationSnapshotService.captureChannel(id));
        return CommonResult.success(true);
    }

    @PutMapping("/channel/update-status")
    public CommonResult<Boolean> updateChannelStatus(@RequestParam Long id, @RequestParam Integer status) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        var before = operationSnapshotService.captureChannel(id);
        contentAdminService.updateChannelStatus(id, status);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_CHANNEL, "切换频道状态",
                adminId, before, operationSnapshotService.captureChannel(id));
        return CommonResult.success(true);
    }

    // ==================== 品牌 ====================

    @GetMapping("/brand/list")
    public CommonResult<List<ContentBrandDO>> brandList() {
        return CommonResult.success(contentAdminService.brandList());
    }

    @PostMapping("/brand/create")
    public CommonResult<Boolean> createBrand(@RequestBody ContentBrandDO brand) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        Long id = contentAdminService.createBrand(brand);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_BRAND, "新建品牌",
                adminId, operationSnapshotService.captureBrand(null), operationSnapshotService.captureBrand(id));
        return CommonResult.success(true);
    }

    @PutMapping("/brand/update")
    public CommonResult<Boolean> updateBrand(@RequestBody ContentBrandDO brand) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        var before = operationSnapshotService.captureBrand(brand == null ? null : brand.getId());
        contentAdminService.updateBrand(brand);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_BRAND, "更新品牌",
                adminId, before, operationSnapshotService.captureBrand(brand == null ? null : brand.getId()));
        return CommonResult.success(true);
    }

    @DeleteMapping("/brand/delete")
    public CommonResult<Boolean> deleteBrand(@RequestParam Long id) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        var before = operationSnapshotService.captureBrand(id);
        contentAdminService.deleteBrand(id);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_BRAND, "删除品牌",
                adminId, before, operationSnapshotService.captureBrand(id));
        return CommonResult.success(true);
    }

    @PutMapping("/brand/update-status")
    public CommonResult<Boolean> updateBrandStatus(@RequestParam Long id, @RequestParam Integer status) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        var before = operationSnapshotService.captureBrand(id);
        contentAdminService.updateBrandStatus(id, status);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_BRAND, "切换品牌状态",
                adminId, before, operationSnapshotService.captureBrand(id));
        return CommonResult.success(true);
    }

    // ==================== 专题 ====================

    @GetMapping("/topic/list")
    public CommonResult<List<ContentTopicDO>> topicList() {
        return CommonResult.success(contentAdminService.topicList());
    }

    @PostMapping("/topic/create")
    public CommonResult<Boolean> createTopic(@RequestBody ContentTopicDO topic) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        Long id = contentAdminService.createTopic(topic);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_TOPIC, "新建专题",
                adminId, operationSnapshotService.captureTopic(null), operationSnapshotService.captureTopic(id));
        return CommonResult.success(true);
    }

    @PutMapping("/topic/update")
    public CommonResult<Boolean> updateTopic(@RequestBody ContentTopicDO topic) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        var before = operationSnapshotService.captureTopic(topic == null ? null : topic.getId());
        contentAdminService.updateTopic(topic);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_TOPIC, "更新专题",
                adminId, before, operationSnapshotService.captureTopic(topic == null ? null : topic.getId()));
        return CommonResult.success(true);
    }

    @DeleteMapping("/topic/delete")
    public CommonResult<Boolean> deleteTopic(@RequestParam Long id) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        var before = operationSnapshotService.captureTopic(id);
        contentAdminService.deleteTopic(id);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_TOPIC, "删除专题",
                adminId, before, operationSnapshotService.captureTopic(id));
        return CommonResult.success(true);
    }

    @PutMapping("/topic/update-status")
    public CommonResult<Boolean> updateTopicStatus(@RequestParam Long id, @RequestParam Integer status) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        var before = operationSnapshotService.captureTopic(id);
        contentAdminService.updateTopicStatus(id, status);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_TOPIC, "切换专题状态",
                adminId, before, operationSnapshotService.captureTopic(id));
        return CommonResult.success(true);
    }

    // ==================== 专题关联商品 ====================

    @GetMapping("/topic/products")
    public CommonResult<List<Long>> getTopicProducts(@RequestParam Long topicId) {
        return CommonResult.success(contentAdminService.getTopicProductIds(topicId));
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/topic/products")
    public CommonResult<Boolean> setTopicProducts(@RequestBody Map<String, Object> body) {
        Long adminId = SecurityUtils.getRequiredAdminId();
        Long topicId = ((Number) body.get("topicId")).longValue();
        var before = operationSnapshotService.captureTopicProducts(topicId);
        List<Long> spuIds = ((List<Number>) body.get("spuIds")).stream()
                .map(Number::longValue)
                .toList();
        contentAdminService.setTopicProducts(topicId, spuIds);
        operationSnapshotService.recordContentOperation(AdminOperationSnapshotService.SCENE_TOPIC_PRODUCTS,
                "调整专题关联商品", adminId, before, operationSnapshotService.captureTopicProducts(topicId));
        return CommonResult.success(true);
    }

    @GetMapping("/rollback/latest")
    public CommonResult<List<AdminOperationSnapshotRespVO>> latestRollbacks(
            @RequestParam(defaultValue = "5") Integer limit) {
        return CommonResult.success(operationSnapshotService.listRecentContentSnapshots(limit == null ? 5 : limit));
    }

    @PostMapping("/rollback")
    public CommonResult<Boolean> rollback(@RequestParam Long snapshotId) {
        operationSnapshotService.rollbackContentSnapshot(snapshotId, SecurityUtils.getRequiredAdminId());
        return CommonResult.success(true);
    }
}
