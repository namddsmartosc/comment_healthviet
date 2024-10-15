package step;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Excel {

    // Get Workbook
    private static Workbook getWorkbook(InputStream inputStream, String excelFilePath) throws IOException {
        Workbook workbook;
        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (excelFilePath.endsWith("xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }

        return workbook;
    }

    // Get cell value
    private static Object getCellValue(Cell cell) {
        CellType cellType = cell.getCellType();
        Object cellValue = null;
        switch (cellType) {
            case BOOLEAN:
                cellValue = cell.getBooleanCellValue();
                break;
            case FORMULA:
                Workbook workbook = cell.getSheet().getWorkbook();
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                cellValue = evaluator.evaluate(cell).getNumberValue();
                break;
            case NUMERIC:
                cellValue = cell.getNumericCellValue();
                break;
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            case _NONE:
            case BLANK:
            case ERROR:
            default:
                break;
        }
        return cellValue;
    }

    public static List<CommentModel> readData(String excelPath) throws IOException {
        List<CommentModel> listData = new ArrayList<>();
        // Get file
        InputStream inputStream = Files.newInputStream(new File(excelPath).toPath());
        // Get workbook
        Workbook workbook = getWorkbook(inputStream, excelPath);
        // Get sheet
        Sheet sheet = workbook.getSheetAt(0);
        // Get all rows
        for (Row nextRow : sheet) {
            if (nextRow.getRowNum() == 0) {
                // Ignore header
                continue;
            }
            // Get all cells
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            // Read cells and set value for book object
            CommentModel commentModel = new CommentModel();
            while (cellIterator.hasNext()) {
                // Read cell
                Cell cell = cellIterator.next();
                Object cellValue = getCellValue(cell);
                if (cellValue == null || cellValue.toString().isEmpty()) {
                    continue;
                }
                // Set value for book object
                int columnIndex = cell.getColumnIndex();
                switch (columnIndex) {
                    case 1:
                        commentModel.setName((String) getCellValue(cell));
                        break;
                    case 3:
                        commentModel.setContent((String) getCellValue(cell));
                        break;
                    default:
                        break;
                }
            }
            if (commentModel.getName() == null || commentModel.getName().isEmpty()
                    || commentModel.getContent() == null || commentModel.getContent().isEmpty()) {
                continue;
            }
            listData.add(commentModel);
        }
        workbook.close();
        inputStream.close();

        return listData;
    }

}
