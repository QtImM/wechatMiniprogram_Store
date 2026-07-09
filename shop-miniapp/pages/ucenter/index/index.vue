<template>
	<view class="page">
		<!-- 顶部用户区（绿色渐变背景） -->
		<view class="header">
			<view class="user-section" @tap="onUserTap">
				<image class="avatar" :src="avatarUrl" mode="aspectFill"></image>
				<view class="user-info">
					<text class="nickname">{{ nicknameText }}</text>
					<text class="user-desc" v-if="!isLogin">点击登录，享受更多权益</text>
					<text class="user-desc" v-else>{{ userInfo.mobile || '欢迎回来' }}</text>
				</view>
				<view class="vip-card" @tap.stop="goMember">
					<text class="vip-title">会员中心</text>
					<text class="vip-sub">专享折扣/优先发货</text>
				</view>
			</view>
		</view>

		<!-- 数据统计行 -->
		<view class="stats-row">
			<navigator url="/pages/ucenter/coupon/coupon" class="stat-item">
				<text class="stat-num">{{ couponCount }}</text>
				<text class="stat-label">优惠券</text>
			</navigator>
			<navigator url="/pages/ucenter/collect/collect" class="stat-item">
				<text class="stat-num">{{ collectCount }}</text>
				<text class="stat-label">收藏</text>
			</navigator>
			<navigator url="/pages/ucenter/footprint/footprint" class="stat-item">
				<text class="stat-num">{{ footprintCount }}</text>
				<text class="stat-label">足迹</text>
			</navigator>
			<view class="stat-item" @tap="goMember">
				<text class="stat-num stat-vip">VIP</text>
				<text class="stat-label">会员</text>
			</view>
		</view>

		<!-- 我的订单 -->
		<view class="section-card">
			<view class="section-header">
				<text class="section-title">我的订单</text>
				<navigator url="/pages/ucenter/order/order" class="section-more">
					<text>全部订单</text>
					<text class="arrow">›</text>
				</navigator>
			</view>
			<view class="order-tabs">
				<navigator :url="'/pages/ucenter/order/order?status=1'" class="order-tab">
					<image class="order-icon-img" src="/static/images/service/order_pay.svg"></image>
					<text class="order-label">待付款</text>
				</navigator>
				<navigator :url="'/pages/ucenter/order/order?status=2'" class="order-tab">
					<image class="order-icon-img" src="/static/images/service/order_shipping.svg"></image>
					<text class="order-label">待发货</text>
				</navigator>
				<navigator :url="'/pages/ucenter/order/order?status=3'" class="order-tab">
					<image class="order-icon-img" src="/static/images/service/order_delivery.svg"></image>
					<text class="order-label">待收货</text>
				</navigator>
				<navigator :url="'/pages/ucenter/order/order?status=4'" class="order-tab">
					<image class="order-icon-img" src="/static/images/service/order_rate.svg"></image>
					<text class="order-label">待评价</text>
				</navigator>
				<navigator :url="'/pages/ucenter/order/order?status=5'" class="order-tab">
					<image class="order-icon-img" src="/static/images/service/order_refund.svg"></image>
					<text class="order-label">退款/售后</text>
				</navigator>
			</view>
		</view>

		<!-- 我的服务 -->
		<view class="section-card">
			<view class="section-header">
				<text class="section-title">我的服务</text>
			</view>
			<view class="service-grid">
				<navigator url="/pages/ucenter/address/address" class="service-item">
					<view class="service-icon" style="background-color: #E8F2E7;">
						<image class="service-icon-img" src="/static/images/service/service_address.svg"></image>
					</view>
					<text class="service-label">地址管理</text>
				</navigator>
				<view class="service-item" @tap="goMember">
					<view class="service-icon" style="background-color: #FBF4E4;">
						<image class="service-icon-img" src="/static/images/service/service_vip.svg"></image>
					</view>
					<text class="service-label">会员中心</text>
				</view>
				<view class="service-item" @tap="goDistribution">
					<view class="service-icon" style="background-color: #EAF0F9;">
						<image class="service-icon-img" src="/static/images/service/service_distribution.svg"></image>
					</view>
					<text class="service-label">分销中心</text>
				</view>
				<button class="service-item service-btn" open-type="contact">
					<view class="service-icon" style="background-color: #E8F2E7;">
						<image class="service-icon-img" src="/static/images/service/service_kefu.svg"></image>
					</view>
					<text class="service-label">在线客服</text>
				</button>
				<navigator url="/pages/ucenter/coupon/coupon" class="service-item">
					<view class="service-icon" style="background-color: #FEF0E5;">
						<image class="service-icon-img" src="/static/images/service/service_coupon.svg"></image>
					</view>
					<text class="service-label">优惠券</text>
				</navigator>
				<view class="service-item" @tap="goWallet">
					<view class="service-icon" style="background-color: #F3EFF8;">
						<image class="service-icon-img" src="/static/images/service/service_wallet.svg"></image>
					</view>
					<text class="service-label">余额钱包</text>
				</view>
				<navigator url="/pages/ucenter/feedback/feedback" class="service-item">
					<view class="service-icon" style="background-color: #E8F2E7;">
						<image class="service-icon-img" src="/static/images/service/service_feedback.svg"></image>
					</view>
					<text class="service-label">意见反馈</text>
				</navigator>
				<navigator url="/pages/ucenter/help/help" class="service-item">
					<view class="service-icon" style="background-color: #F6F7F4;">
						<image class="service-icon-img" src="/static/images/service/service_help.svg"></image>
					</view>
					<text class="service-label">帮助中心</text>
				</navigator>
			</view>
		</view>

		<!-- 底部 -->
		<view class="footer">
			<text class="footer-text">药食同源 · 让健康触手可及</text>
		</view>

		<!-- 退出登录 -->
		<view class="logout-btn" v-if="isLogin" @tap="exitLogin">
			<text>退出登录</text>
		</view>
	</view>
