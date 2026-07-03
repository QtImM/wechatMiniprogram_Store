<template>
	<view class="page">
		<!-- 顶部服务保障 -->
		<view class="guarantee-bar" v-if="cartGoods.length > 0">
			<view class="guarantee-item">
				<text class="guarantee-dot">✓</text>
				<text>正品保障</text>
			</view>
			<view class="guarantee-item">
				<text class="guarantee-dot">✓</text>
				<text>极速退款</text>
			</view>
			<view class="guarantee-item">
				<text class="guarantee-dot">✓</text>
				<text>满299包邮</text>
			</view>
		</view>

		<!-- 包邮进度条 -->
		<view class="shipping-bar" v-if="cartGoods.length > 0">
			<view class="shipping-info" v-if="freeShippingDiff > 0">
				<text class="shipping-text">再买</text>
				<text class="shipping-amount">¥{{freeShippingDiff}}</text>
				<text class="shipping-text">即可享受包邮~</text>
			</view>
			<view class="shipping-info" v-else>
				<text class="shipping-done">🎉 已满足包邮条件</text>
			</view>
			<view class="shipping-progress">
				<view class="shipping-progress-fill" :style="{width: shippingPercent + '%'}"></view>
			</view>
		</view>

		<!-- 空购物车 -->
		<view class="empty-cart" v-if="cartGoods.length <= 0">
			<view class="empty-icon">🛒</view>
			<text class="empty-title">购物车是空的</text>
			<text class="empty-desc">去逛逛，发现更多好物吧</text>
			<view class="empty-btn" @tap="toIndexPage">去逛逛</view>
		</view>

		<!-- 购物车列表 -->
		<view class="cart-list" v-if="cartGoods.length > 0">
			<view
				class="cart-item"
				v-for="(item, index) in cartGoods"
				:key="item.id"
			>
				<view class="checkbox" :class="{checked: item.checked}" @tap="checkedItem(index)">
					<text class="check-icon" v-if="item.checked">✓</text>
				</view>

				<navigator :url="'/pages/goods/goods?id=' + item.goodsId" class="item-img-wrap">
					<image class="item-img" :src="item.listPicUrl" mode="aspectFill"></image>
				</navigator>

				<view class="item-info">
					<text class="item-name">{{item.goodsName}}</text>
					<text class="item-spec" v-if="item.goodsSpecifitionNameValue">{{item.goodsSpecifitionNameValue}}</text>
					<view class="item-bottom">
						<text class="item-price">¥{{item.retailPrice}}</text>
						<view class="stepper">
							<view class="stepper-btn minus" :class="{disabled: item.number <= 1}" @tap="cutNumber(index)">
								<text>−</text>
							</view>
							<text class="stepper-num">{{item.number}}</text>
							<view class="stepper-btn plus" @tap="addNumber(index)">
								<text>+</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 凑单助手 - 仅未满包邮时显示 -->
		<view class="addon-section" v-if="freeShippingDiff > 0 && recommendList.length > 0">
			<view class="addon-header">
				<text class="addon-title">再凑¥{{freeShippingDiff}}即可包邮</text>
				<text class="addon-hint">左滑查看更多</text>
			</view>
			<scroll-view class="addon-scroll" scroll-x>
				<view class="addon-list">
					<navigator class="addon-card" v-for="(item, index) in recommendList"
					 :key="index" :url="'/pages/goods/goods?id='+item.id">
						<image class="addon-img" :src="item.listPicUrl" mode="aspectFill"></image>
						<text class="addon-name">{{item.name}}</text>
						<text class="addon-price">¥{{item.retailPrice}}</text>
					</navigator>
				</view>
			</scroll-view>
		</view>

		<!-- 底部结算栏 -->
		<view class="bottom-bar" v-if="cartGoods.length > 0">
			<view class="bottom-left">
				<view class="checkbox bottom-check" :class="{checked: checkedAllStatus}" @tap="checkedAll">
					<text class="check-icon" v-if="checkedAllStatus">✓</text>
				</view>
				<text class="bottom-all-text">全选</text>
			</view>

			<view class="bottom-center">
				<text class="total-label">合计:</text>
				<text class="total-price">¥{{cartTotal.checkedGoodsAmount}}</text>
			</view>

			<view class="bottom-right">
				<view class="edit-btn" @tap="editCart" v-if="!isEditCart">
					<text>编辑</text>
				</view>
				<view class="edit-btn" @tap="editCart" v-else>
					<text>完成</text>
				</view>

				<view class="delete-btn" @tap="deleteCart" v-if="isEditCart">
					<text>删除</text>
				</view>
				<view class="checkout-btn" @tap="checkoutOrder" v-if="!isEditCart">
					<text>结算({{cartTotal.checkedGoodsCount}})</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
