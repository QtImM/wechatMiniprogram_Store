package com.shop.module.trade.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shop.framework.mybatis.core.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("trade_cart")
public class TradeCartDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long spuId;
    private Long skuId;
    private String goodsName;
    private String goodsPicUrl;
    private String specName;
    private Integer price;
    private Integer count;
    private Integer checked;
}
