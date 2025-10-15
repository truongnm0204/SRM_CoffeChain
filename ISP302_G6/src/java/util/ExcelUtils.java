package util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling Excel file operations
 */
public class ExcelUtils {

    /**
     * Read Excel file and return list of rows as String arrays
     * @param inputStream Excel file input stream
     * @param sheetIndex Sheet index (0-based)
     * @param skipFirstRow Skip header row if true
     * @return List of rows, each row is a String array
     */
    public static List<String[]> readExcelFile(InputStream inputStream, int sheetIndex, boolean skipFirstRow)
            throws IOException {
        List<String[]> data = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);

            int startRow = skipFirstRow ? 1 : 0;

            for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String[] rowData = new String[row.getLastCellNum()];
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    rowData[j] = getCellValueAsString(cell);
                }
                data.add(rowData);
            }
        }

        return data;
    }

    /**
     * Convert cell value to String
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Handle numeric values - remove decimal point if it's a whole number
                    double numValue = cell.getNumericCellValue();
                    if (numValue == (long) numValue) {
                        return String.valueOf((long) numValue);
                    } else {
                        return String.valueOf(numValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    /**
     * Validate required fields in a row
     * @param row Row data
     * @param requiredIndices Indices of required columns
     * @return true if all required fields have values
     */
    public static boolean validateRequiredFields(String[] row, int... requiredIndices) {
        for (int index : requiredIndices) {
            if (index >= row.length || row[index] == null || row[index].trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Parse double value from string, return default value if parsing fails
     */
    public static double parseDouble(String value, double defaultValue) {
        try {
            return value == null || value.trim().isEmpty() ? defaultValue : Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Parse integer value from string, return default value if parsing fails
     */
    public static int parseInt(String value, int defaultValue) {
        try {
            return value == null || value.trim().isEmpty() ? defaultValue : Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
