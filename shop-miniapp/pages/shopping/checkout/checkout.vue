<template>
	<view class="page">
		<!-- 收货地址 -->
		<view class="address-card" @tap="selectAddress" v-if="checkedAddress.id > 0">
			<view class="address-left">
				<view class="address-user">
					<text class="address-name">{{checkedAddress.userName||''}}</text>
					<text class="address-phone">{{checkedAddress.telNumber||''}}</text>
					<view class="address-default" v-if="checkedAddress.isDefault === 1">默认</view>
				</view>
				<text class="address-detail">{{checkedAddress.fullRegion + checkedAddress.detailInfo}}</text>
			</view>
			<text class="address-arrow">›</text>
		</view>
		<view class="address-card address-empty" @tap="addAddress" v-else>
			<view class="address-empty-copy">
				<text class="empty-title">请先添加收货地址</text>
				<text class="empty-hint">下单前需要填写收货信息</text>
			</view>
			<text class="empty-add">去添加</text>
			<text class="address-arrow">›</text>
		</view>

		<!-- 商品列表 -->
		<view class="goods-card">
			<view class="goods-item" v-for="(item, index) in checkedGoodsList" :key="item.id">
				<image class="goods-img" :src="$imageUrl(item.listPicUrl)" mode="aspectFill" @error="$setImageFallback(item, 'listPicUrl')"></image>
				<view class="goods-info">
					<text class="goods-name">{{item.goodsName||''}}</text>
					<text class="goods-spec" v-if="item.goodsSpecifitionNameValue">{{item.goodsSpecifitionNameValue}}</text>
					<view class="goods-bottom">
						<text class="goods-price">￥{{item.retailPrice||''}}</text>
						<text class="goods-num">x{{item.number||''}}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 优惠券选择入口 -->
		<view class="option-card" @tap="openCouponPopup">
			<text class="option-label">优惠券</text>
			<view class="option-right">
				<text class="option-value" :class="{ 'has-coupon': selectedCouponId }">
					{{selectedCouponId ? '-￥' + couponPrice : (couponList.length > 0 ? couponList.length + '张可用' : '暂无可用')}}
				</text>
				<text class="option-arrow">›</text>
			</view>
		</view>

		<!-- 满减提示条 -->
		<view v-if="promotion && !selectedCouponId" class="promo-bar">
			<text v-if="promotionGap" class="promo-text">再买 ￥{{promotionGap}} 可享「{{promotion.name}}」</text>
			<text v-else class="promo-text">已享「{{promotion.name}}」优惠</text>
		</view>

		<!-- 金额明细 -->
		<view class="amount-card">
			<view class="amount-row">
				<text class="amount-label">商品合计</text>
				<text class="amount-value">￥{{goodsTotalPrice}}</text>
			</view>
			<view class="amount-row">
				<text class="amount-label">运费</text>
				<text class="amount-value">￥{{freightPrice}}</text>
			</view>
			<view class="amount-row" v-if="couponPrice > 0">
				<text class="amount-label">优惠{{discountSource === 'promotion' ? '(满减)' : '(优惠券)'}}</text>
				<text class="amount-value discount">-￥{{couponPrice}}</text>
			</view>
		</view>

		<!-- 优惠券选择弹窗 -->
		<view v-if="showCouponPopup" class="coupon-popup-mask" @tap="showCouponPopup = false">
			<view class="coupon-popup" @tap.stop>
				<view class="popup-header">
					<text class="popup-title">选择优惠券</text>
					<text class="popup-close" @tap="showCouponPopup = false">×</text>
				</view>
				<scroll-view scroll-y class="popup-body">
					<view class="popup-option" :class="{ selected: !selectedCouponId }" @tap="clearCoupon">
						<text class="popup-option-name">不使用优惠券</text>
						<text v-if="!selectedCouponId" class="popup-check">✓</text>
					</view>
					<view v-for="c in couponList" :key="c.id" class="popup-option" :class="{ selected: selectedCouponId === c.id }" @tap="selectCoupon(c)">
						<view class="popup-option-info">
							<text class="popup-option-name">{{c.name}} -￥{{c.discountAmount}}</text>
							<text class="popup-option-expire">有效期至 {{c.expireTime}}</text>
						</view>
						<text v-if="selectedCouponId === c.id" class="popup-check">✓</text>
					</view>
					<view v-if="couponList.length === 0" class="popup-empty">暂无可用优惠券</view>
				</scroll-view>
			</view>
		</view>

		<!-- 底部提交栏 -->
		<view class="submit-bar">
			<view class="submit-left">
				<text class="submit-label">实付</text>
				<text class="submit-price">￥{{actualPrice}}</text>
			</view>
			<view class="submit-btn" :class="{ disabled: submitting }" @tap="submitOrder">
				<text>{{submitting ? '正在提交...' : '提交订单'}}</text>
			</view>
		</view>
	</view>
