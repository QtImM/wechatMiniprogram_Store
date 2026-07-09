<template>
	<view class="page">
		<!-- 搜索栏 -->
		<view class="search-bar">
			<navigator url="/pages/search/search" class="search-box">
				<text class="search-icon">🔍</text>
				<text class="search-text">搜索商品, 共{{goodsCount}}款好物</text>
			</navigator>
		</view>

		<!-- 分类主体 -->
		<view class="catalog-body">
			<!-- 左侧一级分类 -->
			<scroll-view class="side-nav" scroll-y :style="{height: scrollHeight + 'px'}">
				<view
					class="nav-item"
					:class="{active: currentId === item.id}"
					v-for="(item, index) in navList"
					:key="index"
					@tap="switchCate(item.id)"
				>
					<view class="nav-indicator" v-if="currentId === item.id"></view>
					<text class="nav-text">{{item.name}}</text>
				</view>
			</scroll-view>

			<!-- 右侧商品列表 -->
			<scroll-view class="main-content" scroll-y :style="{height: scrollHeight + 'px'}"
			 @scrolltolower="loadMore">
				<!-- 当前分类名 -->
				<view class="cate-name-bar">
					<text class="cate-name">{{currentName}}</text>
					<text class="cate-count">{{goodsList.length}}件商品</text>
				</view>

				<!-- 商品网格 -->
				<view class="goods-grid">
					<view class="goods-card" v-for="(item, index) in goodsList" :key="index"
					 @tap="goToGoods(item.id)">
						<image class="goods-img" :src="item.listPicUrl" mode="aspectFill"></image>
						<view class="goods-info">
							<text class="goods-name">{{item.name||''}}</text>
							<view class="goods-bottom">
								<text class="goods-price">¥{{item.retailPrice}}</text>
								<view class="goods-cart-btn" @tap.stop="quickAddToCart(item, $event)">
									<text class="goods-cart-btn-icon">+</text>
								</view>
							</view>
						</view>
					</view>
				</view>

				<view class="load-tip" v-if="goodsList.length > 0">
					<text class="load-text">{{noMore ? '— 已加载全部 —' : '加载中...'}}</text>
				</view>
				<view class="empty-tip" v-if="goodsList.length === 0 && !loading">
					<text>该分类暂无商品</text>
				</view>
			</scroll-view>
		</view>

		<!-- 抛物线飞球插槽 -->
		<view 
			class="cart-ball" 
			v-for="ball in cartBalls" 
			:key="ball.id" 
			:style="ball.style" 
			v-if="ball.show"
		>
			<view class="inner-ball"></view>
		</view>
	</view>
</template>

<script>
const util = require('@/utils/util.js');
const api = require('@/utils/api.js');

export default {
	data() {
		return {
			navList: [],
			currentId: 0,
			currentName: '',
			goodsList: [],
			goodsCount: 0,
			scrollHeight: 600,
			page: 1,
			size: 10,
			totalPages: 1,
			noMore: false,
			loading: false,
			cartBalls: []
		}
	},
	methods: {
		getCatalog() {
			util.request(api.CatalogList).then(res => {
				if (res.code === 0) {
					this.navList = res.data.categoryList;
					if (res.data.currentCategory) {
						this.currentId = res.data.currentCategory.id;
						this.currentName = res.data.currentCategory.name;
					} else if (this.navList.length > 0) {
						this.currentId = this.navList[0].id;
						this.currentName = this.navList[0].name;
					}
					this.loadGoods();
				}
			});
			util.request(api.GoodsCount).then(res => {
				if (res.code === 0) this.goodsCount = res.data.goodsCount;
			});
		},
		switchCate(id) {
			if (this.currentId === id) return;
			this.currentId = id;
			let found = this.navList.find(n => n.id === id);
			this.currentName = found ? found.name : '';
			this.page = 1;
			this.goodsList = [];
			this.noMore = false;
			this.loadGoods();
		},
		loadGoods() {
			if (this.noMore || this.loading) return;
			this.loading = true;
			util.request(api.GoodsList, {
				categoryId: this.currentId,
				page: this.page,
				size: this.size
			}).then(res => {
				if (res.code === 0 && res.data.goodsList) {
					let records = res.data.goodsList.records || [];
					this.goodsList = this.goodsList.concat(records);
					this.totalPages = res.data.goodsList.pages || 1;
					if (this.page >= this.totalPages || records.length < this.size) {
						this.noMore = true;
					}
					this.page++;
				}
				this.loading = false;
			});
		},
		loadMore() {
			this.loadGoods();
		},
		calcHeight() {
			const sysInfo = uni.getSystemInfoSync();
			this.scrollHeight = sysInfo.windowHeight - 100;
		},
		goToGoods(id) {
			uni.navigateTo({ url: '/pages/goods/goods?id=' + id });
		},
		quickAddToCart(goods, e) {
			let that = this;
			let currentProductId = 1;
			util.request(api.CartAdd, { goodsId: goods.id, number: 1, productId: currentProductId }, 'POST', 'application/json').then(res => {
				if (res.code === 0) {
					uni.showToast({
						title: '已加入购物车',
						icon: 'none',
						duration: 1000
					});
					
					let clientX = 100;
					let clientY = 100;
					if (e.touches && e.touches.length > 0) {
						clientX = e.touches[0].clientX;
						clientY = e.touches[0].clientY;
					} else if (e.detail) {
						clientX = e.detail.x || 100;
						clientY = e.detail.y || 100;
					}

					const ballId = Date.now();
					const ball = {
						id: ballId,
						show: true,
						style: `left: ${clientX}px; top: ${clientY}px;`
					};
					this.cartBalls.push(ball);

					this.$nextTick(() => {
						const sysInfo = uni.getSystemInfoSync();
						const targetX = sysInfo.windowWidth * 0.62;
						const targetY = sysInfo.windowHeight - 30;

						const index = this.cartBalls.findIndex(b => b.id === ballId);
						if (index > -1) {
							this.$set(this.cartBalls, index, {
								...ball,
								style: `left: ${targetX}px; top: ${targetY}px;`
							});
						}
					});

					setTimeout(() => {
						const index = this.cartBalls.findIndex(b => b.id === ballId);
						if (index > -1) {
							this.cartBalls[index].show = false;
						}
					}, 600);
				} else {
					uni.showToast({ image: '/static/images/icon_error.png', title: res.msg, mask: true });
				}
			});
		}
	},
	onLoad() {
		this.calcHeight();
		this.getCatalog();
	}
}
</script>

