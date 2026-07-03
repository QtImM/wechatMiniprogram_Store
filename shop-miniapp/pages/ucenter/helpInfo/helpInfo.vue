<template>
	<view class="container">
		<view class="help-card" v-for="(item, index) in helpList" :key="index">
			<view class="help-question">{{item.question||''}}</view>
			<view class="help-answer">{{item.answer||''}}</view>
		</view>
		<view class="empty-view" v-if="helpList.length === 0">
			<text class="empty-text">暂无相关问题</text>
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
			getHelpList(id) {
				let that = this;
				util.request(api.HelpIssueList, { typeId: id }).then(function(res) {
					if (res.code === 0) {
						that.helpList = res.data.list || res.data || [];
					}
				});
			}
		},
		onLoad: function(options) {
			this.getHelpList(options.id);
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

	.help-card {
		background: #fff;
		border-radius: 16rpx;
		padding: 28rpx 30rpx;
		margin-bottom: 16rpx;
		box-shadow: 0 2rpx 10rpx rgba(91,140,90,0.08);
	}

	.help-question {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 16rpx;
	}

	.help-answer {
		font-size: 26rpx;
		color: #666;
		line-height: 1.7;
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