</template>

<script>
const util = require("@/utils/util.js");
const api = require('@/utils/api.js');
const app = getApp();

export default {
	data() {
		return {
			checkedGoodsList: [],
			checkedAddress: { id: 0 },
			goodsTotalPrice: 0.00,
			freightPrice: 0.00,
			orderTotalPrice: 0.00,
			actualPrice: 0.00,
			couponPrice: 0.00,
			discountSource: '',
			addressId: 0,
			isBuy: false,
			buyType: '',
			submitting: false,
			requestId: '',
			couponList: [],
			selectedCouponId: null,
			showCouponPopup: false,
			promotion: null,
			promotionGap: '',
			addressGuideShown: false
		}
	},
	methods: {
		getCheckoutInfo() {
			let buyType = this.isBuy ? 'buy' : 'cart';
			let params = {
				addressId: this.addressId,
				type: buyType
			};
			if (this.selectedCouponId) {
				params.couponId = this.selectedCouponId;
			}
			util.request(api.CartCheckout, params).then(res => {
				if (res.code === 0) {
					this.checkedGoodsList = res.data.checkedGoodsList;
					this.checkedAddress = res.data.checkedAddress || { id: 0 };
					this.actualPrice = res.data.actualPrice;
					this.freightPrice = res.data.freightPrice;
					this.goodsTotalPrice = res.data.goodsTotalPrice;
					this.orderTotalPrice = res.data.orderTotalPrice;
					this.couponPrice = res.data.couponPrice || 0.00;
					this.discountSource = res.data.discountSource || '';
					this.couponList = res.data.couponList || [];
					this.selectedCouponId = res.data.selectedCouponId || null;
					this.promotion = res.data.promotion || null;
					this.promotionGap = res.data.promotionGap || '';
					if (this.checkedAddress.id) {
						this.addressId = this.checkedAddress.id;
					} else if (!this.addressGuideShown) {
						this.addressGuideShown = true;
						this.showAddressGuide();
					}
				}
			});
		},
		selectAddress() {
			uni.navigateTo({ url: '/pages/shopping/address/address' });
		},
		addAddress() {
			uni.navigateTo({ url: '/pages/shopping/addressAdd/addressAdd' });
		},
		showAddressGuide() {
			uni.showModal({
				title: '还差一步',
				content: '下单前需要添加收货地址。现在去添加吗？',
				confirmText: '去添加',
				cancelText: '暂不添加',
				confirmColor: '#5B8C5A',
				success: (res) => {
					if (res.confirm) this.addAddress();
				}
			});
		},
		showOrderSubmitGuide(message) {
			const reason = String(message || '订单信息发生变化，请重新确认后再提交');
			if (/地址|收货人/.test(reason)) {
				this.showAddressGuide();
				return;
			}
			if (/商品|库存|下架|失效|规格/.test(reason)) {
				uni.showModal({
					title: '商品状态有变化',
					content: `${reason}。请返回检查商品后再下单。`,
					confirmText: this.isBuy ? '返回商品' : '查看购物车',
					cancelText: '留在此页',
					confirmColor: '#5B8C5A',
					success: (res) => {
						if (!res.confirm) return;
						if (this.isBuy) uni.navigateBack();
						else uni.switchTab({ url: '/pages/cart/cart' });
					}
				});
				return;
			}
			if (/优惠券/.test(reason)) {
				uni.showModal({
					title: '优惠券不可用',
					content: `${reason}。请重新选择优惠券或不使用优惠券继续下单。`,
					confirmText: '重新选择',
					cancelText: '稍后处理',
					confirmColor: '#5B8C5A',
					success: (res) => {
						if (!res.confirm) return;
						this.selectedCouponId = null;
						this.showCouponPopup = true;
						this.getCheckoutInfo();
					}
				});
				return;
			}
			uni.showModal({
				title: '暂时无法下单',
				content: `${reason}。请重新检查订单信息后再试。`,
				confirmText: '重新检查',
				cancelText: '稍后再试',
				confirmColor: '#5B8C5A',
				success: (res) => {
					if (res.confirm) this.getCheckoutInfo();
				}
			});
		},
		submitOrder() {
			if (this.submitting) {
				return;
			}
			if (this.addressId <= 0) {
				this.showAddressGuide();
				return;
			}
			this.submitting = true;
			let params = {
				addressId: this.addressId,
				type: this.buyType,
				requestId: this.requestId
			};
			if (this.selectedCouponId) {
				params.couponId = this.selectedCouponId;
			}
			util.request(api.OrderSubmit, params, 'POST', 'application/json').then(res => {
				if (res.code === 0) {
					const orderId = res.data.orderInfo.id;
					util.payOrder(parseInt(orderId)).then(() => {
						uni.redirectTo({ url: '/pages/payResult/payResult?status=1&orderId=' + orderId });
					}).catch((error) => {
						const status = error && error.pending ? 2 : 0;
						uni.redirectTo({ url: '/pages/payResult/payResult?status=' + status + '&orderId=' + orderId });
					});
				} else {
					this.submitting = false;
					this.showOrderSubmitGuide(res.msg);
				}
			}).catch(() => {
				this.submitting = false;
				this.showOrderSubmitGuide('网络连接异常');
			});
		},
		openCouponPopup() {
			this.showCouponPopup = true;
		},
		selectCoupon(coupon) {
			this.selectedCouponId = coupon.id;
			this.showCouponPopup = false;
			this.getCheckoutInfo();
		},
		clearCoupon() {
			this.selectedCouponId = null;
			this.showCouponPopup = false;
			this.getCheckoutInfo();
		}
	},
	onShow() {
		try {
			var addressId = uni.getStorageSync('addressId');
			if (addressId) this.addressId = addressId;
		} catch (e) {}
		this.getCheckoutInfo();
	},
	onLoad(options) {
		if (options.isBuy != null) this.isBuy = options.isBuy;
		this.buyType = this.isBuy ? 'buy' : 'cart';
		this.requestId = 'MP' + Date.now() + Math.random().toString(36).slice(2, 10);
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
}

.page {
	min-height: 100vh;
	padding: 16rpx 24rpx 130rpx;
}

/* 地址卡片 */
.address-card {
	background: #FEFEFC;
	border-radius: 20rpx;
	padding: 28rpx;
	margin-bottom: 16rpx;
	display: flex;
	align-items: center;
	box-shadow: 0 2rpx 12rpx rgba(91, 140, 90, 0.05);
}

.address-left {
	flex: 1;
	overflow: hidden;
}

.address-user {
	display: flex;
	align-items: center;
	margin-bottom: 12rpx;
}

.address-name {
	font-size: 30rpx;
	font-weight: 700;
	color: $text-primary;
	margin-right: 16rpx;
}

.address-phone {
	font-size: 26rpx;
	color: $text-secondary;
}

.address-default {
	margin-left: 12rpx;
	font-size: 20rpx;
	color: $green;
	border: 2rpx solid $green;
	border-radius: 6rpx;
	padding: 2rpx 10rpx;
}

.address-detail {
	font-size: 26rpx;
	color: $text-secondary;
	line-height: 1.5;
	display: block;
}

.address-arrow {
	font-size: 36rpx;
	color: $text-hint;
	margin-left: 12rpx;
}

.address-empty {
	border: 2rpx dashed #A9C6AA;
	padding: 40rpx 28rpx;
}

.address-empty-copy {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 8rpx;
}

.empty-title {
	font-size: 30rpx;
	font-weight: 600;
	color: $text-primary;
}

.empty-hint {
	font-size: 24rpx;
	color: $text-hint;
}

.empty-add {
	font-size: 28rpx;
	color: $green;
	font-weight: 600;
	margin-left: 20rpx;
}

/* 商品卡片 */
.goods-card {
	background: #FEFEFC;
	border-radius: 20rpx;
	padding: 24rpx;
	margin-bottom: 16rpx;
	box-shadow: 0 2rpx 12rpx rgba(91, 140, 90, 0.05);
}

.goods-item {
	display: flex;
	padding: 16rpx 0;
	border-bottom: 1rpx solid $green-bg;

	&:last-child {
		border-bottom: none;
		padding-bottom: 0;
	}

	&:first-child {
		padding-top: 0;
	}
}

.goods-img {
	width: 140rpx;
	height: 140rpx;
	border-radius: 12rpx;
	background: $green-bg;
	margin-right: 20rpx;
	flex-shrink: 0;
}

.goods-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	overflow: hidden;
}

