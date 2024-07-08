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

    public void exportHtmlToExcel(String htmlContent, String subtitle, String subtitle3, HttpServletResponse response) throws IOException {
        Document doc = Jsoup.parse(htmlContent);
        Elements tables = doc.select("table");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Exported Data");

        // Crear estilos para el título y el subtítulo
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 11);
        titleStyle.setFont(titleFont);

        int rowIndex = 0;

        // Escribir el título
        Row titleRow = sheet.createRow(rowIndex++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("PLANILLA DE KILOMETRAJE");
        titleCell.setCellStyle(titleStyle);

        // Escribir el subtítulo
        Row subtitleRow = sheet.createRow(rowIndex++);
        Cell subtitleCell = subtitleRow.createCell(0);
        subtitleCell.setCellValue(subtitle);
        subtitleCell.setCellStyle(titleStyle);

        Row subtitle1Row = sheet.createRow(rowIndex++);
        Cell subtitle1Cell = subtitle1Row.createCell(0);
        subtitle1Cell.setCellValue("CONVENCIÓN COLECTIVA DE TRABAJO N° ITEMS 4.2.3, 4.2.4, 4.2.5, 4.2.6, 4.2.17 Y 6.1.2");
        subtitle1Cell.setCellStyle(titleStyle);

        Row subtitle2Row = sheet.createRow(rowIndex++);
        Cell subtitle1Cel2 = subtitle2Row.createCell(0);
        subtitle1Cel2.setCellValue("NOMBRE DE LA FIRMA EMPLEADORA Y/O EMPLEADOR: ABATE SEBASTIÁN MAURICIO - CUIT: 23-32671022-9");
        subtitle1Cel2.setCellStyle(titleStyle);

        Row subtitle3Row = sheet.createRow(rowIndex++);
        Cell subtitle1Cel3 = subtitle3Row.createCell(0);
        subtitle1Cel3.setCellValue(subtitle3);
        subtitle1Cel3.setCellStyle(titleStyle);

        // Añadir una fila en blanco después del subtítulo
        sheet.createRow(rowIndex++);

        // Procesar la tabla HTML
        for (Element table : tables) {
            for (Element row : table.select("tr")) {
                Row excelRow = sheet.createRow(rowIndex++);
                int colIndex = 0;
                for (Element cell : row.select("th, td")) {
                    Cell excelCell = excelRow.createCell(colIndex++);
                    excelCell.setCellValue(cell.text());
                }
            }
        }

        // Autoajustar columnas
        for (int i = 0; i < sheet.getRow(3).getPhysicalNumberOfCells(); i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=exported_data.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

}
