const IMG = 'https://picsum.photos/seed/a1/400/400';

const goodsNames = [
	'东阿阿胶糕', '同仁堂枸杞', '江中健胃消食片', '云南白药三七粉',
	'百花蜂蜜', '宁夏枸杞王', '长白山人参', '铁皮石斛',
	'西洋参切片', '桃胶雪燕羹', '红枣枸杞茶', '玫瑰花茶',
	'菊花枸杞茶', '金银花茶', '陈皮普洱', '红枣桂圆茶',
	'碧根果', '腰果原味', '夏威夷果', '巴日木',
	'维生素C片', '钙片', '益生菌粉', '蛋白粉',
	'当归补血汤', '四物汤料包', '六味地黄丸', '逍遥丸',
	'茯苓饼', '薏米粉', '黑芝麻丸', '阿胶枣'
];

function makeGoods(id) {
	const price = (9.9 + (id * 7.3) % 290).toFixed(2);
	return {
		id: id,
		name: goodsNames[(id - 1) % goodsNames.length],
		listPicUrl: 'https://picsum.photos/seed/goods' + (id % 20) + '/200/200',
		retailPrice: price
	};
}

function makeGoodsList(page, size) {
	const total = goodsNames.length;
	const pages = Math.ceil(total / size);
	const from = Math.min((page - 1) * size, total);
	const to = Math.min(from + size, total);
	const records = [];
	for (let i = from; i < to; i++) {
		records.push(makeGoods(i + 1));
	}
	return {
		goodsList: { records, current: page, size, total, pages },
		filterCategory: [
			{ id: 1, name: '滋补养生', checked: false },
			{ id: 2, name: '茶饮花茶', checked: false },
			{ id: 3, name: '零食坚果', checked: false }
		]
	};
}

// 模拟购物车数据（内存存储）
let cartData = [
	{ id: 1, goodsId: 1, productId: 101, goodsName: '东阿阿胶糕', goodsSpecifitionNameValue: '250g', listPicUrl: 'https://picsum.photos/seed/goods0/200/200', retailPrice: 99.90, number: 1, checked: true },
	{ id: 2, goodsId: 2, productId: 102, goodsName: '同仁堂枸杞', goodsSpecifitionNameValue: '500g', listPicUrl: 'https://picsum.photos/seed/goods1/200/200', retailPrice: 49.90, number: 2, checked: true }
];

function getCartTotal() {
	let goodsCount = 0, goodsAmount = 0, checkedGoodsCount = 0, checkedGoodsAmount = 0;
	cartData.forEach(item => {
		goodsCount += item.number;
		goodsAmount += item.retailPrice * item.number;
		if (item.checked) {
			checkedGoodsCount += item.number;
			checkedGoodsAmount += item.retailPrice * item.number;
		}
	});
	return {
		goodsCount,
		goodsAmount: goodsAmount.toFixed(2),
		checkedGoodsCount,
		checkedGoodsAmount: checkedGoodsAmount.toFixed(2)
	};
}