<style lang="scss">
$green: #5B8C5A;
$green-light: #E8F2E7;
$green-bg: #F6F7F4;
$text-primary: #2D3A2E;
$text-secondary: #5C6B5D;
$text-hint: #9CA89D;
$red: #CF4A3E;

page {
	height: 100%;
	background: #fff;
}

.page {
	height: 100%;
	display: flex;
	flex-direction: column;
}

/* 搜索栏 */
.search-bar {
	padding: 12rpx 24rpx;
	background: #fff;
	flex-shrink: 0;
}

.search-box {
	display: flex;
	align-items: center;
	height: 64rpx;
	background: $green-bg;
	border-radius: 32rpx;
	padding: 0 24rpx;
	text-decoration: none;
}

.search-icon {
	font-size: 26rpx;
	margin-right: 10rpx;
}

.search-text {
	font-size: 24rpx;
	color: $text-hint;
}

/* 分类主体 */
.catalog-body {
	flex: 1;
	display: flex;
	overflow: hidden;
}

/* 左侧导航 */
.side-nav {
	width: 176rpx;
	background: $green-bg;
	flex-shrink: 0;
}

.nav-item {
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
	height: 104rpx;
	color: $text-secondary;

	&.active {
		background: #fff;
		color: $green;
		font-weight: 700;
	}
}

.nav-indicator {
	position: absolute;
	left: 0;
	top: 50%;
	transform: translateY(-50%);
	width: 6rpx;
	height: 40rpx;
	background: $green;
	border-radius: 0 4rpx 4rpx 0;
}

.nav-text {
	font-size: 26rpx;
	max-width: 136rpx;
	text-align: center;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

/* 右侧内容 */
.main-content {
	flex: 1;
	background: $green-bg;
}

/* 分类名称条 */
.cate-name-bar {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 20rpx 20rpx 8rpx;
}

.cate-name {
	font-size: 28rpx;
	color: $text-primary;
	font-weight: 700;
}

.cate-count {
	font-size: 22rpx;
	color: $text-hint;
}

/* 商品网格 - 关键: 宽度精确计算 */
.goods-grid {
	padding: 8rpx 10rpx;
	overflow: hidden;
}

.goods-card {
	float: left;
	width: 270rpx;
	background: #fff;
	border-radius: 12rpx;
	overflow: hidden;
	margin-bottom: 12rpx;
	text-decoration: none;
	box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);

	&:nth-child(odd) {
		margin-right: 10rpx;
	}
}

.goods-img {
	width: 270rpx;
	height: 270rpx;
	display: block;
}

.goods-info {
	padding: 12rpx 14rpx 16rpx;
}

.goods-name {
	font-size: 24rpx;
	color: $text-primary;
	display: -webkit-box;
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 2;
	overflow: hidden;
	line-height: 1.4;
	height: 68rpx;
}

.goods-bottom {
	margin-top: 8rpx;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.goods-price {
	font-size: 28rpx;
	color: $red;
	font-weight: 700;
}

/* 加载提示 */
.load-tip {
	padding: 20rpx 0 40rpx;
	text-align: center;
}

.load-text {
	font-size: 22rpx;
	color: $text-hint;
}

.empty-tip {
	padding: 80rpx 0;
	text-align: center;
	font-size: 26rpx;
	color: $text-hint;
}

/* 一键加购绿色小圆钮 */
.goods-cart-btn {
	width: 44rpx;
	height: 44rpx;
	background: $green;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 4rpx 10rpx rgba(91, 140, 90, 0.2);
	transition: transform 0.1s ease;

	&:active {
		transform: scale(0.85);
	}
}

.goods-cart-btn-icon {
	color: #fff;
	font-size: 26rpx;
	font-weight: 700;
	line-height: 1;
}

/* 抛物线飞球样式 */
.cart-ball {
	position: fixed;
	z-index: 9999;
	transition: left 0.6s linear, top 0.6s cubic-bezier(0.3, -0.2, 1, 0.2);
	pointer-events: none;
}

.inner-ball {
	width: 32rpx;
	height: 32rpx;
	border-radius: 50%;
	background: #CF4A3E;
	box-shadow: 0 4rpx 10rpx rgba(207, 74, 62, 0.4);
}
</style>
