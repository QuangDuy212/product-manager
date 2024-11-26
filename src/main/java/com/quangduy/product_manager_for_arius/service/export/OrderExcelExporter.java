package com.quangduy.product_manager_for_arius.service.export;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.quangduy.product_manager_for_arius.dto.response.OrderResponse;
import com.quangduy.product_manager_for_arius.dto.response.UserResponse;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class OrderExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<OrderResponse> data;

    public OrderExcelExporter(List<OrderResponse> data) {
        this.data = data;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Users");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Order ID", style);
        createCell(row, 1, "Total price", style);
        createCell(row, 2, "Reciver name", style);
        createCell(row, 3, "Reciver address", style);
        createCell(row, 4, "Reciver phone", style);
        createCell(row, 5, "Status", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (OrderResponse i : data) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, i.getId(), style);
            createCell(row, columnCount++, String.valueOf(i.getTotalPrice()), style);
            createCell(row, columnCount++, i.getReciverName(), style);
            createCell(row, columnCount++, i.getReciverAddress(), style);
            createCell(row, columnCount++, i.getReciverPhone(), style);
            createCell(row, columnCount++, i.getStatus(), style);

        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}
