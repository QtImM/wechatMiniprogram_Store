<template>
	<view class="container">
		<view class="no-use-btn" @tap="noUseCoupon">不使用优惠券</view>
		<view class="empty-view" v-if="couponList && couponList.length <= 0">
			<text class="empty-text">暂无可用优惠券</text>
		</view>
		<view class="coupon-list" v-if="couponList && couponList.length > 0">
			<view class="coupon-card" v-for="(item, index) in couponList" :key="item.id"
			 :class="item.enabled == 1 ? 'active' : 'disabled'" @tap="tapCoupon(item)">
				<view class="coupon-main">
					<view class="coupon-left">
						<text class="coupon-amount">¥{{item.typeMoney}}</text>
						<text class="coupon-condition">满{{item.minGoodsAmount}}可用</text>
					</view>
					<view class="coupon-right">
						<text class="coupon-name">{{item.name}}</text>
						<text class="coupon-date">有效期至 {{item.useEndDate}}</text>
					</view>
				</view>
				<view class="coupon-tag" v-if="item.enabled == 1">可用</view>
				<view class="coupon-tag tag-disabled" v-else>不可用</view>
			</view>
		</view>
	</view>
</template>

<script>
	const util = require("@/utils/util.js");
	const api = require('@/utils/api.js');
	export default {
		data() {
			return {
				couponList: null,
				buyType: ''
			}
		},
		methods: {
			loadListData: function() {
				let that = this;
				util.request(api.GoodsCouponList, { type: this.buyType }).then(function(res) {
					if (res.code === 0) {
						that.couponList = res.data || [];
					}
				});
			},
			noUseCoupon: function() {
				uni.navigateBack();
			},
			tapCoupon: function(item) {
				if (item.enabled == 0) return;
				uni.navigateBack();
			}
		},
		onLoad: function(options) {
			this.buyType = options.buyType;
			this.loadListData();
		}
	}
</script>

<style lang="scss">
	$green: #5B8C5A;
	$green-light: #7BAF7A;
	$green-bg: #F6F7F4;
	$gold: #B8860B;

	page {
		background: $green-bg;
		min-height: 100%;
	}

	.container {
		padding: 24rpx;
	}

	.no-use-btn {
		width: 100%;
		height: 80rpx;
		line-height: 80rpx;
		text-align: center;
		background: #FEFEFC;
		border-radius: 12rpx;
		font-size: 28rpx;
		color: #666;
		margin-bottom: 24rpx;
		box-shadow: 0 2rpx 8rpx rgba(91,140,90,0.06);
	}

	.coupon-list {
		width: 100%;
	}

	.coupon-card {
		position: relative;
		background: linear-gradient(135deg, $green 0%, $green-light 100%);
		border-radius: 16rpx;
		margin-bottom: 20rpx;
		overflow: hidden;
		box-shadow: 0 4rpx 16rpx rgba(91,140,90,0.2);

		&.disabled {
			background: linear-gradient(135deg, #bbb 0%, #ddd 100%);
			box-shadow: none;
		}
	}

	.coupon-main {
		display: flex;
		align-items: center;
		padding: 30rpx;
	}

	.coupon-left {
		width: 180rpx;
		text-align: center;
		border-right: 1rpx dashed rgba(255,255,255,0.4);
		padding-right: 24rpx;
		margin-right: 24rpx;
	}

	.coupon-amount {
		display: block;
		font-size: 48rpx;
		font-weight: bold;
		color: #FEFEFC;
	}

	.coupon-condition {
		display: block;
		font-size: 20rpx;
		color: rgba(255,255,255,0.8);
		margin-top: 6rpx;
	}

	.coupon-right {
		flex: 1;
	}

	.coupon-name {
		display: block;
		font-size: 30rpx;
		color: #FEFEFC;
		font-weight: 500;
		margin-bottom: 8rpx;
	}

	.coupon-date {
		display: block;
		font-size: 22rpx;
		color: rgba(255,255,255,0.7);
	}

	.coupon-tag {
		position: absolute;
		top: 16rpx;
		right: 16rpx;
		font-size: 20rpx;
		color: #FEFEFC;
		background: rgba(255,255,255,0.2);
		padding: 4rpx 12rpx;
		border-radius: 10rpx;

		&.tag-disabled {
			background: rgba(0,0,0,0.1);
			color: rgba(255,255,255,0.6);
		}
	}

	.empty-view {
		padding: 100rpx 0;
		text-align: center;
	}

	.empty-text {
		font-size: 28rpx;
		color: #999;
	}
</style>
