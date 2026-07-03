<template>
	<view class="page">
		<!-- 订单列表 -->
		<view class="order-list" v-if="orderList.length > 0">
			<navigator class="order-card" v-for="(item, index) in orderList" :key="item.id"
				:url="'../orderDetail/orderDetail?id='+item.id">
				<!-- 订单头部 -->
				<view class="order-header">
					<text class="order-sn">订单号：{{item.orderSn||''}}</text>
					<text class="order-status">{{item.orderStatusText||''}}</text>
				</view>

				<!-- 商品信息 -->
				<view class="order-goods" v-for="(gitem, gindex) in item.goodsList" :key="gitem.id">
					<image class="order-goods-img" :src="gitem.listPicUrl" mode="aspectFill"></image>
					<view class="order-goods-info">
						<text class="order-goods-name">{{gitem.goodsName||''}}</text>
						<text class="order-goods-num">共{{gitem.number||0}}件</text>
					</view>
				</view>

				<!-- 订单底部 -->
				<view class="order-footer">
					<text class="order-total">实付：<text class="order-price">¥{{item.actualPrice||'0'}}</text></text>
					<view class="order-actions">
						<view class="pay-btn" :data-order-index="index" @click.stop.prevent="payOrder" v-if="item.handleOption.pay">立即支付</view>
					</view>
				</view>
			</navigator>
		</view>

		<!-- 空状态 -->
		<view class="empty-state" v-else>
			<text class="empty-icon">📋</text>
			<text class="empty-text">暂无订单</text>
			<navigator class="empty-btn" url="/pages/index/index" open-type="switchTab">去逛逛</navigator>
		</view>
	</view>
</template>

<script>
const util = require("@/utils/util.js");
const api = require('@/utils/api.js');

export default {
	data() {
		return {
			orderList: [],
			page: 1,
			size: 10,
			totalPages: 1
		}
	},
	methods: {
		getOrderList() {
			util.request(api.OrderList, {
				page: this.page,
				size: this.size
			}).then(res => {
				if (res.code === 0) {
					this.orderList = this.orderList.concat(res.data.list);
					this.page = res.data.page + 1;
					this.totalPages = res.data.total;
				}
			});
		},
		payOrder(event) {
			let orderIndex = event.currentTarget.dataset.orderIndex;
			let order = this.orderList[orderIndex];
			util.payOrder(parseInt(order.id)).then(() => {
				this.orderList = [];
				this.page = 1;
				this.getOrderList();
			}).catch(() => {
				util.toast('支付失败');
			});
		}
	},
	onReachBottom() {
		this.getOrderList();
	},
	onShow() {
		this.orderList = [];
		this.page = 1;
		this.getOrderList();
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

/* 订单卡片 */
.order-card {
	display: block;
	background: #fff;
	border-radius: 20rpx;
	margin-bottom: 16rpx;
	padding: 24rpx;
	text-decoration: none;
	box-shadow: 0 2rpx 12rpx rgba(91, 140, 90, 0.05);
}

.order-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-bottom: 20rpx;
	border-bottom: 1rpx solid $green-bg;
}

.order-sn {
	font-size: 24rpx;
	color: $text-hint;
}

.order-status {
	font-size: 24rpx;
	color: $green;
	font-weight: 600;
}

/* 商品 */
.order-goods {
	display: flex;
	align-items: center;
	padding: 20rpx 0;
}

.order-goods-img {
	width: 120rpx;
	height: 120rpx;
	border-radius: 12rpx;
	background: $green-bg;
	flex-shrink: 0;
	margin-right: 20rpx;
}

.order-goods-info {
	flex: 1;
	overflow: hidden;
}

.order-goods-name {
	font-size: 26rpx;
	color: $text-primary;
	display: -webkit-box;
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 2;
	overflow: hidden;
	line-height: 1.4;
}

.order-goods-num {
	display: block;
	font-size: 22rpx;
	color: $text-hint;
	margin-top: 8rpx;
}

/* 底部 */
.order-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-top: 20rpx;
	border-top: 1rpx solid $green-bg;
}

.order-total {
	font-size: 26rpx;
	color: $text-secondary;
}

.order-price {
	color: $red;
	font-weight: 700;
	font-size: 30rpx;
}

.order-actions {
	display: flex;
	gap: 12rpx;
}

.pay-btn {
	height: 60rpx;
	line-height: 60rpx;
	padding: 0 28rpx;
	background: linear-gradient(135deg, $green, $green-dark);
	color: #fff;
	border-radius: 30rpx;
	font-size: 24rpx;
	font-weight: 600;
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
	color: $text-hint;
	margin-top: 24rpx;
}

.empty-btn {
	margin-top: 40rpx;
	height: 72rpx;
	line-height: 72rpx;
	padding: 0 48rpx;
	background: $green;
	color: #fff;
	border-radius: 36rpx;
	font-size: 28rpx;
	text-decoration: none;
}
</style>
