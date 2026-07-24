package com.shop.module.trade.dal.mysql;

import com.shop.framework.mybatis.core.BaseMapperX;
import com.shop.module.trade.dal.dataobject.TradeCartDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TradeCartMapper extends BaseMapperX<TradeCartDO> {
}
