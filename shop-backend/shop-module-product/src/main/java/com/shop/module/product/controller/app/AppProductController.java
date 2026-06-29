package com.shop.module.product.controller.app;

import com.shop.common.pojo.CommonResult;
import com.shop.common.pojo.PageParam;
import com.shop.common.pojo.PageResult;
import com.shop.module.product.dal.dataobject.CategoryDO;
import com.shop.module.product.dal.dataobject.ProductSpuDO;
import com.shop.module.product.service.CategoryService;
import com.shop.module.product.service.ProductSpuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app-api/product")
@RequiredArgsConstructor
public class AppProductController {

    private final CategoryService categoryService;
    private final ProductSpuService productSpuService;

    @GetMapping("/category/list")
    public CommonResult<List<CategoryDO>> getCategoryList() {
        return CommonResult.success(categoryService.getEnabledList());
    }

    @GetMapping("/spu/page")
    public CommonResult<PageResult<ProductSpuDO>> getSpuPage(PageParam pageParam,
                                                              @RequestParam(required = false) Long categoryId) {
        return CommonResult.success(productSpuService.getSpuPage(pageParam, categoryId));
    }

    @GetMapping("/spu/detail")
    public CommonResult<ProductSpuDO> getSpuDetail(@RequestParam Long id) {
        return CommonResult.success(productSpuService.getSpuDetail(id));
    }
}
