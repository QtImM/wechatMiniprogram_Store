<template>
	<view class="page" :style="{paddingTop: statusBarHeight + 'px'}">
		<!-- 自定义顶部导航 -->
		<view class="nav-bar" :style="{top: statusBarHeight + 'px'}">
			<view class="nav-brand">
				<image class="nav-logo" src="/static/images/logo.png" mode="aspectFit"></image>
				<view class="nav-title">
					<text class="brand-name">药食同源</text>
					<text class="brand-slogan">让健康触手可及</text>
				</view>
			</view>
		</view>

		<!-- 搜索栏 -->
		<view class="search-bar">
			<navigator url="/pages/search/search" class="search-box">
				<image class="search-icon" src="/static/images/icon-search.png" mode="aspectFit"></image>
				<text class="search-placeholder">搜索商品、品牌</text>
			</navigator>
		</view>

		<!-- 分类横滑Tab -->
		<scroll-view scroll-x class="category-tabs" :show-scrollbar="false">
			<view
				class="tab-item"
				:class="{active: currentTab === index}"
				v-for="(item, index) in categoryTabs"
				:key="index"
				@tap="switchTab(index)"
			>
				<text>{{item.name}}</text>
				<view class="tab-line" v-if="currentTab === index"></view>
			</view>
		</scroll-view>

		<!-- 轮播Banner -->
		<view class="banner-wrap">
			<swiper class="banner-swiper" indicator-dots circular autoplay :interval="4000" :duration="500"
				indicator-color="rgba(91,140,90,0.3)" indicator-active-color="#5B8C5A">
				<swiper-item v-for="(item, index) in banner" :key="index">
					<navigator v-if="item.link" :url="item.link">
						<image class="banner-img" :src="item.imageUrl" mode="aspectFill"></image>
					</navigator>
					<image v-else class="banner-img" :src="item.imageUrl" mode="aspectFill"></image>
				</swiper-item>
			</swiper>
		</view>

		<!-- 金刚区 -->
		<view class="grid-menu">
			<view class="menu-item" v-for="(item, index) in menuItems" :key="index" @tap="onMenuTap(item)">
				<view class="menu-icon" :style="{backgroundColor: item.bgColor}">
					<text class="menu-icon-text">{{item.icon}}</text>
				</view>
				<text class="menu-label">{{item.name}}</text>
			</view>
		</view>

		<!-- 公告栏 -->
		<view class="notice-bar" v-if="notice">
			<text class="notice-tag">公告</text>
			<swiper class="notice-swiper" vertical autoplay :interval="3000" circular :show-indicator="false">
				<swiper-item v-for="(item, index) in notices" :key="index">
					<text class="notice-text">{{item.text}}</text>
				</swiper-item>
			</swiper>
		</view>

		<!-- 限时特惠 + 热卖双栏 -->
		<view class="dual-section">
			<view class="dual-left" @tap="goToHot">
				<view class="dual-header">
					<text class="dual-title">限时特惠</text>
					<text class="dual-sub">低至5折</text>
				</view>
				<view class="dual-products">
					<view class="dual-product" v-for="(item, index) in hotGoods.slice(0,2)" :key="index">
						<image class="dual-product-img" :src="item.listPicUrl" mode="aspectFill"></image>
						<text class="dual-product-price">¥{{item.retailPrice}}</text>
					</view>
				</view>
			</view>
			<view class="dual-right">
				<view class="dual-card" @tap="goToNew">
					<text class="dual-card-title">新品尝鲜</text>
					<text class="dual-card-sub">每周上新</text>
					<view class="dual-card-img-wrap" v-if="newGoods.length > 0">
						<image class="dual-card-img" :src="newGoods[0].listPicUrl" mode="aspectFill"></image>
					</view>
				</view>
				<view class="dual-card" @tap="goToBrand">
					<text class="dual-card-title">品牌精选</text>
					<text class="dual-card-sub">大牌直供</text>
					<view class="dual-card-img-wrap" v-if="brands.length > 0">
						<image class="dual-card-img" :src="brands[0].newPicUrl" mode="aspectFill"></image>
					</view>
				</view>
			</view>
		</view>

		<!-- 活动Tab区 -->
		<view class="section-tabs">
			<view
				class="section-tab"
				:class="{active: activeSection === index}"
				v-for="(item, index) in sectionTabs"
				:key="index"
				@tap="activeSection = index"
			>
				<text>{{item}}</text>
			</view>
		</view>

		<!-- 商品列表 (双列瀑布流) -->
		<view class="goods-grid">
			<view class="goods-column">
				<view class="goods-card" v-for="(item, index) in leftGoods" :key="index" @tap="goToGoods(item.id)">
					<image class="goods-img" :src="item.listPicUrl" mode="aspectFill"></image>
					<view class="goods-info">
						<text class="goods-name">{{item.name}}</text>
						<view class="goods-price-row">
							<text class="goods-price">¥{{item.retailPrice}}</text>
							<text class="goods-sales" v-if="item.salesCount">已售{{item.salesCount}}</text>
						</view>
					</view>
				</view>
			</view>
			<view class="goods-column">
				<view class="goods-card" v-for="(item, index) in rightGoods" :key="index" @tap="goToGoods(item.id)">
					<image class="goods-img" :src="item.listPicUrl" mode="aspectFill"></image>
					<view class="goods-info">
						<text class="goods-name">{{item.name}}</text>
						<view class="goods-price-row">
							<text class="goods-price">¥{{item.retailPrice}}</text>
							<text class="goods-sales" v-if="item.salesCount">已售{{item.salesCount}}</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 加载更多 -->
		<view class="load-more" v-if="goodsList.length > 0">
			<text class="load-more-text">— 更多好物探索中 —</text>
		</view>
	</view>
