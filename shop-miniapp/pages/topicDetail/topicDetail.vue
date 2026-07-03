<template>
	<view class="container">
		<view class="content-area">
			<uParse :content="topic.content" noData="" />
		</view>

		<view class="comment-section">
			<view class="section-header">
				<text class="section-title">精选留言</text>
				<view class="comment-add" @tap="postComment">
					<text>写留言</text>
				</view>
			</view>
			<view class="comment-list" v-if="commentList.length > 0">
				<view class="comment-item" v-for="(item, index) in commentList" :key="item.id">
					<view class="comment-user">
						<image class="user-avatar" :src="item.userInfo.avatar"></image>
						<view class="user-info">
							<text class="user-name">{{item.userInfo.nickname}}</text>
							<text class="comment-time">{{item.addTime}}</text>
						</view>
					</view>
					<view class="comment-content">{{item.content}}</view>
				</view>
				<navigator class="load-more" v-if="commentCount > 5"
				 :url="'/pages/topicComment/topicComment?valueId='+topic.id+'&typeId=1'">
					查看更多留言 ›
				</navigator>
			</view>
			<view class="no-comment" v-else>
				<text class="no-comment-text">暂无留言，快来抢沙发~</text>
			</view>
		</view>

		<view class="recommend-section" v-if="topicList.length > 0">
			<view class="section-header">
				<text class="section-title">专题推荐</text>
			</view>
			<view class="recommend-list">
				<navigator class="recommend-item" v-for="(item, index) in topicList" :key="index"
				 :url="'../topicDetail/topicDetail?id='+item.id">
					<image class="recommend-img" :src="item.scenePicUrl" mode="aspectFill"></image>
					<text class="recommend-title">{{item.title}}</text>
				</navigator>
			</view>
		</view>
	</view>
</template>

<script>
	const util = require("@/utils/util.js")
	const api = require('@/utils/api.js');
	import uParse from '@/components/uParse/src/wxParse'
	export default {
		components: { uParse },
		data() {
			return {
				id: 0,
				topic: {},
				topicList: [],
				commentCount: 0,
				commentList: []
			}
		},
		methods: {
			getCommentList() {
				let that = this;
				util.request(api.CommentList, {
					valueId: that.id,
					typeId: 1,
					size: 5
				}).then(function(res) {
					if (res.code === 0) {
						that.commentList = res.data.records || [];
						that.commentCount = res.data.total || 0;
					}
				});
			},
			postComment() {
				uni.navigateTo({
					url: '/pages/commentPost/commentPost?valueId=' + this.id + '&typeId=1'
				});
			}
		},
		onShow: function() {
			this.getCommentList();
		},
		onLoad: function(options) {
			let that = this;
			that.id = parseInt(options.id);
			util.request(api.TopicDetail, { id: that.id }).then(function(res) {
				if (res.code === 0) {
					that.topic = res.data;
				}
			});
			util.request(api.TopicRelated, { id: that.id }).then(function(res) {
				if (res.code === 0) {
					that.topicList = res.data || [];
				}
			});
		}
	}
</script>

<style lang="scss">
	$green: #5B8C5A;
	$green-light: #7BAF7A;
	$green-bg: #F6F7F4;

	page {
		background: $green-bg;
	}

	.container {
		padding-bottom: 30rpx;
	}

	.content-area {
		width: 100%;
		background: #fff;
		font-size: 0;

		image {
			display: inline-block;
			width: 100%;
		}
	}

	.comment-section {
		margin: 20rpx 24rpx;
		background: #fff;
		border-radius: 16rpx;
		padding: 0 28rpx;
		box-shadow: 0 2rpx 10rpx rgba(91,140,90,0.08);
	}

	.section-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		height: 90rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.section-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
	}

	.comment-add {
		font-size: 24rpx;
		color: $green;
		border: 1rpx solid $green;
		padding: 6rpx 18rpx;
		border-radius: 20rpx;
	}

	.comment-list {
		padding: 10rpx 0;
	}

	.comment-item {
		padding: 24rpx 0;
		border-bottom: 1rpx solid #f5f5f5;

		&:last-child {
			border-bottom: none;
		}
	}

	.comment-user {
		display: flex;
		align-items: center;
		margin-bottom: 16rpx;
	}

	.user-avatar {
		width: 60rpx;
		height: 60rpx;
		border-radius: 50%;
		margin-right: 16rpx;
	}

	.user-info {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.user-name {
		font-size: 26rpx;
		color: #333;
	}

	.comment-time {
		font-size: 22rpx;
		color: #999;
	}

	.comment-content {
		font-size: 28rpx;
		color: #333;
		line-height: 1.6;
	}

	.load-more {
		text-align: center;
		font-size: 26rpx;
		color: $green;
		padding: 24rpx 0;
	}

	.no-comment {
		padding: 50rpx 0;
		text-align: center;
	}

	.no-comment-text {
		font-size: 26rpx;
		color: #999;
	}

	.recommend-section {
		margin: 0 24rpx;
		background: #fff;
		border-radius: 16rpx;
		padding: 0 28rpx 24rpx;
		box-shadow: 0 2rpx 10rpx rgba(91,140,90,0.08);
	}

	.recommend-list {
		padding-top: 16rpx;
	}

	.recommend-item {
		display: block;
		width: 100%;
		margin-bottom: 20rpx;
		border-radius: 12rpx;
		overflow: hidden;
		box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05);
	}

	.recommend-img {
		width: 100%;
		height: 280rpx;
	}

	.recommend-title {
		display: block;
		padding: 16rpx 20rpx;
		font-size: 26rpx;
		color: #333;
		background: #fff;
	}
</style>