.goods-name {
	font-size: 26rpx;
	color: $text-primary;
	display: -webkit-box;
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 2;
	overflow: hidden;
	line-height: 1.4;
}

.goods-spec {
	font-size: 22rpx;
	color: $text-hint;
	margin-top: 8rpx;
	background: $green-bg;
	padding: 4rpx 12rpx;
	border-radius: 6rpx;
	align-self: flex-start;
}

.goods-bottom {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-top: 8rpx;
}

.goods-price {
	font-size: 28rpx;
	color: $red;
	font-weight: 700;
}

.goods-num {
	font-size: 24rpx;
	color: $text-hint;
}

/* 选项卡片 */
.option-card {
	background: #FEFEFC;
	border-radius: 20rpx;
	padding: 28rpx;
	margin-bottom: 16rpx;
	display: flex;
	align-items: center;
	justify-content: space-between;
	box-shadow: 0 2rpx 12rpx rgba(91, 140, 90, 0.05);
}

.option-label {
	font-size: 28rpx;
	color: $text-primary;
}

.option-right {
	display: flex;
	align-items: center;
}

.option-value {
	font-size: 26rpx;
	color: $text-hint;
	margin-right: 8rpx;

	&.has-coupon {
		color: $red;
	}
}

.option-arrow {
	font-size: 32rpx;
	color: $text-hint;
}