</template>

<script>
const api = require('@/utils/api.js');
const util = require('@/utils/util.js');

export default {
	data() {
		return {
			statusBarHeight: 44,
			currentTab: 0,
			activeSection: 0,
			banner: [],
			channel: [],
			brands: [],
			newGoods: [],
			hotGoods: [],
			goodsList: [],
			notice: true,
			notices: [
				{ text: '欢迎光临药食同源商城，新人享专属优惠~' },
				{ text: '会员年卡限时特惠 99元/年，尊享9折' }
			],
			categoryTabs: [
				{ name: '精选', id: 0 },
				{ name: '食品保健', id: 1 },
				{ name: '美妆护肤', id: 2 },
				{ name: '农特产品', id: 3 },
				{ name: '课程研学', id: 4 },
				{ name: '礼盒套装', id: 5 }
			],
			menuItems: [
				{ name: '新人礼', icon: '🎁', bgColor: '#FEF0E5', url: '' },
				{ name: '会员', icon: '👑', bgColor: '#FBF4E4', url: '' },
				{ name: '优惠券', icon: '🎫', bgColor: '#E8F2E7', url: '' },
				{ name: '分销', icon: '🤝', bgColor: '#EAF0F9', url: '' },
				{ name: '全部分类', icon: '📋', bgColor: '#F3EFF8', url: '/pages/catalog/catalog' }
			],
			sectionTabs: ['今日主推', '热销爆款', '新品上架']
		}
	},
	computed: {
		displayGoods() {
			if (this.activeSection === 0) return this.goodsList;
			if (this.activeSection === 1) return this.hotGoods;
			return this.newGoods;
		},
		leftGoods() {
			return this.displayGoods.filter((_, i) => i % 2 === 0);
		},
		rightGoods() {
			return this.displayGoods.filter((_, i) => i % 2 === 1);
		}
	},
	methods: {
		getIndexData() {
			util.request(api.IndexUrlBanner).then(res => {
				if (res.code === 0) this.banner = res.data.banner;
			});
			util.request(api.IndexUrlChannel).then(res => {
				if (res.code === 0) this.channel = res.data.channel;
			});
			util.request(api.IndexUrlBrand).then(res => {
				if (res.code === 0) this.brands = res.data.brandList;
			});
			util.request(api.IndexUrlNewGoods).then(res => {
				if (res.code === 0) this.newGoods = res.data.newGoodsList;
			});
			util.request(api.IndexUrlHotGoods).then(res => {
				if (res.code === 0) this.hotGoods = res.data.hotGoodsList;
			});
			util.request(api.IndexUrlCategory).then(res => {
				if (res.code === 0) {
					let all = [];
					res.data.categoryList.forEach(cat => {
						if (cat.goodsList) all = all.concat(cat.goodsList);
					});
					this.goodsList = all;
				}
			});
		},
		switchTab(index) {
			this.currentTab = index;
		},
		onMenuTap(item) {
			if (item.url) {
				if (item.url.indexOf('/pages/catalog') > -1) {
					uni.switchTab({ url: item.url });
				} else {
					uni.navigateTo({ url: item.url });
				}
			}
		},
		goToGoods(id) {
			uni.navigateTo({ url: '/pages/goods/goods?id=' + id });
		},
		goToHot() {
			uni.navigateTo({ url: '/pages/hotGoods/hotGoods' });
		},
		goToNew() {
			uni.navigateTo({ url: '/pages/newGoods/newGoods' });
		},
		goToBrand() {
			uni.navigateTo({ url: '/pages/brand/brand' });
		}
	},
	onPullDownRefresh() {
		this.getIndexData();
		setTimeout(() => uni.stopPullDownRefresh(), 800);
	},
	onShareAppMessage() {
		return {
			title: '药食同源商城 - 让健康触手可及',
			path: '/pages/index/index'
		}
	},
	onLoad() {
		const sysInfo = uni.getSystemInfoSync();
		this.statusBarHeight = sysInfo.statusBarHeight || 44;

		uni.login({
			success: (resp) => {
				util.request(api.Code + resp.code, {}, 'GET').then(res => {
					if (res.code === 0) {
						uni.setStorageSync('userInfo', res.data.userInfo);
						uni.setStorageSync('token', res.data.token);
						uni.setStorageSync('userId', res.data.userId);
					}
				});
			}
		});
		this.getIndexData();
	}
}
</script>

