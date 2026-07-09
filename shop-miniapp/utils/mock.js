const goodsListAll = [
	{ id: 1, categoryId: 1, name: '东阿阿胶糕', goodsBrief: '补气养血，美容养颜，传统手工熬制', retailPrice: '99.90', listPicUrl: 'https://images.unsplash.com/photo-1505252585461-04db1eb84625?w=400', isNew: 1, isHot: 1 },
	{ id: 2, categoryId: 1, name: '同仁堂枸杞', goodsBrief: '宁夏特级免洗枸杞，粒大饱满，甘甜可口', retailPrice: '24.50', listPicUrl: 'https://images.unsplash.com/photo-1509358271058-acd22cc93898?w=400', isNew: 0, isHot: 1 },
	{ id: 3, categoryId: 1, name: '长白山人参', goodsBrief: '整枝鲜参，长白山道地直供，元气满满', retailPrice: '199.00', listPicUrl: 'https://images.unsplash.com/photo-1514733670139-4d87a19b179d?w=400', isNew: 1, isHot: 0 },
	{ id: 4, categoryId: 1, name: '铁皮石斛', goodsBrief: '正宗霍山铁皮石斛，胶质浓郁，养阴清热', retailPrice: '159.50', listPicUrl: 'https://images.unsplash.com/photo-1611080626919-7cf5a9dbab5b?w=400', isNew: 0, isHot: 0 },
	{ id: 5, categoryId: 1, name: '百花蜂蜜', goodsBrief: '农家天然土蜂蜜，质地浓稠，蜜香浓郁', retailPrice: '39.90', listPicUrl: 'https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=400', isNew: 1, isHot: 0 },
	{ id: 6, categoryId: 1, name: '云南白药三七粉', goodsBrief: '超细三七粉，温水冲服，活血化瘀佳品', retailPrice: '39.10', listPicUrl: 'https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=400', isNew: 0, isHot: 1 },
	{ id: 7, categoryId: 1, name: '西洋参切片', goodsBrief: '进口花旗参切片，参味浓郁，清热生津', retailPrice: '88.00', listPicUrl: 'https://images.unsplash.com/photo-1514733670139-4d87a19b179d?w=400', isNew: 0, isHot: 0 },
	{ id: 9, categoryId: 2, name: '宁夏枸杞王', goodsBrief: '精选大粒贡果，养肝明目，泡水圣品', retailPrice: '49.90', listPicUrl: 'https://images.unsplash.com/photo-1509358271058-acd22cc93898?w=400', isNew: 1, isHot: 1 },
	{ id: 10, categoryId: 2, name: '金边玫瑰花茶', goodsBrief: '云南墨红玫瑰，花香浓郁，疏肝理气', retailPrice: '28.00', listPicUrl: 'https://images.unsplash.com/photo-1506084868230-bb9d95c24759?w=400', isNew: 1, isHot: 0 },
	{ id: 11, categoryId: 2, name: '胎菊王菊花茶', goodsBrief: '桐乡特级胎菊，清热明目，汤色金黄', retailPrice: '32.00', listPicUrl: 'https://images.unsplash.com/photo-1597481499750-3e6b22637e12?w=400', isNew: 0, isHot: 1 },
	{ id: 12, categoryId: 2, name: '清热金银花茶', goodsBrief: '封丘金银花，质地饱满，清热解暑必备', retailPrice: '35.00', listPicUrl: 'https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=400', isNew: 0, isHot: 0 },
	{ id: 13, categoryId: 2, name: '新会小青柑普洱', goodsBrief: '正宗新会陈皮与核心产区普洱，醇厚回甘', retailPrice: '58.00', listPicUrl: 'https://images.unsplash.com/photo-1594631252845-29fc4cc8cfa9?w=400', isNew: 0, isHot: 0 },
	{ id: 14, categoryId: 2, name: '红枣桂圆双桂茶', goodsBrief: '红枣与桂圆气血双补，香甜可口', retailPrice: '29.90', listPicUrl: 'https://images.unsplash.com/photo-1596701062351-8c2c14d1fdd0?w=400', isNew: 1, isHot: 1 },
	{ id: 15, categoryId: 2, name: '桑葚干泡茶', goodsBrief: '天然无沙桑葚干，花青素爆表，补血滋阴', retailPrice: '26.00', listPicUrl: 'https://images.unsplash.com/photo-1509358271058-acd22cc93898?w=400', isNew: 0, isHot: 0 },
 
	{ id: 16, categoryId: 3, name: '有机黑芝麻丸', goodsBrief: '九蒸九晒黑芝麻丸，软糯浓郁，以黑养黑', retailPrice: '39.90', listPicUrl: 'https://images.unsplash.com/photo-1595855759920-86582396756a?w=400', isNew: 0, isHot: 1 },
	{ id: 17, categoryId: 3, name: '手剥奶油夏威夷果', goodsBrief: '大颗粒奶香夏威夷果，果仁饱满，酥脆香甜', retailPrice: '45.00', listPicUrl: 'https://images.unsplash.com/photo-1534149711956-f9b7d528f64d?w=400', isNew: 1, isHot: 1 },
	{ id: 18, categoryId: 3, name: '盐焗原味腰果', goodsBrief: '精选大腰果，轻度烘焙，保留坚果原香', retailPrice: '48.00', listPicUrl: 'https://images.unsplash.com/photo-1600189020840-e9918c25265d?w=400', isNew: 0, isHot: 0 },
	{ id: 19, categoryId: 3, name: '薄壳手剥碧根果', goodsBrief: '临安碧根果，薄壳易剥，果仁香脆', retailPrice: '42.00', listPicUrl: 'https://images.unsplash.com/photo-1585238342024-78d387f4a707?w=400', isNew: 0, isHot: 0 },
	{ id: 20, categoryId: 3, name: '东阿阿胶枣', goodsBrief: '浓浓阿胶浆浸泡，枣香浓郁，甜而不腻', retailPrice: '18.80', listPicUrl: 'https://images.unsplash.com/photo-1601004890684-d8cbf643f5f2?w=400', isNew: 1, isHot: 0 },
	{ id: 21, categoryId: 3, name: '长白山野生松子', goodsBrief: '开口开口松子，粒粒甄选，松香四溢', retailPrice: '68.00', listPicUrl: 'https://images.unsplash.com/photo-1534149711956-f9b7d528f64d?w=400', isNew: 0, isHot: 1 },
 
	{ id: 22, categoryId: 4, name: '江中健胃消食片', goodsBrief: '消食导滞，用于脾胃虚弱所致的食积', retailPrice: '31.80', listPicUrl: 'https://images.unsplash.com/photo-1607619056574-7b8f304b3c93?w=400', isNew: 0, isHot: 1 },
	{ id: 23, categoryId: 4, name: '天然维生素C片', goodsBrief: '每片含高活性维C，增强免疫，美白抗氧化', retailPrice: '29.90', listPicUrl: 'https://images.unsplash.com/photo-1616679911721-eff6eec18fcd?w=400', isNew: 0, isHot: 0 },
	{ id: 24, categoryId: 4, name: '高钙片', goodsBrief: '补钙搭档D3，促进钙吸收，强壮骨骼', retailPrice: '39.90', listPicUrl: 'https://images.unsplash.com/photo-1584017911766-d451b3d0e843?w=400', isNew: 0, isHot: 0 },
	{ id: 25, categoryId: 4, name: '益生菌粉', goodsBrief: '百亿活菌守护肠道，改善消化，调理肠胃', retailPrice: '59.00', listPicUrl: 'https://images.unsplash.com/photo-1579758629938-03607ccdbaba?w=400', isNew: 1, isHot: 0 },
	{ id: 26, categoryId: 4, name: '有机高蛋白营养粉', goodsBrief: '动植物双蛋白配合，补充日常营养', retailPrice: '98.00', listPicUrl: 'https://images.unsplash.com/photo-1593095948071-474c5cc2989d?w=400', isNew: 1, isHot: 1 },
 
	{ id: 27, categoryId: 5, name: '经典当归补血汤料包', goodsBrief: '传统当归黄芪配方，补气养血，药膳煲汤必备', retailPrice: '45.00', listPicUrl: 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400', isNew: 1, isHot: 1 },
	{ id: 28, categoryId: 5, name: '正宗四物汤调理包', goodsBrief: '当归熟地川芎白芍，调经要药，气血双补', retailPrice: '48.00', listPicUrl: 'https://images.unsplash.com/photo-1514733670139-4d87a19b179d?w=400', isNew: 0, isHot: 0 },
	{ id: 29, categoryId: 5, name: '黄金草虫草花', goodsBrief: '特级无根虫草花，色泽金黄，汤汁鲜美', retailPrice: '35.00', listPicUrl: 'https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?w=400', isNew: 0, isHot: 1 },
	{ id: 30, categoryId: 5, name: '岷县特级当归片', goodsBrief: '当归头切片，油性足，参味香浓，煲汤良伴', retailPrice: '28.00', listPicUrl: 'https://images.unsplash.com/photo-1611080626919-7cf5a9dbab5b?w=400', isNew: 0, isHot: 0 },
	{ id: 31, categoryId: 5, name: '甘肃道地黄芪片', goodsBrief: '核心产区黄芪，菊花心明显，补气固表', retailPrice: '24.00', listPicUrl: 'https://images.unsplash.com/photo-1509358271058-acd22cc93898?w=400', isNew: 0, isHot: 0 }
];

function makeGoods(id) {
	return goodsListAll.find(g => g.id === id) || goodsListAll[0];
}

function makeGoodsList(page, size, categoryId, isNew, isHot) {
	let filtered = goodsListAll;
	if (categoryId && Number(categoryId) !== 0) {
		filtered = filtered.filter(g => g.categoryId == categoryId);
	}
	if (isNew !== undefined && isNew !== null && Number(isNew) === 1) {
		filtered = filtered.filter(g => g.isNew == 1);
	}
	if (isHot !== undefined && isHot !== null && Number(isHot) === 1) {
		filtered = filtered.filter(g => g.isHot == 1);
	}
	const total = filtered.length;
	const pages = Math.ceil(total / size);
	const from = Math.min((page - 1) * size, total);
	const to = Math.min(from + size, total);
	const records = filtered.slice(from, to);
	return {
		goodsList: { records, current: page, size, total, pages },
		filterCategory: [
			{ id: 1, name: '滋补养生', checked: Number(categoryId) === 1 },
			{ id: 2, name: '茶饮花茶', checked: Number(categoryId) === 2 },
			{ id: 3, name: '零食坚果', checked: Number(categoryId) === 3 },
			{ id: 4, name: '保健食品', checked: Number(categoryId) === 4 },
			{ id: 5, name: '药膳食材', checked: Number(categoryId) === 5 }
		]
	};
}

// 模拟购物车数据（内存存储）
let cartData = [
	{ id: 1, goodsId: 1, productId: 101, goodsName: '东阿阿胶糕', goodsSpecifitionNameValue: '250g', listPicUrl: 'https://images.unsplash.com/photo-1596701062351-8c2c14d1fdd0?w=400&auto=format&fit=crop', retailPrice: 99.90, number: 1, checked: true },
	{ id: 2, goodsId: 2, productId: 102, goodsName: '同仁堂枸杞', goodsSpecifitionNameValue: '500g', listPicUrl: 'https://images.unsplash.com/photo-1509358271058-acd22cc93898?w=400&auto=format&fit=crop', retailPrice: 24.50, number: 2, checked: true }
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

// 省市区数据（parentId 层级）
const regionData = {
	1: [
		{ id: 110000, name: '北京市', type: 1, parentId: 1 },
		{ id: 310000, name: '上海市', type: 1, parentId: 1 },
		{ id: 440000, name: '广东省', type: 1, parentId: 1 },
		{ id: 330000, name: '浙江省', type: 1, parentId: 1 },
		{ id: 320000, name: '江苏省', type: 1, parentId: 1 },
		{ id: 510000, name: '四川省', type: 1, parentId: 1 },
		{ id: 420000, name: '湖北省', type: 1, parentId: 1 }
	],
	110000: [{ id: 110100, name: '北京市', type: 2, parentId: 110000 }],
	310000: [{ id: 310100, name: '上海市', type: 2, parentId: 310000 }],
	440000: [
		{ id: 440100, name: '广州市', type: 2, parentId: 440000 },
		{ id: 440300, name: '深圳市', type: 2, parentId: 440000 }
	],
	330000: [
		{ id: 330100, name: '杭州市', type: 2, parentId: 330000 },
		{ id: 330200, name: '宁波市', type: 2, parentId: 330000 }
	],
	320000: [
		{ id: 320100, name: '南京市', type: 2, parentId: 320000 },
		{ id: 320500, name: '苏州市', type: 2, parentId: 320000 }
	],
	510000: [
		{ id: 510100, name: '成都市', type: 2, parentId: 510000 },
		{ id: 510300, name: '自贡市', type: 2, parentId: 510000 }
	],
	420000: [
		{ id: 420100, name: '武汉市', type: 2, parentId: 420000 },
		{ id: 420200, name: '黄石市', type: 2, parentId: 420000 }
	],
	110100: [
		{ id: 110101, name: '东城区', type: 3, parentId: 110100 },
		{ id: 110102, name: '西城区', type: 3, parentId: 110100 },
		{ id: 110105, name: '朝阳区', type: 3, parentId: 110100 },
		{ id: 110108, name: '海淀区', type: 3, parentId: 110100 }
	],
	310100: [
		{ id: 310101, name: '黄浦区', type: 3, parentId: 310100 },
		{ id: 310104, name: '徐汇区', type: 3, parentId: 310100 },
		{ id: 310110, name: '杨浦区', type: 3, parentId: 310100 }
	],
	440100: [
		{ id: 440103, name: '荔湾区', type: 3, parentId: 440100 },
		{ id: 440104, name: '越秀区', type: 3, parentId: 440100 },
		{ id: 440105, name: '海珠区', type: 3, parentId: 440100 }
	],
	440300: [
		{ id: 440303, name: '罗湖区', type: 3, parentId: 440300 },
		{ id: 440304, name: '福田区', type: 3, parentId: 440300 },
		{ id: 440305, name: '南山区', type: 3, parentId: 440300 }
	],
	330100: [
		{ id: 330102, name: '上城区', type: 3, parentId: 330100 },
		{ id: 330103, name: '下城区', type: 3, parentId: 330100 },
		{ id: 330106, name: '西湖区', type: 3, parentId: 330100 }
	],
	330200: [
		{ id: 330203, name: '海曙区', type: 3, parentId: 330200 },
		{ id: 330205, name: '江北区', type: 3, parentId: 330200 },
		{ id: 330206, name: '北仑区', type: 3, parentId: 330200 }
	],
	320100: [
		{ id: 320102, name: '玄武区', type: 3, parentId: 320100 },
		{ id: 320104, name: '秦淮区', type: 3, parentId: 320100 },
		{ id: 320105, name: '建邺区', type: 3, parentId: 320100 }
	],
	320500: [
		{ id: 320505, name: '虎丘区', type: 3, parentId: 320500 },
		{ id: 320506, name: '吴中区', type: 3, parentId: 320500 },
		{ id: 320507, name: '相城区', type: 3, parentId: 320500 }
	],
	510100: [
		{ id: 510104, name: '锦江区', type: 3, parentId: 510100 },
		{ id: 510105, name: '青羊区', type: 3, parentId: 510100 },
		{ id: 510106, name: '金牛区', type: 3, parentId: 510100 }
	],
	510300: [
		{ id: 510302, name: '自流井区', type: 3, parentId: 510300 },
		{ id: 510303, name: '贡井区', type: 3, parentId: 510300 }
	],
	420100: [
		{ id: 420102, name: '江岸区', type: 3, parentId: 420100 },
		{ id: 420103, name: '江汉区', type: 3, parentId: 420100 },
		{ id: 420111, name: '洪山区', type: 3, parentId: 420100 }
	],
	420200: [
		{ id: 420202, name: '黄石港区', type: 3, parentId: 420200 },
		{ id: 420203, name: '西塞山区', type: 3, parentId: 420200 }
	]
};

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
	'goods/list'(params) { return makeGoodsList(params.page || 1, params.size || 10, params.categoryId, params.isNew, params.isHot); },
	'goods/hot'() { return { bannerInfo: { imgUrl: 'https://images.unsplash.com/photo-1596701062351-8c2c14d1fdd0?w=750&auto=format&fit=crop', name: '热销爆款' } }; },
	'goods/new'() { return { bannerInfo: { imgUrl: 'https://images.unsplash.com/photo-1597481499750-3e6b22637e12?w=750&auto=format&fit=crop', name: '新品推荐' } }; },
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
		const id = Number(params.id || 1);
		const goods = makeGoods(id);
		const name = goods.name;
		const price = goods.retailPrice;
		return {
			info: {
				id, name, goodsBrief: goods.goodsBrief,
				retailPrice: price, counterPrice: (price * 1.5).toFixed(2), sellVolume: 328,
				listPicUrl: goods.listPicUrl,
				goodsDesc: '<p><img src="' + goods.listPicUrl + '" style="width:100%"/></p><p>' + name + '，源自道地产区，品质保证。</p>'
			},
			gallery: [
				{ id: 1, imgUrl: goods.listPicUrl },
				{ id: 2, imgUrl: goods.listPicUrl },
				{ id: 3, imgUrl: goods.listPicUrl }
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
				data: { avatar: 'https://picsum.photos/seed/user1/100/100', nickname: '养生达人', addTime: '2026-06-01', content: '品质很好，推荐购买！', picList: [] }
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
			const goodsId = Number(params.goodsId || 1);
			const goods = makeGoods(goodsId);
			cartData.push({
				id: cartData.length + 100,
				goodsId: goodsId,
				productId: params.productId,
				goodsName: goods.name,
				goodsSpecifitionNameValue: '250g',
				listPicUrl: goods.listPicUrl,
				retailPrice: parseFloat(goods.retailPrice),
				number: params.number || 1,
				checked: true
			});
		}
		return { cartTotal: getCartTotal() };
	},
	'buy/add'(params) {
		// 立即购买：先清空已选，将当前商品加入购物车并选中
		cartData.forEach(c => { c.checked = false; });
		const goodsId = Number(params.goodsId || 1);
		const goods = makeGoods(goodsId);
		const exists = cartData.find(c => c.productId == params.productId);
		if (exists) {
			exists.number = params.number || 1;
			exists.checked = true;
		} else {
			cartData.push({
				id: cartData.length + 200,
				goodsId: goodsId,
				productId: params.productId,
				goodsName: goods.name,
				goodsSpecifitionNameValue: '250g',
				listPicUrl: goods.listPicUrl,
				retailPrice: parseFloat(goods.retailPrice),
				number: params.number || 1,
				checked: true
			});
		}
		return {};
	},
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
	'collect/list'() {
		return [
			{ id: 1, valueId: 1, listPicUrl: makeGoods(1).listPicUrl, name: '东阿阿胶糕', goodsBrief: '补气养血，美容养颜，传统手工熬制', retailPrice: '99.90' },
			{ id: 2, valueId: 3, listPicUrl: makeGoods(3).listPicUrl, name: '长白山人参', goodsBrief: '整枝鲜参，长白山道地直供，元气满满', retailPrice: '199.00' },
			{ id: 3, valueId: 4, listPicUrl: makeGoods(4).listPicUrl, name: '铁皮石斛', goodsBrief: '正宗霍山铁皮石斛，胶质浓郁，养阴清热', retailPrice: '159.50' }
		];
	},

	// 订单
	'order/submit'() {
		// 清空购物车已选商品（模拟提交）
		cartData = cartData.filter(c => !c.checked);
		return { orderInfo: { id: 10001, orderSn: '202607090001' } };
	},
	'order/list'(params) {
		const showType = parseInt(params.showType) || 0;
		const allOrders = [
			{
				id: 10001, orderSn: '202607090001', orderStatusText: '待发货', actualPrice: '199.70',
				handleOption: { pay: false, cancel: true, confirm: false },
				goodsList: [{ id: 1, goodsName: '东阿阿胶糕', number: 1, listPicUrl: makeGoods(1).listPicUrl }]
			},
			{
				id: 10002, orderSn: '202607080002', orderStatusText: '待付款', actualPrice: '89.80',
				handleOption: { pay: 1, cancel: true, confirm: false },
				goodsList: [{ id: 22, goodsName: '江中健胃消食片', number: 2, listPicUrl: makeGoods(22).listPicUrl }]
			},
			{
				id: 10003, orderSn: '202607050003', orderStatusText: '待收货', actualPrice: '159.50',
				handleOption: { pay: false, cancel: false, confirm: 1 },
				goodsList: [{ id: 4, goodsName: '铁皮石斛', number: 1, listPicUrl: makeGoods(4).listPicUrl }]
			},
			{
				id: 10004, orderSn: '202606280004', orderStatusText: '已完成', actualPrice: '299.00',
				handleOption: { pay: false, cancel: false, confirm: false },
				goodsList: [{ id: 3, goodsName: '长白山人参', number: 1, listPicUrl: makeGoods(3).listPicUrl }]
			}
		];
		const filterMap = {
			1: o => o.handleOption.pay,
			2: o => o.orderStatusText === '待发货',
			3: o => o.handleOption.confirm,
			4: o => o.orderStatusText === '已完成',
			5: () => false
		};
		const list = showType === 0 ? allOrders : allOrders.filter(filterMap[showType] || (() => false));
		return { list, page: 1, total: 1 };
	},
	'order/detail'(params) {
		const id = params.orderId || params.id || 10001;
		return {
			orderInfo: {
				id, orderSn: '202607090001',
				addTime: '2026-07-09 10:30:00',
				orderStatusText: '待发货',
				actualPrice: '124.40',
				goodsPrice: '124.40',
				freightPrice: '0.00',
				handleOption: { cancel: true, confirm: false, pay: false },
				consignee: '张三',
				mobile: '13800138000',
				fullRegion: '北京市朝阳区',
				address: '望京SOHO T3 1201'
			},
			orderGoods: [
				{
					id: 1, goodsName: '东阿阿胶糕', number: 1,
					listPicUrl: makeGoods(1).listPicUrl,
					goodsSpecifitionNameValue: '250g', retailPrice: '99.90'
				},
				{
					id: 2, goodsName: '同仁堂枸杞', number: 1,
					listPicUrl: makeGoods(2).listPicUrl,
					goodsSpecifitionNameValue: '500g', retailPrice: '24.50'
				}
			],
			handleOption: { cancel: true, confirm: false, pay: false }
		};
	},
	'order/cancelOrder'() { return '订单已取消'; },
	'order/confirmOrder'() { return '收货确认成功'; },

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
	'coupon/listByGoods'() {
		return [
			{ id: 1, name: '新人专享券', typeMoney: 10, minGoodsAmount: 99, useEndDate: '2026-12-31', couponStatus: 1 }
		];
	},

	// 支付（mock模式下payOrder不调用这个，但保留以防万一）
	'pay/prepay'() {
		return { timeStamp: '1234567890', nonceStr: 'mock', package: 'prepay_id=mock', signType: 'MD5', paySign: 'mock' };
	},
	'pay/query'() { return { orderStatus: 'paid' }; },

	// 地址
	'address/list'() {
		return [{ id: 1, userName: '张三', telNumber: '13800138000', fullRegion: '北京市朝阳区', detailInfo: '望京SOHO T3 1201', isDefault: 1 }];
	},
	'address/detail'(params) {
		return {
			id: params.id || 1,
			userName: '张三', telNumber: '13800138000',
			provinceId: 110000, cityId: 110100, districtId: 110105,
			provinceName: '北京市', cityName: '北京市', districtName: '朝阳区',
			fullRegion: '北京市北京市朝阳区', detailInfo: '望京SOHO T3 1201', isDefault: 1
		};
	},
	'address/save'() { return {}; },
	'address/delete'() { return {}; },

	// 省市区
	'region/list'(params) {
		const parentId = params.parentId || 1;
		return regionData[parentId] || [];
	},

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
		return { id: 1, title: '药食同源养生指南', subtitle: '传统中医智慧', scenePicUrl: IMG, content: '<p>药食同源是中国传统养生理念，源于《黄帝内经》。许多食物既是食品，也是药材，长期食用有益健康。</p>' };
	},
	'topic/related'() {
		return { topicList: [
			{ id: 2, title: '四季养生茶饮', subtitle: '顺应时节', scenePicUrl: IMG, priceInfo: '29.9' }
		]};
	},

	// 评论
	'comment/list'() {
		return { records: [
			{ id: 1, userInfo: { avatar: 'https://picsum.photos/seed/u1/100/100', nickname: '养生达人' }, addTime: '2026-06-01', content: '品质很好，包装精美，收到就迫不及待地开吃了！', picList: [] },
			{ id: 2, userInfo: { avatar: 'https://picsum.photos/seed/u2/100/100', nickname: '健康生活家' }, addTime: '2026-05-20', content: '正品保证，味道纯正，下次还会再买。', picList: [] }
		], total: 2 };
	},
	'comment/count'() { return { allCount: 12 }; },
	'comment/post'() { return {}; },

	// 足迹
	'footprint/list'() {
		return {
			data: [
				[
					{ id: 1, goodsId: 1, addTime: '2026-07-09', listPicUrl: makeGoods(1).listPicUrl, name: '东阿阿胶糕', goodsBrief: '补气养血，美容养颜，传统手工熬制', retailPrice: '99.90' },
					{ id: 2, goodsId: 3, addTime: '2026-07-09', listPicUrl: makeGoods(3).listPicUrl, name: '长白山人参', goodsBrief: '整枝鲜参，长白山道地直供，元气满满', retailPrice: '199.00' }
				],
				[
					{ id: 3, goodsId: 4, addTime: '2026-07-08', listPicUrl: makeGoods(4).listPicUrl, name: '铁皮石斛', goodsBrief: '正宗霍山铁皮石斛，胶质浓郁，养阴清热', retailPrice: '159.50' },
					{ id: 4, goodsId: 2, addTime: '2026-07-08', listPicUrl: makeGoods(2).listPicUrl, name: '同仁堂枸杞', goodsBrief: '宁夏特级免洗枸杞，粒大饱满，甘甜可口', retailPrice: '24.50' }
				]
			],
			totalPages: 1
		};
	},
	'footprint/delete'() { return {}; },

	// 用户
	'user/info'() { return { userInfo: { nickName: '测试用户', avatarUrl: IMG, mobile: '138****8888' } }; },
	'user/smscode'() { return {}; },
	'user/bindMobile'() { return { token: 'mock_token_123' }; },

	// 反馈
	'feedback/save'() { return {}; },

	// 帮助
	'helpissue/typeList'() { return { list: [{ id: 1, name: '商品相关' }, { id: 2, name: '订单相关' }, { id: 3, name: '物流相关' }] }; },
	'helpissue/issueList'() { return { list: [
		{ id: 1, question: '如何退货？', answer: '在订单详情页点击"申请退款"，填写退款原因后提交即可。' },
		{ id: 2, question: '运费怎么算？', answer: '满299元包邮，不足299元收取10元运费。' },
		{ id: 3, question: '如何查询物流？', answer: '在订单详情页可查看物流信息。' }
	] }; },

	// 登录
	'auth/LoginByMa'() { return { token: 'mock_token_123', userInfo: { nickName: '测试用户', avatarUrl: IMG }, userId: 1 }; },
	'auth/login'() { return { token: 'mock_token_123', userInfo: { nickName: '测试用户', avatarUrl: IMG }, userId: 1 }; },
	'auth/register'() { return { token: 'mock_token_123', userInfo: { nickName: '测试用户', avatarUrl: IMG }, userId: 1 }; }
};

