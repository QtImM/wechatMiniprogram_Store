<template>
	<view class="container" @tap="closeSwipe">
		<view class="footprint" v-if="footprintList.length>0">
			<view class="day-section" v-for="(item, index) in footprintList" :key="index">
				<view class="day-title">{{item[0].addTime}}</view>
				<view class="day-list">
					<view class="swipe-wrap" v-for="(iitem, iindex) in item" :key="iitem.id">
						<view class="footprint-item"
							:class="{ 'swiped': swipeId === iitem.id }"
							:data-footprint="iitem"
							@touchstart="touchStart($event, iitem.id)"
							@touchmove="touchMove($event, iitem.id)"
							@touchend="touchEnd($event, iitem.id)"
							@tap.stop="onItemTap(iitem)">
							<image class="item-img" :src="$imageUrl(iitem.listPicUrl)" mode="aspectFill" @error="$setImageFallback(iitem, 'listPicUrl')"></image>
							<view class="item-info">
								<text class="item-name">{{iitem.name||''}}</text>
								<text class="item-brief">{{iitem.goodsBrief||''}}</text>
								<text class="item-price">￥{{iitem.retailPrice||''}}</text>
							</view>
						</view>
						<view class="swipe-action" @tap.stop="confirmDelete(iitem)">
							<text>删除</text>
						</view>
					</view>
				</view>
			</view>
		</view>
		<show-empty v-else text="暂无浏览记录"></show-empty>
	</view>
</template>

<script>
	const util = require("@/utils/util.js");
	const api = require('@/utils/api.js');
	export default {
		data() {
			return {
				footprintList: [],
				swipeId: null,
				startX: 0,
				startY: 0,
				moveX: 0
			}
		},
		methods: {
			getFootprintList() {
				let that = this;
				util.request(api.FootprintList).then(function(res) {
					if (res.code === 0) {
						that.footprintList = (res.data && res.data.data) ? res.data.data : [];
					}
				});
			},
			onItemTap(iitem) {
				if (this.swipeId === iitem.id) {
					this.swipeId = null;
					return;
				}
				this.swipeId = null;
				uni.navigateTo({ url: '/pages/goods/goods?id=' + iitem.goodsId });
			},
			touchStart(e, id) {
				this.startX = e.touches[0].clientX;
				this.startY = e.touches[0].clientY;
				this.moveX = 0;
			},
			touchMove(e, id) {
				const dx = e.touches[0].clientX - this.startX;
				const dy = e.touches[0].clientY - this.startY;
				if (Math.abs(dx) > Math.abs(dy)) {
					this.moveX = dx;
				}
			},
			touchEnd(e, id) {
				if (this.moveX < -40) {
					this.swipeId = id;
				} else if (this.moveX > 40) {
					this.swipeId = null;
				}
			},
			closeSwipe() {
				this.swipeId = null;
			},
			confirmDelete(iitem) {
				let that = this;
				uni.showModal({
					title: '提示',
					content: '要删除所选足迹？',
					confirmColor: '#5B8C5A',
					success: function(res) {
						if (res.confirm) {
							util.request(api.FootprintDelete, { footprintId: iitem.id }).then(function(res) {
								if (res.code === 0) {
									uni.showToast({ title: '删除成功', icon: 'success' });
									that.swipeId = null;
									that.getFootprintList();
								}
							});
						}
					}
				});
			}
		},
		onPullDownRefresh() {
			this.getFootprintList();
		},
		onShow: function() {
			this.getFootprintList();
		}
	}
</script>

<style lang="scss">
	$green: #5B8C5A;
	$green-bg: #F6F7F4;
	$red: #CF4A3E;

	page {
		background: $green-bg;
		min-height: 100%;
	}

	.container {
		padding: 24rpx;
	}

	.day-section {
		margin-bottom: 24rpx;
	}

	.day-title {
		font-size: 26rpx;
		color: #999;
		margin-bottom: 12rpx;
		padding-left: 8rpx;
	}

	.day-list {
		background: #FEFEFC;
		border-radius: 16rpx;
		overflow: hidden;
		box-shadow: 0 2rpx 10rpx rgba(91,140,90,0.08);
	}

	.swipe-wrap {
		position: relative;
		overflow: hidden;
	}

	.footprint-item {
		display: flex;
		align-items: center;
		padding: 24rpx;
		border-bottom: 1rpx solid #f5f5f5;
		background: #FEFEFC;
		transition: transform 0.25s ease;
		position: relative;
		z-index: 1;

		&:last-child {
			border-bottom: none;
		}

		&.swiped {
			transform: translateX(-160rpx);
		}
	}

	.swipe-action {
		position: absolute;
		right: 0;
		top: 0;
		bottom: 0;
		width: 160rpx;
		background: $red;
		color: #fff;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 28rpx;
	}

	.item-img {
		width: 140rpx;
		height: 140rpx;
		border-radius: 12rpx;
		margin-right: 20rpx;
	}

	.item-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: center;
	}

	.item-name {
		font-size: 28rpx;
		color: #333;
		line-height: 1.4;
		margin-bottom: 8rpx;
	}

	.item-brief {
		font-size: 24rpx;
		color: #999;
		margin-bottom: 12rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.item-price {
		font-size: 30rpx;
		font-weight: bold;
		color: $red;
	}
</style>