</template>

<script>
const util = require('@/utils/util.js');
const api = require('@/utils/api.js');
const app = getApp();

const DEFAULT_AVATAR = 'https://platform-wxmall.oss-cn-beijing.aliyuncs.com/upload/20180727/150547696d798c.png';

export default {
	data() {
		return {
			userInfo: {},
			couponCount: 0,
			collectCount: 0,
			footprintCount: 0
		};
	},
	computed: {
		isLogin() {
			return !!(this.userInfo && this.userInfo.nickname && this.userInfo.nickname !== 'Hi, 游客');
		},
		nicknameText() {
			return this.userInfo.nickname || '点击登录';
		},
		avatarUrl() {
			return this.userInfo.avatar || DEFAULT_AVATAR;
		}
	},
	methods: {
		onUserTap() {
			if (!this.isLogin) {
				uni.navigateTo({ url: '/pages/auth/btnAuth/btnAuth' });
			}
		},
		goMember() {
			uni.showToast({ title: '会员中心开发中', icon: 'none' });
		},
		goDistribution() {
			uni.showToast({ title: '分销中心开发中', icon: 'none' });
		},
		goWallet() {
			uni.showToast({ title: '余额钱包开发中', icon: 'none' });
		},
		exitLogin() {
			uni.showModal({
				title: '提示',
				content: '确定退出登录？',
				confirmColor: '#5B8C5A',
				success: (res) => {
					if (res.confirm) {
						uni.removeStorageSync('token');
						uni.removeStorageSync('userInfo');
						app.globalData.userInfo = {};
						this.userInfo = {};
						util.toast('已退出登录');
					}
				}
			});
		}
	},
	onShow() {
		let userInfo = uni.getStorageSync('userInfo');
		let token = uni.getStorageSync('token');
		if (userInfo && token) {
			this.userInfo = userInfo;
			app.globalData.userInfo = userInfo;
			app.globalData.token = token;
		} else {
			this.userInfo = {};
		}
	}
};
</script>

