<template>
	<view class="container">
		<view class="address-list" v-if="addressList.length > 0">
			<view class="address-card" v-for="(item, index) in addressList" :key="item.id" @tap="addressAddOrUpdate(item.id)">
				<view class="card-header">
					<view class="user-name">{{item.userName||''}}</view>
					<view class="default-tag" v-if="item.isDefault">默认</view>
					<view class="user-tel">{{item.telNumber}}</view>
				</view>
				<view class="card-address">{{item.fullRegion + item.detailInfo}}</view>
				<view class="card-actions">
					<view class="action-btn edit-btn" @tap.stop="addressAddOrUpdate(item.id)">
						<text>编辑</text>
					</view>
					<view class="action-btn del-btn" @tap.stop="deleteAddress(item.id)">
						<text>删除</text>
					</view>
				</view>
			</view>
		</view>
		<view class="empty-view" v-else>
			<view class="empty-icon">📭</view>
			<text class="empty-text">还没有收货地址哦</text>
		</view>
		<view class="add-btn" @tap="addressAddOrUpdate(0)">
			<text class="add-btn-text">+ 新建收货地址</text>
		</view>
	</view>
</template>

<script>
	const util = require("@/utils/util.js");
	const api = require('@/utils/api.js');
	export default {
		data() {
			return {
				addressList: []
			}
		},
		methods: {
			getAddressList() {
				let that = this;
				util.request(api.AddressList).then(function(res) {
					if (res.code === 0) {
						that.addressList = res.data || [];
					}
				});
			},
			deleteAddress(addressId) {
				let that = this;
				uni.showModal({
					title: '提示',
					content: '确定要删除这个地址吗？',
					confirmColor: '#5B8C5A',
					success: function(res) {
						if (res.confirm) {
							util.request(api.AddressDelete, {
								id: addressId
							}, 'POST', 'application/json').then(function(res) {
								if (res.code === 0) {
									that.getAddressList();
								}
							});
						}
					}
				})
			},
			addressAddOrUpdate(id) {
				uni.navigateTo({
					url: '/pages/ucenter/addressAdd/addressAdd?id=' + id
				});
			}
		},
		onShow: function() {
			this.getAddressList();
		}
	}
</script>

<style lang="scss">
	$green: #5B8C5A;
	$green-light: #7BAF7A;
	$green-bg: #F6F7F4;
	$red: #CF4A3E;

	page {
		height: 100%;
		background: $green-bg;
	}

	.container {
		padding: 24rpx;
		padding-bottom: 140rpx;
	}

	.address-list {
		width: 100%;
	}

	.address-card {
		background: #fff;
		border-radius: 16rpx;
		padding: 28rpx 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 12rpx rgba(91,140,90,0.08);
	}

	.card-header {
		display: flex;
		align-items: center;
		margin-bottom: 14rpx;
	}

	.user-name {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		margin-right: 16rpx;
	}

	.default-tag {
		font-size: 20rpx;
		color: $green;
		border: 1rpx solid $green;
		border-radius: 6rpx;
		padding: 2rpx 10rpx;
		margin-right: 16rpx;
	}

	.user-tel {
		font-size: 26rpx;
		color: #666;
	}

	.card-address {
		font-size: 26rpx;
		color: #666;
		line-height: 1.5;
		margin-bottom: 20rpx;
	}

	.card-actions {
		display: flex;
		justify-content: flex-end;
		border-top: 1rpx solid #f0f0f0;
		padding-top: 18rpx;
	}

	.action-btn {
		font-size: 24rpx;
		padding: 8rpx 24rpx;
		border-radius: 20rpx;
		margin-left: 16rpx;
	}

	.edit-btn {
		color: $green;
		border: 1rpx solid $green;
	}

	.del-btn {
		color: #999;
		border: 1rpx solid #ddd;
	}

	.empty-view {
		padding-top: 200rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.empty-icon {
		font-size: 80rpx;
		margin-bottom: 20rpx;
	}

	.empty-text {
		font-size: 28rpx;
		color: #999;
	}

	.add-btn {
		position: fixed;
		bottom: 30rpx;
		left: 24rpx;
		right: 24rpx;
		height: 90rpx;
		background: linear-gradient(135deg, $green 0%, $green-light 100%);
		border-radius: 45rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 6rpx 24rpx rgba(91,140,90,0.3);
	}

	.add-btn-text {
		font-size: 30rpx;
		color: #fff;
		font-weight: 500;
	}
</style>