// 路由匹配
const mockRoutes = {
	'index/banner'() {
		return {
			banner: [
				{ id: 1, imageUrl: 'https://picsum.photos/seed/b1/750/360', link: '/pages/goods/goods?id=1' },
				{ id: 2, imageUrl: 'https://picsum.photos/seed/b2/750/360', link: '/pages/goods/goods?id=2' },
				{ id: 3, imageUrl: 'https://picsum.photos/seed/b3/750/360', link: '/pages/topic/topic' }
			]
		};
	},
	'index/channel'() {
		return {
			channel: [
				{ id: 1, name: '新品首发', iconUrl: 'https://picsum.photos/seed/c1/96/96', url: '/pages/newGoods/newGoods' },
				{ id: 2, name: '人气推荐', iconUrl: 'https://picsum.photos/seed/c2/96/96', url: '/pages/hotGoods/hotGoods' },
				{ id: 3, name: '品牌直供', iconUrl: 'https://picsum.photos/seed/c3/96/96', url: '/pages/brand/brand' },
				{ id: 4, name: '专题精选', iconUrl: 'https://picsum.photos/seed/c4/96/96', url: '/pages/topic/topic' },
				{ id: 5, name: '全部分类', iconUrl: 'https://picsum.photos/seed/c5/96/96', url: '/pages/catalog/catalog' }
			]
		};
	},
	'index/brand'() {
		return {
			brandList: [
				{ id: 1, name: '东阿阿胶', newPicUrl: IMG, floorPrice: '99.00' },
				{ id: 2, name: '同仁堂', newPicUrl: IMG, floorPrice: '59.00' },
				{ id: 3, name: '江中', newPicUrl: IMG, floorPrice: '39.00' }
			]
		};
	},
	'index/topic'() {
		return {
			topicList: [
				{ id: 1, title: '药食同源养生指南', subtitle: '传统中医智慧', scenePicUrl: IMG, priceInfo: '49.9' },
				{ id: 2, title: '四季养生茶饮', subtitle: '顺应时节', scenePicUrl: IMG, priceInfo: '29.9' }
			]
		};
	},
	'index/newGoods'() {
		return { newGoodsList: [makeGoods(1), makeGoods(2), makeGoods(3), makeGoods(4)] };
	},
	'index/hotGoods'() {
		return { hotGoodsList: [makeGoods(5), makeGoods(6), makeGoods(7), makeGoods(8)] };
	},
	'index/category'() {
		return {
			categoryList: [
				{ id: 1, name: '滋补养生', goodsList: [makeGoods(1), makeGoods(2)] },
				{ id: 2, name: '茶饮花茶', goodsList: [makeGoods(3), makeGoods(4)] },
				{ id: 3, name: '零食坚果', goodsList: [makeGoods(5), makeGoods(6)] }
			]
		};
	},

	// 分类页
	'catalog/index'() {
		return {
			categoryList: [
				{ id: 1, name: '滋补养生' }, { id: 2, name: '茶饮花茶' },
				{ id: 3, name: '零食坚果' }, { id: 4, name: '保健食品' }, { id: 5, name: '药膳食材' }
			],
			currentCategory: buildCategory(1)
		};
	},
	'catalog/current'(params) {
		return { currentCategory: buildCategory(params.id || 1) };
	},

	// 商品
	'goods/count'() { return { goodsCount: 68 }; },
	'goods/list'(params) { return makeGoodsList(params.page || 1, params.size || 10); },
	'goods/hot'() { return { bannerInfo: { imgUrl: 'https://picsum.photos/seed/hot/750/300', name: '热销爆款' } }; },
	'goods/new'() { return { bannerInfo: { imgUrl: 'https://picsum.photos/seed/new/750/300', name: '新品推荐' } }; },
	'goods/category'(params) {
		const id = params.id || 1;
		const names = { 1: '滋补养生', 2: '茶饮花茶', 3: '零食坚果', 4: '保健食品', 5: '药膳食材' };
		return {
			brotherCategory: [
				{ id: 1, name: '滋补养生' }, { id: 2, name: '茶饮花茶' },
				{ id: 3, name: '零食坚果' }, { id: 4, name: '保健食品' }, { id: 5, name: '药膳食材' }
			],
			currentCategory: { id, name: names[id] || '分类' + id, frontName: (names[id] || '分类') + '精选好物' }
		};
	},
	'goods/detail'(params) {
		const id = params.id || 1;
		const name = goodsNames[(id - 1) % goodsNames.length];
		const price = (29.9 + (id * 7.3) % 200).toFixed(2);
		return {
			info: {
				id, name, goodsBrief: '精选优质原料，传统工艺制作',
				retailPrice: price, counterPrice: (price * 1.5).toFixed(2), sellVolume: 328,
				listPicUrl: 'https://picsum.photos/seed/dg1/600/600',
				goodsDesc: '<p><img src="https://picsum.photos/seed/desc1/750/400" style="width:100%"/></p><p>' + name + '，源自道地产区，品质保证。</p>'
			},
			gallery: [
				{ id: 1, imgUrl: 'https://picsum.photos/seed/dg1/600/600' },
				{ id: 2, imgUrl: 'https://picsum.photos/seed/dg2/600/600' },
				{ id: 3, imgUrl: 'https://picsum.photos/seed/dg3/600/600' }
			],
			brand: { id: 1, name: '药食同源精选' },
			attribute: [
				{ name: '产地', value: '中国' }, { name: '规格', value: '250g/罐' },
				{ name: '保质期', value: '24个月' }, { name: '储存方式', value: '阴凉干燥处' }
			],
			issue: [
				{ id: 1, question: '如何保存？', answer: '密封后放置于阴凉干燥处。' },
				{ id: 2, question: '保质期多久？', answer: '24个月，请在有效期内食用。' }
			],
			specificationList: [{
				specificationId: 1, name: '规格',
				valueList: [
					{ id: 1, specificationId: 1, value: '250g', checked: false },
					{ id: 2, specificationId: 1, value: '500g', checked: false }
				]
			}],
			productList: [
				{ id: 101, goodsSpecificationIds: '1', goodsNumber: 99 },
				{ id: 102, goodsSpecificationIds: '2', goodsNumber: 50 }
			],
			comment: {
				count: 12,
				data: { avatar: 'https://picsum.photos/seed/user1/100/100', nickname: '养生达人', addTime: '2025-12-01', content: '品质很好，推荐购买！', picList: [] }
			},
			userHasCollect: 0
		};
	},
	'goods/related'(params) {
		const id = params.id || 1;
		return { goodsList: [makeGoods(id + 1), makeGoods(id + 2), makeGoods(id + 3), makeGoods(id + 4)] };
	},

	// 购物车
	'cart/goodscount'() { return { cartTotal: getCartTotal() }; },
	'cart/index'() { return { cartList: cartData, cartTotal: getCartTotal() }; },
	'cart/add'(params) {
		const exists = cartData.find(c => c.productId == params.productId);
		if (exists) {
			exists.number += (params.number || 1);
		} else {
			cartData.push({
				id: Date.now(), goodsId: params.goodsId, productId: params.productId,
				goodsName: '商品 #' + params.goodsId, goodsSpecifitionNameValue: '',
				listPicUrl: 'https://picsum.photos/seed/goods' + (params.goodsId % 20) + '/200/200',
				retailPrice: 39.90, number: params.number || 1, checked: true
			});
		}
		return { cartTotal: getCartTotal() };
	},
	'buy/add'() { return {}; },
	'cart/update'(params) {
		const item = cartData.find(c => c.id == params.id);
		if (item) item.number = params.number;
		return {};
	},
	'cart/delete'(params) {
		const ids = (params.productIds || '').split(',').map(Number);
		cartData = cartData.filter(c => !ids.includes(c.productId));
		return { cartList: cartData, cartTotal: getCartTotal() };
	},
	'cart/checked'(params) {
		const ids = (params.productIds || '').split(',').map(Number);
		const isChecked = params.isChecked == 1;
		cartData.forEach(c => {
			if (ids.includes(c.productId)) c.checked = isChecked;
		});
		return { cartList: cartData, cartTotal: getCartTotal() };
	},
	'cart/checkout'() {
		const checkedGoods = cartData.filter(c => c.checked);
		const total = checkedGoods.reduce((sum, c) => sum + c.retailPrice * c.number, 0);
		return {
			checkedGoodsList: checkedGoods,
			checkedAddress: { id: 1, userName: '张三', telNumber: '13800138000', fullRegion: '北京市朝阳区', detailInfo: '望京SOHO T3 1201', isDefault: 1 },
			actualPrice: total.toFixed(2),
			checkedCoupon: null, couponList: [], couponPrice: 0,
			freightPrice: 0, goodsTotalPrice: total.toFixed(2), orderTotalPrice: total.toFixed(2)
		};
	},

	// 收藏
	'collect/addordelete'() { return { type: 'add' }; },
	'collect/list'() { return { collectList: [], totalPages: 0 }; },

	// 订单
	'order/submit'() { return { orderInfo: { id: 10001, orderSn: '202607030001' } }; },
	'order/list'() {
		return {
			list: [{
				id: 10001, orderSn: '202607030001', orderStatusText: '待发货', actualPrice: '199.70',
				handleOption: { pay: false }, goodsList: [{ id: 1, goodsName: '东阿阿胶糕', number: 1, listPicUrl: 'https://picsum.photos/seed/goods0/200/200' }]
			}],
			page: 1, total: 1
		};
	},

	// 搜索
	'search/index'() {
		return {
			historyKeywordList: ['阿胶', '枸杞', '人参'],
			hotKeywordList: [
				{ keyword: '阿胶糕', isHot: 1 }, { keyword: '枸杞', isHot: 1 },
				{ keyword: '黑芝麻丸', isHot: 0 }, { keyword: '人参', isHot: 0 },
				{ keyword: '花茶', isHot: 0 }
			],
			defaultKeyword: { keyword: '阿胶糕' }
		};
	},
	'search/helper'(params) {
		const kw = params.keyword || '';
		return [kw + '糕', kw + '茶', kw + '丸', kw + '片'];
	},
	'search/clearhistory'() { return {}; },

	// 优惠券
	'coupon/list'() {
		return [
			{ id: 1, name: '新人专享券', typeMoney: 10, minGoodsAmount: 99, useEndDate: '2026-12-31', couponStatus: 1 },
			{ id: 2, name: '满减优惠券', typeMoney: 20, minGoodsAmount: 199, useEndDate: '2026-08-31', couponStatus: 1 },
			{ id: 3, name: '会员折扣券', typeMoney: 50, minGoodsAmount: 399, useEndDate: '2026-06-01', couponStatus: 3 }
		];
	},

	// 支付
	'pay/prepay'() {
		return { timeStamp: '1234567890', nonceStr: 'mock', package: 'prepay_id=mock', signType: 'MD5', paySign: 'mock' };
	},
	'pay/query'() { return { orderStatus: 'paid' }; },

	// 地址
	'address/list'() {
		return [{ id: 1, userName: '张三', telNumber: '13800138000', fullRegion: '北京市朝阳区', detailInfo: '望京SOHO T3 1201', isDefault: 1 }];
	},
	'address/save'() { return {}; },
	'address/delete'() { return {}; },

	// 品牌
	'brand/list'() {
		return {
			brandList: [
				{ id: 1, name: '东阿阿胶', picUrl: 'https://picsum.photos/seed/brand1/200/200', floorPrice: '99.00' },
				{ id: 2, name: '同仁堂', picUrl: 'https://picsum.photos/seed/brand2/200/200', floorPrice: '59.00' }
			], totalPages: 1
		};
	},
	'brand/detail'(params) {
		const names = { 1: '东阿阿胶', 2: '同仁堂', 3: '江中' };
		const id = params.id || 1;
		return { brand: { id, name: names[id] || '品牌', picUrl: 'https://picsum.photos/seed/brand' + id + '/600/300', simpleDesc: '品质保证' } };
	},

	// 专题
	'topic/list'() {
		return { records: [
			{ id: 1, title: '药食同源养生指南', subtitle: '传统中医智慧', scenePicUrl: IMG, priceInfo: '49.9' },
			{ id: 2, title: '四季养生茶饮', subtitle: '顺应时节', scenePicUrl: IMG, priceInfo: '29.9' }
		], total: 2, pages: 1 };
	},
	'topic/detail'() {
		return { id: 1, title: '药食同源养生指南', subtitle: '传统中医智慧', scenePicUrl: IMG, content: '<p>药食同源是中国传统养生理念。</p>' };
	},

	// 评论
	'comment/list'() {
		return { records: [
			{ id: 1, nickname: '养生达人', avatar: 'https://picsum.photos/seed/u1/100/100', addTime: '2025-12-01', content: '品质很好！', picList: [] }
		], total: 1 };
	},

	// 足迹
	'footprint/list'() { return { list: [], totalPages: 0 }; },

	// 用户
	'user/info'() { return { userInfo: { nickName: '测试用户', avatarUrl: IMG, mobile: '138****8888' } }; },

	// 帮助
	'helpissue/typeList'() { return { list: [{ id: 1, name: '商品相关' }, { id: 2, name: '订单相关' }] }; },
	'helpissue/issueList'() { return { list: [{ id: 1, question: '如何退货？', answer: '在订单详情页申请' }] }; },

	// 登录
	'auth/LoginByMa'() { return { token: 'mock_token_123', userInfo: { nickName: '测试用户', avatarUrl: IMG } }; }
};

