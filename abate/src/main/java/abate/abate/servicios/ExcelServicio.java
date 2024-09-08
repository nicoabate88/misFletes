package abate.abate.servicios;

import abate.abate.entidades.Camion;
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
    
    public void exportHtmlToExcelCombustible(String htmlContent, HttpServletResponse response, Double consumo) throws IOException {
        Document doc = Jsoup.parse(htmlContent);
        Elements tables = doc.select("table");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Combustible");
        
        // Crear estilos para el título y el subtítulo
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 11);
        titleStyle.setFont(titleFont);

        int rowIndex = 0;

        // Escribir el título
        Row titleRow = sheet.createRow(rowIndex++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Consumo "+consumo+" L / 100 Km");
        titleCell.setCellStyle(titleStyle);

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
        response.setHeader("Content-Disposition", "attachment; filename=Combustible.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
    
      public void exportHtmlToExcelEstadistica(String htmlContent, HttpServletResponse response, Camion camion) throws IOException {
        Document doc = Jsoup.parse(htmlContent);
        Elements tables = doc.select("table");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Estadistica");
        
        // Crear estilos para el título y el subtítulo
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 11);
        titleStyle.setFont(titleFont);

        int rowIndex = 0;

        // Escribir el título
        Row titleRow = sheet.createRow(rowIndex++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(camion.getDominio()+' '+camion.getMarca()+' '+camion.getModelo());
        titleCell.setCellStyle(titleStyle);

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
        response.setHeader("Content-Disposition", "attachment; filename=Estadistica.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
   



