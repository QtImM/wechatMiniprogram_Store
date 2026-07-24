package com.shop.module.trade.service;

import com.shop.common.exception.ServerException;
import com.shop.module.product.controller.MockData;
import com.shop.module.product.dal.dataobject.ProductSpuDO;
import com.shop.module.product.dal.mysql.ProductSpuMapper;
import com.shop.module.trade.util.TradeMoneyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TradeProductService {

    private final ProductSpuMapper productSpuMapper;

    public TradeProductSnapshot getSnapshot(Long goodsId, Long productId) {
        Long cartSkuId = buildCartSkuId(goodsId, productId);
        ProductSpuDO spu = goodsId == null ? null : productSpuMapper.selectById(goodsId);
        if (spu != null) {
            if (spu.getStatus() != null && spu.getStatus() == 0) {
                throw new ServerException(1102, "商品已下架");
            }
            TradeProductSnapshot snapshot = new TradeProductSnapshot();
            snapshot.setSpuId(spu.getId());
            snapshot.setSkuId(cartSkuId);
            snapshot.setName(spu.getName());
            snapshot.setPicUrl(spu.getPicUrl());
            snapshot.setSpecName("默认规格");
            snapshot.setPrice(spu.getPrice());
            snapshot.setStock(spu.getStock() == null ? 0 : spu.getStock());
            snapshot.setDatabaseProduct(true);
            return snapshot;
        }

        Map<String, Object> goods = goodsId == null ? null : MockData.getGoodsById(goodsId);
        if (goods == null || goods.isEmpty()) {
            throw new ServerException(1101, "商品不存在");
        }
        TradeProductSnapshot snapshot = new TradeProductSnapshot();
        snapshot.setSpuId(goodsId);
        snapshot.setSkuId(cartSkuId);
        snapshot.setName(String.valueOf(goods.get("name")));
        snapshot.setPicUrl(String.valueOf(goods.get("listPicUrl")));
        snapshot.setSpecName("默认规格");
        snapshot.setPrice(TradeMoneyUtils.parseCent(goods.get("retailPrice")));
        snapshot.setStock(999);
        snapshot.setDatabaseProduct(false);
        return snapshot;
    }

    public void reduceStock(TradeProductSnapshot snapshot, int count) {
        if (!snapshot.isDatabaseProduct()) {
            return;
        }
        ProductSpuDO spu = productSpuMapper.selectById(snapshot.getSpuId());
        if (spu == null || spu.getStock() == null || spu.getStock() < count) {
            throw new ServerException(1201, "商品库存不足");
        }
        ProductSpuDO update = new ProductSpuDO();
        update.setId(spu.getId());
        update.setStock(spu.getStock() - count);
        productSpuMapper.updateById(update);
    }

    public void recoverStock(Long spuId, int count) {
        ProductSpuDO spu = productSpuMapper.selectById(spuId);
        if (spu == null || spu.getStock() == null) {
            return;
        }
        ProductSpuDO update = new ProductSpuDO();
        update.setId(spu.getId());
        update.setStock(spu.getStock() + count);
        productSpuMapper.updateById(update);
    }

    private Long buildCartSkuId(Long goodsId, Long productId) {
        long goodsPart = goodsId == null ? 0L : goodsId;
        long specPart = productId == null || productId <= 0 ? 0L : productId;
        return goodsPart * 1000000000L + specPart;
    }
}