/* 金额卡片 */
.amount-card {
	background: #FEFEFC;
	border-radius: 20rpx;
	padding: 24rpx 28rpx;
	margin-bottom: 16rpx;
	box-shadow: 0 2rpx 12rpx rgba(91, 140, 90, 0.05);
}

.amount-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 12rpx 0;
}

.amount-label {
	font-size: 26rpx;
	color: $text-secondary;
}

.amount-value {
	font-size: 26rpx;
	color: $text-primary;

	&.discount {
		color: $red;
	}
}

/* 底部提交栏 */
.submit-bar {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 0;
	height: 110rpx;
	background: #FEFEFC;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 24rpx;
	box-shadow: 0 -4rpx 16rpx rgba(91, 140, 90, 0.08);
	z-index: 100;
}

.submit-left {
	display: flex;
	align-items: baseline;
}

.submit-label {
	font-size: 26rpx;
	color: $text-secondary;
	margin-right: 8rpx;
}

.submit-price {
	font-size: 40rpx;
	color: $red;
	font-weight: 700;
}

.submit-btn {
	height: 80rpx;
	padding: 0 56rpx;
	background: linear-gradient(135deg, $green, $green-dark);
	border-radius: 40rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 30rpx;
	color: #FEFEFC;
	font-weight: 600;

	&.disabled {
		opacity: 0.55;
	}
}

/* 满减提示条 */
.promo-bar {
	background: $green-light;
	border-radius: 12rpx;
	padding: 16rpx 24rpx;
	margin-bottom: 16rpx;
}

.promo-text {
	font-size: 24rpx;
	color: $green;
}

/* 优惠券弹窗 */
.coupon-popup-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.45);
	z-index: 200;
	display: flex;
	align-items: flex-end;
}

.coupon-popup {
	width: 100%;
	background: #FEFEFC;
	border-radius: 24rpx 24rpx 0 0;
	max-height: 70vh;
	display: flex;
	flex-direction: column;
}

.popup-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 28rpx 32rpx;
	border-bottom: 1rpx solid #E8ECE8;
}

.popup-title {
	font-size: 32rpx;
	font-weight: 600;
	color: $text-primary;
}

.popup-close {
	font-size: 40rpx;
	color: $text-hint;
	padding: 0 8rpx;
}

.popup-body {
	max-height: 60vh;
	padding: 16rpx 32rpx;
}

.popup-option {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 24rpx 16rpx;
	border-bottom: 1rpx solid $green-bg;

	&.selected {
		background: $green-light;
		border-radius: 12rpx;
	}
}

.popup-option-info {
	flex: 1;
	overflow: hidden;
}

.popup-option-name {
	font-size: 28rpx;
	color: $text-primary;
}

.popup-option-expire {
	font-size: 22rpx;
	color: $text-hint;
	display: block;
	margin-top: 4rpx;
}

.popup-check {
	font-size: 32rpx;
	color: $green;
	font-weight: 700;
	margin-left: 16rpx;
}

.popup-empty {
	text-align: center;
	color: $text-hint;
	padding: 80rpx 0;
	font-size: 28rpx;
}
</style>
