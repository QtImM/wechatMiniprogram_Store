package com.shop.module.trade.dal.mysql;

import com.shop.framework.mybatis.core.BaseMapperX;
import com.shop.module.trade.dal.dataobject.TradeOrderItemDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TradeOrderItemMapper extends BaseMapperX<TradeOrderItemDO> {
}