const util = require('@/utils/util.js');
const api = require('@/utils/api.js');

export default {
	data() {
		return {
			cartGoods: [],
			cartTotal: {
				goodsCount: 0,
				goodsAmount: 0.00,
				checkedGoodsCount: 0,
				checkedGoodsAmount: 0.00
			},
			isEditCart: false,
			checkedAllStatus: true,
			recommendList: []
		}
	},
	computed: {
		freeShippingDiff() {
			const threshold = 299;
			const amount = parseFloat(this.cartTotal.checkedGoodsAmount) || 0;
			const diff = threshold - amount;
			return diff > 0 ? diff.toFixed(2) : 0;
		},
		shippingPercent() {
			const threshold = 299;
			const amount = parseFloat(this.cartTotal.checkedGoodsAmount) || 0;
			return Math.min(100, (amount / threshold) * 100);
		}
	},
	methods: {
		getCartList() {
			util.request(api.CartList).then(res => {
				if (res.code === 0) {
					this.cartGoods = res.data.cartList;
					this.cartTotal = res.data.cartTotal;
				}
				this.checkedAllStatus = this.isCheckedAll();
			});
		},
		getRecommend() {
			util.request(api.GoodsList, { page: 1, size: 6 }).then(res => {
				if (res.code === 0 && res.data.goodsList) {
					this.recommendList = res.data.goodsList.records || [];
				}
			});
		},
		isCheckedAll() {
			return this.cartGoods.every(item => item.checked === true);
		},
		getCheckedGoodsCount() {
			let count = 0;
			this.cartGoods.forEach(v => {
				if (v.checked) count += v.number;
			});
			return count;
		},
		checkedItem(index) {
			if (!this.isEditCart) {
				util.request(api.CartChecked, {
					productIds: this.cartGoods[index].productId,
					isChecked: this.cartGoods[index].checked ? 0 : 1
				}, 'POST', 'application/json').then(res => {
					if (res.code === 0) {
						this.cartGoods = res.data.cartList;
						this.cartTotal = res.data.cartTotal;
					}
					this.checkedAllStatus = this.isCheckedAll();
				});
			} else {
				this.cartGoods[index].checked = !this.cartGoods[index].checked;
				this.cartGoods = [...this.cartGoods];
				this.cartTotal.checkedGoodsCount = this.getCheckedGoodsCount();
				this.checkedAllStatus = this.isCheckedAll();
			}
		},
		checkedAll() {
			if (!this.isEditCart) {
				const productIds = this.cartGoods.map(v => v.productId);
				util.request(api.CartChecked, {
					productIds: productIds.join(','),
					isChecked: this.isCheckedAll() ? 0 : 1
				}, 'POST', 'application/json').then(res => {
					if (res.code === 0) {
						this.cartGoods = res.data.cartList;
						this.cartTotal = res.data.cartTotal;
					}
					this.checkedAllStatus = this.isCheckedAll();
				});
			} else {
				const allChecked = this.isCheckedAll();
				this.cartGoods = this.cartGoods.map(v => ({ ...v, checked: !allChecked }));
				this.cartTotal.checkedGoodsCount = this.getCheckedGoodsCount();
				this.checkedAllStatus = this.isCheckedAll();
			}
		},
		editCart() {
			if (this.isEditCart) {
				this.getCartList();
			} else {
				this.cartGoods = this.cartGoods.map(v => ({ ...v, checked: false }));
				this.cartTotal.checkedGoodsCount = 0;
				this.checkedAllStatus = false;
			}
			this.isEditCart = !this.isEditCart;
		},
		cutNumber(index) {
			const item = this.cartGoods[index];
			if (item.number <= 1) return;
			item.number -= 1;
			this.cartGoods = [...this.cartGoods];
			this.updateCart(item.productId, item.goodsId, item.number, item.id);
		},
		addNumber(index) {
			const item = this.cartGoods[index];
			item.number += 1;
			this.cartGoods = [...this.cartGoods];
			this.updateCart(item.productId, item.goodsId, item.number, item.id);
		},
		updateCart(productId, goodsId, number, id) {
			util.request(api.CartUpdate, { productId, goodsId, number, id }).then(res => {
				this.checkedAllStatus = this.isCheckedAll();
			});
		},
		deleteCart() {
			const productIds = this.cartGoods
				.filter(v => v.checked)
				.map(v => v.productId);
			if (productIds.length <= 0) return;

			util.request(api.CartDelete, {
				productIds: productIds.join(',')
			}, 'POST', 'application/json').then(res => {
				if (res.code === 0) {
					this.cartGoods = res.data.cartList.map(v => ({ ...v, checked: false }));
					this.cartTotal = res.data.cartTotal;
				}
				this.checkedAllStatus = this.isCheckedAll();
			});
		},
		checkoutOrder() {
			const checked = this.cartGoods.filter(v => v.checked);
			if (checked.length <= 0) {
				uni.showToast({ title: '请选择商品', icon: 'none' });
				return;
			}
			uni.navigateTo({ url: '/pages/shopping/checkout/checkout' });
		},
		toIndexPage() {
			uni.switchTab({ url: '/pages/index/index' });
		}
	},
	onShow() {
		this.isEditCart = false;
		this.getCartList();
		this.getRecommend();
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
	height: 100%;
}

.page {
	min-height: 100%;
	padding-bottom: 130rpx;
}

/* 服务保障 */
.guarantee-bar {
	display: flex;
	justify-content: center;
	padding: 18rpx 24rpx;
	background: #fff;
}

.guarantee-item {
	display: flex;
	align-items: center;
	font-size: 22rpx;
	color: $text-secondary;
	margin: 0 20rpx;
}

.guarantee-dot {
	color: $green;
	font-size: 22rpx;
	margin-right: 6rpx;
	font-weight: 700;
}

/* 包邮进度条 */
.shipping-bar {
	margin: 16rpx 24rpx 0;
	background: #fff;
	border-radius: 14rpx;
	padding: 20rpx 24rpx;
	box-shadow: 0 2rpx 8rpx rgba(91,140,90,0.05);
}

.shipping-info {
	margin-bottom: 12rpx;
}

.shipping-text {
	font-size: 24rpx;
	color: $text-secondary;
}

.shipping-amount {
	font-size: 26rpx;
	color: $red;
	font-weight: 700;
	margin: 0 4rpx;
}

.shipping-done {
	font-size: 24rpx;
	color: $green;
	font-weight: 500;
}

.shipping-progress {
	height: 8rpx;
	background: #eee;
	border-radius: 4rpx;
	overflow: hidden;
}

.shipping-progress-fill {
	height: 100%;
	background: linear-gradient(90deg, $green-light, $green);
	border-radius: 4rpx;
	transition: width 0.3s;
}

/* 空购物车 */
.empty-cart {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding-top: 240rpx;
}

.empty-icon {
	font-size: 120rpx;
	opacity: 0.6;
}

.empty-title {
	font-size: 32rpx;
	color: $text-primary;
	margin-top: 24rpx;
	font-weight: 600;
}

.empty-desc {
	font-size: 26rpx;
	color: $text-hint;
	margin-top: 12rpx;
}

.empty-btn {
	margin-top: 48rpx;
	width: 240rpx;
	height: 80rpx;
	line-height: 80rpx;
	text-align: center;
	background: $green;
	color: #fff;
	border-radius: 40rpx;
	font-size: 28rpx;
	font-weight: 600;
}

/* 购物车列表 */
.cart-list {
	padding: 16rpx 24rpx 0;
}

.cart-item {
	display: flex;
	align-items: center;
	background: #fff;
	border-radius: 20rpx;
	padding: 24rpx;
	margin-bottom: 16rpx;
	box-shadow: 0 2rpx 12rpx rgba(91, 140, 90, 0.05);
}

/* 选择框 */
.checkbox {
	width: 44rpx;
	height: 44rpx;
	border-radius: 50%;
	border: 3rpx solid #ddd;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;

	&.checked {
		background: $green;
		border-color: $green;
	}
}

.check-icon {
	color: #fff;
	font-size: 24rpx;
	font-weight: 700;
}

/* 商品图 */
.item-img-wrap {
	margin: 0 20rpx;
	flex-shrink: 0;
}

.item-img {
	width: 160rpx;
	height: 160rpx;
	border-radius: 16rpx;
	background: $green-bg;
}

/* 商品信息 */
.item-info {
	flex: 1;
	overflow: hidden;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	min-height: 160rpx;
}

.item-name {
	font-size: 26rpx;
	color: $text-primary;
	display: -webkit-box;
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 2;
	overflow: hidden;
	line-height: 1.4;
}

.item-spec {
	font-size: 22rpx;
	color: $text-hint;
	background: $green-bg;
	padding: 4rpx 12rpx;
	border-radius: 6rpx;
	margin-top: 8rpx;
	align-self: flex-start;
}

.item-bottom {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-top: 12rpx;
}

.item-price {
	font-size: 30rpx;
	color: $red;
	font-weight: 700;
}

/* 数量步进器 */
.stepper {
	display: flex;
	align-items: center;
	height: 52rpx;
	border-radius: 26rpx;
	background: $green-bg;
	overflow: hidden;
}

.stepper-btn {
	width: 52rpx;
	height: 52rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 28rpx;
	color: $text-primary;

	&.disabled {
		color: $text-hint;
		opacity: 0.5;
	}

	&.plus {
		color: $green;
		font-weight: 700;
	}
}

.stepper-num {
	min-width: 56rpx;
	text-align: center;
	font-size: 26rpx;
	color: $text-primary;
	font-weight: 600;
}

/* 凑单助手 */
.addon-section {
	padding: 24rpx 0 0;
	margin-top: 8rpx;
}

.addon-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 24rpx 16rpx;
}

