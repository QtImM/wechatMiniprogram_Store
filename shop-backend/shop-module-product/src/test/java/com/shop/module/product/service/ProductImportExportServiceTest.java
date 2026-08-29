package com.shop.module.product.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.shop.module.product.dal.dataobject.CategoryDO;
import com.shop.module.product.dal.dataobject.ProductSkuDO;
import com.shop.module.product.dal.dataobject.ProductSpuDO;
import com.shop.module.product.dal.mysql.CategoryMapper;
import com.shop.module.product.dal.mysql.ProductSkuMapper;
import com.shop.module.product.dal.mysql.ProductSpuMapper;
import com.shop.module.product.vo.ProductImportPreviewRespVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductImportExportServiceTest {

    private ProductSpuMapper productSpuMapper;
    private ProductSkuMapper productSkuMapper;
    private CategoryMapper categoryMapper;
    private ProductAdminService productAdminService;
    private MaterialAssetService materialAssetService;
    private ProductImportExportService service;

    @BeforeEach
    void setUp() {
        productSpuMapper = mock(ProductSpuMapper.class);
        productSkuMapper = mock(ProductSkuMapper.class);
        categoryMapper = mock(CategoryMapper.class);
        productAdminService = mock(ProductAdminService.class);
        materialAssetService = mock(MaterialAssetService.class);
        service = new ProductImportExportService(
                productSpuMapper, productSkuMapper, categoryMapper, productAdminService, materialAssetService);

        CategoryDO category = new CategoryDO();
        category.setId(1L);
        category.setName("茶饮花茶");
        category.setStatus(1);
        category.setSort(10);
        when(categoryMapper.selectList(any(Wrapper.class))).thenReturn(List.of(category));
        when(categoryMapper.selectById(1L)).thenReturn(category);
        when(productSkuMapper.selectList(any(Wrapper.class))).thenReturn(List.of());
    }

    @Test
    void shouldGenerateCustomerTemplateForCsvAndExcel() {
        String csv = new String(service.templateCsv(), StandardCharsets.UTF_8);
        byte[] xlsx = service.templateWorkbook();

        assertTrue(csv.startsWith("\uFEFF"));
        assertTrue(csv.contains("商品组编码"));
        assertTrue(csv.contains("商品类型"));
        assertTrue(csv.contains("SKU编码"));
        assertNotNull(xlsx);
        assertTrue(xlsx.length > 4);
        assertEquals('P', xlsx[0]);
        assertEquals('K', xlsx[1]);
    }

    @Test
    void shouldPreviewErrorsWithoutPersisting() {
        MockMultipartFile file = csvFile(header() + ",,,bad-category,,,,,,,,BAD SKU,,,0,1,-1,未知,,,\n");

        ProductImportPreviewRespVO result = service.preview(file, "CREATE");

        assertEquals("CREATE", result.getImportMode());
        assertEquals(1, result.getTotalRows());
        assertEquals(0, result.getValidRows());
        assertEquals(1, result.getErrorRows());
        assertTrue(result.getRows().getFirst().getErrorColumns().contains("商品组编码"));
        assertTrue(result.getRows().getFirst().getErrorColumns().contains("商品名称"));
        assertTrue(result.getRows().getFirst().getErrorColumns().contains("分类ID"));
        assertTrue(result.getRows().getFirst().getErrorColumns().contains("库存"));
        assertTrue(result.getRows().getFirst().getErrorColumns().contains("上架状态(上架/下架)"));
        verify(productAdminService, never()).saveProduct(any(ProductSpuDO.class), anyList(), any(), any());
    }

    @Test
    void shouldImportCreateModeProducts() {
        StringBuilder content = new StringBuilder(header());
        for (int i = 1; i <= 3; i++) {
            content.append(row("GROUP-" + i, "批量导入商品" + i, "IMPORT-" + i, "120g"));
        }

        ProductImportPreviewRespVO result = service.importProducts(csvFile(content.toString()), "CREATE", 88L);

        assertEquals(3, result.getTotalRows());
        assertEquals(3, result.getValidRows());
        assertEquals(0, result.getErrorRows());
        assertEquals(3, result.getCreatedProductCount());
        assertEquals(3, result.getCreatedSkuCount());
        assertEquals(0, result.getUpdatedProductCount());
        verify(productAdminService, times(3)).saveProduct(
                any(ProductSpuDO.class), anyList(), eq(88L), eq("商品导入初始化库存"));
    }

    @Test
    void shouldUpdateExistingProductWhenModeIsUpdate() {
        ProductSpuDO spu = new ProductSpuDO();
        spu.setId(10L);
        spu.setCategoryId(1L);
        spu.setName("原商品");
        spu.setType(1);
        spu.setStatus(0);
        spu.setPicUrl("https://cdn.example.com/main.png");
        spu.setSliderPicUrls("[\"https://cdn.example.com/main.png\"]");
        when(productSpuMapper.selectById(10L)).thenReturn(spu);

        ProductSkuDO sku = existingSku(101L, 10L, "IMPORT-001");
        when(productSkuMapper.selectList(any(Wrapper.class))).thenReturn(List.of(sku));

        ProductImportPreviewRespVO result = service.importProducts(
                csvFile(header() + row("SPU-10", "更新商品", "IMPORT-001", "120g")),
                "UPDATE",
                66L
        );

        assertEquals(0, result.getErrorRows());
        assertEquals(1, result.getUpdatedProductCount());
        assertEquals(1, result.getUpdatedSkuCount());

        ArgumentCaptor<ProductSpuDO> spuCaptor = ArgumentCaptor.forClass(ProductSpuDO.class);
        ArgumentCaptor<List<ProductSkuDO>> skuCaptor = ArgumentCaptor.forClass(List.class);
        verify(productAdminService).saveProduct(spuCaptor.capture(), skuCaptor.capture(), eq(66L), eq("商品导入批量更新"));
        assertEquals(10L, spuCaptor.getValue().getId());
        assertEquals(101L, skuCaptor.getValue().getFirst().getId());
    }

    @Test
    void shouldRejectUpdateWhenWholeSkuSetIsMissing() {
        ProductSkuDO skuA = existingSku(101L, 10L, "IMPORT-001");
        ProductSkuDO skuB = existingSku(102L, 10L, "IMPORT-002");
        when(productSkuMapper.selectList(any(Wrapper.class))).thenReturn(List.of(skuA, skuB));
        ProductSpuDO spu = new ProductSpuDO();
        spu.setId(10L);
        when(productSpuMapper.selectById(10L)).thenReturn(spu);

        ProductImportPreviewRespVO result = service.preview(
                csvFile(header() + row("SPU-10", "更新商品", "IMPORT-001", "120g")),
                "UPDATE"
        );

        assertEquals(1, result.getErrorRows());
        assertTrue(result.getRows().getFirst().getErrors().stream()
                .anyMatch(error -> error.contains("必须包含该商品全部 SKU")));
        verify(productAdminService, never()).saveProduct(any(ProductSpuDO.class), anyList(), any(), any());
    }

    @Test
    void shouldExportProductsWithGroupCodeAndCreateTime() {
        ProductSpuDO spu = new ProductSpuDO();
        spu.setId(10L);
        spu.setName("导出商品");
        spu.setType(1);
        spu.setCategoryId(1L);
        spu.setPicUrl("https://cdn.example.com/a.png");
        spu.setSliderPicUrls("[\"https://cdn.example.com/a.png\"]");
        spu.setDescription("<p>详情说明</p><p><img src=\"https://cdn.example.com/detail.png\" /></p>");
        spu.setPrice(1990);
        spu.setMarketPrice(2990);
        spu.setStock(8);
        spu.setStatus(1);
        spu.setSort(10);
        spu.setCreateTime(LocalDateTime.of(2026, 8, 16, 12, 0, 0));
        ProductSkuDO sku = new ProductSkuDO();
        sku.setSkuCode("EXPORT-001");
        sku.setProperties("[{\"name\":\"规格\",\"valueName\":\"120g\"}]");
        sku.setPrice(1990);
        sku.setMarketPrice(2990);
        sku.setStock(8);
        sku.setPicUrl("https://cdn.example.com/sku.png");
        when(productSpuMapper.selectList(any(Wrapper.class))).thenReturn(List.of(spu));
        when(productSkuMapper.selectList(any(Wrapper.class))).thenReturn(List.of(sku));

        String csv = new String(service.exportCsv(null, null, null, null, null), StandardCharsets.UTF_8);

        assertTrue(csv.contains("商品组编码"));
        assertTrue(csv.contains("SPU-10"));
        assertTrue(csv.contains("EXPORT-001"));
        assertTrue(csv.contains("2026-08-16 12:00:00"));
    }

    private ProductSkuDO existingSku(Long id, Long spuId, String skuCode) {
        ProductSkuDO sku = new ProductSkuDO();
        sku.setId(id);
        sku.setSpuId(spuId);
        sku.setSkuCode(skuCode);
        sku.setProperties("[{\"name\":\"规格\",\"valueName\":\"120g\"}]");
        sku.setPrice(1990);
        sku.setMarketPrice(2990);
        sku.setStock(8);
        sku.setPicUrl("https://cdn.example.com/sku.png");
        return sku;
    }

    private MockMultipartFile csvFile(String content) {
        return new MockMultipartFile("file", "products.csv", "text/csv", content.getBytes(StandardCharsets.UTF_8));
    }

    private String header() {
        return "商品组编码,商品名称,商品类型,分类ID,分类名称,关键词,简介,主图URL,轮播图URL(多个用|分隔),详情说明,详情图URL(多个用|分隔),"
                + "SKU编码,规格名称,规格值,售价(元),市场价(元),库存,上架状态(上架/下架),排序,SKU图片URL,创建时间\n";
    }

    private String row(String groupCode, String productName, String skuCode, String specValue) {
        return groupCode + "," + productName + ",实物,1,,关键词,简介,https://cdn.example.com/main.png,https://cdn.example.com/main.png,"
                + "详情说明,https://cdn.example.com/detail.png," + skuCode
                + ",规格," + specValue + ",19.90,29.90,10,下架,10,https://cdn.example.com/sku.png,\n";
    }
}