function buildCategory(id) {
	const names = { 1: '滋补养生', 2: '茶饮花茶', 3: '零食坚果', 4: '保健食品', 5: '药膳食材' };
	const name = names[id] || '分类' + id;
	return {
		id, name, frontName: name + '精选好物',
		wapBannerUrl: 'https://picsum.photos/seed/cat' + id + '/600/300',
		subCategoryList: [
			{ id: id * 10 + 1, name: name + '-精选', wapBannerUrl: 'https://picsum.photos/seed/cat' + id + 'a/200/200' },
			{ id: id * 10 + 2, name: name + '-经典', wapBannerUrl: 'https://picsum.photos/seed/cat' + id + 'b/200/200' },
			{ id: id * 10 + 3, name: name + '-新品', wapBannerUrl: 'https://picsum.photos/seed/cat' + id + 'c/200/200' }
		]
	};
}

/**
 * 处理 mock 请求
 * @returns {object|null} 返回 mock 数据或 null（表示无匹配）
 */
function handleMock(url, params) {
	const handler = mockRoutes[url];
	if (!handler) return null;
	const data = handler(params || {});
	// search/helper 和 coupon/list、address/list 直接返回数组
	if (Array.isArray(data)) {
		return { code: 0, msg: 'success', data: data };
	}
	return { code: 0, msg: 'success', data: data };
}

module.exports = { handleMock };
