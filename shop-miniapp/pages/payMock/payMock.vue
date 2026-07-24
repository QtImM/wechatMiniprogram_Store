<template>
	<view class="overlay" @tap="onCancel">
		<!-- 底部支付面板，阻止冒泡 -->
		<view class="pay-sheet" @tap.stop>
			<!-- 顶部把手 -->
			<view class="sheet-handle"></view>

			<!-- 商户信息 -->
			<view class="merchant-row">
				<view class="merchant-logo">
					<text class="logo-text">药</text>
				</view>
				<view class="merchant-info">
					<text class="merchant-name">药食同源商城</text>
					<text class="merchant-desc">健康食品官方旗舰店</text>
				</view>
				<image class="wechat-pay-logo" src="/static/images/wechat-pay.png" mode="aspectFit"></image>
			</view>

			<!-- 金额 -->
			<view class="amount-row">
				<text class="amount-label">付款金额</text>
				<view class="amount-value">
					<text class="amount-unit">¥</text>
					<text class="amount-num">{{amount}}</text>
				</view>
			</view>

			<!-- 支付方式 -->
			<view class="pay-method">
				<view class="method-left">
					<view class="wechat-icon">
						<text class="wechat-icon-text">💚</text>
					</view>
					<view class="method-info">
						<text class="method-name">微信零钱</text>
						<text class="method-balance">余额 ¥1,288.00</text>
					</view>
				</view>
				<view class="method-check">
					<view class="check-dot"></view>
				</view>
			</view>

			<!-- 安全提示 -->
			<view class="security-tip">
				<text class="lock-icon">🔒</text>
				<text class="tip-text">支付安全由微信保障</text>
			</view>

			<!-- 按钮区 -->
			<view class="btn-area">
				<view class="btn-cancel" @tap="onCancel">取消</view>
				<view class="btn-pay" @tap="openPwdInput" :class="{paying: isPaying}">
					<text>确认支付</text>
				</view>
			</view>

			<!-- 底部安全距离 -->
			<view class="safe-bottom"></view>
		</view>

		<!-- 高仿微信支付密码弹窗 -->
		<view class="pwd-mask" v-if="showPasswordPanel" @tap.stop>
			<view class="pwd-panel">
				<view class="pwd-header">
					<text class="pwd-close" @tap="closePwdPanel">×</text>
					<text class="pwd-title">请输入支付密码</text>
					<view class="pwd-empty-spacer"></view>
				</view>
				<view class="pwd-merchant">药食同源健康商城</view>
				<view class="pwd-amount">¥{{amount}}</view>
				<view class="pwd-grid">
					<view class="pwd-dot-cell" v-for="i in 6" :key="i">
						<view class="pwd-dot" v-if="password.length >= i"></view>
					</view>
				</view>
			</view>
			<!-- 自定义安全键盘 -->
			<view class="safe-keyboard">
				<view class="keyboard-row" v-for="(row, rIdx) in keyboardRows" :key="rIdx">
					<view 
						class="key-btn" 
						:class="{special: key === '' || key === 'delete'}"
						v-for="(key, kIdx) in row" 
						:key="kIdx"
						@tap="onKeyPress(key)"
					>
						<text v-if="key !== 'delete'">{{key}}</text>
						<text v-else class="delete-icon">⌫</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 支付成功微信绿勾转圈特效 -->
		<view class="success-mask" v-if="showSuccessAnimation" @tap.stop>
			<view class="success-content">
				<view class="wechat-circle">
					<view class="wechat-check-icon"></view>
				</view>
				<text class="success-text">支付成功</text>
			</view>
		</view>
	</view>
</template>

<script>
const util = require("@/utils/util.js");

