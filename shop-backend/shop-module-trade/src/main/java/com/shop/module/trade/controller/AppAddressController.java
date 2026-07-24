package com.shop.module.trade.controller;

import com.shop.common.pojo.CommonResult;
import com.shop.module.trade.service.MemberAddressService;
import com.shop.module.trade.util.TradeRequestUtils;
import com.shop.module.trade.util.TradeSecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AppAddressController {

    private final MemberAddressService memberAddressService;

    @RequestMapping("/app-api/address/list")
    public CommonResult<List<Map<String, Object>>> list() {
        Long userId = TradeSecurityUtils.getRequiredUserId();
        return CommonResult.success(memberAddressService.getAddressList(userId));
    }

    @RequestMapping("/app-api/address/detail")
    public CommonResult<Map<String, Object>> detail(@RequestBody(required = false) String rawBody,
                                                     @RequestParam Map<String, Object> params,
                                                     @RequestParam(value = "id", required = false) Long paramId) {
        Long userId = TradeSecurityUtils.getRequiredUserId();
        Map<String, Object> request = TradeRequestUtils.parse(rawBody, params);
        Long id = paramId != null ? paramId : TradeRequestUtils.getLong(request, "id", 0L);
        return CommonResult.success(memberAddressService.getAddressDetail(userId, id));
    }

    @RequestMapping("/app-api/address/save")
    public CommonResult<Boolean> save(@RequestBody(required = false) String rawBody,
                                      @RequestParam Map<String, Object> params) {
        Long userId = TradeSecurityUtils.getRequiredUserId();
        memberAddressService.saveAddress(userId, TradeRequestUtils.parse(rawBody, params));
        return CommonResult.success(true);
    }

    @RequestMapping("/app-api/address/delete")
    public CommonResult<Boolean> delete(@RequestBody(required = false) String rawBody,
                                        @RequestParam Map<String, Object> params) {
        Long userId = TradeSecurityUtils.getRequiredUserId();
        Map<String, Object> request = TradeRequestUtils.parse(rawBody, params);
        memberAddressService.deleteAddress(userId, TradeRequestUtils.getLong(request, "id", 0L));
        return CommonResult.success(true);
    }

    @RequestMapping("/app-api/region/list")
    public CommonResult<List<Map<String, Object>>> regionList(@RequestBody(required = false) String rawBody,
                                                               @RequestParam Map<String, Object> params,
                                                               @RequestParam(value = "parentId", required = false) Long parentId) {
        Map<String, Object> request = TradeRequestUtils.parse(rawBody, params);
        Long id = parentId != null ? parentId : TradeRequestUtils.getLong(request, "parentId", 1L);
        return CommonResult.success(getRegionList(id));
    }

    private List<Map<String, Object>> getRegionList(Long parentId) {
        if (parentId == null || parentId == 1L) {
            return List.of(
                    Map.of("id", 11, "name", "北京市", "parentId", 1, "type", 1),
                    Map.of("id", 31, "name", "上海市", "parentId", 1, "type", 1),
                    Map.of("id", 44, "name", "广东省", "parentId", 1, "type", 1),
                    Map.of("id", 33, "name", "浙江省", "parentId", 1, "type", 1)
            );
        }
        if (parentId == 11L) {
            return List.of(Map.of("id", 1101, "name", "北京市", "parentId", 11, "type", 2));
        }
        if (parentId == 31L) {
            return List.of(Map.of("id", 3101, "name", "上海市", "parentId", 31, "type", 2));
        }
        if (parentId == 44L) {
            return List.of(
                    Map.of("id", 4401, "name", "广州市", "parentId", 44, "type", 2),
                    Map.of("id", 4403, "name", "深圳市", "parentId", 44, "type", 2)
            );
        }
        if (parentId == 33L) {
            return List.of(
                    Map.of("id", 3301, "name", "杭州市", "parentId", 33, "type", 2),
                    Map.of("id", 3302, "name", "宁波市", "parentId", 33, "type", 2)
            );
        }
        return List.of(
                Map.of("id", parentId * 100 + 1, "name", "城区", "parentId", parentId, "type", 3),
                Map.of("id", parentId * 100 + 2, "name", "新区", "parentId", parentId, "type", 3)
        );
    }
}
