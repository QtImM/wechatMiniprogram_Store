package com.shop.framework.mybatis.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.pojo.PageParam;
import com.shop.common.pojo.PageResult;

public interface BaseMapperX<T> extends BaseMapper<T> {

    default PageResult<T> selectPage(PageParam pageParam, com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<T> wrapper) {
        IPage<T> page = selectPage(new Page<>(pageParam.getPageNo(), pageParam.getPageSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }
}