<style lang="scss">
$green: #5B8C5A;
$green-light: #E8F2E7;
$green-bg: #F6F7F4;
$gold: #B8860B;
$gold-light: #FBF4E4;
$orange: #E07C4F;
$red: #CF4A3E;
$text-primary: #2D3A2E;
$text-secondary: #5C6B5D;
$text-hint: #9CA89D;

.page {
	background-color: $green-bg;
	min-height: 100vh;
	padding-bottom: 20rpx;
}

/* 顶部导航 */
.nav-bar {
	position: sticky;
	top: 0;
	z-index: 100;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 16rpx 30rpx;
	background: #fff;
}

.nav-brand {
	display: flex;
	align-items: center;
}

.nav-logo {
	width: 64rpx;
	height: 64rpx;
	border-radius: 50%;
	margin-right: 16rpx;
}

.nav-title {
	display: flex;
	flex-direction: column;
}

.brand-name {
	font-size: 32rpx;
	font-weight: 700;
	color: $green;
}

.brand-slogan {
	font-size: 20rpx;
	color: $text-hint;
	margin-top: 2rpx;
}

/* 搜索栏 */
.search-bar {
	padding: 12rpx 30rpx 20rpx;
	background: #fff;
}

.search-box {
	display: flex;
	align-items: center;
	height: 72rpx;
	background: $green-bg;
	border-radius: 36rpx;
	padding: 0 28rpx;
}

.search-icon {
	width: 32rpx;
	height: 32rpx;
	margin-right: 12rpx;
	opacity: 0.5;
}

.search-placeholder {
	font-size: 26rpx;
	color: $text-hint;
}

/* 分类Tab */
.category-tabs {
	white-space: nowrap;
	background: #fff;
	padding: 0 20rpx 20rpx;
}

.tab-item {
	display: inline-flex;
	flex-direction: column;
	align-items: center;
	padding: 12rpx 24rpx;
	font-size: 28rpx;
	color: $text-secondary;
	position: relative;

	&.active {
		color: $green;
		font-weight: 700;
		font-size: 30rpx;
	}
}

.tab-line {
	width: 40rpx;
	height: 6rpx;
	background: $green;
	border-radius: 3rpx;
	margin-top: 6rpx;
}

/* Banner */
.banner-wrap {
	padding: 20rpx 24rpx;
}

.banner-swiper {
	height: 320rpx;
	border-radius: 20rpx;
	overflow: hidden;
}

.banner-img {
	width: 100%;
	height: 320rpx;
	border-radius: 20rpx;
}

/* 金刚区 */
.grid-menu {
	display: flex;
	justify-content: space-around;
	padding: 24rpx 20rpx 32rpx;
	background: #fff;
	margin: 0 24rpx;
	border-radius: 20rpx;
}