function buildCategory(id) {
	const names = { 1: '滋补养生', 2: '茶饮花茶', 3: '零食坚果', 4: '保健食品', 5: '药膳食材' };
	const banners = {
		1: 'https://images.unsplash.com/photo-1596701062351-8c2c14d1fdd0?w=600&auto=format&fit=crop',
		2: 'https://images.unsplash.com/photo-1597481499750-3e6b22637e12?w=600&auto=format&fit=crop',
		3: 'https://images.unsplash.com/photo-1534149711956-f9b7d528f64d?w=600&auto=format&fit=crop',
		4: 'https://images.unsplash.com/photo-1616679911721-eff6eec18fcd?w=600&auto=format&fit=crop',
		5: 'https://images.unsplash.com/photo-1514733670139-4d87a19b179d?w=600&auto=format&fit=crop'
	};
	const name = names[id] || '分类' + id;
	const bannerUrl = banners[id] || 'https://images.unsplash.com/photo-1596701062351-8c2c14d1fdd0?w=600';
	return {
		id, name, frontName: name + '精选好物',
		wapBannerUrl: bannerUrl,
		subCategoryList: [
			{ id: id * 10 + 1, name: name + '-精选', wapBannerUrl: bannerUrl },
			{ id: id * 10 + 2, name: name + '-经典', wapBannerUrl: bannerUrl },
			{ id: id * 10 + 3, name: name + '-新品', wapBannerUrl: bannerUrl }
		]
	};
}

/**
 * 处理 mock 请求
 */
function handleMock(url, params) {
	const handler = mockRoutes[url];
	if (!handler) return null;
	const data = handler(params || {});
	return { code: 0, msg: 'success', data: data };
}

module.exports = { handleMock };
