package com.quangduy.product_manager_for_arius.service.export;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.quangduy.product_manager_for_arius.dto.response.ProductResponse;
import com.quangduy.product_manager_for_arius.dto.response.TagResponse;
import com.quangduy.product_manager_for_arius.dto.response.UserResponse;
import com.quangduy.product_manager_for_arius.entity.Tag;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.net.URL;
import java.util.List;

public class ProductExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<ProductResponse> data;

    public ProductExcelExporter(List<ProductResponse> data) {
        this.data = data;
        workbook = new XSSFWorkbook();
    }

    private String convertToString(List<TagResponse> tags) {
        String result = "";
        for (TagResponse i : tags) {
            String x = i.getName() + ",";
            result += x;
        }
        return result;
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Users");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "ID", style);
        createCell(row, 1, "Name", style);
        createCell(row, 2, "Short description", style);
        createCell(row, 3, "Quantity", style);
        createCell(row, 4, "Price", style);
        createCell(row, 5, "Discount", style);
        createCell(row, 6, "Category", style);
        createCell(row, 7, "Tags", style);
        createCell(row, 8, "Thumbnail", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else {
            cell.setCellValue((Boolean) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() throws IOException {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        var list = data;
        for (ProductResponse i : data) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, i.getId(), style);
            createCell(row, columnCount++, i.getName(), style);
            createCell(row, columnCount++, i.getShortDes(), style);
            createCell(row, columnCount++, i.getQuantity(), style);
            createCell(row, columnCount++, i.getPrice(), style);
            createCell(row, columnCount++, i.getDiscount(), style);
            createCell(row, columnCount++, i.getCategory() != null ? i.getCategory().getName() : "", style);
            createCell(row, columnCount++, convertToString(i.getTags()), style);

            // Thêm ảnh vào cột
            byte[] imageBytes = fetchImageFromURL(i.getThumbnail());
            if (imageBytes != null) {
                addImageToCell(row.getRowNum(), columnCount++, imageBytes);
            } else {
                createCell(row, columnCount++, "No Image", style); // Nếu không có ảnh
            }

        }
    }

    private void addImageToCell(int row, int col, byte[] imageBytes) {
        int pictureIdx = workbook.addPicture(imageBytes, XSSFWorkbook.PICTURE_TYPE_PNG); // Thêm ảnh vào workbook
        XSSFDrawing drawing = sheet.createDrawingPatriarch(); // Tạo đối tượng vẽ Drawing

        XSSFClientAnchor anchor = new XSSFClientAnchor();
        anchor.setCol1(col); // Cột bắt đầu
        anchor.setRow1(row); // Dòng bắt đầu
        anchor.setCol2(col + 1); // Cột kết thúc (chiếm nhiều hơn 1 cột)
        anchor.setRow2(row + 1); // Dòng kết thúc (chiếm nhiều hơn 1 dòng)
        anchor.setAnchorType(AnchorType.MOVE_AND_RESIZE);

        drawing.createPicture(anchor, pictureIdx); // Thêm ảnh vào vị trí
    }

    private byte[] fetchImageFromURL(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        try (InputStream inputStream = new URL(imageUrl).openStream()) {
            return IOUtils.toByteArray(inputStream); // Đọc ảnh thành byte[]
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
