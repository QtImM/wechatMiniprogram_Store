package com.shop.module.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shop.module.product.dal.dataobject.CategoryDO;
import com.shop.module.product.dal.mysql.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public List<CategoryDO> getEnabledList() {
        return categoryMapper.selectList(new LambdaQueryWrapper<CategoryDO>()
                .eq(CategoryDO::getStatus, 1)
                .orderByDesc(CategoryDO::getSort));
    }

    public List<CategoryDO> getAllList() {
        return categoryMapper.selectList(new LambdaQueryWrapper<CategoryDO>()
                .orderByDesc(CategoryDO::getSort));
    }

    public void create(CategoryDO category) {
        categoryMapper.insert(category);
    }

    public void update(CategoryDO category) {
        categoryMapper.updateById(category);
    }

    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
}
