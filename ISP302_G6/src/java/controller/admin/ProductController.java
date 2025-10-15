package controller.admin;

import dao.ProductDAO;
import dao.IngredientDAO;
import model.Product;
import model.Ingredient;
import model.ProductIngredient;
import service.ProductImportService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

/**
 * Controller xử lý các chức năng liên quan đến Product trong phần Admin
 */
@WebServlet(name = "ProductController", urlPatterns = { "/admin/product/*" })
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class ProductController extends HttpServlet {

    private ProductDAO productDAO;
    private IngredientDAO ingredientDAO;
    private ProductImportService importService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.productDAO = new ProductDAO();
        this.ingredientDAO = new IngredientDAO();
        this.importService = new ProductImportService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
            // Hiển thị danh sách sản phẩm
            showProductList(request, response);
        } else if (pathInfo.startsWith("/detail/")) {
            // Hiển thị chi tiết sản phẩm
            showProductDetail(request, response, pathInfo);
        } else if (pathInfo.equals("/add")) {
            // Hiển thị form thêm sản phẩm mới
            showAddProductForm(request, response);
        } else if (pathInfo.equals("/import")) {
            // Hiển thị form import Excel
            showImportForm(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.equals("/add")) {
            // Thêm sản phẩm mới
            addProduct(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/update/")) {
            // Cập nhật sản phẩm
            updateProduct(request, response, pathInfo);
        } else if (pathInfo != null && pathInfo.startsWith("/delete/")) {
            // Xóa sản phẩm
            deleteProduct(request, response, pathInfo);
        } else if (pathInfo != null && pathInfo.equals("/import")) {
            // Import sản phẩm từ Excel
            importProducts(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Hiển thị danh sách sản phẩm
     */
    private void showProductList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Product> products = productDAO.getAllProducts();
            request.setAttribute("products", products);
            request.setAttribute("pageTitle", "Danh sách sản phẩm");
            request.getRequestDispatcher("/view/admin/product-list.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải danh sách sản phẩm: " + e.getMessage());
            request.getRequestDispatcher("/view/common/error.jsp").forward(request, response);
        }
    }

    /**
     * Hiển thị chi tiết sản phẩm
     */
    private void showProductDetail(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws ServletException, IOException {

        try {
            String productIdStr = pathInfo.substring("/detail/".length());
            int productId = Integer.parseInt(productIdStr);

            Product product = productDAO.getProductById(productId);
            if (product == null) {
                request.setAttribute("error", "Không tìm thấy sản phẩm #" + productId);
                request.getRequestDispatcher("/view/common/error.jsp").forward(request, response);
                return;
            }

            List<ProductIngredient> productIngredients = productDAO.getIngredientsForProduct(productId);

            request.setAttribute("product", product);
            request.setAttribute("productIngredients", productIngredients);
            request.setAttribute("pageTitle", "Chi tiết sản phẩm #" + productId);
            request.getRequestDispatcher("/view/admin/product-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID sản phẩm không hợp lệ");
            request.getRequestDispatcher("/view/common/error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải chi tiết sản phẩm: " + e.getMessage());
            request.getRequestDispatcher("/view/common/error.jsp").forward(request, response);
        }
    }

    /**
     * Hiển thị form thêm sản phẩm mới
     */
    private void showAddProductForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Ingredient> ingredients = ingredientDAO.getAllIngredients();
            request.setAttribute("ingredients", ingredients);
            request.setAttribute("pageTitle", "Thêm sản phẩm mới");
            request.getRequestDispatcher("/view/admin/product-add.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải form thêm sản phẩm: " + e.getMessage());
            request.getRequestDispatcher("/view/common/error.jsp").forward(request, response);
        }
    }

    /**
     * Thêm sản phẩm mới
     */
    private void addProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String productName = request.getParameter("productName");
            String sku = request.getParameter("sku");
            double price = Double.parseDouble(request.getParameter("price"));
            String description = request.getParameter("description");

            Product newProduct = new Product();
            newProduct.setProductName(productName);
            newProduct.setSku(sku);
            newProduct.setPrice(price);
            newProduct.setDescription(description);

            // Handle ingredients
            String[] ingredientIds = request.getParameterValues("ingredientId");
            String[] requiredQuantities = request.getParameterValues("requiredQuantity");

            List<ProductIngredient> productIngredients = new ArrayList<>();
            if (ingredientIds != null && requiredQuantities != null) {
                for (int i = 0; i < ingredientIds.length; i++) {
                    int ingredientId = Integer.parseInt(ingredientIds[i]);
                    double requiredQuantity = Double.parseDouble(requiredQuantities[i]);
                    if (requiredQuantity > 0) {
                        productIngredients.add(new ProductIngredient(0, ingredientId, requiredQuantity)); // productId
                                                                                                          // will be set
                                                                                                          // after
                                                                                                          // product
                                                                                                          // creation
                    }
                }
            }

            int productId = productDAO.addProduct(newProduct, productIngredients);

            if (productId > 0) {
                request.getSession().setAttribute("ms", "Thêm sản phẩm '" + productName + "' thành công!");
                response.sendRedirect(request.getContextPath() + "/admin/product/detail/" + productId);
            } else {
                request.setAttribute("error", "Không thể thêm sản phẩm.");
                showAddProductForm(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Dữ liệu số không hợp lệ: " + e.getMessage());
            showAddProductForm(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
            showAddProductForm(request, response);
        }
    }

    /**
     * Cập nhật sản phẩm
     */
    private void updateProduct(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws ServletException, IOException {
        try {
            String productIdStr = pathInfo.substring("/update/".length());
            int productId = Integer.parseInt(productIdStr);

            String productName = request.getParameter("productName");
            String sku = request.getParameter("sku");
            double price = Double.parseDouble(request.getParameter("price"));
            String description = request.getParameter("description");

            Product productToUpdate = new Product();
            productToUpdate.setProductId(productId);
            productToUpdate.setProductName(productName);
            productToUpdate.setSku(sku);
            productToUpdate.setPrice(price);
            productToUpdate.setDescription(description);

            // Handle ingredients update
            String[] ingredientIds = request.getParameterValues("ingredientId");
            String[] requiredQuantities = request.getParameterValues("requiredQuantity");

            List<ProductIngredient> productIngredients = new ArrayList<>();
            if (ingredientIds != null && requiredQuantities != null) {
                for (int i = 0; i < ingredientIds.length; i++) {
                    int ingredientId = Integer.parseInt(ingredientIds[i]);
                    double requiredQuantity = Double.parseDouble(requiredQuantities[i]);
                    if (requiredQuantity > 0) {
                        productIngredients.add(new ProductIngredient(productId, ingredientId, requiredQuantity));
                    }
                }
            }

            boolean updated = productDAO.updateProduct(productToUpdate, productIngredients);

            if (updated) {
                request.getSession().setAttribute("ms", "Cập nhật sản phẩm #" + productId + " thành công!");
                response.sendRedirect(request.getContextPath() + "/admin/product/detail/" + productId);
            } else {
                request.setAttribute("error", "Không thể cập nhật sản phẩm.");
                showProductDetail(request, response, pathInfo); // Re-show detail form with error
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Dữ liệu số không hợp lệ: " + e.getMessage());
            showProductDetail(request, response, pathInfo);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            showProductDetail(request, response, pathInfo);
        }
    }

    /**
     * Xóa sản phẩm
     */
    private void deleteProduct(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws ServletException, IOException {
        try {
            String productIdStr = pathInfo.substring("/delete/".length());
            int productId = Integer.parseInt(productIdStr);

            boolean deleted = productDAO.deleteProduct(productId);

            if (deleted) {
                request.getSession().setAttribute("ms", "Xóa sản phẩm #" + productId + " thành công!");
                response.sendRedirect(request.getContextPath() + "/admin/product/list");
            } else {
                request.getSession().setAttribute("error",
                        "Không thể xóa sản phẩm #" + productId + ". Có thể sản phẩm đang được sử dụng.");
                response.sendRedirect(request.getContextPath() + "/admin/product/detail/" + productId);
            }

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "ID sản phẩm không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/product/list");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/product/list");
        }
    }

    /**
     * Hiển thị form import Excel
     */
    private void showImportForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("pageTitle", "Import sản phẩm từ Excel");
        request.getRequestDispatcher("/view/admin/product-import.jsp").forward(request, response);
    }

    /**
     * Import sản phẩm từ file Excel
     */
    private void importProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra người dùng đã đăng nhập
        if (request.getSession(false) == null || request.getSession().getAttribute("authUser") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bạn chưa đăng nhập");
            return;
        }

        try {
            // Lấy file upload
            Part filePart = request.getPart("excelFile");

            if (filePart == null || filePart.getSize() == 0) {
                request.setAttribute("error", "Vui lòng chọn file Excel để upload");
                request.getRequestDispatcher("/view/admin/product-import.jsp").forward(request, response);
                return;
            }

            // Kiểm tra loại file
            String fileName = getFileName(filePart);
            if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
                request.setAttribute("error", "File không đúng định dạng. Vui lòng upload file Excel (.xlsx hoặc .xls)");
                request.getRequestDispatcher("/view/admin/product-import.jsp").forward(request, response);
                return;
            }

            // Lấy tùy chọn cập nhật
            String updateExistingParam = request.getParameter("updateExisting");
            boolean updateExisting = "true".equals(updateExistingParam);

            // Xử lý import
            InputStream fileContent = filePart.getInputStream();
            ProductImportService.ImportResult result = importService.importProducts(fileContent, updateExisting);
            fileContent.close();

            // Hiển thị kết quả
            request.setAttribute("importResult", result);
            request.setAttribute("pageTitle", "Kết quả Import sản phẩm");
            request.getRequestDispatcher("/view/admin/product-import-result.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi xử lý file Excel: " + e.getMessage());
            request.getRequestDispatcher("/view/admin/product-import.jsp").forward(request, response);
        }
    }

    /**
     * Lấy tên file từ Part header
     */
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }
}
