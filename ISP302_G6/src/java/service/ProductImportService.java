package service;

import dao.IngredientDAO;
import dao.ProductDAO;
import model.Ingredient;
import model.Product;
import model.ProductIngredient;
import util.ExcelUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for importing products from Excel files
 *
 * Expected Excel format:
 * Column 0: SKU (optional for new products, required for updates)
 * Column 1: Product Name (required)
 * Column 2: Price (required)
 * Column 3: Description (optional)
 * Column 4: Ingredient Name 1 (optional)
 * Column 5: Required Quantity 1 (optional)
 * Column 6: Ingredient Name 2 (optional)
 * Column 7: Required Quantity 2 (optional)
 * ... (more ingredient pairs can be added)
 */
public class ProductImportService {

    private final ProductDAO productDAO;
    private final IngredientDAO ingredientDAO;

    public ProductImportService() {
        this.productDAO = new ProductDAO();
        this.ingredientDAO = new IngredientDAO();
    }

    /**
     * Import result summary
     */
    public static class ImportResult {
        public int totalRows;
        public int successCount;
        public int failedCount;
        public int updatedCount;
        public int insertedCount;
        public List<String> errors;

        public ImportResult() {
            this.errors = new ArrayList<>();
        }

        public void addError(int rowNum, String message) {
            errors.add("Dòng " + rowNum + ": " + message);
        }
    }

    /**
     * Import products from Excel file
     *
     * @param inputStream Excel file input stream
     * @param updateExisting If true, update existing products by SKU; if false, skip duplicates
     * @return Import result summary
     */
    public ImportResult importProducts(InputStream inputStream, boolean updateExisting) {
        ImportResult result = new ImportResult();

        try {
            // Read Excel file (skip header row)
            List<String[]> rows = ExcelUtils.readExcelFile(inputStream, 0, true);
            result.totalRows = rows.size();

            // Cache ingredients by name for faster lookup
            Map<String, Ingredient> ingredientCache = buildIngredientCache();

            int rowNum = 2; // Start from row 2 (row 1 is header)
            for (String[] row : rows) {
                try {
                    // Validate required fields: Product Name and Price
                    if (!ExcelUtils.validateRequiredFields(row, 1, 2)) {
                        result.addError(rowNum, "Thiếu tên sản phẩm hoặc giá");
                        result.failedCount++;
                        rowNum++;
                        continue;
                    }

                    // Parse product data
                    String sku = row.length > 0 ? row[0].trim() : "";
                    String productName = row[1].trim();
                    double price = ExcelUtils.parseDouble(row[2], 0);
                    String description = row.length > 3 ? row[3].trim() : "";

                    if (price <= 0) {
                        result.addError(rowNum, "Giá sản phẩm không hợp lệ: " + row[2]);
                        result.failedCount++;
                        rowNum++;
                        continue;
                    }

                    // Create Product object
                    Product product = new Product();
                    product.setProductName(productName);
                    product.setSku(sku.isEmpty() ? null : sku);
                    product.setPrice(price);
                    product.setDescription(description);

                    // Parse ingredients (from column 4 onwards, in pairs)
                    List<ProductIngredient> productIngredients = parseIngredients(row, ingredientCache, rowNum, result);

                    // Check if product exists by SKU
                    Product existingProduct = null;
                    if (sku != null && !sku.isEmpty()) {
                        existingProduct = productDAO.getProductBySku(sku);
                    }

                    if (existingProduct != null) {
                        // Product exists
                        if (updateExisting) {
                            // Update existing product
                            product.setProductId(existingProduct.getProductId());
                            boolean updated = productDAO.updateProduct(product, productIngredients);
                            if (updated) {
                                result.successCount++;
                                result.updatedCount++;
                            } else {
                                result.addError(rowNum, "Không thể cập nhật sản phẩm: " + productName);
                                result.failedCount++;
                            }
                        } else {
                            // Skip existing product
                            result.addError(rowNum, "Sản phẩm đã tồn tại (SKU: " + sku + "), bỏ qua");
                            result.failedCount++;
                        }
                    } else {
                        // Insert new product
                        int productId = productDAO.addProduct(product, productIngredients);
                        if (productId > 0) {
                            result.successCount++;
                            result.insertedCount++;
                        } else {
                            result.addError(rowNum, "Không thể thêm sản phẩm: " + productName);
                            result.failedCount++;
                        }
                    }

                } catch (Exception e) {
                    result.addError(rowNum, "Lỗi xử lý: " + e.getMessage());
                    result.failedCount++;
                }

                rowNum++;
            }

        } catch (Exception e) {
            result.addError(0, "Lỗi đọc file Excel: " + e.getMessage());
        }

        return result;
    }

    /**
     * Parse ingredients from row data
     */
    private List<ProductIngredient> parseIngredients(String[] row, Map<String, Ingredient> ingredientCache,
                                                      int rowNum, ImportResult result) {
        List<ProductIngredient> productIngredients = new ArrayList<>();

        // Start from column 4, process in pairs (ingredient name, quantity)
        for (int i = 4; i < row.length; i += 2) {
            if (i + 1 >= row.length) break; // Need both name and quantity

            String ingredientName = row[i].trim();
            String quantityStr = row[i + 1].trim();

            if (ingredientName.isEmpty()) continue; // Skip empty ingredient

            // Look up ingredient
            Ingredient ingredient = ingredientCache.get(ingredientName.toLowerCase());
            if (ingredient == null) {
                result.addError(rowNum, "Không tìm thấy nguyên liệu: " + ingredientName);
                continue;
            }

            // Parse quantity
            double quantity = ExcelUtils.parseDouble(quantityStr, 0);
            if (quantity <= 0) {
                result.addError(rowNum, "Số lượng nguyên liệu không hợp lệ: " + quantityStr);
                continue;
            }

            ProductIngredient pi = new ProductIngredient();
            pi.setIngredientId(ingredient.getIngredientID());
            pi.setRequiredQuantity(quantity);
            productIngredients.add(pi);
        }

        return productIngredients;
    }

    /**
     * Build a cache of ingredients by name (lowercase) for faster lookup
     */
    private Map<String, Ingredient> buildIngredientCache() {
        Map<String, Ingredient> cache = new HashMap<>();
        List<Ingredient> ingredients = ingredientDAO.getAllIngredients();

        for (Ingredient ingredient : ingredients) {
            cache.put(ingredient.getIngredientName().toLowerCase(), ingredient);
        }

        return cache;
    }
}
