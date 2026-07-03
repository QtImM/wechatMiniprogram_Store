<template>
	<view class="container">
		<view class="help-header">常见问题</view>
		<view class="help-list">
			<navigator :url="'../helpInfo/helpInfo?id='+item.id" class="help-item"
			 v-for="(item,index) in helpList" :key="index">
				<text class="help-name">{{item.typeName || item.name || ''}}</text>
				<text class="help-arrow">›</text>
			</navigator>
		</view>
	</view>
</template>

<script>
	const util = require("@/utils/util.js");
	const api = require('@/utils/api.js');
	export default {
		data() {
			return {
				helpList: []
			}
		},
		methods: {
			getHelpList() {
				let that = this;
				util.request(api.HelpTypeList, {}).then(function(res) {
					if (res.code === 0) {
						that.helpList = res.data.list || res.data || [];
					}
				});
			}
		},
		onLoad: function() {
			this.getHelpList();
		}
	}
</script>

<style lang="scss">
	$green: #5B8C5A;
	$green-bg: #F6F7F4;

	page {
		background: $green-bg;
		min-height: 100%;
	}

	.container {
		padding: 24rpx;
	}

	.help-header {
		font-size: 34rpx;
		font-weight: bold;
		color: $green;
		padding: 20rpx 8rpx 30rpx;
	}

	.help-list {
		background: #fff;
		border-radius: 16rpx;
		overflow: hidden;
		box-shadow: 0 2rpx 10rpx rgba(91,140,90,0.08);
	}

	.help-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 30rpx;
		border-bottom: 1rpx solid #f5f5f5;

		&:last-child {
			border-bottom: none;
		}
	}

	.help-name {
		font-size: 28rpx;
		color: #333;
	}

	.help-arrow {
		font-size: 32rpx;
		color: #ccc;
	}
</style>
