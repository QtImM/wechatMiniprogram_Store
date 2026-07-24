package com.shop.module.product.controller.app;

import com.shop.common.pojo.CommonResult;
import com.shop.module.product.service.AppProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AppGoodsController {
    private final AppProductQueryService service;
    @RequestMapping("/app-api/catalog/index") public CommonResult<Map<String,Object>> catalogIndex(){return CommonResult.success(service.catalogIndex());}
    @RequestMapping("/app-api/catalog/current") public CommonResult<Map<String,Object>> catalogCurrent(@RequestParam(defaultValue="1") Long id){return CommonResult.success(Map.of("currentCategory",service.catalog(id)));}
    @RequestMapping("/app-api/catalog/{id}") public CommonResult<Map<String,Object>> catalog(@PathVariable Long id){return CommonResult.success(Map.of("currentCategory",service.catalog(id)));}
    @RequestMapping("/app-api/goods/category") public CommonResult<Map<String,Object>> category(@RequestParam(defaultValue="1") Long id){return CommonResult.success(service.goodsCategory(id));}
    @RequestMapping("/app-api/goods/count") public CommonResult<Map<String,Object>> count(){return CommonResult.success(service.count());}
    @RequestMapping("/app-api/goods/list") public CommonResult<Map<String,Object>> list(@RequestParam(defaultValue="0") Long categoryId,@RequestParam(defaultValue="") String keyword,@RequestParam(defaultValue="0") int isHot,@RequestParam(defaultValue="0") int isNew,@RequestParam(defaultValue="1") int page,@RequestParam(defaultValue="10") int size){return CommonResult.success(service.list(categoryId,keyword,isHot,isNew,page,size));}
    @RequestMapping("/app-api/goods/detail") public CommonResult<Map<String,Object>> detail(@RequestParam Long id){return CommonResult.success(service.detail(id));}
    @RequestMapping("/app-api/goods/related") public CommonResult<Map<String,Object>> related(@RequestParam Long id){return CommonResult.success(Map.of("goodsList",service.related(id)));}
}
