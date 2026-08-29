<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { EditPen, RefreshRight } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getBannerList,
  getBrandList,
  getChannelList,
  getContentRollbackList,
  getTopicList,
  rollbackContentOperation,
  updateBanner,
  updateBrand,
  updateChannel,
  updateTopic
} from "@/api/content";
import {
  getProductDetail,
  getProductPage,
  getProductRollbackList,
  rollbackProductOperation,
  saveProduct
} from "@/api/product";
import { getCategoryList } from "@/api/category";
import { getSkuList } from "@/api/sku";
import MaterialImagePicker from "@/components/MaterialImagePicker/index.vue";
import LinkSelector from "@/components/LinkSelector/index.vue";
import type {
  AdminOperationSnapshotItem,
  Category,
  ContentBanner,
  ContentBrand,
  ContentChannel,
  ContentTopic,
  ProductSku,
  ProductSpu
} from "@/api/types";
import { hasAnyPerms } from "@/utils/auth";
import {
  clearPreviewDraft,
  getAllPreviewDrafts,
  getPreviewCommitToken,
  getPreviewDraft,
  notifyPreviewDataCommitted,
  setPreviewDraft,
  type PreviewCenterScene,
  type PreviewDraftEnvelope
} from "@/utils/preview-center";

defineOptions({ name: "ContentPreviewCenter" });

interface ProductDraftPayload {
  id?: number;
  name: string;
  type: number;
  categoryId?: number;
  categoryName?: string;
  introduction?: string;
  description?: string;
  picUrl?: string;
  sliderPicUrls?: string[];
  detailImageUrls?: string[];
  price?: number;
  marketPrice?: number;
  stock?: number;
  status?: number;
  keywordTags?: string[];
  skuSummary?: Array<{
    label: string;
    skuCode?: string;
    price?: number;
    marketPrice?: number;
    stock?: number;
    picUrl?: string;
  }>;
}

interface PreviewEditableSku extends ProductSku {
  label: string;
  priceYuan: number;
  marketPriceYuan: number;
}

type PreviewListItem<T> = T & { __draft?: boolean };
type VisualEditorScene = "banner" | "channel" | "brand" | "topic" | "product";

interface ProductEditorFormState {
  id?: number;
  name: string;
  type: number;
  categoryId?: number;
  introduction: string;
  description: string;
  picUrl: string;
  sliderPicUrls: string[];
  detailImageUrls: string[];
  price: number;
  marketPrice: number;
  stock: number;
  sort: number;
  status: number;
}

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const rollbackLoading = ref(false);
const visualEditorLoading = ref(false);
const visualSaving = ref(false);
const activeTab = ref((route.query.scene as string) || "home");
const products = ref<ProductSpu[]>([]);
const categories = ref<Category[]>([]);
const banners = ref<ContentBanner[]>([]);
const channels = ref<ContentChannel[]>([]);
const brands = ref<ContentBrand[]>([]);
const topics = ref<ContentTopic[]>([]);
const selectedProductId = ref<number>();
const drafts = ref<PreviewDraftEnvelope[]>([]);
const productRollbacks = ref<AdminOperationSnapshotItem[]>([]);
const contentRollbacks = ref<AdminOperationSnapshotItem[]>([]);
const canManageProduct = hasAnyPerms(["product:manage"]);
const canManageContent = hasAnyPerms(["content:manage"]);
const visualEditorScene = ref<VisualEditorScene | "">("");
const visualEditorEntityId = ref<number>();
const editorLinkKey = ref(0);
const productKeywordTags = ref<string[]>([]);
const productEditorSkus = ref<PreviewEditableSku[]>([]);
const productOriginalSkuStocks = ref(new Map<number, number>());
const productOriginalSingleStock = ref(0);
const productStockAdjustReason = ref("");

const typeOptions = [
  { label: "实物商品", value: 1 },
  { label: "虚拟商品", value: 2 }
] ;
const statusOptions = [
  { label: "上架", value: 1 },
  { label: "下架", value: 0 }
] ;
const keywordOptions = [
  "药食同源",
  "滋补养生",
  "花草茶饮",
  "轻养零食",
  "节气调理",
  "送礼礼盒",
  "家庭常备",
  "办公室冲泡",
  "即食便携",
  "无糖清润"
];

const bannerEditor = reactive({
  id: undefined as number | undefined,
  title: "",
  picUrl: "",
  url: "",
  sort: 0,
  status: 1
});

const channelEditor = reactive({
  id: undefined as number | undefined,
  name: "",
  iconUrl: "",
  url: "",
  sort: 0,
  status: 1
});

const brandEditor = reactive({
  id: undefined as number | undefined,
  name: "",
  picUrl: "",
  floorPriceYuan: 0,
  sort: 0,
  status: 1
});

const topicEditor = reactive({
  id: undefined as number | undefined,
  title: "",
  subtitle: "",
  picUrl: "",
  priceInfo: "",
  sort: 0,
  status: 1
});

const productEditor = reactive<ProductEditorFormState>({
  id: undefined,
  name: "",
  type: 1,
  categoryId: undefined,
  introduction: "",
  description: "",
  picUrl: "",
  sliderPicUrls: [],
  detailImageUrls: [],
  price: 0,
  marketPrice: 0,
  stock: 0,
  sort: 0,
  status: 1
});

let pollTimer: number | null = null;
let lastCommitToken = "";

function formatYuanFromFen(value?: number) {
  if (value == null) return "￥0.00";
  return `￥${(value / 100).toFixed(2)}`;
}

function formatYuan(value?: number) {
  if (value == null) return "￥0.00";
  return `￥${value.toFixed(2)}`;
}

function normalizeTopicPriceText(value?: string) {
  if (!value) return "39元起";
  return /元|￥/.test(value) ? value : `${value}元起`;
}