export default {
	data() {
		return {
			amount: '0.00',
			orderId: 0,
			isPaying: false,
			showPasswordPanel: false,
			showSuccessAnimation: false,
			password: '',
			keyboardRows: [
				[1, 2, 3],
				[4, 5, 6],
				[7, 8, 9],
				['', 0, 'delete']
			]
		}
	},
	methods: {
		openPwdInput() {
			this.password = '';
			this.showPasswordPanel = true;
		},
		closePwdPanel() {
			this.showPasswordPanel = false;
		},
		onKeyPress(key) {
			if (key === '') return;
			if (key === 'delete') {
				if (this.password.length > 0) {
					this.password = this.password.slice(0, -1);
				}
				return;
			}
			if (this.password.length < 6) {
				this.password += key;
				if (this.password.length === 6) {
					this.triggerPaySuccess();
				}
			}
		},
		triggerPaySuccess() {
			this.showPasswordPanel = false;
			this.showSuccessAnimation = true;

			// 模拟支付完成回调
			setTimeout(() => {
				this.showSuccessAnimation = false;
				const app = getApp();
				util.request('pay/mock-success', { orderId: this.orderId }, 'POST', 'application/json', false, true).then(() => {
					if (app.globalData._payResolve) {
						app.globalData._payResolve({ errMsg: 'requestPayment:ok' });
						app.globalData._payResolve = null;
						app.globalData._payReject = null;
					}
					uni.navigateBack();
				}).catch(() => {
					if (app.globalData._payReject) {
						app.globalData._payReject({ errMsg: 'requestPayment:fail' });
						app.globalData._payResolve = null;
						app.globalData._payReject = null;
					}
					uni.navigateBack();
				});
			}, 1500);
		},
		onCancel() {
			const app = getApp();
			if (app.globalData._payReject) {
				app.globalData._payReject({ errMsg: 'requestPayment:cancel' });
				app.globalData._payResolve = null;
				app.globalData._payReject = null;
			}
			uni.navigateBack();
		}
	},
	onLoad(options) {
		this.orderId = options.orderId || 0;
		this.amount = options.amount || '0.00';

		// 页面进入动画
		uni.setNavigationBarColor &&
			uni.setNavigationBarColor({ frontColor: '#FEFEFC', backgroundColor: 'transparent' });
	},
	onBackPress() {
		this.onCancel();
		return true;
	}
}
</script>

<style lang="scss">
page {
	background: transparent;
}

.overlay {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	flex-direction: column;
	justify-content: flex-end;
}

.pay-sheet {
	background: #f5f5f5;
	border-radius: 24rpx 24rpx 0 0;
	padding: 0 0 0;
	animation: slideUp 0.3s ease;
}

@keyframes slideUp {
	from { transform: translateY(100%); }
	to { transform: translateY(0); }
}

/* 把手 */
.sheet-handle {
	width: 64rpx;
	height: 8rpx;
	background: #ddd;
	border-radius: 4rpx;
	margin: 20rpx auto 24rpx;
}

/* 商户信息 */
.merchant-row {
	display: flex;
	align-items: center;
	padding: 24rpx 40rpx 32rpx;
	background: #FEFEFC;
	border-bottom: 1rpx solid #f0f0f0;
}

.merchant-logo {
	width: 80rpx;
	height: 80rpx;
	border-radius: 16rpx;
	background: linear-gradient(135deg, #5B8C5A, #3D6B3C);
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
}

.logo-text {
	font-size: 36rpx;
	color: #FEFEFC;
	font-weight: 700;
}

.merchant-info {
	flex: 1;
	margin-left: 20rpx;
}

.merchant-name {
	font-size: 28rpx;
	color: #333;
	font-weight: 600;
	display: block;
}

.merchant-desc {
	font-size: 22rpx;
	color: #999;
	margin-top: 4rpx;
	display: block;
}

.wechat-pay-logo {
	width: 100rpx;
	height: 36rpx;
	opacity: 0.8;
}

/* 金额 */
.amount-row {
	background: #FEFEFC;
	padding: 48rpx 40rpx 40rpx;
	text-align: center;
	border-bottom: 1rpx solid #f0f0f0;
}

.amount-label {
	font-size: 26rpx;
	color: #999;
	display: block;
	margin-bottom: 16rpx;
}

.amount-value {
	display: flex;
	align-items: flex-start;
	justify-content: center;
}

.amount-unit {
	font-size: 36rpx;
	color: #1a1a1a;
	font-weight: 500;
	margin-top: 12rpx;
	margin-right: 4rpx;
}

.amount-num {
	font-size: 80rpx;
	color: #1a1a1a;
	font-weight: 600;
	line-height: 1;
}

/* 支付方式 */
.pay-method {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 28rpx 40rpx;
	background: #FEFEFC;
	margin-top: 16rpx;
}

.method-left {
	display: flex;
	align-items: center;
}

.wechat-icon {
	width: 72rpx;
	height: 72rpx;
	background: #07C160;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
}

.wechat-icon-text {
	font-size: 36rpx;
}

.method-info {
	margin-left: 20rpx;
}

.method-name {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
	display: block;
}

.method-balance {
	font-size: 22rpx;
	color: #999;
	margin-top: 4rpx;
	display: block;
}

.method-check {
	width: 40rpx;
	height: 40rpx;
	border-radius: 50%;
	background: #07C160;
	display: flex;
	align-items: center;
	justify-content: center;
}

.check-dot {
	width: 16rpx;
	height: 28rpx;
	border-right: 4rpx solid #FEFEFC;
	border-bottom: 4rpx solid #FEFEFC;
	transform: rotate(45deg) translate(-2rpx, -4rpx);
}

/* 安全提示 */
.security-tip {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 20rpx;
	margin-top: 16rpx;
}

.lock-icon {
	font-size: 22rpx;
	margin-right: 8rpx;
}

.tip-text {
	font-size: 22rpx;
	color: #bbb;
}

/* 按钮区 */
.btn-area {
	display: flex;
	padding: 24rpx 40rpx 0;
}

.btn-cancel {
	flex: 1;
	height: 88rpx;
	line-height: 88rpx;
	text-align: center;
	background: #FEFEFC;
	border-radius: 44rpx;
	font-size: 30rpx;
	color: #666;
	margin-right: 20rpx;
	border: 1rpx solid #e0e0e0;
}

.btn-pay {
	flex: 2;
	height: 88rpx;
	line-height: 88rpx;
	text-align: center;
	background: #07C160;
	border-radius: 44rpx;
	font-size: 32rpx;
	color: #FEFEFC;
	font-weight: 600;

	&.paying {
		opacity: 0.7;
	}
}

.safe-bottom {
	height: env(safe-area-inset-bottom, 0px);
	padding-bottom: 30rpx;
}

/* 密码弹窗遮罩 */
.pwd-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.4);
	z-index: 1000;
	display: flex;
	flex-direction: column;
	justify-content: flex-end;
}

