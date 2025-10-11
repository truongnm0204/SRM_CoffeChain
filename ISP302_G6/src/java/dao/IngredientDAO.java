package dao;

import java.math.BigDecimal;
import model.Ingredient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.IngredientCategory;
import model.Setting;
import service.JDBCUtils;

public class IngredientDAO {

    // Lấy danh sách tất cả các nguyên vật liệu
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredientList = new ArrayList<>();
        String sql = "SELECT * FROM \"Ingredient\" ORDER BY \"ingredientname\" ASC";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setIngredientID(rs.getInt("ingredientid"));
                ingredient.setIngredientName(rs.getString("ingredientname"));
                ingredient.setDescription(rs.getString("description"));
                ingredient.setCurrentStock(rs.getBigDecimal("currentstock"));
                ingredient.setMinStock(rs.getBigDecimal("minstock"));
                ingredient.setUnitSettingID(rs.getInt("unitsettingid"));
                ingredient.setCategoryID(rs.getInt("categoryid"));
                ingredientList.add(ingredient);
            }
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        return ingredientList;
    }

    public List<Ingredient> searchAndFilterIngredients(String searchTerm, Integer categoryId, int page, int pageSize) {
        List<Ingredient> ingredientList = new ArrayList<>();
        String baseSql = "SELECT i.*, c.\"categoryname\", c.\"status\", s.\"settingvalue\" AS unitname "
                + "FROM \"Ingredient\" i "
                + "LEFT JOIN \"IngredientCategory\" c ON i.\"categoryid\" = c.\"categoryid\" "
                + "LEFT JOIN \"Setting\" s ON i.\"unitsettingid\" = s.\"settingid\"";

        StringBuilder sqlBuilder = new StringBuilder(baseSql);
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;

        if (categoryId != null && categoryId > 0) {
            sqlBuilder.append(" WHERE i.\"categoryid\" = ?");
            params.add(categoryId);
            hasWhere = true;
        }

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sqlBuilder.append(hasWhere ? " AND (" : " WHERE (");
            sqlBuilder.append("i.\"ingredientname\" ILIKE ? OR c.\"categoryname\" ILIKE ?)");
            String likeTerm = "%" + searchTerm + "%";
            params.add(likeTerm);
            params.add(likeTerm);
        }

        sqlBuilder.append(" ORDER BY i.\"ingredientname\" ASC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ingredientList.add(mapResultSetToIngredient(rs));
                }
            }
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        return ingredientList;
    }

    public int getIngredientCount(String searchTerm, Integer categoryId) {
        int count = 0;
        String baseSql = "SELECT COUNT(*) FROM \"Ingredient\" i "
                + "LEFT JOIN \"IngredientCategory\" c ON i.\"categoryid\" = c.\"categoryid\"";

        StringBuilder sqlBuilder = new StringBuilder(baseSql);
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;

        if (categoryId != null && categoryId > 0) {
            sqlBuilder.append(" WHERE i.\"categoryid\" = ?");
            params.add(categoryId);
            hasWhere = true;
        }

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sqlBuilder.append(hasWhere ? " AND (" : " WHERE (");
            sqlBuilder.append("i.\"ingredientname\" ILIKE ? OR c.\"categoryname\" ILIKE ?)");
            String likeTerm = "%" + searchTerm + "%";
            params.add(likeTerm);
            params.add(likeTerm);
        }

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        return count;
    }

    private Ingredient mapResultSetToIngredient(ResultSet rs) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientID(rs.getInt("ingredientid"));
        ingredient.setIngredientName(rs.getString("ingredientname"));
        ingredient.setDescription(rs.getString("description"));
        ingredient.setCurrentStock(rs.getBigDecimal("currentstock"));
        ingredient.setMinStock(rs.getBigDecimal("minstock"));
        ingredient.setUnitSettingID(rs.getInt("unitsettingid"));
        ingredient.setCategoryID(rs.getInt("categoryid"));
        ingredient.setStatus(rs.getString("status"));
        ingredient.setCategoryName(rs.getString("categoryname"));
        ingredient.setUnitName(rs.getString("unitname"));
        return ingredient;
    }

    public List<IngredientCategory> getAllCategories() {
        List<IngredientCategory> categoryList = new ArrayList<>();
        String sql = "SELECT * FROM \"IngredientCategory\" WHERE \"status\" = 'Active' ORDER BY \"categoryname\" ASC";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                IngredientCategory category = new IngredientCategory();
                category.setCategoryID(rs.getInt("CategoryID"));
                category.setCategoryName(rs.getString("CategoryName"));
                category.setDescription(rs.getString("Description"));
                category.setHasExpiry(rs.getBoolean("HasExpiry"));
                category.setStatus(rs.getString("Status"));
                categoryList.add(category);
            }
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        return categoryList;
    }

    public boolean addIngredient(Ingredient ingredient) {
        String sql = "INSERT INTO \"Ingredient\" (\"ingredientname\", \"description\", \"currentstock\", \"minstock\", \"unitsettingid\", \"categoryid\") VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ingredient.getIngredientName());
            ps.setString(2, ingredient.getDescription());
            ps.setBigDecimal(3, ingredient.getCurrentStock() != null ? ingredient.getCurrentStock() : java.math.BigDecimal.ZERO);
            ps.setBigDecimal(4, ingredient.getMinStock() != null ? ingredient.getMinStock() : java.math.BigDecimal.ZERO);

            if (ingredient.getUnitSettingID() != null) {
                ps.setInt(5, ingredient.getUnitSettingID());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            if (ingredient.getCategoryID() != null) {
                ps.setInt(6, ingredient.getCategoryID());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
            return false;
        }
    }

    public Ingredient findIngredientById(int ingredientId) {
        Ingredient ingredient = null;
        // Câu lệnh SQL với JOIN để lấy đầy đủ thông tin
        String sql = "SELECT i.*, c.categoryname, c.status, s.settingvalue AS unitname "
                + "FROM \"Ingredient\" i "
                + "LEFT JOIN \"IngredientCategory\" c ON i.categoryid = c.categoryid "
                + "LEFT JOIN \"Setting\" s ON i.unitsettingid = s.settingid "
                + "WHERE i.ingredientid = ?";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ingredientId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Sử dụng lại hàm mapResultSetToIngredient đã có để tránh lặp code
                    ingredient = mapResultSetToIngredient(rs);
                }
            }
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }

        return ingredient;
    }

    public boolean updateIngredient(Ingredient ingredient) {
        String sql = "UPDATE \"Ingredient\" SET ingredientname = ?, description = ?, minstock = ?, unitsettingid = ?, categoryid = ? WHERE ingredientid = ?";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ingredient.getIngredientName());
            ps.setString(2, ingredient.getDescription());
            ps.setBigDecimal(3, ingredient.getMinStock());
            ps.setInt(4, ingredient.getUnitSettingID());
            ps.setInt(5, ingredient.getCategoryID());
            ps.setInt(6, ingredient.getIngredientID());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
            return false;
        }
    }

    public static List<Ingredient> getAllActiveIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();

        // Câu lệnh SQL đã được sửa lỗi
        String sql = "SELECT "
                + "    i.ingredientid, i.ingredientname, i.description, "
                + "    i.currentstock, i.minstock, "
                + "    i.unitsettingid, i.categoryid, "
                + "    ic.status, "
                + // Lấy status từ bảng category (ic)
                "    ic.categoryname, "
                + "    s.settingvalue AS unitname "
                + "FROM "
                + "    \"Ingredient\" i "
                + "LEFT JOIN \"IngredientCategory\" ic ON i.categoryid = ic.categoryid "
                + "LEFT JOIN \"Setting\" s ON i.unitsettingid = s.settingid "
                + "WHERE "
                + "    ic.status = 'Active' "
                + // Lọc theo status của category (ic)
                "    AND (s.settingtype = 'UNIT' OR s.settingtype IS NULL)"
                + "ORDER BY "
                + "    i.ingredientname ASC";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Ingredient ingredient = new Ingredient();

                ingredient.setIngredientID(rs.getInt("ingredientid"));
                ingredient.setIngredientName(rs.getString("ingredientname"));
                ingredient.setDescription(rs.getString("description"));
                ingredient.setCurrentStock(rs.getBigDecimal("currentstock"));
                ingredient.setMinStock(rs.getBigDecimal("minstock"));
                ingredient.setUnitSettingID(rs.getInt("unitsettingid"));
                ingredient.setCategoryID(rs.getInt("categoryid"));

                // Lấy dữ liệu từ các bảng đã JOIN, bao gồm cả status đúng
                ingredient.setStatus(rs.getString("status")); // Giờ đã có thể lấy status
                ingredient.setCategoryName(rs.getString("categoryname"));
                ingredient.setUnitName(rs.getString("unitname"));

                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }

        return ingredients;
    }

    public static void main(String[] args) {
        IngredientDAO dao = new IngredientDAO();

        System.out.println("===== TEST getAllIngredients() =====");
        List<Ingredient> all = getAllActiveIngredients();
        if (all.isEmpty()) {
            System.out.println("✅ Hàm chạy OK — nhưng DB chưa có dữ liệu (getAllIngredients)");
        } else {
            all.forEach(i -> System.out.println(i.getIngredientName()));
        }

    }
}