function parseImageList(value?: string) {
  if (!value) return [];
  try {
    const parsed = JSON.parse(value);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

const IMAGE_TAG = /<img\b[^>]*\bsrc\s*=\s*(['"])(.*?)\1[^>]*>/gi;

function parseSliderPics(raw: string) {
  if (!raw) return [];
  try {
    const parsed = JSON.parse(raw);
    if (Array.isArray(parsed)) return parsed.filter(Boolean);
  } catch {
    // ignore malformed old data
  }
  return raw.split(",").map(item => item.trim()).filter(Boolean);
}

function extractImageUrlsFromDescription(description: string) {
  const urls: string[] = [];
  let match: RegExpExecArray | null;
  IMAGE_TAG.lastIndex = 0;
  while ((match = IMAGE_TAG.exec(description)) !== null) {
    if (match[2]) urls.push(match[2]);
  }
  return urls;
}

function stripImageTags(description: string) {
  IMAGE_TAG.lastIndex = 0;
  return description.replace(IMAGE_TAG, "").trim();
}

function escapeHtmlAttr(value: string) {
  return value
    .replace(/&/g, "&amp;")
    .replace(/"/g, "&quot;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");
}

function buildDescriptionHtml(text: string, imageUrls: string[]) {
  const content = text.trim();
  const images = imageUrls
    .filter(Boolean)
    .map(url => `<p><img src="${escapeHtmlAttr(url)}" /></p>`)
    .join("\n");
  return [content, images].filter(Boolean).join("\n");
}

function parseKeywordTags(raw: string) {
  if (!raw) return [];
  return raw
    .split(/[\s,，|/]+/)
    .map(item => item.trim())
    .filter(Boolean);
}

function parseSkuLabel(properties?: string) {
  try {
    const props = JSON.parse(properties || "[]");
    const labels = Array.isArray(props)
      ? props.map((item: any) => item?.valueName || item?.name).filter(Boolean)
      : [];
    return labels.length > 0 ? labels.join(" / ") : "默认规格";
  } catch {
    return "默认规格";
  }
}

function buildEditableSkus(skus: ProductSku[]) {
  return skus.map(sku => ({
    ...sku,
    label: parseSkuLabel(sku.properties),
    priceYuan: ((sku.price ?? 0) / 100),
    marketPriceYuan: ((sku.marketPrice ?? 0) / 100)
  }));
}

async function fetchData() {
  loading.value = true;
  try {
    const [bannerList, channelList, brandList, topicList, productPage, categoryList] = await Promise.all([
      getBannerList() as Promise<ContentBanner[]>,
      getChannelList() as Promise<ContentChannel[]>,
      getBrandList() as Promise<ContentBrand[]>,
      getTopicList() as Promise<ContentTopic[]>,
      getProductPage({ pageNo: 1, pageSize: 200, status: 1 }) as Promise<any>,
      getCategoryList() as Promise<Category[]>
    ]);
    banners.value = bannerList || [];
    channels.value = channelList || [];
    brands.value = brandList || [];
    topics.value = topicList || [];
    products.value = productPage?.list || [];
    categories.value = categoryList || [];
    if (!selectedProductId.value && products.value.length > 0) {
      selectedProductId.value = products.value[0].id;
    }
  } finally {
    loading.value = false;
  }
}

async function fetchRollbackData() {
  rollbackLoading.value = true;
  try {
    const tasks: Array<Promise<void>> = [];
    if (canManageProduct) {
      tasks.push(
        getProductRollbackList().then(list => {
          productRollbacks.value = list || [];
        })
      );
    } else {
      productRollbacks.value = [];
    }
    if (canManageContent) {
      tasks.push(
        getContentRollbackList().then(list => {
          contentRollbacks.value = list || [];
        })
      );
    } else {
      contentRollbacks.value = [];
    }
    await Promise.all(tasks);
  } finally {
    rollbackLoading.value = false;
  }
}

function refreshDrafts() {
  drafts.value = getAllPreviewDrafts();
}

async function syncPreviewCenterState() {
  refreshDrafts();
  const commitToken = getPreviewCommitToken();
  if (!commitToken || commitToken === lastCommitToken) return;
  lastCommitToken = commitToken;
  await Promise.all([fetchData(), fetchRollbackData()]);
  if (visualEditorScene.value && visualEditorEntityId.value) {
    await loadVisualEditor(visualEditorScene.value, visualEditorEntityId.value, false);
  }
}

function handleStorageChange() {
  void syncPreviewCenterState();
}

function startDraftPolling() {
  void syncPreviewCenterState();
  pollTimer = window.setInterval(() => {
    void syncPreviewCenterState();
  }, 1200);
  window.addEventListener("storage", handleStorageChange);
}

function stopDraftPolling() {
  if (pollTimer != null) {
    window.clearInterval(pollTimer);
    pollTimer = null;
  }
  window.removeEventListener("storage", handleStorageChange);
}

function mergeDraftItem<T extends { id?: number }>(
  list: T[],
  scene: PreviewCenterScene,
  factory: (payload: any) => T
) {
  const draft = getPreviewDraft<any>(scene);
  const baseList = list.map(item => ({ ...item })) as PreviewListItem<T>[];
  if (!draft) return baseList;
  const draftItem = { ...factory(draft.payload), __draft: true } as PreviewListItem<T>;
  if (draft.payload?.id) {
    const index = baseList.findIndex(item => item.id === draft.payload.id);
    if (index >= 0) {
      baseList[index] = draftItem;
      return baseList;
    }
  }
  return [draftItem, ...baseList];
}

const mergedBanners = computed(() =>
  mergeDraftItem(
    banners.value.filter(item => item.status === 1),
    "banner",
    payload => ({
      id: payload.id,
      title: payload.title || "未命名 Banner",
      picUrl: payload.picUrl || "",
      url: payload.url || "",
      sort: payload.sort || 0,
      status: payload.status ?? 1
    })
  )
);

const mergedChannels = computed(() =>
  mergeDraftItem(
    channels.value.filter(item => item.status === 1),
    "channel",
    payload => ({
      id: payload.id,
      name: payload.name || "未命名频道",
      iconUrl: payload.iconUrl || "",
      url: payload.url || "",
      sort: payload.sort || 0,
      status: payload.status ?? 1
    })
  )
);

const mergedBrands = computed(() =>
  mergeDraftItem(
    brands.value.filter(item => item.status === 1),
    "brand",
    payload => ({
      id: payload.id,
      name: payload.name || "未命名品牌",
      picUrl: payload.picUrl || "",
      floorPrice: Math.round((payload.floorPriceYuan || 0) * 100),
      sort: payload.sort || 0,
      status: payload.status ?? 1
    })
  )
);

const mergedTopics = computed(() =>
  mergeDraftItem(
    topics.value.filter(item => item.status === 1),
    "topic",
    payload => ({
      id: payload.id,
      title: payload.title || "未命名专题",
      subtitle: payload.subtitle || "",
      picUrl: payload.picUrl || "",
      priceInfo: payload.priceInfo || "",
      sort: payload.sort || 0,
      status: payload.status ?? 1
    })
  )
);

const productDraft = computed(() => getPreviewDraft<ProductDraftPayload>("product"));

const mergedHomeProducts = computed(() => {
  const baseList = products.value.map(item => ({ ...item })) as PreviewListItem<ProductSpu>[];
  const draft = productDraft.value;
  if (!draft) return baseList;
  const draftItem: PreviewListItem<ProductSpu> = {
    id: draft.payload.id,
    name: draft.payload.name || "未命名商品",
    categoryId: draft.payload.categoryId || 0,
    introduction: draft.payload.introduction || "",
    picUrl: draft.payload.picUrl || draft.payload.sliderPicUrls?.[0] || "",
    type: draft.payload.type ?? 1,
    price: Math.round((draft.payload.price || 0) * 100),
    marketPrice: Math.round((draft.payload.marketPrice || 0) * 100),
    stock: draft.payload.stock || 0,
    status: draft.payload.status ?? 1,
    __draft: true
  };
  if (draft.payload.id) {
    const index = baseList.findIndex(item => item.id === draft.payload.id);
    if (index >= 0) {
      baseList[index] = { ...baseList[index], ...draftItem };
      return baseList;
    }
  }
  return [draftItem, ...baseList];
});

const selectedProduct = computed(
  () => products.value.find(item => item.id === selectedProductId.value) || products.value[0]
);

const categoryNameMap = computed(() => {
  const map = new Map<number, string>();
  categories.value.forEach(item => {
    if (item.id != null) map.set(item.id, item.name);
  });
  return map;
});

const previewProduct = computed(() => {
  const draft = productDraft.value;
  if (draft) {
    return {
      source: "draft",
      id: draft.payload.id,
      name: draft.payload.name || "未命名商品",
      categoryName:
        draft.payload.categoryName ||
        (draft.payload.categoryId ? categoryNameMap.value.get(draft.payload.categoryId) : "") ||
        "未选择分类",
      introduction: draft.payload.introduction || "",
      description: draft.payload.description || "",
      picUrl: draft.payload.picUrl || draft.payload.sliderPicUrls?.[0] || "",
      sliderPicUrls: draft.payload.sliderPicUrls || [],
      type: draft.payload.type ?? 1,
      status: draft.payload.status ?? 1,
      price: draft.payload.price ?? 0,
      marketPrice: draft.payload.marketPrice ?? 0,
      stock: draft.payload.stock ?? 0,
      keywordTags: draft.payload.keywordTags || [],
      skuSummary: draft.payload.skuSummary || []
    };
  }
  const product = selectedProduct.value;
  return {
    source: "saved",
    id: product?.id,
    name: product?.name || "暂无商品",
    categoryName: product?.categoryId ? categoryNameMap.value.get(product.categoryId) || "未分类" : "未分类",
    introduction: product?.introduction || "",
    description: product?.description || "",
    picUrl: product?.picUrl || "",
    sliderPicUrls: parseImageList(product?.sliderPicUrls),
    type: product?.type ?? 1,
    status: product?.status ?? 1,
    price: product?.price ?? 0,
    marketPrice: product?.marketPrice ?? 0,
    stock: product?.stock ?? 0,
    keywordTags: product?.keyword ? product.keyword.split(/\s+/).filter(Boolean) : [],
    skuSummary: []
  };
});

const previewProductPriceText = computed(() => {
  const draftSkus = previewProduct.value.skuSummary.filter(item => (item.price || 0) > 0);
  if (draftSkus.length > 0) {
    const prices = draftSkus.map(item => item.price || 0);
    const min = Math.min(...prices);
    const max = Math.max(...prices);
    return min === max ? formatYuan(min) : `${formatYuan(min)} 起`;
  }
  return formatYuanFromFen(previewProduct.value.price);
});

const previewProductMarketPriceText = computed(() => {
  const draftSkus = previewProduct.value.skuSummary.filter(item => (item.marketPrice || 0) > 0);
  if (draftSkus.length > 0) {
    return formatYuan(Math.min(...draftSkus.map(item => item.marketPrice || 0)));
  }
  return previewProduct.value.marketPrice ? formatYuanFromFen(previewProduct.value.marketPrice) : "";
});

const previewProductStock = computed(() => {
  const draftSkus = previewProduct.value.skuSummary;
  if (draftSkus.length > 0) {
    let totalStock = 0;
    draftSkus.forEach(sku => {
      totalStock += Number(sku.stock || 0);
    });
    return totalStock;
  }
  return previewProduct.value.stock || 0;
});

const editorCategoryName = computed(
  () => categories.value.find(item => item.id === productEditor.categoryId)?.name || "未选择分类"
);

const editorPreviewImages = computed(() => {
  const sliderImages = productEditor.sliderPicUrls.filter(Boolean);
  if (sliderImages.length > 0) return sliderImages;
  if (productEditor.picUrl) return [productEditor.picUrl];
  return [];
});

const editorPreviewSkuSummary = computed(() => {
  if (productEditorSkus.value.length > 0) {
    return productEditorSkus.value.map(item => ({
      label: item.label,
      skuCode: item.skuCode,
      price: item.priceYuan,
      marketPrice: item.marketPriceYuan,
      stock: item.stock,
      picUrl: item.picUrl
    }));
  }
  return [{
    label: "默认规格",
    skuCode: "",
    price: productEditor.price,
    marketPrice: productEditor.marketPrice,
    stock: productEditor.stock,
    picUrl: productEditor.picUrl
  }];
});

const productEditorStockChanged = computed(() => {
  if (productEditorSkus.value.length > 0) {
    const currentIds = new Set<number>();
    for (const sku of productEditorSkus.value) {
      if (!sku.id || !productOriginalSkuStocks.value.has(sku.id)) return true;
      currentIds.add(sku.id);
      if ((productOriginalSkuStocks.value.get(sku.id) ?? 0) !== (sku.stock ?? 0)) return true;
    }
    return currentIds.size !== productOriginalSkuStocks.value.size;
  }
  return (productEditor.stock || 0) !== productOriginalSingleStock.value;
});

const editorPanelTitle = computed(() => {
  switch (visualEditorScene.value) {
    case "banner":
      return "Banner 可视化编辑";
    case "channel":
      return "频道可视化编辑";
    case "brand":
      return "品牌可视化编辑";
    case "topic":
      return "专题可视化编辑";
    case "product":
      return "商品可视化编辑";
    default:
      return "点选编辑";
  }
});

const editorSelectionLabel = computed(() => {
  switch (visualEditorScene.value) {
    case "banner":
      return bannerEditor.title || "未命名 Banner";
    case "channel":
      return channelEditor.name || "未命名频道";
    case "brand":
      return brandEditor.name || "未命名品牌";
    case "topic":
      return topicEditor.title || "未命名专题";
    case "product":
      return productEditor.name || "未命名商品";
    default:
      return "";
  }
});

function clearSceneDraft(scene: PreviewCenterScene) {
  clearPreviewDraft(scene);
  refreshDrafts();
}

function currentEditorPreviewScene(): PreviewCenterScene | null {
  switch (visualEditorScene.value) {
    case "banner":
    case "channel":
    case "brand":
    case "topic":
    case "product":
      return visualEditorScene.value;
    default:
      return null;
  }
}

function formatSceneLabel(sceneCode: string) {
  switch (sceneCode) {
    case "PRODUCT":
      return "商品";
    case "BANNER":
      return "Banner";
    case "CHANNEL":
      return "频道";
    case "BRAND":
      return "品牌";
    case "TOPIC":
      return "专题";
    case "TOPIC_PRODUCTS":
      return "专题商品";
    default:
      return sceneCode;
  }
}

function sceneDraftCode(sceneCode: string): PreviewCenterScene | undefined {
  switch (sceneCode) {
    case "PRODUCT":
      return "product";
    case "BANNER":
      return "banner";
    case "CHANNEL":
      return "channel";
    case "BRAND":
      return "brand";
    case "TOPIC":
    case "TOPIC_PRODUCTS":
      return "topic";
    default:
      return undefined;
  }
}

function isEditorSelected(scene: VisualEditorScene, id?: number) {
  return visualEditorScene.value === scene && visualEditorEntityId.value === id;
}

function prepareBannerEditor(item: ContentBanner | PreviewListItem<ContentBanner>) {
  bannerEditor.id = item.id;
  bannerEditor.title = item.title;
  bannerEditor.picUrl = item.picUrl || "";
  bannerEditor.url = item.url || "";
  bannerEditor.sort = item.sort ?? 0;
  bannerEditor.status = item.status ?? 1;
  editorLinkKey.value++;
}

function prepareChannelEditor(item: ContentChannel | PreviewListItem<ContentChannel>) {
  channelEditor.id = item.id;
  channelEditor.name = item.name;
  channelEditor.iconUrl = item.iconUrl || "";
  channelEditor.url = item.url || "";
  channelEditor.sort = item.sort ?? 0;
  channelEditor.status = item.status ?? 1;
  editorLinkKey.value++;
}

function prepareBrandEditor(item: ContentBrand | PreviewListItem<ContentBrand>) {
  brandEditor.id = item.id;
  brandEditor.name = item.name;
  brandEditor.picUrl = item.picUrl || "";
  brandEditor.floorPriceYuan = item.floorPrice != null ? item.floorPrice / 100 : 0;
  brandEditor.sort = item.sort ?? 0;
  brandEditor.status = item.status ?? 1;
}

function prepareTopicEditor(item: ContentTopic | PreviewListItem<ContentTopic>) {
  topicEditor.id = item.id;
  topicEditor.title = item.title;
  topicEditor.subtitle = item.subtitle || "";
  topicEditor.picUrl = item.picUrl || "";
  topicEditor.priceInfo = item.priceInfo || "";
  topicEditor.sort = item.sort ?? 0;
  topicEditor.status = item.status ?? 1;
}

function prepareEmptyProductEditor() {
  productEditor.id = undefined;
  productEditor.name = "";
  productEditor.type = 1;
  productEditor.categoryId = undefined;
  productEditor.introduction = "";
  productEditor.description = "";
  productEditor.picUrl = "";
  productEditor.sliderPicUrls = [];
  productEditor.detailImageUrls = [];
  productEditor.price = 0;
  productEditor.marketPrice = 0;
  productEditor.stock = 0;
  productEditor.sort = 0;
  productEditor.status = 1;
  productKeywordTags.value = [];
  productEditorSkus.value = [];
  productOriginalSkuStocks.value = new Map();
  productOriginalSingleStock.value = 0;
  productStockAdjustReason.value = "";
}

async function loadProductEditor(productId: number) {
  prepareEmptyProductEditor();
  const currentDraft = productDraft.value;
  if (currentDraft?.payload?.id && currentDraft.payload.id === productId) {
    productEditor.id = currentDraft.payload.id;
    productEditor.name = currentDraft.payload.name || "";
    productEditor.type = currentDraft.payload.type ?? 1;
    productEditor.categoryId = currentDraft.payload.categoryId;
    productEditor.introduction = currentDraft.payload.introduction || "";
    productEditor.description = currentDraft.payload.description || "";
    productEditor.picUrl = currentDraft.payload.picUrl || "";
    productEditor.sliderPicUrls = currentDraft.payload.sliderPicUrls || [];
    productEditor.detailImageUrls = currentDraft.payload.detailImageUrls || [];
    productEditor.price = currentDraft.payload.price || 0;
    productEditor.marketPrice = currentDraft.payload.marketPrice || 0;
    productEditor.stock = currentDraft.payload.stock || 0;
    productEditor.status = currentDraft.payload.status ?? 1;
    productKeywordTags.value = currentDraft.payload.keywordTags || [];
    productEditorSkus.value = (currentDraft.payload.skuSummary || []).map((item, index) => ({
      id: index + 1,
      spuId: currentDraft.payload.id || 0,
      skuCode: item.skuCode || "",
      properties: "[]",
      label: item.label,
      priceYuan: item.price || 0,
      marketPriceYuan: item.marketPrice || 0,
      stock: item.stock || 0,
      picUrl: item.picUrl || currentDraft.payload.picUrl || ""
    }));
    productOriginalSingleStock.value = productEditor.stock || 0;
    return;
  }

  const [detail, skus] = await Promise.all([
    getProductDetail(productId) as Promise<ProductSpu>,
    getSkuList(productId) as Promise<ProductSku[]>
  ]);
  productEditor.id = detail.id;
  productEditor.name = detail.name;
  productEditor.type = detail.type ?? 1;
  productEditor.categoryId = detail.categoryId;
  productEditor.introduction = detail.introduction || "";
  productEditor.description = stripImageTags(detail.description || "");
  productEditor.picUrl = detail.picUrl || "";
  productEditor.sliderPicUrls = detail.sliderPicUrls ? parseSliderPics(detail.sliderPicUrls) : [];
  productEditor.detailImageUrls = extractImageUrlsFromDescription(detail.description || "");
  productEditor.price = (detail.price ?? 0) / 100;
  productEditor.marketPrice = (detail.marketPrice ?? 0) / 100;
  productEditor.stock = detail.stock ?? 0;
  productEditor.sort = detail.sort ?? 0;
  productEditor.status = detail.status ?? 1;
  productKeywordTags.value = parseKeywordTags(detail.keyword || "");
  productEditorSkus.value = buildEditableSkus(skus);
  productOriginalSkuStocks.value = new Map(
    productEditorSkus.value.filter(item => !!item.id).map(item => [item.id!, item.stock ?? 0])
  );
  productOriginalSingleStock.value = productEditor.stock ?? 0;
  productStockAdjustReason.value = "";
}

async function loadVisualEditor(scene: VisualEditorScene, entityId: number, switchScene = true) {
  if (switchScene) {
    const previousScene = currentEditorPreviewScene();
    if (previousScene && previousScene !== scene) {
      clearPreviewDraft(previousScene);
    }
  }
  visualEditorLoading.value = true;
  try {
    visualEditorScene.value = scene;
    visualEditorEntityId.value = entityId;
    if (scene === "product") {
      selectedProductId.value = entityId;
      await loadProductEditor(entityId);
      return;
    }
    if (scene === "banner") {
      const item = mergedBanners.value.find(entry => entry.id === entityId);
      if (!item) return;
      prepareBannerEditor(item);
      return;
    }
    if (scene === "channel") {
      const item = mergedChannels.value.find(entry => entry.id === entityId);
      if (!item) return;
      prepareChannelEditor(item);
      return;
    }
    if (scene === "brand") {
      const item = mergedBrands.value.find(entry => entry.id === entityId);
      if (!item) return;
      prepareBrandEditor(item);
      return;
    }
    if (scene === "topic") {
      const item = mergedTopics.value.find(entry => entry.id === entityId);
      if (!item) return;
      prepareTopicEditor(item);
    }
  } finally {
    visualEditorLoading.value = false;
  }
}

function openVisualEditor(scene: VisualEditorScene, entityId?: number, targetTab?: string) {
  if (!entityId) return;
  if (targetTab) activeTab.value = targetTab;
  void loadVisualEditor(scene, entityId, true);
}

function closeVisualEditor() {
  const previewScene = currentEditorPreviewScene();
  if (previewScene) {
    clearPreviewDraft(previewScene);
  }
  refreshDrafts();
  visualEditorScene.value = "";
  visualEditorEntityId.value = undefined;
}

async function resetVisualEditor() {
  if (!visualEditorScene.value || !visualEditorEntityId.value) return;
  const previewScene = currentEditorPreviewScene();
  if (previewScene) {
    clearPreviewDraft(previewScene);
    refreshDrafts();
  }
  await loadVisualEditor(visualEditorScene.value, visualEditorEntityId.value, false);
  ElMessage.success("已恢复为当前正式数据");
}

function buildProductSavePayload() {
  const hasSkuRows = productEditorSkus.value.length > 0;
  const prices = hasSkuRows
    ? productEditorSkus.value.map(item => item.priceYuan).filter(value => value > 0)
    : [];
  const marketPrices = hasSkuRows
    ? productEditorSkus.value.map(item => item.marketPriceYuan).filter(value => value > 0)
    : [];
  const totalStock = hasSkuRows
    ? productEditorSkus.value.reduce((sum, item) => sum + Number(item.stock || 0), 0)
    : productEditor.stock;

  const spuPayload: ProductSpu = {
    id: productEditor.id,
    name: productEditor.name.trim(),
    type: productEditor.type,
    categoryId: productEditor.categoryId!,
    keyword: productKeywordTags.value.map(item => item.trim()).filter(Boolean).join(" "),
    introduction: productEditor.introduction.trim(),
    description: buildDescriptionHtml(productEditor.description, productEditor.detailImageUrls),
    picUrl: productEditor.picUrl,
    sliderPicUrls: productEditor.sliderPicUrls.filter(Boolean).length > 0
      ? JSON.stringify(productEditor.sliderPicUrls.filter(Boolean))
      : "",
    price: Math.round((prices.length > 0 ? Math.min(...prices) : productEditor.price) * 100),
    marketPrice: Math.round((marketPrices.length > 0 ? Math.min(...marketPrices) : productEditor.marketPrice) * 100),
    stock: totalStock,
    sort: productEditor.sort,
    status: productEditor.status
  };

  const skuPayload = productEditorSkus.value.map(item => ({
    id: item.id,
    spuId: item.spuId,
    skuCode: item.skuCode,
    properties: item.properties,
    price: Math.round(item.priceYuan * 100),
    marketPrice: Math.round(item.marketPriceYuan * 100),
    stock: item.stock,
    picUrl: item.picUrl,
    weight: item.weight,
    volume: item.volume
  })) as ProductSku[];

  return { spuPayload, skuPayload };
}

async function handleVisualSave() {
  if (!visualEditorScene.value || !visualEditorEntityId.value) return;
  visualSaving.value = true;
  try {
    if (visualEditorScene.value === "banner") {
      if (!bannerEditor.title.trim()) {
        ElMessage.warning("请输入 Banner 标题");
        return;
      }
      if (!bannerEditor.picUrl.trim()) {
        ElMessage.warning("请选择 Banner 图片");
        return;
      }
      await updateBanner({
        id: bannerEditor.id,
        title: bannerEditor.title.trim(),
        picUrl: bannerEditor.picUrl.trim(),
        url: bannerEditor.url.trim(),
        sort: bannerEditor.sort,
        status: bannerEditor.status
      });
      notifyPreviewDataCommitted("banner");
    }

    if (visualEditorScene.value === "channel") {
      if (!channelEditor.name.trim()) {
        ElMessage.warning("请输入频道名称");
        return;
      }
      await updateChannel({
        id: channelEditor.id,
        name: channelEditor.name.trim(),
        iconUrl: channelEditor.iconUrl.trim(),
        url: channelEditor.url.trim(),
        sort: channelEditor.sort,
        status: channelEditor.status
      });
      notifyPreviewDataCommitted("channel");
    }

    if (visualEditorScene.value === "brand") {
      if (!brandEditor.name.trim()) {
        ElMessage.warning("请输入品牌名称");
        return;
      }
      await updateBrand({
        id: brandEditor.id,
        name: brandEditor.name.trim(),
        picUrl: brandEditor.picUrl.trim(),
        floorPrice: Math.round(brandEditor.floorPriceYuan * 100),
        sort: brandEditor.sort,
        status: brandEditor.status
      });
      notifyPreviewDataCommitted("brand");
    }

    if (visualEditorScene.value === "topic") {
      if (!topicEditor.title.trim()) {
        ElMessage.warning("请输入专题标题");
        return;
      }
      await updateTopic({
        id: topicEditor.id,
        title: topicEditor.title.trim(),
        subtitle: topicEditor.subtitle.trim(),
        picUrl: topicEditor.picUrl.trim(),
        priceInfo: topicEditor.priceInfo.trim(),
        sort: topicEditor.sort,
        status: topicEditor.status
      });
      notifyPreviewDataCommitted("topic");
    }

    if (visualEditorScene.value === "product") {
      if (!productEditor.name.trim()) {
        ElMessage.warning("请输入商品名称");
        return;
      }
      if (!productEditor.categoryId) {
        ElMessage.warning("请选择商品分类");
        return;
      }
      if (productEditorStockChanged.value) {
        const reason = productStockAdjustReason.value.trim();
        if (reason.length < 4 || reason.length > 200) {
          ElMessage.warning("库存发生变化，请填写 4 至 200 个字符的调整原因");
          return;
        }
      }
      const { spuPayload, skuPayload } = buildProductSavePayload();
      const savedId = await saveProduct(spuPayload, skuPayload, productStockAdjustReason.value.trim());
      selectedProductId.value = savedId;
      notifyPreviewDataCommitted("product");
    }

    const previewScene = currentEditorPreviewScene();
    if (previewScene) {
      clearPreviewDraft(previewScene);
      refreshDrafts();
    }
    await Promise.all([fetchData(), fetchRollbackData()]);
    await loadVisualEditor(visualEditorScene.value, visualEditorEntityId.value, false);
    ElMessage.success("已保存并同步到正式预览");
  } finally {
    visualSaving.value = false;
  }
}

function openFullEditor() {
  if (!visualEditorScene.value || !visualEditorEntityId.value) return;
  if (visualEditorScene.value === "product") {
    const target = router.resolve({ path: `/product/spu-form/${visualEditorEntityId.value}` });
    window.open(target.href, "_blank");
    return;
  }
  const pathMap: Record<Exclude<VisualEditorScene, "product">, string> = {
    banner: "/content/banner",
    channel: "/content/channel",
    brand: "/content/brand",
    topic: "/content/topic"
  };
  const target = router.resolve({ path: pathMap[visualEditorScene.value] });
  window.open(target.href, "_blank");
}

async function handleRollback(scope: "product" | "content", row: AdminOperationSnapshotItem) {
  const sceneLabel = formatSceneLabel(row.sceneCode);
  await ElMessageBox.confirm(
    `将回退「${sceneLabel} / ${row.entityName || "未命名对象"}」最近一次操作，恢复到修改前状态。是否继续？`,
    "确认一键回退",
    {
      type: "warning",
      confirmButtonText: "确认回退",
      cancelButtonText: "取消"
    }
  );
  rollbackLoading.value = true;
  try {
    if (scope === "product") {
      await rollbackProductOperation(row.id);
    } else {
      await rollbackContentOperation(row.id);
    }
    const draftScene = sceneDraftCode(row.sceneCode);
    if (draftScene) {
      clearPreviewDraft(draftScene);
    }
    refreshDrafts();
    await Promise.all([fetchData(), fetchRollbackData()]);
    if (visualEditorScene.value && visualEditorEntityId.value) {
      await loadVisualEditor(visualEditorScene.value, visualEditorEntityId.value, false);
    }
    ElMessage.success("已回退到最近一次修改前状态");
  } finally {
    rollbackLoading.value = false;
  }
}

watch(
  () => [
    visualEditorScene.value,
    bannerEditor.id,
    bannerEditor.title,
    bannerEditor.picUrl,
    bannerEditor.url,
    bannerEditor.sort,
    bannerEditor.status
  ],
  () => {
    if (visualEditorScene.value !== "banner" || !bannerEditor.id) return;
    setPreviewDraft("banner", {
      id: bannerEditor.id,
      title: bannerEditor.title,
      picUrl: bannerEditor.picUrl,
      url: bannerEditor.url,
      sort: bannerEditor.sort,
      status: bannerEditor.status
    });
    refreshDrafts();
  }
);

watch(
  () => [
    visualEditorScene.value,
    channelEditor.id,
    channelEditor.name,
    channelEditor.iconUrl,
    channelEditor.url,
    channelEditor.sort,
    channelEditor.status
  ],
  () => {
    if (visualEditorScene.value !== "channel" || !channelEditor.id) return;
    setPreviewDraft("channel", {
      id: channelEditor.id,
      name: channelEditor.name,
      iconUrl: channelEditor.iconUrl,
      url: channelEditor.url,
      sort: channelEditor.sort,
      status: channelEditor.status
    });
    refreshDrafts();
  }
);

watch(
  () => [
    visualEditorScene.value,
    brandEditor.id,
    brandEditor.name,
    brandEditor.picUrl,
    brandEditor.floorPriceYuan,
    brandEditor.sort,
    brandEditor.status
  ],
  () => {
    if (visualEditorScene.value !== "brand" || !brandEditor.id) return;
    setPreviewDraft("brand", {
      id: brandEditor.id,
      name: brandEditor.name,
      picUrl: brandEditor.picUrl,
      floorPriceYuan: brandEditor.floorPriceYuan,
      sort: brandEditor.sort,
      status: brandEditor.status
    });
    refreshDrafts();
  }
);

watch(
  () => [
    visualEditorScene.value,
    topicEditor.id,
    topicEditor.title,
    topicEditor.subtitle,
    topicEditor.picUrl,
    topicEditor.priceInfo,
    topicEditor.sort,
    topicEditor.status
  ],
  () => {
    if (visualEditorScene.value !== "topic" || !topicEditor.id) return;
    setPreviewDraft("topic", {
      id: topicEditor.id,
      title: topicEditor.title,
      subtitle: topicEditor.subtitle,
      picUrl: topicEditor.picUrl,
      priceInfo: topicEditor.priceInfo,
      sort: topicEditor.sort,
      status: topicEditor.status
    });
    refreshDrafts();
  }
);

watch(
  () => [
    visualEditorScene.value,
    productEditor.id,
    productEditor.name,
    productEditor.type,
    productEditor.categoryId,
    productEditor.introduction,
    productEditor.description,
    productEditor.picUrl,
    productEditor.sliderPicUrls.map(item => item),
    productEditor.detailImageUrls.map(item => item),
    productEditor.price,
    productEditor.marketPrice,
    productEditor.stock,
    productEditor.sort,
    productEditor.status,
    productKeywordTags.value.map(item => item),
    productEditorSkus.value.map(item => ({
      label: item.label,
      skuCode: item.skuCode,
      price: item.priceYuan,
      marketPrice: item.marketPriceYuan,
      stock: item.stock,
      picUrl: item.picUrl
    }))
  ],
  () => {
    if (visualEditorScene.value !== "product" || !productEditor.id) return;
    setPreviewDraft("product", {
      id: productEditor.id,
      name: productEditor.name,
      type: productEditor.type,
      categoryId: productEditor.categoryId,
      categoryName: editorCategoryName.value,
      introduction: productEditor.introduction,
      description: productEditor.description,
      picUrl: productEditor.picUrl,
      sliderPicUrls: [...productEditor.sliderPicUrls],
      detailImageUrls: [...productEditor.detailImageUrls],
      price: productEditor.price,
      marketPrice: productEditor.marketPrice,
      stock: productEditor.stock,
      status: productEditor.status,
      keywordTags: [...productKeywordTags.value],
      skuSummary: editorPreviewSkuSummary.value.map(item => ({ ...item }))
    });
    refreshDrafts();
  },
  { deep: true }
);

onMounted(async () => {
  await Promise.all([fetchData(), fetchRollbackData()]);
  startDraftPolling();
});

onBeforeUnmount(() => {
  stopDraftPolling();
  const previewScene = currentEditorPreviewScene();
  if (previewScene) {
    clearPreviewDraft(previewScene);
  }
});
</script>

<template>
  <div class="app-container preview-center-page" v-loading="loading">
    <div class="page-header">
      <div>
        <h2 class="page-title">全站预览中心</h2>
        <p class="page-subtitle">现在不只是看预览了。你可以直接点左侧页面模块，在右侧边改边看前端效果。</p>
      </div>
      <el-button @click="fetchData">
        <el-icon><RefreshRight /></el-icon>
        刷新正式数据
      </el-button>
    </div>

    <el-alert
      title="当前版本已支持 Banner、频道、品牌、专题、商品详情的可点击编辑；点击左侧模块后，右侧会自动切到对应编辑表单。"
      type="success"
      :closable="false"
      show-icon
      class="page-alert"
    />

    <div class="page-grid">
      <div class="page-main">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="首页预览" name="home">
            <div class="phone-shell">
              <div class="phone-screen home-screen">
                <div class="home-hero">
                  <div class="brand-block">
                    <div class="brand-title">药食同源</div>
                    <div class="brand-subtitle">实时首页预览</div>
                  </div>
                  <div class="search-pill">搜索药食、花茶、滋补好物</div>
                </div>

                <div class="section-block">
                  <div class="section-head-row">
                    <div class="section-title">首页 Banner</div>
                    <span class="section-tip">点击卡片直接编辑</span>
                  </div>
                  <div class="banner-stack">
                    <button
                      v-for="item in mergedBanners.slice(0, 2)"
                      :key="`banner-${item.id}-${item.title}`"
                      type="button"
                      class="editable-card banner-card"
                      :class="{ 'is-selected': isEditorSelected('banner', item.id) }"
                      @click="openVisualEditor('banner', item.id, 'home')"
                    >
                      <img v-if="item.picUrl" :src="item.picUrl" :alt="item.title" class="banner-image" />
                      <div v-else class="image-placeholder">待上传 Banner 图</div>
                      <div class="badge-row">
                        <span v-if="item.__draft" class="draft-badge">草稿</span>
                        <span class="badge-title">{{ item.title }}</span>
                      </div>
                      <span class="edit-chip">点此编辑</span>
                    </button>
                  </div>
                </div>

                <div class="section-block">
                  <div class="section-head-row">
                    <div class="section-title">频道入口</div>
                    <span class="section-tip">点图标即可编辑</span>
                  </div>
                  <div class="channel-grid">
                    <button
                      v-for="item in mergedChannels.slice(0, 8)"
                      :key="`channel-${item.id}-${item.name}`"
                      type="button"
                      class="channel-item channel-trigger"
                      :class="{ 'is-selected': isEditorSelected('channel', item.id) }"
                      @click="openVisualEditor('channel', item.id, 'home')"
                    >
                      <div class="channel-icon">
                        <img v-if="item.iconUrl" :src="item.iconUrl" :alt="item.name" class="channel-icon-image" />
                        <span v-else>{{ item.name.slice(0, 1) }}</span>
                      </div>
                      <div class="channel-name">{{ item.name }}</div>
                      <span v-if="item.__draft" class="draft-dot" />
                    </button>
                  </div>
                </div>

                <div class="section-block">
                  <div class="section-head-row">
                    <div class="section-title">品牌推荐</div>
                    <span class="section-tip">点击品牌卡编辑</span>
                  </div>
                  <div class="brand-list">
                    <button
                      v-for="item in mergedBrands.slice(0, 3)"
                      :key="`brand-${item.id}-${item.name}`"
                      type="button"
                      class="editable-card brand-card"
                      :class="{ 'is-selected': isEditorSelected('brand', item.id) }"
                      @click="openVisualEditor('brand', item.id, 'home')"
                    >
                      <img v-if="item.picUrl" :src="item.picUrl" :alt="item.name" class="brand-image" />
                      <div v-else class="brand-image image-placeholder">待上传品牌图</div>
                      <div class="brand-info">
                        <div class="brand-name">{{ item.name }}</div>
                        <div class="brand-price">{{ formatYuanFromFen(item.floorPrice) }}</div>
                      </div>
                      <span v-if="item.__draft" class="draft-badge brand-draft">草稿</span>
                      <span class="edit-chip brand-chip">点此编辑</span>
                    </button>
                  </div>
                </div>

                <div class="section-block">
                  <div class="section-head-row">
                    <div class="section-title">专题卡片</div>
                    <span class="section-tip">点击专题卡编辑</span>
                  </div>
                  <div class="topic-list">
                    <button
                      v-for="item in mergedTopics.slice(0, 2)"
                      :key="`topic-${item.id}-${item.title}`"
                      type="button"
                      class="editable-card topic-card"
                      :class="{ 'is-selected': isEditorSelected('topic', item.id) }"
                      @click="openVisualEditor('topic', item.id, 'home')"
                    >
                      <img v-if="item.picUrl" :src="item.picUrl" :alt="item.title" class="topic-image" />
                      <div v-else class="topic-image image-placeholder">待上传专题图</div>
                      <div class="topic-info">
                        <div class="topic-title">{{ item.title }}</div>
                        <div class="topic-subtitle">{{ item.subtitle || "这里显示专题副标题" }}</div>
                        <div class="topic-price">{{ normalizeTopicPriceText(item.priceInfo) }}</div>
                      </div>
                      <span v-if="item.__draft" class="draft-badge topic-draft">草稿</span>
                      <span class="edit-chip topic-chip">点此编辑</span>
                    </button>
                  </div>
                </div>

                <div class="section-block">
                  <div class="section-head-row">
                    <div class="section-title">推荐商品</div>
                    <span class="section-tip">点击商品卡编辑</span>
                  </div>
                  <div class="home-product-list">
                    <button
                      v-for="item in mergedHomeProducts.slice(0, 2)"
                      :key="`home-product-${item.id}-${item.name}`"
                      type="button"
                      class="home-product-card"
                      :class="{ 'is-selected': isEditorSelected('product', item.id) }"
                      @click="openVisualEditor('product', item.id, 'home')"
                    >
                      <div class="home-product-image-wrap">
                        <img v-if="item.picUrl" :src="item.picUrl" :alt="item.name" class="home-product-image" />
                        <div v-else class="image-placeholder">待上传商品图</div>
                      </div>
                      <div class="home-product-info">
                        <div class="home-product-name">{{ item.name }}</div>
                        <div class="home-product-intro">{{ item.introduction || "这里显示商品简介" }}</div>
                        <div class="home-product-price">{{ formatYuanFromFen(item.price) }}</div>
                      </div>
                      <span v-if="item.__draft" class="draft-badge home-product-draft">草稿</span>
                      <span class="edit-chip home-product-chip">点此编辑</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="商品详情预览" name="product">
            <div class="product-preview-layout">
              <el-card shadow="never" class="product-selector-card">
                <div class="selector-title">选择商品</div>
                <el-select
                  v-model="selectedProductId"
                  placeholder="请选择一个正式商品"
                  style="width: 100%"
                  :disabled="!!productDraft"
                >
                  <el-option
                    v-for="item in products"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id!"
                  />
                </el-select>
                <div class="selector-tip">
                  当前展示的商品详情首屏也支持点击编辑，适合给客户看“改了以后前端长什么样”。
                </div>
              </el-card>

              <div class="phone-shell">
                <button
                  type="button"
                  class="phone-screen detail-screen detail-screen-button"
                  :class="{ 'is-selected': isEditorSelected('product', previewProduct.id) }"
                  @click="openVisualEditor('product', previewProduct.id, 'product')"
                >
                  <div class="detail-gallery">
                    <img v-if="previewProduct.picUrl" :src="previewProduct.picUrl" :alt="previewProduct.name" class="detail-hero" />
                    <div v-else class="image-placeholder">待上传商品主图</div>
                    <span class="edit-chip detail-chip">点此编辑商品</span>
                  </div>
                  <div class="detail-card">
                    <div class="title-row">
                      <div class="detail-title">{{ previewProduct.name }}</div>
                      <span v-if="productDraft" class="draft-badge">草稿</span>
                    </div>
                    <div class="detail-intro">
                      {{ previewProduct.introduction || "这里会显示商品简介。" }}
                    </div>
                    <div class="detail-price-row">
                      <span class="detail-price">{{ previewProductPriceText }}</span>
                      <span v-if="previewProductMarketPriceText" class="detail-market-price">{{ previewProductMarketPriceText }}</span>
                    </div>
                    <div class="detail-meta">
                      <span>分类：{{ previewProduct.categoryName }}</span>
                      <span>库存：{{ previewProductStock }}</span>
                    </div>
                    <div class="detail-tags">
                      <span
                        v-for="tag in previewProduct.keywordTags.slice(0, 4)"
                        :key="tag"
                        class="detail-tag"
                      >
                        {{ tag }}
                      </span>
                    </div>
                  </div>

                  <div class="detail-card">
                    <div class="section-title">规格与价格</div>
                    <div v-if="previewProduct.skuSummary.length > 0" class="sku-preview-list">
                      <div v-for="(item, index) in previewProduct.skuSummary.slice(0, 4)" :key="`${item.skuCode}-${index}`" class="sku-preview-item">
                        <span>{{ item.label }}</span>
                        <strong>{{ formatYuan(item.price || 0) }}</strong>
                      </div>
                    </div>
                    <div v-else class="detail-subcopy">当前商品为单规格或未同步 SKU 草稿。</div>
                  </div>
                </button>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="内容位预览" name="content">
            <div class="content-grid">
              <el-card shadow="never">
                <template #header><span class="card-title">Banner 预览</span></template>
                <div class="content-stack">
                  <button
                    v-for="item in mergedBanners.slice(0, 3)"
                    :key="`banner-card-${item.id}-${item.title}`"
                    type="button"
                    class="content-preview-card"
                    :class="{ 'is-selected': isEditorSelected('banner', item.id) }"
                    @click="openVisualEditor('banner', item.id, 'content')"
                  >
                    <img v-if="item.picUrl" :src="item.picUrl" :alt="item.title" class="content-preview-image" />
                    <div v-else class="content-preview-image image-placeholder">待上传图片</div>
                    <div class="content-preview-info">
                      <strong>{{ item.title }}</strong>
                      <span>{{ item.url || "未配置跳转" }}</span>
                    </div>
                    <span v-if="item.__draft" class="draft-badge">草稿</span>
                  </button>
                </div>
              </el-card>

              <el-card shadow="never">
                <template #header><span class="card-title">频道预览</span></template>
                <div class="mini-channel-grid">
                  <button
                    v-for="item in mergedChannels.slice(0, 8)"
                    :key="`channel-card-${item.id}-${item.name}`"
                    type="button"
                    class="mini-channel-item"
                    :class="{ 'is-selected': isEditorSelected('channel', item.id) }"
                    @click="openVisualEditor('channel', item.id, 'content')"
                  >
                    <div class="mini-channel-icon">
                      <img v-if="item.iconUrl" :src="item.iconUrl" :alt="item.name" class="channel-icon-image" />
                      <span v-else>{{ item.name.slice(0, 1) }}</span>
                    </div>
                    <span>{{ item.name }}</span>
                    <span v-if="item.__draft" class="draft-dot" />
                  </button>
                </div>
              </el-card>

              <el-card shadow="never">
                <template #header><span class="card-title">品牌预览</span></template>
                <div class="content-stack">
                  <button
                    v-for="item in mergedBrands.slice(0, 3)"
                    :key="`brand-card-${item.id}-${item.name}`"
                    type="button"
                    class="content-preview-card"
                    :class="{ 'is-selected': isEditorSelected('brand', item.id) }"
                    @click="openVisualEditor('brand', item.id, 'content')"
                  >
                    <img v-if="item.picUrl" :src="item.picUrl" :alt="item.name" class="content-preview-image" />
                    <div v-else class="content-preview-image image-placeholder">待上传品牌图</div>
                    <div class="content-preview-info">
                      <strong>{{ item.name }}</strong>
                      <span>{{ formatYuanFromFen(item.floorPrice) }}</span>
                    </div>
                    <span v-if="item.__draft" class="draft-badge">草稿</span>
                  </button>
                </div>
              </el-card>

              <el-card shadow="never">
                <template #header><span class="card-title">专题预览</span></template>
                <div class="content-stack">
                  <button
                    v-for="item in mergedTopics.slice(0, 3)"
                    :key="`topic-card-${item.id}-${item.title}`"
                    type="button"
                    class="content-preview-card topic-preview-card"
                    :class="{ 'is-selected': isEditorSelected('topic', item.id) }"
                    @click="openVisualEditor('topic', item.id, 'content')"
                  >
                    <img v-if="item.picUrl" :src="item.picUrl" :alt="item.title" class="content-preview-image" />
                    <div v-else class="content-preview-image image-placeholder">待上传专题图</div>
                    <div class="content-preview-info">
                      <strong>{{ item.title }}</strong>
                      <span>{{ item.subtitle || "未填写副标题" }}</span>
                      <span class="topic-price-inline">{{ normalizeTopicPriceText(item.priceInfo) }}</span>
                    </div>
                    <span v-if="item.__draft" class="draft-badge">草稿</span>
                  </button>
                </div>
              </el-card>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <div class="page-side">
        <el-card shadow="never" class="side-card">
          <template #header>
            <div class="editor-card-header">
              <div>
                <span class="card-title">{{ editorPanelTitle }}</span>
                <div v-if="editorSelectionLabel" class="editor-subtitle">{{ editorSelectionLabel }}</div>
              </div>
              <el-tag v-if="visualEditorScene" type="success" effect="light">点哪里改哪里</el-tag>
            </div>
          </template>

          <div v-if="!visualEditorScene" class="editor-empty">
            <el-icon class="editor-empty-icon"><EditPen /></el-icon>
            <div class="editor-empty-title">点击左侧预览模块开始编辑</div>
            <div class="editor-empty-desc">优先支持 Banner、频道、品牌、专题、商品详情。保存后左侧会立刻刷新成正式效果。</div>
          </div>

          <div v-else v-loading="visualEditorLoading" class="editor-form-wrap">
            <div class="editor-actions">
              <el-button plain size="small" @click="openFullEditor">打开原后台页</el-button>
              <el-button plain size="small" @click="resetVisualEditor">恢复正式数据</el-button>
              <el-button plain size="small" @click="closeVisualEditor">关闭编辑</el-button>
            </div>

            <el-form v-if="visualEditorScene === 'banner'" label-position="top">
              <el-form-item label="Banner 标题" required>
                <el-input v-model="bannerEditor.title" placeholder="请输入 Banner 标题" maxlength="50" />
              </el-form-item>
              <el-form-item label="Banner 图片" required>
                <MaterialImagePicker v-model="bannerEditor.picUrl" biz-type="content" empty-text="请选择 Banner 图" />
              </el-form-item>
              <el-form-item label="跳转目标">
                <LinkSelector :key="editorLinkKey" v-model="bannerEditor.url" />
              </el-form-item>
              <el-form-item label="排序权重">
                <el-input-number v-model="bannerEditor.sort" :min="0" :max="9999" controls-position="right" />
              </el-form-item>
              <el-form-item label="状态">
                <el-segmented
                  v-model="bannerEditor.status"
                  :options="[{ label: '上架', value: 1 }, { label: '下架', value: 0 }]"
                />
              </el-form-item>
            </el-form>

            <el-form v-else-if="visualEditorScene === 'channel'" label-position="top">
              <el-form-item label="频道名称" required>
                <el-input v-model="channelEditor.name" placeholder="请输入频道名称" maxlength="20" />
              </el-form-item>
              <el-form-item label="频道图标">
                <MaterialImagePicker v-model="channelEditor.iconUrl" biz-type="content" empty-text="请选择频道图标" />
              </el-form-item>
              <el-form-item label="跳转目标">
                <LinkSelector :key="editorLinkKey" v-model="channelEditor.url" />
              </el-form-item>
              <el-form-item label="排序权重">
                <el-input-number v-model="channelEditor.sort" :min="0" :max="9999" controls-position="right" />
              </el-form-item>
              <el-form-item label="状态">
                <el-segmented
                  v-model="channelEditor.status"
                  :options="[{ label: '上架', value: 1 }, { label: '下架', value: 0 }]"
                />
              </el-form-item>
            </el-form>

            <el-form v-else-if="visualEditorScene === 'brand'" label-position="top">
              <el-form-item label="品牌名称" required>
                <el-input v-model="brandEditor.name" placeholder="请输入品牌名称" maxlength="50" />
              </el-form-item>
              <el-form-item label="品牌图片">
                <MaterialImagePicker v-model="brandEditor.picUrl" biz-type="content" empty-text="请选择品牌图" />
              </el-form-item>
              <el-form-item label="起售价（元）">
                <el-input-number
                  v-model="brandEditor.floorPriceYuan"
                  :min="0"
                  :precision="2"
                  :step="1"
                  controls-position="right"
                />
              </el-form-item>
              <el-form-item label="排序权重">
                <el-input-number v-model="brandEditor.sort" :min="0" :max="9999" controls-position="right" />
              </el-form-item>
              <el-form-item label="状态">
                <el-segmented
                  v-model="brandEditor.status"
                  :options="[{ label: '上架', value: 1 }, { label: '下架', value: 0 }]"
                />
              </el-form-item>
            </el-form>

            <el-form v-else-if="visualEditorScene === 'topic'" label-position="top">
              <el-form-item label="专题标题" required>
                <el-input v-model="topicEditor.title" placeholder="请输入专题标题" maxlength="50" />
              </el-form-item>
              <el-form-item label="专题副标题">
                <el-input v-model="topicEditor.subtitle" placeholder="请输入副标题" maxlength="100" />
              </el-form-item>
              <el-form-item label="专题图片">
                <MaterialImagePicker v-model="topicEditor.picUrl" biz-type="content" empty-text="请选择专题图" />
              </el-form-item>
              <el-form-item label="价格说明">
                <el-input v-model="topicEditor.priceInfo" placeholder="如 39.9 或 39元起" maxlength="30" />
              </el-form-item>
              <el-form-item label="排序权重">
                <el-input-number v-model="topicEditor.sort" :min="0" :max="9999" controls-position="right" />
              </el-form-item>
              <el-form-item label="状态">
                <el-segmented
                  v-model="topicEditor.status"
                  :options="[{ label: '上架', value: 1 }, { label: '下架', value: 0 }]"
                />
              </el-form-item>
            </el-form>

            <el-form v-else-if="visualEditorScene === 'product'" label-position="top">
              <el-form-item label="商品名称" required>
                <el-input v-model="productEditor.name" placeholder="请输入商品名称" maxlength="100" />
              </el-form-item>
              <el-form-item label="商品分类" required>
                <el-select v-model="productEditor.categoryId" placeholder="请选择分类" style="width: 100%">
                  <el-option
                    v-for="cat in categories"
                    :key="cat.id"
                    :label="cat.name"
                    :value="cat.id!"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="商品类型">
                <el-segmented
                  v-model="productEditor.type"
                  :options="typeOptions"
                />
              </el-form-item>
              <el-form-item label="商品简介">
                <el-input
                  v-model="productEditor.introduction"
                  type="textarea"
                  :rows="3"
                  maxlength="200"
                  show-word-limit
                  placeholder="请输入一句话卖点"
                />
              </el-form-item>
              <el-form-item label="关键词标签">
                <el-select
                  v-model="productKeywordTags"
                  multiple
                  filterable
                  allow-create
                  default-first-option
                  style="width: 100%"
                  placeholder="选择或输入关键词"
                >
                  <el-option v-for="item in keywordOptions" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
              <el-form-item label="主图">
                <MaterialImagePicker v-model="productEditor.picUrl" biz-type="product" empty-text="请选择主图" />
              </el-form-item>
              <el-form-item label="轮播图">
                <MaterialImagePicker
                  v-model="productEditor.sliderPicUrls"
                  multiple
                  biz-type="product"
                  :max="10"
                  empty-text="请选择轮播图"
                />
              </el-form-item>
              <el-form-item label="详情文案">
                <el-input
                  v-model="productEditor.description"
                  type="textarea"
                  :rows="5"
                  maxlength="2000"
                  show-word-limit
                  placeholder="输入详情文案，右侧预览会同步变化"
                />
              </el-form-item>
              <el-form-item label="详情图">
                <MaterialImagePicker
                  v-model="productEditor.detailImageUrls"
                  multiple
                  biz-type="product"
                  :max="20"
                  empty-text="请选择详情图"
                />
              </el-form-item>

              <template v-if="productEditorSkus.length === 0">
                <el-form-item label="售价（元）">
                  <el-input-number v-model="productEditor.price" :min="0" :precision="2" :step="1" controls-position="right" />
                </el-form-item>
                <el-form-item label="市场价（元）">
                  <el-input-number v-model="productEditor.marketPrice" :min="0" :precision="2" :step="1" controls-position="right" />
                </el-form-item>
                <el-form-item label="库存">
                  <el-input-number v-model="productEditor.stock" :min="0" controls-position="right" />
                </el-form-item>
              </template>

              <div v-else class="sku-editor-block">
                <div class="sku-editor-title">SKU 价格与库存</div>
                <div
                  v-for="(item, index) in productEditorSkus"
                  :key="`${item.id}-${index}`"
                  class="sku-editor-card"
                >
                  <div class="sku-editor-head">
                    <strong>{{ item.label }}</strong>
                    <span>{{ item.skuCode || "未设置 SKU 编码" }}</span>
                  </div>
                  <MaterialImagePicker v-model="item.picUrl" biz-type="product" empty-text="SKU 图可选" />
                  <div class="sku-editor-grid">
                    <el-form-item label="售价（元）">
                      <el-input-number v-model="item.priceYuan" :min="0" :precision="2" :step="1" controls-position="right" />
                    </el-form-item>
                    <el-form-item label="市场价（元）">
                      <el-input-number v-model="item.marketPriceYuan" :min="0" :precision="2" :step="1" controls-position="right" />
                    </el-form-item>
                    <el-form-item label="库存">
                      <el-input-number v-model="item.stock" :min="0" controls-position="right" />
                    </el-form-item>
                  </div>
                </div>
              </div>

              <el-form-item label="排序权重">
                <el-input-number v-model="productEditor.sort" :min="0" :max="9999" controls-position="right" />
              </el-form-item>
              <el-form-item label="状态">
                <el-segmented
                  v-model="productEditor.status"
                  :options="statusOptions"
                />
              </el-form-item>
              <el-alert
                v-if="productEditorSkus.length > 1"
                :closable="false"
                type="info"
                show-icon
                title="这里支持多规格商品的常用改价、改库存和改图片；新增规格维度、重建 SKU 矩阵仍建议点“打开原后台页”。"
              />
              <el-form-item v-if="productEditorStockChanged" label="库存调整原因" required>
                <el-input
                  v-model="productStockAdjustReason"
                  type="textarea"
                  :rows="3"
                  maxlength="200"
                  show-word-limit
                  placeholder="填写盘点入库、损耗修正等具体原因"
                />
              </el-form-item>
            </el-form>

            <div class="editor-footer">
              <el-button @click="closeVisualEditor">取消</el-button>
              <el-button type="primary" :loading="visualSaving" @click="handleVisualSave">保存并更新预览</el-button>
            </div>
          </div>
        </el-card>

        <el-card shadow="never">
          <template #header><span class="card-title">草稿同步状态</span></template>
          <div v-if="drafts.length > 0" class="draft-list">
            <div v-for="item in drafts" :key="item.scene" class="draft-row">
              <div>
                <div class="draft-scene">{{ item.scene }}</div>
                <div class="draft-time">{{ item.updatedAt.replace("T", " ").slice(0, 19) }}</div>
              </div>
              <el-button size="small" text @click="clearSceneDraft(item.scene)">清除</el-button>
            </div>
          </div>
          <el-empty v-else description="当前没有收到实时草稿" :image-size="90" />
        </el-card>

        <el-card v-if="canManageProduct" shadow="never" class="side-card">
          <template #header><span class="card-title">商品一键回退</span></template>
          <div v-loading="rollbackLoading">
            <div v-if="productRollbacks.length > 0" class="rollback-list">
              <div v-for="item in productRollbacks" :key="`product-rollback-${item.id}`" class="rollback-row">
                <div class="rollback-main">
                  <div class="rollback-title">{{ item.operationLabel }}</div>
                  <div class="rollback-subtitle">{{ item.entityName || "未命名商品" }}</div>
                  <div class="rollback-time">{{ item.createTime?.replace("T", " ").slice(0, 19) }}</div>
                </div>
                <el-button size="small" type="primary" plain @click="handleRollback('product', item)">
                  一键回退
                </el-button>
              </div>
            </div>
            <el-empty v-else description="当前没有可回退的商品操作" :image-size="88" />
          </div>
        </el-card>

        <el-card v-if="canManageContent" shadow="never" class="side-card">
          <template #header><span class="card-title">内容一键回退</span></template>
          <div v-loading="rollbackLoading">
            <div v-if="contentRollbacks.length > 0" class="rollback-list">
              <div v-for="item in contentRollbacks" :key="`content-rollback-${item.id}`" class="rollback-row">
                <div class="rollback-main">
                  <div class="rollback-title">{{ item.operationLabel }}</div>
                  <div class="rollback-subtitle">
                    {{ formatSceneLabel(item.sceneCode) }} / {{ item.entityName || "未命名内容" }}
                  </div>
                  <div class="rollback-time">{{ item.createTime?.replace("T", " ").slice(0, 19) }}</div>
                </div>
                <el-button size="small" type="primary" plain @click="handleRollback('content', item)">
                  一键回退
                </el-button>
              </div>
            </div>
            <el-empty v-else description="当前没有可回退的内容操作" :image-size="88" />
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.preview-center-page {
  padding: 16px;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.page-title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #2d3a2e;
}

.page-subtitle {
  margin: 6px 0 0;
  color: #7f8d82;
  font-size: 13px;
}

.page-alert {
  margin-bottom: 16px;
}

.page-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 380px;
  gap: 16px;
}

.page-main,
.page-side {
  min-width: 0;
}

.page-side {
  display: grid;
  gap: 16px;
  align-self: start;
}

.phone-shell {
  max-width: 420px;
  border-radius: 28px;
  background: #25262b;
  padding: 12px;
}

.phone-screen {
  min-height: 760px;
  border-radius: 22px;
  background: #f6f7f4;
  overflow: hidden;
}

.home-hero {
  padding: 24px 18px 16px;
  background: linear-gradient(180deg, #dde9d8 0%, #f6f7f4 100%);
}

.brand-block {
  margin-bottom: 12px;
}

.brand-title {
  font-size: 22px;
  font-weight: 700;
  color: #2d3a2e;
}

.brand-subtitle {
  margin-top: 6px;
  color: #6c7c6d;
  font-size: 13px;
}

.search-pill {
  height: 38px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.82);
  color: #9ca89d;
  display: flex;
  align-items: center;
  padding: 0 14px;
  font-size: 13px;
}

.section-block {
  padding: 14px 16px 0;
}

.section-head-row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
}

.section-title,
.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #2d3a2e;
}

.section-tip,
.editor-subtitle {
  color: #909399;
  font-size: 12px;
}

.banner-stack,
.topic-list,
.brand-list,
.content-stack,
.home-product-list {
  display: grid;
  gap: 12px;
  margin-top: 12px;
}

.editable-card,
.content-preview-card,
.mini-channel-item,
.detail-screen-button,
.channel-trigger,
.home-product-card {
  position: relative;
  border: 2px solid transparent;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
  cursor: pointer;
}

.editable-card:hover,
.content-preview-card:hover,
.mini-channel-item:hover,
.detail-screen-button:hover,
.channel-trigger:hover,
.home-product-card:hover {
  border-color: #b7cdb8;
  box-shadow: 0 8px 18px rgba(91, 140, 90, 0.12);
}

.editable-card.is-selected,
.content-preview-card.is-selected,
.mini-channel-item.is-selected,
.detail-screen-button.is-selected,
.channel-trigger.is-selected,
.home-product-card.is-selected {
  border-color: #5f8f74;
  box-shadow: 0 0 0 1px rgba(95, 143, 116, 0.25), 0 10px 20px rgba(95, 143, 116, 0.15);
}

.banner-card,
.topic-card,
.brand-card,
.content-preview-card,
.home-product-card {
  overflow: hidden;
  border-radius: 18px;
  background: #fefefc;
}

.banner-image,
.topic-image,
.brand-image,
.content-preview-image,
.detail-hero,
.channel-icon-image,
.home-product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.banner-card {
  aspect-ratio: 16 / 8;
}

.badge-row {
  position: absolute;
  left: 12px;
  right: 12px;
  bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.badge-title {
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.35);
}

.draft-badge {
  display: inline-flex;
  align-items: center;
  height: 24px;
  padding: 0 10px;
  border-radius: 999px;
  background: rgba(252, 195, 71, 0.92);
  color: #5a3d00;
  font-size: 12px;
  font-weight: 700;
}

.edit-chip {
  position: absolute;
  top: 12px;
  left: 12px;
  display: inline-flex;
  align-items: center;
  height: 24px;
  padding: 0 10px;
  border-radius: 999px;
  background: rgba(45, 58, 46, 0.72);
  color: #fff;
  font-size: 12px;
}

.brand-chip,
.topic-chip,
.home-product-chip {
  top: auto;
  left: auto;
  right: 12px;
  bottom: 12px;
}

.detail-chip {
  top: 14px;
  left: auto;
  right: 14px;
}

.channel-grid,
.mini-channel-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.channel-item,
.mini-channel-item {
  background: transparent;
  padding: 6px 4px;
  text-align: center;
  border-radius: 16px;
}

.channel-icon,
.mini-channel-icon {
  width: 54px;
  height: 54px;
  margin: 0 auto 8px;
  border-radius: 16px;
  background: #e8f2e7;
  color: #4f7e50;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  font-weight: 700;
}

.channel-name,
.mini-channel-item span {
  color: #4b5950;
  font-size: 12px;
  line-height: 1.4;
}

.draft-dot {
  position: absolute;
  top: 6px;
  right: 10px;
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: #f5b941;
}

.brand-card {
  display: grid;
  grid-template-columns: 100px minmax(0, 1fr);
  min-height: 90px;
  padding: 0;
}

.brand-info,
.topic-info,
.content-preview-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 6px;
  padding: 14px;
  text-align: left;
}

.brand-name,
.topic-title {
  color: #2d3a2e;
  font-size: 15px;
  font-weight: 700;
}

.brand-price,
.topic-price,
.topic-price-inline {
  color: #cf4a3e;
  font-size: 13px;
  font-weight: 700;
}

.topic-card {
  display: grid;
  grid-template-columns: 110px minmax(0, 1fr);
  min-height: 110px;
  padding: 0;
}

.topic-image {
  min-height: 110px;
}

.topic-subtitle {
  color: #7f8d82;
  font-size: 12px;
  line-height: 1.6;
}

.brand-draft,
.topic-draft,
.home-product-draft {
  position: absolute;
  top: 12px;
  right: 12px;
}

.home-product-card {
  display: grid;
  grid-template-columns: 110px minmax(0, 1fr);
  min-height: 120px;
  padding: 0;
  text-align: left;
}

.home-product-image-wrap {
  min-height: 120px;
  background: #eef2ec;
}

.home-product-info {
  padding: 14px;
}

.home-product-name {
  color: #2d3a2e;
  font-size: 15px;
  font-weight: 700;
  line-height: 1.4;
}

.home-product-intro {
  margin-top: 6px;
  color: #7f8d82;
  font-size: 12px;
  line-height: 1.6;
}

.home-product-price {
  margin-top: 10px;
  color: #cf4a3e;
  font-size: 16px;
  font-weight: 700;
}

.product-preview-layout {
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  gap: 16px;
  align-items: start;
}

.product-selector-card {
  align-self: start;
}

.selector-title {
  color: #2d3a2e;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 10px;
}

.selector-tip,
.detail-subcopy {
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
  line-height: 1.6;
}

.detail-screen {
  background: #f4f6f2;
}

.detail-screen-button {
  width: 100%;
  padding: 0;
  border-radius: 22px;
  background: #f4f6f2;
  text-align: left;
}

.detail-gallery {
  position: relative;
  aspect-ratio: 1 / 1;
  background: #eef2ec;
}

.detail-card {
  margin: 14px;
  padding: 16px;
  border-radius: 18px;
  background: #fefefc;
}

.title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.detail-title {
  color: #2d3a2e;
  font-size: 18px;
  font-weight: 700;
}

.detail-intro {
  margin-top: 8px;
  color: #7f8d82;
  font-size: 13px;
  line-height: 1.6;
}

.detail-price-row {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-top: 12px;
}

.detail-price {
  color: #cf4a3e;
  font-size: 26px;
  font-weight: 700;
}

.detail-market-price {
  color: #a0aab7;
  font-size: 14px;
  text-decoration: line-through;
}

.detail-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 12px;
  color: #7f8d82;
  font-size: 12px;
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
}

