<template>
	<view class="page">
		<view class="bg-circle"></view>
		<view class="bg-circle bg-circle--2"></view>
		<view class="content">
			<view class="brand-area">
				<image class="logo" src="/static/images/logo.png" mode="aspectFit"></image>
				<view class="brand-name">药食同源</view>
				<view class="brand-slogan">传承经典 · 健康生活</view>
			</view>
			<view class="auth-card">
				<view class="auth-title">授权登录</view>
				<view class="auth-desc">获取您的公开信息（昵称、头像等）以提供更好的服务体验</view>
				<button class="login-btn" v-if="canIUseGetUserProfile" @tap="getUserProfile">
					<text class="btn-text">微信一键登录</text>
				</button>
				<button class="login-btn" v-else open-type="getUserInfo" @getuserinfo="bindGetUserInfo">
					<text class="btn-text">微信一键登录</text>
				</button>
				<view class="skip-btn" @tap="skipLogin">暂不登录，先逛逛</view>
			</view>
		</view>
	</view>
</template>

<script>
	const util = require("@/utils/util.js")
	const api = require('@/utils/api.js');
	export default {
		data() {
			return {
				canIUseGetUserProfile: false,
				navUrl: '',
				code: ''
			}
		},
		methods: {
			getUserProfile() {
				let that = this;
				uni.getUserProfile({
					desc: '用于完善会员资料',
					success: (resp) => {
						if (that.code) {
							that.loginByWeixin(resp)
						} else {
							uni.login({
								success: function(resp) {
									if (resp.code) {
										that.code = resp.code
										that.loginByWeixin(resp)
									}
								}
							});
						}
					}
				})
			},
			bindGetUserInfo: function(e) {
				this.loginByWeixin(e.detail)
			},
			loginByWeixin: function(userInfo) {
				let that = this;
				if (that.code) {
					util.request(api.AuthLoginByWeixin, {
						code: that.code,
						userInfo: userInfo
					}, 'POST', 'application/json').then(res => {
						if (res.code === 0) {
							uni.setStorageSync('userInfo', res.data.userInfo);
							uni.setStorageSync('token', res.data.token);
							uni.setStorageSync('userId', res.data.userId);
							that.goBack();
						} else {
							uni.showModal({
								title: '提示',
								content: res.msg,
								showCancel: false
							});
						}
					});
				}
			},
			skipLogin() {
				this.goBack();
			},
			goBack() {
				if (this.navUrl && this.navUrl == '/pages/index/index') {
					uni.switchTab({ url: this.navUrl })
				} else if (this.navUrl) {
					uni.redirectTo({ url: this.navUrl })
				} else {
					uni.switchTab({ url: '/pages/index/index' })
				}
			}
		},
		onLoad: function(options) {
			let that = this;
			if (uni.getStorageSync("navUrl")) {
				that.navUrl = uni.getStorageSync("navUrl")
			} else {
				that.navUrl = '/pages/index/index'
			}
			if (uni.getUserProfile) {
				that.canIUseGetUserProfile = true
			}
			uni.login({
				success: function(res) {
					if (res.code) {
						that.code = res.code
					}
				}
			});
		}
	}
</script>

<style lang="scss">
	$green: #5B8C5A;
	$green-light: #7BAF7A;
	$green-bg: #F6F7F4;
	$gold: #B8860B;

	page {
		height: 100%;
		background: linear-gradient(160deg, $green 0%, $green-light 50%, $green-bg 100%);
	}

	.page {
		height: 100%;
		position: relative;
		overflow: hidden;
	}

	.bg-circle {
		position: absolute;
		width: 600rpx;
		height: 600rpx;
		border-radius: 50%;
		background: rgba(255,255,255,0.06);
		top: -200rpx;
		right: -200rpx;

		&--2 {
			width: 400rpx;
			height: 400rpx;
			top: auto;
			right: auto;
			bottom: -100rpx;
			left: -150rpx;
		}
	}

	.content {
		height: 100%;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 0 60rpx;
		position: relative;
		z-index: 1;
	}

	.brand-area {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-bottom: 80rpx;
	}

	.logo {
		width: 180rpx;
		height: 180rpx;
		border-radius: 50%;
		background: #fff;
		box-shadow: 0 8rpx 40rpx rgba(0,0,0,0.1);
		margin-bottom: 30rpx;
	}

	.brand-name {
		font-size: 48rpx;
		font-weight: bold;
		color: #fff;
		letter-spacing: 4rpx;
	}

	.brand-slogan {
		font-size: 26rpx;
		color: rgba(255,255,255,0.85);
		margin-top: 12rpx;
		letter-spacing: 2rpx;
	}

	.auth-card {
		width: 100%;
		background: #fff;
		border-radius: 24rpx;
		padding: 60rpx 40rpx;
		box-shadow: 0 8rpx 60rpx rgba(91,140,90,0.2);
	}

	.auth-title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		text-align: center;
		margin-bottom: 16rpx;
	}

	.auth-desc {
		font-size: 24rpx;
		color: #999;
		text-align: center;
		line-height: 1.6;
		margin-bottom: 50rpx;
	}

	.login-btn {
		width: 100%;
		height: 88rpx;
		background: linear-gradient(135deg, $green 0%, $green-light 100%);
		border-radius: 44rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		border: none;
		margin: 0;
		padding: 0;

		&::after {
			border: none;
		}
	}

	.btn-text {
		font-size: 32rpx;
		color: #fff;
		font-weight: 500;
		letter-spacing: 2rpx;
	}

	.skip-btn {
		text-align: center;
		font-size: 26rpx;
		color: #999;
		margin-top: 30rpx;
		padding: 10rpx;
	}
</style>
