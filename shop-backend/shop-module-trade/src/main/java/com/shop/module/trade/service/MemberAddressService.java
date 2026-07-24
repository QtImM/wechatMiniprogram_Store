package com.shop.module.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.shop.common.exception.ServerException;
import com.shop.module.trade.dal.dataobject.MemberAddressDO;
import com.shop.module.trade.dal.mysql.MemberAddressMapper;
import com.shop.module.trade.util.TradeRequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberAddressService {

    private final MemberAddressMapper memberAddressMapper;

    public List<Map<String, Object>> getAddressList(Long userId) {
        return memberAddressMapper.selectList(new LambdaQueryWrapper<MemberAddressDO>()
                        .eq(MemberAddressDO::getUserId, userId)
                        .orderByDesc(MemberAddressDO::getIsDefault)
                        .orderByDesc(MemberAddressDO::getUpdateTime))
                .stream()
                .map(this::toResp)
                .toList();
    }

    public MemberAddressDO getDefaultOrFirst(Long userId) {
        List<MemberAddressDO> list = memberAddressMapper.selectList(new LambdaQueryWrapper<MemberAddressDO>()
                .eq(MemberAddressDO::getUserId, userId)
                .orderByDesc(MemberAddressDO::getIsDefault)
                .orderByDesc(MemberAddressDO::getUpdateTime));
        return list.isEmpty() ? null : list.get(0);
    }

    public MemberAddressDO getAddress(Long userId, Long id) {
        if (id == null || id <= 0) {
            return getDefaultOrFirst(userId);
        }
        MemberAddressDO address = memberAddressMapper.selectOne(new LambdaQueryWrapper<MemberAddressDO>()
                .eq(MemberAddressDO::getUserId, userId)
                .eq(MemberAddressDO::getId, id));
        if (address == null) {
            throw new ServerException(1404, "收货地址不存在");
        }
        return address;
    }

    public Map<String, Object> getAddressDetail(Long userId, Long id) {
        return toResp(getAddress(userId, id));
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveAddress(Long userId, Map<String, Object> body) {
        Long id = TradeRequestUtils.getLong(body, "id", 0L);
        Integer isDefault = TradeRequestUtils.getInt(body, "isDefault", 0);
        if (isDefault == 1) {
            memberAddressMapper.update(null, new LambdaUpdateWrapper<MemberAddressDO>()
                    .eq(MemberAddressDO::getUserId, userId)
                    .set(MemberAddressDO::getIsDefault, 0));
        }

        MemberAddressDO address = id > 0 ? getAddress(userId, id) : new MemberAddressDO();
        address.setUserId(userId);
        address.setUserName(TradeRequestUtils.getString(body, "userName", ""));
        address.setTelNumber(TradeRequestUtils.getString(body, "telNumber", ""));
        address.setProvinceId(TradeRequestUtils.getLong(body, "provinceId", 0L));
        address.setCityId(TradeRequestUtils.getLong(body, "cityId", 0L));
        address.setDistrictId(TradeRequestUtils.getLong(body, "districtId", 0L));
        address.setProvinceName(TradeRequestUtils.getString(body, "provinceName", ""));
        address.setCityName(TradeRequestUtils.getString(body, "cityName", ""));
        address.setDistrictName(TradeRequestUtils.getString(body, "countyName",
                TradeRequestUtils.getString(body, "districtName", "")));
        address.setFullRegion(address.getProvinceName() + address.getCityName() + address.getDistrictName());
        address.setDetailInfo(TradeRequestUtils.getString(body, "detailInfo", ""));
        address.setIsDefault(isDefault);

        if (address.getUserName().isBlank() || address.getTelNumber().isBlank() || address.getDetailInfo().isBlank()) {
            throw new ServerException(400, "收货地址信息不完整");
        }

        if (id > 0) {
            memberAddressMapper.updateById(address);
        } else {
            memberAddressMapper.insert(address);
        }
    }

    public void deleteAddress(Long userId, Long id) {
        MemberAddressDO address = getAddress(userId, id);
        memberAddressMapper.deleteById(address.getId());
    }

    public Map<String, Object> toResp(MemberAddressDO address) {
        if (address == null) {
            Map<String, Object> empty = new LinkedHashMap<>();
            empty.put("id", 0);
            return empty;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", address.getId());
        result.put("userName", address.getUserName());
        result.put("telNumber", address.getTelNumber());
        result.put("provinceId", address.getProvinceId());
        result.put("cityId", address.getCityId());
        result.put("districtId", address.getDistrictId());
        result.put("provinceName", address.getProvinceName());
        result.put("cityName", address.getCityName());
        result.put("districtName", address.getDistrictName());
        result.put("fullRegion", address.getFullRegion());
        result.put("detailInfo", address.getDetailInfo());
        result.put("isDefault", address.getIsDefault());
        return result;
    }
}