.detail-tag {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 12px;
  border-radius: 999px;
  background: #e8f2e7;
  color: #4f7e50;
  font-size: 12px;
}

.sku-preview-list {
  display: grid;
  gap: 10px;
  margin-top: 12px;
}

.sku-preview-item {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 12px;
  border: 1px solid #ebeef2;
  border-radius: 12px;
  color: #4b5950;
  font-size: 13px;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.content-preview-card {
  display: grid;
  grid-template-columns: 120px minmax(0, 1fr);
  min-height: 100px;
  padding: 0;
  text-align: left;
}

.content-preview-image {
  height: 100%;
}

.topic-preview-card .content-preview-info {
  align-items: flex-start;
}

.editor-card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.side-card {
  min-height: 220px;
}

.editor-empty {
  display: grid;
  justify-items: center;
  gap: 10px;
  padding: 12px 0 4px;
  text-align: center;
}

.editor-empty-icon {
  font-size: 28px;
  color: #5f8f74;
}

.editor-empty-title {
  color: #2d3a2e;
  font-size: 14px;
  font-weight: 600;
}

.editor-empty-desc {
  color: #909399;
  font-size: 12px;
  line-height: 1.6;
}

.editor-form-wrap {
  display: grid;
  gap: 14px;
}

.editor-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.editor-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.sku-editor-block {
  display: grid;
  gap: 12px;
  margin-bottom: 8px;
}

.sku-editor-title {
  color: #2d3a2e;
  font-size: 14px;
  font-weight: 600;
}

.sku-editor-card {
  padding: 12px;
  border: 1px solid #ebeef2;
  border-radius: 14px;
  background: #fbfcfa;
}

.sku-editor-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
  color: #606266;
  font-size: 12px;
}

