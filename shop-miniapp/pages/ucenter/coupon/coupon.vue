<template>
	<view class="page">
		<!-- 优惠券列表 -->
		<view class="coupon-list" v-if="couponList.length > 0">
			<view class="coupon-card" :class="{disabled: item.couponStatus != 1}"
				v-for="(item, index) in couponList" :key="item.id">
				<!-- 左侧金额 -->
				<view class="coupon-left">
					<text class="coupon-amount">¥{{item.typeMoney}}</text>
					<text class="coupon-condition">满{{item.minGoodsAmount}}可用</text>
				</view>
				<!-- 右侧信息 -->
				<view class="coupon-right">
					<text class="coupon-name">{{item.name||''}}</text>
					<text class="coupon-time">有效期至 {{item.useEndDate||''}}</text>
					<view class="coupon-status">
						<text class="status-text" v-if="item.couponStatus==1">可使用</text>
						<text class="status-text used" v-if="item.couponStatus==2">已使用</text>
						<text class="status-text expired" v-if="item.couponStatus==3">已过期</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 空状态 -->
		<view class="empty-state" v-else>
			<text class="empty-icon">🎫</text>
			<text class="empty-text">暂无优惠券</text>
			<text class="empty-hint">去首页领取专属优惠吧</text>
		</view>
	</view>
</template>

<script>
const util = require("@/utils/util.js");
const api = require('@/utils/api.js');

export default {
	data() {
		return {
			couponList: []
		}
	},
	methods: {
		loadListData() {
			util.request(api.CouponList).then(res => {
				if (res.code === 0) this.couponList = res.data;
			});
		}
	},
	onShow() {
		this.loadListData();
	}
}
</script>

<style lang="scss">
$green: #5B8C5A;
$green-light: #E8F2E7;
$green-bg: #F6F7F4;
$green-dark: #3D6B3C;
$text-primary: #2D3A2E;
$text-secondary: #5C6B5D;
$text-hint: #9CA89D;
$red: #CF4A3E;

page {
	background: $green-bg;
	min-height: 100%;
}

.page {
	padding: 16rpx 24rpx;
	min-height: 100vh;
}

/* 优惠券卡片 */
.coupon-card {
	display: flex;
	background: #FEFEFC;
	border-radius: 20rpx;
	margin-bottom: 16rpx;
	overflow: hidden;
	box-shadow: 0 2rpx 12rpx rgba(91, 140, 90, 0.05);

	&.disabled {
		opacity: 0.6;

		.coupon-left {
			background: linear-gradient(135deg, #aaa, #bbb);
		}
	}
}

.coupon-left {
	width: 200rpx;
	background: linear-gradient(135deg, $green, $green-dark);
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 24rpx 0;
	flex-shrink: 0;
}

.coupon-amount {
	font-size: 44rpx;
	color: #FEFEFC;
	font-weight: 700;
}

.coupon-condition {
	font-size: 20rpx;
	color: rgba(255, 255, 255, 0.8);
	margin-top: 8rpx;
}

.coupon-right {
	flex: 1;
	padding: 24rpx;
	display: flex;
	flex-direction: column;
	justify-content: center;
}

.coupon-name {
	font-size: 28rpx;
	color: $text-primary;
	font-weight: 600;
}

.coupon-time {
	font-size: 22rpx;
	color: $text-hint;
	margin-top: 8rpx;
}

.coupon-status {
	margin-top: 12rpx;
}

.status-text {
	font-size: 22rpx;
	color: $green;
	font-weight: 600;

	&.used {
		color: $text-hint;
	}

	&.expired {
		color: $text-hint;
	}
}

/* 空状态 */
.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding-top: 240rpx;
}

.empty-icon {
	font-size: 100rpx;
	opacity: 0.5;
}

.empty-text {
	font-size: 30rpx;
	color: $text-primary;
	margin-top: 24rpx;
}

.empty-hint {
	font-size: 26rpx;
	color: $text-hint;
	margin-top: 12rpx;
}
</style>