<style lang="scss">
$green: #5B8C5A;
$green-light: #E8F2E7;
$green-bg: #F6F7F4;
$green-dark: #3D6B3C;
$gold: #B8860B;
$gold-light: #FBF4E4;
$text-primary: #2D3A2E;
$text-secondary: #5C6B5D;
$text-hint: #9CA89D;
$red: #CF4A3E;

page {
	background: $green-bg;
}

.page {
	min-height: 100vh;
	padding-bottom: env(safe-area-inset-bottom);
}

/* 顶部区域 */
.header {
	background: linear-gradient(160deg, $green 0%, #4A7D49 60%, #3D6B3C 100%);
	padding: 80rpx 30rpx 60rpx;
	border-radius: 0 0 40rpx 40rpx;
}

.user-section {
	display: flex;
	align-items: center;
}

.avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 50%;
	border: 4rpx solid rgba(255, 255, 255, 0.4);
	flex-shrink: 0;
}

.user-info {
	flex: 1;
	margin-left: 24rpx;
	overflow: hidden;
}

.nickname {
	font-size: 34rpx;
	font-weight: 700;
	color: #fff;
	display: block;
}

.user-desc {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.75);
	margin-top: 6rpx;
	display: block;
}

.vip-card {
	flex-shrink: 0;
	background: linear-gradient(135deg, #D4A843, $gold);
	border-radius: 16rpx;
	padding: 16rpx 20rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.vip-title {
	font-size: 22rpx;
	color: #fff;
	font-weight: 700;
}

.vip-sub {
	font-size: 18rpx;
	color: rgba(255, 255, 255, 0.8);
	margin-top: 4rpx;
}

/* 数据统计行 */
.stats-row {
	display: flex;
	background: #fff;
	margin: -30rpx 24rpx 0;
	border-radius: 20rpx;
	padding: 28rpx 0;
	position: relative;
	z-index: 10;
	box-shadow: 0 8rpx 24rpx rgba(91, 140, 90, 0.08);
}

.stat-item {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	text-decoration: none;
}

.stat-num {
	font-size: 34rpx;
	font-weight: 700;
	color: $text-primary;
}

.stat-vip {
	color: $gold;
}

.stat-label {
	font-size: 22rpx;
	color: $text-hint;
	margin-top: 6rpx;
}

/* 区块卡片 */
.section-card {
	background: #fff;
	margin: 20rpx 24rpx 0;
	border-radius: 20rpx;
	padding: 28rpx 28rpx 8rpx;
}

.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 24rpx;
}

.section-title {
	font-size: 30rpx;
	font-weight: 700;
	color: $text-primary;
}

.section-more {
	display: flex;
	align-items: center;
	font-size: 24rpx;
	color: $text-hint;
	text-decoration: none;
}

.arrow {
	font-size: 32rpx;
	margin-left: 4rpx;
}

/* 订单Tab */
.order-tabs {
	display: flex;
	padding-bottom: 20rpx;
}

.order-tab {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	text-decoration: none;
}

.order-icon-img {
	width: 48rpx;
	height: 48rpx;
	margin-bottom: 10rpx;
}

.order-label {
	font-size: 22rpx;
	color: $text-secondary;
}

/* 服务网格 */
.service-grid {
	display: flex;
	flex-wrap: wrap;
}

.service-item {
	width: 25%;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 20rpx 0;
	text-decoration: none;
}

.service-btn {
	margin: 0;
	padding: 20rpx 0;
	border: 0;
	background: transparent;
	border-radius: 0;
	line-height: normal;
	font-size: inherit;

	&::after {
		border: none;
	}
}

.service-icon {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 12rpx;
}

.service-icon-img {
	width: 42rpx;
	height: 42rpx;
}

.service-label {
	font-size: 22rpx;
	color: $text-primary;
}

/* 底部 */
.footer {
	padding: 48rpx 0 20rpx;
	text-align: center;
}

.footer-text {
	font-size: 22rpx;
	color: $text-hint;
}

/* 退出登录 */
.logout-btn {
	margin: 20rpx 24rpx 40rpx;
	height: 88rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	background: #fff;
	border-radius: 20rpx;
	font-size: 28rpx;
	color: $red;
}
</style>