.addon-title {
	font-size: 26rpx;
	color: $green;
	font-weight: 600;
}

.addon-hint {
	font-size: 22rpx;
	color: $text-hint;
}

.addon-scroll {
	white-space: nowrap;
	padding-left: 24rpx;
}

.addon-list {
	display: inline-flex;
}

.addon-card {
	display: inline-flex;
	flex-direction: column;
	width: 200rpx;
	margin-right: 16rpx;
	background: #fff;
	border-radius: 14rpx;
	overflow: hidden;
	box-shadow: 0 2rpx 8rpx rgba(91,140,90,0.06);
	text-decoration: none;
	flex-shrink: 0;
}

.addon-img {
	width: 200rpx;
	height: 200rpx;
}

.addon-name {
	font-size: 22rpx;
	color: $text-primary;
	padding: 10rpx 12rpx 0;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.addon-price {
	font-size: 26rpx;
	color: $red;
	font-weight: 700;
	padding: 4rpx 12rpx 14rpx;
}

/* 底部结算栏 */
.bottom-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	height: 110rpx;
	background: #fff;
	display: flex;
	align-items: center;
	padding: 0 24rpx;
	box-shadow: 0 -4rpx 16rpx rgba(91, 140, 90, 0.08);
	z-index: 100;
}

.bottom-left {
	display: flex;
	align-items: center;
}

.bottom-check {
	width: 40rpx;
	height: 40rpx;
}

.bottom-all-text {
	font-size: 26rpx;
	color: $text-secondary;
	margin-left: 12rpx;
}

.bottom-center {
	flex: 1;
	display: flex;
	align-items: baseline;
	justify-content: flex-end;
	margin-right: 20rpx;
}

.total-label {
	font-size: 26rpx;
	color: $text-secondary;
}

.total-price {
	font-size: 36rpx;
	color: $red;
	font-weight: 700;
	margin-left: 4rpx;
}

.bottom-right {
	display: flex;
	align-items: center;
}

.edit-btn {
	height: 68rpx;
	padding: 0 24rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 26rpx;
	color: $text-secondary;
	border: 2rpx solid #ddd;
	border-radius: 34rpx;
	margin-right: 12rpx;
}

.delete-btn {
	height: 68rpx;
	padding: 0 32rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 26rpx;
	color: #fff;
	background: $red;
	border-radius: 34rpx;
}

.checkout-btn {
	height: 68rpx;
	padding: 0 36rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 28rpx;
	color: #fff;
	background: linear-gradient(135deg, $green, $green-dark);
	border-radius: 34rpx;
	font-weight: 600;
}
</style>