.menu-item {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.menu-icon {
	width: 88rpx;
	height: 88rpx;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 12rpx;
}

.menu-icon-text {
	font-size: 40rpx;
}

.menu-label {
	font-size: 22rpx;
	color: $text-primary;
}

/* 公告栏 */
.notice-bar {
	display: flex;
	align-items: center;
	margin: 20rpx 24rpx 0;
	padding: 16rpx 24rpx;
	background: #fff;
	border-radius: 16rpx;
}

.notice-tag {
	font-size: 20rpx;
	color: #fff;
	background: $green;
	padding: 4rpx 12rpx;
	border-radius: 8rpx;
	margin-right: 16rpx;
	flex-shrink: 0;
}

.notice-swiper {
	height: 36rpx;
	flex: 1;
}

.notice-text {
	font-size: 24rpx;
	color: $text-secondary;
	line-height: 36rpx;
}

/* 双栏区 */
.dual-section {
	display: flex;
	margin: 20rpx 24rpx 0;
	gap: 16rpx;
	height: 340rpx;
}

.dual-left {
	flex: 1.2;
	background: #fff;
	border-radius: 20rpx;
	padding: 24rpx;
	display: flex;
	flex-direction: column;
}

.dual-header {
	display: flex;
	align-items: baseline;
	margin-bottom: 16rpx;
}

.dual-title {
	font-size: 28rpx;
	font-weight: 700;
	color: $text-primary;
}

.dual-sub {
	font-size: 20rpx;
	color: $orange;
	margin-left: 8rpx;
	background: #FEF0E5;
	padding: 2rpx 10rpx;
	border-radius: 8rpx;
}

.dual-products {
	display: flex;
	gap: 12rpx;
	flex: 1;
}

.dual-product {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.dual-product-img {
	width: 100%;
	height: 160rpx;
	border-radius: 12rpx;
}

.dual-product-price {
	font-size: 26rpx;
	color: $red;
	font-weight: 700;
	margin-top: 8rpx;
}

.dual-right {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}

.dual-card {
	flex: 1;
	background: #fff;
	border-radius: 20rpx;
	padding: 20rpx;
	position: relative;
	overflow: hidden;
}

.dual-card-title {
	font-size: 26rpx;
	font-weight: 700;
	color: $text-primary;
	display: block;
}

.dual-card-sub {
	font-size: 20rpx;
	color: $text-hint;
	display: block;
	margin-top: 4rpx;
}

.dual-card-img-wrap {
	position: absolute;
	right: 12rpx;
	bottom: 12rpx;
	width: 80rpx;
	height: 80rpx;
}

.dual-card-img {
	width: 80rpx;
	height: 80rpx;
	border-radius: 10rpx;
}

/* 活动Tab */
.section-tabs {
	display: flex;
	padding: 32rpx 24rpx 20rpx;
}

.section-tab {
	font-size: 28rpx;
	color: $text-hint;
	margin-right: 48rpx;
	padding-bottom: 8rpx;
	position: relative;

	&.active {
		color: $text-primary;
		font-weight: 700;

		&::after {
			content: '';
			position: absolute;
			bottom: 0;
			left: 50%;
			transform: translateX(-50%);
			width: 32rpx;
			height: 6rpx;
			background: $green;
			border-radius: 3rpx;
		}
	}
}

/* 商品双列网格 */
.goods-grid {
	display: flex;
	padding: 0 24rpx;
	gap: 16rpx;
}

.goods-column {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}

.goods-card {
	background: #fff;
	border-radius: 20rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 16rpx rgba(91, 140, 90, 0.06);
}

.goods-img {
	width: 100%;
	height: 340rpx;
}

.goods-info {
	padding: 16rpx 20rpx 20rpx;
}

.goods-name {
	font-size: 26rpx;
	color: $text-primary;
	display: -webkit-box;
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 2;
	overflow: hidden;
	line-height: 1.4;
}

.goods-price-row {
	display: flex;
	align-items: baseline;
	justify-content: space-between;
	margin-top: 12rpx;
}

.goods-price {
	font-size: 32rpx;
	color: $red;
	font-weight: 700;
}

.goods-sales {
	font-size: 20rpx;
	color: $text-hint;
}

/* 加载更多 */
.load-more {
	padding: 40rpx 0;
	text-align: center;
}

.load-more-text {
	font-size: 24rpx;
	color: $text-hint;
}
</style>
