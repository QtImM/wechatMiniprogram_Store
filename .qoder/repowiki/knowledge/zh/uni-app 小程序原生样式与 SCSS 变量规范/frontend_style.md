## 1. 技术栈与方法论
- **核心框架**：基于 `uni-app` (Vue 3 + TypeScript) 开发微信小程序。
- **样式方案**：采用 **原生 CSS/SCSS** 结合 `scoped` 样式隔离。未引入 Tailwind CSS、Bootstrap 或 Vant Weapp 等第三方 UI 组件库，倾向于手写布局与样式以实现轻量化和高度定制。
- **预处理器**：使用 **SCSS** (`uni.scss`) 管理全局设计令牌（Design Tokens）。

## 2. 设计令牌 (Design Tokens)
在 `shop-miniapp/src/uni.scss` 中定义了核心视觉变量，作为全站的色彩基准：
- **主色调**：`$primary-color: #07c160` (微信绿，用于选中态、主要操作)。
- **价格色**：`$price-color: #e64340` (红色，用于突出商品价格)。
- **文本色**：`$text-color: #333` (主要文字)，`$text-secondary: #999` (次要/辅助文字)。
- **背景色**：`$bg-color: #f5f5f5` (页面整体背景灰)。

## 3. 布局与单位规范
- **响应式单位**：全面使用微信小程序特有的 **`rpx`** 单位，以适配不同宽度的手机屏幕。
- **布局模式**：
  - **Flexbox**：广泛用于列表排列（如 `.product-grid` 使用 `flex-wrap: wrap` 实现双列流式布局）。
  - **滚动容器**：使用 `<scroll-view>` 实现横向分类栏的滑动交互。
- **全局重置**：在 `App.vue` 中统一设置 `page` 的背景色为 `#f5f5f5`，并指定系统字体栈 `-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif`。

## 4. 组件化样式约定
- **卡片式设计**：商品展示采用 `.product-card`，具有圆角 (`border-radius: 16rpx`)、白色背景和溢出隐藏。
- **状态反馈**：通过动态类名（如 `.category-item.active`）切换主色调背景与白色文字，提供清晰的交互反馈。
- **文本截断**：使用 WebKit 私有属性实现多行文本省略（`-webkit-line-clamp: 2`），确保商品名称在固定区域内整齐显示。

## 5. 开发者准则
1. **优先使用 SCSS 变量**：在编写新页面样式时，应引用 `uni.scss` 中的颜色变量，避免硬编码十六进制颜色值，以保持视觉一致性。
2. **Scoped 样式隔离**：所有页面级组件（`.vue`）的 `<style>` 标签必须添加 `scoped` 属性，防止样式污染全局。
3. **遵循 rpx 规范**：除非涉及边框像素精度（可使用 `px`），否则尺寸、间距、字体大小均应使用 `rpx`。
4. **保持轻量**：目前项目未引入重型 UI 库，新增通用组件时应优先考虑复用现有样式模式，而非盲目引入新依赖。