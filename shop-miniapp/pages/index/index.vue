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
		<view class="banner-wrap" v-if="currentTab === 0">
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
		<view class="grid-menu" v-if="currentTab === 0">
			<view class="menu-item" v-for="(item, index) in menuItems" :key="index" @tap="onMenuTap(item)">
				<view class="menu-icon" :style="{backgroundColor: item.bgColor}">
					<text class="menu-icon-text">{{item.icon}}</text>
				</view>
				<text class="menu-label">{{item.name}}</text>
			</view>
		</view>

		<!-- 公告栏 -->
		<view class="notice-bar" v-if="notice && currentTab === 0">
			<text class="notice-tag">公告</text>
			<swiper class="notice-swiper" vertical autoplay :interval="3000" circular :show-indicator="false">
				<swiper-item v-for="(item, index) in notices" :key="index">
					<text class="notice-text">{{item.text}}</text>
				</swiper-item>
			</swiper>
		</view>

		<!-- 限时特惠 + 热卖双栏 -->
		<view class="dual-section" v-if="currentTab === 0">
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

		<!-- 二级分类专区 Banner -->
		<view class="category-banner-wrap" v-if="currentTab > 0">
			<image class="category-banner-img" :src="categoryBanners[currentTab]" mode="aspectFill"></image>
			<view class="category-banner-text">
				<text class="cat-title">{{categoryTabs[currentTab].name}}专区</text>
				<text class="cat-sub">甄选地道原料，科学合理配比</text>
			</view>
		</view>

		<!-- 活动Tab区 -->
		<view class="section-tabs" v-if="currentTab === 0">
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
							<view class="goods-cart-btn" @tap.stop="quickAddToCart(item, $event)">
								<text class="goods-cart-btn-icon">+</text>
							</view>
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
							<view class="goods-cart-btn" @tap.stop="quickAddToCart(item, $event)">
								<text class="goods-cart-btn-icon">+</text>
							</view>
						</view>
					</view>
				</view>
			</view>
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

		<!-- 加载更多 -->
		<view class="load-more" v-if="goodsList.length > 0">
			<text class="load-more-text">— 更多好物探索中 —</text>
		</view>

		<!-- 新人礼礼包弹窗 -->
		<view class="modal-mask" v-if="showNewUserModal" @tap="showNewUserModal = false">
			<view class="modal-content new-user-modal" @tap.stop>
				<view class="modal-header new-user-header">
					<text class="modal-title">🎁 新人专属百元大礼包</text>
					<text class="modal-sub">已为您准备好以下特惠优惠券</text>
				</view>
				<view class="coupon-list-wrap">
					<view class="coupon-card">
						<view class="coupon-left">
							<text class="coupon-symbol">¥</text>
							<text class="coupon-val">10</text>
						</view>
						<view class="coupon-right">
							<text class="cp-name">新人专享立减券</text>
							<text class="cp-limit">满 ¥99 可用</text>
						</view>
					</view>
					<view class="coupon-card">
						<view class="coupon-left">
							<text class="coupon-symbol">¥</text>
							<text class="coupon-val">20</text>
						</view>
						<view class="coupon-right">
							<text class="cp-name">满减优惠特惠券</text>
							<text class="cp-limit">满 ¥199 可用</text>
						</view>
					</view>
					<view class="coupon-card">
						<view class="coupon-left">
							<text class="coupon-symbol">¥</text>
							<text class="coupon-val">50</text>
						</view>
						<view class="coupon-right">
							<text class="cp-name">会员专享年终特惠</text>
							<text class="cp-limit">满 ¥399 可用</text>
						</view>
					</view>
				</view>
				<button class="modal-btn new-user-btn" @tap="receiveNewUserGift">一键领取大礼包</button>
				<view class="modal-close" @tap="showNewUserModal = false">×</view>
			</view>
		</view>

		<!-- 尊享会员年卡弹窗 -->
		<view class="modal-mask" v-if="showVipModal" @tap="showVipModal = false">
			<view class="modal-content vip-modal" @tap.stop>
				<view class="modal-header vip-header">
					<text class="modal-title">👑 药食同源黄金会员年卡</text>
					<text class="modal-sub">尊享4大终身特权，让健康更实惠</text>
				</view>
				<view class="vip-privileges">
					<view class="vip-privilege">
						<text class="vip-p-icon">⚡</text>
						<view class="vip-p-info">
							<text class="vip-p-title">全场商品9折</text>
							<text class="vip-p-desc">金卡专属，下单立享折上折</text>
						</view>
					</view>
					<view class="vip-privilege">
						<text class="vip-p-icon">🚚</text>
						<view class="vip-p-info">
							<text class="vip-p-title">每月免邮券 2 张</text>
							<text class="vip-p-desc">全年送 24 张免邮券，包邮到家</text>
						</view>
					</view>
					<view class="vip-privilege">
						<text class="vip-p-icon">🎁</text>
						<view class="vip-p-info">
							<text class="vip-p-title">新品抢先试吃</text>
							<text class="vip-p-desc">每月抽取 100 名会员免费试用新品</text>
						</view>
					</view>
				</view>
				<view class="vip-price-row">
					<text class="vip-old-price">原价 ¥199/年</text>
					<text class="vip-now-price">限时特惠 ¥99/年</text>
				</view>
				<button class="modal-btn vip-btn" @tap="activateVipCard">立即开通会员年卡</button>
				<view class="modal-close" @tap="showVipModal = false">×</view>
			</view>
		</view>

		<!-- 分销赚钱弹窗 -->
		<view class="modal-mask" v-if="showShareModal" @tap="showShareModal = false">
			<view class="modal-content share-modal" @tap.stop>
				<view class="modal-header share-header">
					<text class="modal-title">🤝 分享好友，轻松获利</text>
					<text class="modal-sub">让健康传播，让关爱分享</text>
				</view>
				<view class="share-benefit-wrap">
					<text class="share-title">我的佣金比例：10%</text>
					<text class="share-rules">分享您的专属推广海报或二维码给好友，好友通过您的分享购买，您将获得其实际付款金额 10% 的现金奖励！</text>
				</view>
				<view class="share-actions">
					<button class="share-btn-action sec" @tap="copyShareLink">📋 复制分享链接</button>
					<button class="share-btn-action pri" @tap="saveSharePoster">🖼️ 生成海报保存</button>
				</view>
				<view class="modal-close" @tap="showShareModal = false">×</view>
			</view>
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
				{ name: '滋补养生', id: 1 },
				{ name: '茶饮花茶', id: 2 },
				{ name: '零食坚果', id: 3 },
				{ name: '保健食品', id: 4 },
				{ name: '药膳食材', id: 5 }
			],
			categoryBanners: {
				1: 'https://images.unsplash.com/photo-1596701062351-8c2c14d1fdd0?w=750',
				2: 'https://images.unsplash.com/photo-1597481499750-3e6b22637e12?w=750',
				3: 'https://images.unsplash.com/photo-1534149711956-f9b7d528f64d?w=750',
				4: 'https://images.unsplash.com/photo-1616679911721-eff6eec18fcd?w=750',
				5: 'https://images.unsplash.com/photo-1514733670139-4d87a19b179d?w=750'
			},
			menuItems: [
				{ name: '新人礼', icon: '🎁', bgColor: '#FEF0E5', url: '' },
				{ name: '会员', icon: '👑', bgColor: '#FBF4E4', url: '' },
				{ name: '优惠券', icon: '🎫', bgColor: '#E8F2E7', url: '' },
				{ name: '分销', icon: '🤝', bgColor: '#EAF0F9', url: '' },
				{ name: '全部分类', icon: '📋', bgColor: '#F3EFF8', url: '/pages/catalog/catalog' }
			],
			sectionTabs: ['今日主推', '热销爆款', '新品上架'],
			showNewUserModal: false,
			showVipModal: false,
			showShareModal: false,
			cartBalls: []
		}
	},
	computed: {
		displayGoods() {
			if (this.currentTab > 0) {
				return this.goodsList;
			}
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
			const tab = this.categoryTabs[index];
			this.loadTabGoods(tab.id);
		},
		loadTabGoods(categoryId) {
			if (categoryId === 0) {
				// 精选：加载首页混合商品
				util.request(api.IndexUrlCategory).then(res => {
					if (res.code === 0) {
						let all = [];
						res.data.categoryList.forEach(cat => {
							if (cat.goodsList) all = all.concat(cat.goodsList);
						});
						this.goodsList = all;
					}
				});
			} else {
				util.request(api.GoodsList, { categoryId, page: 1, size: 40 }).then(res => {
					if (res.code === 0 && res.data.goodsList) {
						this.goodsList = res.data.goodsList.records || [];
					}
				});
			}
		},
		onMenuTap(item) {
			if (item.name === '新人礼') {
				this.showNewUserModal = true;
			} else if (item.name === '会员') {
				this.showVipModal = true;
			} else if (item.name === '优惠券') {
				uni.navigateTo({ url: '/pages/ucenter/coupon/coupon' });
			} else if (item.name === '分销') {
				this.showShareModal = true;
			} else if (item.url) {
				if (item.url.indexOf('/pages/catalog') > -1) {
					uni.switchTab({ url: item.url });
				} else {
					uni.navigateTo({ url: item.url });
				}
			}
		},
		receiveNewUserGift() {
			this.showNewUserModal = false;
			uni.showToast({
				title: '大礼包领取成功！',
				icon: 'success',
				duration: 2000
			});
		},
		activateVipCard() {
			this.showVipModal = false;
			uni.showToast({
				title: '激活成功，已成为黄金会员！',
				icon: 'success',
				duration: 2000
			});
		},
		copyShareLink() {
			this.showShareModal = false;
			uni.setClipboardData({
				data: 'https://shop-miniapp.wechat/invite?userId=10001',
				success: () => {
					uni.showToast({
						title: '专属链接已复制！',
						icon: 'success'
					});
				}
			});
		},
		saveSharePoster() {
			this.showShareModal = false;
			uni.showToast({
				title: '海报已成功保存！',
				icon: 'success'
			});
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

/* 模态框通用遮罩层 */
.modal-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.6);
	z-index: 999;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 40rpx;
	backdrop-filter: blur(10px);
}

