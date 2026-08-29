package com.shop.module.product.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminOperationSnapshotRespVO {
    private Long id;
    private String scopeCode;
    private String sceneCode;
    private String operationLabel;
    private String entityName;
    private Integer itemCount;
    private Integer rolledBack;
    private LocalDateTime createTime;
}
