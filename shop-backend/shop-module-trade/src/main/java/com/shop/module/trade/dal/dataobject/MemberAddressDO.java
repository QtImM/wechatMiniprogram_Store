package com.shop.module.trade.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shop.framework.mybatis.core.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("member_address")
public class MemberAddressDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String userName;
    private String telNumber;
    private Long provinceId;
    private Long cityId;
    private Long districtId;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String fullRegion;
    private String detailInfo;
    private Integer isDefault;
}
