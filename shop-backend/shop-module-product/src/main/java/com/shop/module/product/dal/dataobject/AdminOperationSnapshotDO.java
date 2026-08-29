package com.shop.module.product.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shop.framework.mybatis.core.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("admin_operation_snapshot")
public class AdminOperationSnapshotDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String scopeCode;
    private String sceneCode;
    private String operationLabel;
    private String entityName;
    private String entityIdsJson;
    private Integer itemCount;
    private Long operatorAdminId;
    private String beforeSnapshot;
    private String afterSnapshot;
    private Integer rolledBack;
    private LocalDateTime rollbackTime;
    private Long rollbackAdminId;
}