.sku-editor-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.image-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: #9ca89d;
  background: linear-gradient(135deg, #eef2ec 0%, #e2e9df 100%);
  font-size: 13px;
}

.draft-list,
.rollback-list {
  display: grid;
  gap: 12px;
}

.draft-row,
.rollback-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px;
  border: 1px solid #ebeef2;
  border-radius: 12px;
}

.draft-scene {
  font-size: 14px;
  font-weight: 600;
  color: #2d3a2e;
  text-transform: capitalize;
}

.draft-time,
.rollback-time {
  margin-top: 4px;
  color: #909399;
  font-size: 12px;
}

.rollback-main {
  min-width: 0;
}

.rollback-title {
  color: #2d3a2e;
  font-size: 14px;
  font-weight: 600;
}

.rollback-subtitle {
  margin-top: 4px;
  color: #606266;
  font-size: 12px;
  line-height: 1.5;
}

@media (max-width: 1400px) {
  .page-grid {
    grid-template-columns: minmax(0, 1fr);
  }

  .page-side {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    align-items: start;
  }

  .side-card:first-child {
    grid-column: 1 / -1;
  }
}

@media (max-width: 1280px) {
  .content-grid,
  .product-preview-layout,
  .sku-editor-grid {
    grid-template-columns: minmax(0, 1fr);
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .page-side,
  .channel-grid,
  .mini-channel-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .content-preview-card,
  .brand-card,
  .topic-card,
  .home-product-card {
    grid-template-columns: 1fr;
  }

  .detail-meta,
  .draft-row,
  .rollback-row,
  .editor-card-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