.modal-content {
	background: #fff;
	border-radius: 36rpx;
	width: 100%;
	max-width: 600rpx;
	padding: 48rpx;
	position: relative;
	box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.15);
	animation: modalShow 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes modalShow {
	from { transform: scale(0.9); opacity: 0; }
	to { transform: scale(1); opacity: 1; }
}

.modal-header {
	text-align: center;
	margin-bottom: 36rpx;
}

.modal-title {
	font-size: 36rpx;
	font-weight: 700;
	color: #2D3A2E;
	display: block;
}

.modal-sub {
	font-size: 24rpx;
	color: #9CA89D;
	margin-top: 10rpx;
	display: block;
}

/* 新人礼特别样式 */
.new-user-modal {
	background: linear-gradient(135deg, #FFF9F6 0%, #FFFFFF 100%);
	border-top: 10rpx solid #CF4A3E;
}

.coupon-list-wrap {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
	margin-bottom: 40rpx;
}

.coupon-card {
	display: flex;
	background: linear-gradient(90deg, #FEF0E5 0%, #FFF9F6 100%);
	border: 2rpx dashed #E07C4F;
	border-radius: 16rpx;
	overflow: hidden;
}

.coupon-left {
	width: 160rpx;
	background: #E07C4F;
	color: #fff;
	display: flex;
	align-items: baseline;
	justify-content: center;
	padding: 20rpx 0;
}

.coupon-symbol {
	font-size: 24rpx;
	font-weight: 700;
}

.coupon-val {
	font-size: 54rpx;
	font-weight: 700;
}

.coupon-right {
	flex: 1;
	padding: 20rpx 28rpx;
	display: flex;
	flex-direction: column;
	justify-content: center;
}

.cp-name {
	font-size: 26rpx;
	font-weight: 700;
	color: #2D3A2E;
}

.cp-limit {
	font-size: 20rpx;
	color: #5C6B5D;
	margin-top: 6rpx;
}

.modal-btn {
	height: 88rpx;
	line-height: 88rpx;
	border-radius: 44rpx;
	font-size: 30rpx;
	font-weight: 700;
	color: #fff;
	text-align: center;
	border: none;
}

.new-user-btn {
	background: linear-gradient(135deg, #CF4A3E 0%, #E07C4F 100%);
	box-shadow: 0 10rpx 30rpx rgba(224, 124, 79, 0.3);
}

/* 会员卡特别样式 */
.vip-modal {
	background: linear-gradient(135deg, #FDFBF7 0%, #FFFFFF 100%);
	border-top: 10rpx solid #B8860B;
}

.vip-privileges {
	display: flex;
	flex-direction: column;
	gap: 28rpx;
	margin-bottom: 40rpx;
}

.vip-privilege {
	display: flex;
	align-items: center;
}

.vip-p-icon {
	font-size: 48rpx;
	margin-right: 24rpx;
	width: 64rpx;
	height: 64rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	background: #FBF4E4;
	border-radius: 50%;
}

.vip-p-info {
	display: flex;
	flex-direction: column;
}

.vip-p-title {
	font-size: 28rpx;
	font-weight: 700;
	color: #B8860B;
}

.vip-p-desc {
	font-size: 22rpx;
	color: #9CA89D;
	margin-top: 4rpx;
}

.vip-price-row {
	text-align: center;
	margin-bottom: 30rpx;
}

.vip-old-price {
	font-size: 24rpx;
	color: #9CA89D;
	text-decoration: line-through;
	margin-right: 16rpx;
}

.vip-now-price {
	font-size: 32rpx;
	color: #B8860B;
	font-weight: 700;
}

.vip-btn {
	background: linear-gradient(135deg, #B8860B 0%, #DAA520 100%);
	box-shadow: 0 10rpx 30rpx rgba(184, 134, 11, 0.3);
}

/* 分销特别样式 */
.share-modal {
	background: linear-gradient(135deg, #F6F7F4 0%, #FFFFFF 100%);
	border-top: 10rpx solid #5B8C5A;
}

.share-benefit-wrap {
	background: #E8F2E7;
	border-radius: 20rpx;
	padding: 30rpx 36rpx;
	margin-bottom: 40rpx;
	text-align: center;
}

.share-title {
	font-size: 30rpx;
	font-weight: 700;
	color: #5B8C5A;
	display: block;
	margin-bottom: 12rpx;
}

.share-rules {
	font-size: 24rpx;
	color: #5C6B5D;
	line-height: 1.6;
	display: block;
}

.share-actions {
	display: flex;
	gap: 20rpx;
}

.share-btn-action {
	flex: 1;
	height: 88rpx;
	line-height: 88rpx;
	font-size: 26rpx;
	font-weight: 700;
	border-radius: 44rpx;
	text-align: center;
	border: none;
}

.share-btn-action.sec {
	background: #E8F2E7;
	color: #5B8C5A;
}

.share-btn-action.pri {
	background: #5B8C5A;
	color: #fff;
	box-shadow: 0 10rpx 30rpx rgba(91, 140, 90, 0.3);
}

/* 关闭按钮 */
.modal-close {
	position: absolute;
	top: 24rpx;
	right: 28rpx;
	font-size: 48rpx;
	color: #9CA89D;
	line-height: 1;
	padding: 10rpx;
	cursor: pointer;
}

/* 二级分类 Banner */
.category-banner-wrap {
	margin: 20rpx 24rpx;
	height: 240rpx;
	border-radius: 20rpx;
	overflow: hidden;
	position: relative;
}

.category-banner-img {
	width: 100%;
	height: 100%;
}

.category-banner-text {
	position: absolute;
	left: 40rpx;
	top: 50%;
	transform: translateY(-50%);
	z-index: 10;
	display: flex;
	flex-direction: column;
}

.cat-title {
	font-size: 36rpx;
	font-weight: 700;
	color: #fff;
	text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.4);
}

.cat-sub {
	font-size: 22rpx;
	color: rgba(255, 255, 255, 0.85);
	margin-top: 8rpx;
	text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.4);
}

/* 一键加购绿色小圆钮 */
.goods-cart-btn {
	width: 48rpx;
	height: 48rpx;
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
	font-size: 28rpx;
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
