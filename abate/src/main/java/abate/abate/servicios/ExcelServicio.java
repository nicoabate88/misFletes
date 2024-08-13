package abate.abate.servicios;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class ExcelServicio {

    public void exportHtmlToExcel(String htmlContent, HttpServletResponse response) throws IOException {
        Document doc = Jsoup.parse(htmlContent);
        Elements tables = doc.select("table");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("MisFletes");

        int rowIndex = 0;

        sheet.createRow(rowIndex++);

        for (Element table : tables) {
            for (Element row : table.select("tr")) {
                Row excelRow = sheet.createRow(rowIndex++);
                int colIndex = 0;
                for (Element cell : row.select("th, td")) {
                    Cell excelCell = excelRow.createCell(colIndex++);
                    String cellText = cell.text();

                    try {
                        // Intenta convertir el texto en un número
                        double numericValue = Double.parseDouble(cellText);
                        excelCell.setCellValue(numericValue);
                    } catch (NumberFormatException e) {
                        // Si no es un número, se guarda como texto
                        excelCell.setCellValue(cellText);
                    }
                }
            }
        }

        int columnCount = 0;
        if (sheet.getRow(3) != null) {
            columnCount = sheet.getRow(3).getPhysicalNumberOfCells();
        }

        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MisFletes.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
   