.pwd-panel {
	background: #FEFEFC;
	border-radius: 24rpx 24rpx 0 0;
	padding: 30rpx 40rpx 48rpx;
	animation: keyboardSlide 0.25s ease-out;
}

.pwd-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 40rpx;
}

.pwd-close {
	font-size: 48rpx;
	color: #999;
	line-height: 1;
	width: 48rpx;
}

.pwd-title {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
}

.pwd-empty-spacer {
	width: 48rpx;
}

.pwd-merchant {
	font-size: 28rpx;
	color: #666;
	text-align: center;
	margin-top: 10rpx;
}

.pwd-amount {
	font-size: 64rpx;
	font-weight: 700;
	color: #111;
	text-align: center;
	margin: 20rpx 0 40rpx;
}

.pwd-grid {
	display: flex;
	border: 1rpx solid #ddd;
	border-radius: 12rpx;
	overflow: hidden;
	height: 100rpx;
	background: #FEFEFC;
}

.pwd-dot-cell {
	flex: 1;
	border-right: 1rpx solid #eee;
	display: flex;
	align-items: center;
	justify-content: center;

	&:last-child {
		border-right: none;
	}
}

.pwd-dot {
	width: 20rpx;
	height: 20rpx;
	background: #000;
	border-radius: 50%;
}

/* 微信安全键盘 */
.safe-keyboard {
	background: #f7f7f7;
	padding-bottom: env(safe-area-inset-bottom, 0px);
	animation: keyboardSlide 0.25s ease-out;
}

@keyframes keyboardSlide {
	from { transform: translateY(100%); }
	to { transform: translateY(0); }
}

.keyboard-row {
	display: flex;
	border-top: 1rpx solid #e5e5e5;
}

.key-btn {
	flex: 1;
	height: 108rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 38rpx;
	font-weight: 500;
	color: #000;
	background: #FEFEFC;
	border-right: 1rpx solid #e5e5e5;

	&:last-child {
		border-right: none;
	}

	&:active {
		background: #e1e1e1;
	}

	&.special {
		background: #f7f7f7;

		&:active {
			background: #e1e1e1;
		}
	}
}

.delete-icon {
	font-size: 34rpx;
	color: #444;
}

/* 支付成功绿勾遮罩 */
.success-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: #FEFEFC;
	z-index: 1100;
	display: flex;
	align-items: center;
	justify-content: center;
}

.success-content {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.wechat-circle {
	width: 140rpx;
	height: 140rpx;
	border-radius: 50%;
	background: #07C160;
	display: flex;
	align-items: center;
	justify-content: center;
	animation: popScale 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes popScale {
	from { transform: scale(0.6); opacity: 0; }
	to { transform: scale(1); opacity: 1; }
}

.wechat-check-icon {
	width: 36rpx;
	height: 60rpx;
	border-right: 6rpx solid #FEFEFC;
	border-bottom: 6rpx solid #FEFEFC;
	transform: rotate(40deg) translate(-4rpx, -8rpx);
}

.success-text {
	font-size: 34rpx;
	font-weight: 600;
	color: #07C160;
	margin-top: 36rpx;
}
</style>
