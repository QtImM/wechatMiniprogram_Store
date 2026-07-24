package com.shop.module.product.dal.mysql;

import com.shop.framework.mybatis.core.BaseMapperX;
import com.shop.module.product.dal.dataobject.ProductSkuDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductSkuMapper extends BaseMapperX<ProductSkuDO> {
}
