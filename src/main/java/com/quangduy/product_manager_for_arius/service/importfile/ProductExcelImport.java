package com.quangduy.product_manager_for_arius.service.importfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.quangduy.product_manager_for_arius.entity.Category;
import com.quangduy.product_manager_for_arius.entity.Product;
import com.quangduy.product_manager_for_arius.entity.User;
import com.quangduy.product_manager_for_arius.repository.CategoryRepository;
import com.quangduy.product_manager_for_arius.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
public class ProductExcelImport {
    private CategoryRepository categoryRepository;
    @NonFinal
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    @NonFinal
    static String[] HEADERs = { "Name", "Thumbnail", "Sliders", "Price", "Price", "Color", "Category", "Tags" };
    @NonFinal
    static String SHEET = "Products";

    public boolean hasExcelFormat(MultipartFile file) {
        var test = file.getContentType();
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    public List<Product> excelToStuList(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();
            List<Product> stuList = new ArrayList<Product>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                Iterator<Cell> cellsInRow = currentRow.iterator();
                Product stu = new Product();
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 0:
                            stu.setName(currentCell.getStringCellValue());
                            break;
                        case 1:
                            Drawing<?> drawing = currentRow.getSheet().createDrawingPatriarch();
                            List<? extends PictureData> pictures = workbook.getAllPictures();
                            String imagePath = null;

                            for (PictureData pictureData : pictures) {
                                if (pictureData instanceof XSSFPictureData) {
                                    // Extract picture details
                                    XSSFPictureData xssfPictureData = (XSSFPictureData) pictureData;

                                    // Save or return the image bytes
                                    byte[] imageBytes = xssfPictureData.getData();

                                    String extension = xssfPictureData.suggestFileExtension();
                                    String fileName = "image_" + Instant.now() + "." + extension;

                                    // Example: Save image to a specific directory
                                    Path outputPath = Paths.get(fileName);
                                    Files.write(outputPath, imageBytes);

                                    // Set the path as a property for the Product
                                    imagePath = outputPath.toString();
                                    imagePath = fileName;

                                }
                            }
                            stu.setThumbnail(imagePath);
                            break;
                        case 2:
                            // Drawing<?> drawing1 = currentRow.getSheet().createDrawingPatriarch();
                            // List<? extends PictureData> pictures1 = workbook.getAllPictures();
                            // List<String> imagePaths = new ArrayList<>();

                            // for (PictureData pictureData : pictures1) {
                            // if (pictureData instanceof XSSFPictureData) {
                            // XSSFPictureData xssfPictureData = (XSSFPictureData) pictureData;

                            // // Check if the image belongs to the current cell
                            // for (POIXMLDocumentPart.RelationPart part : ((XSSFPictureData) pictureData)
                            // .getParent().getRelationParts()) {
                            // if (part.getDocumentPart() instanceof XSSFPicture) {
                            // XSSFPicture picture = (XSSFPicture) part.getDocumentPart();
                            // ClientAnchor anchor = picture.getClientAnchor();

                            // if (anchor != null && anchor.getRow1() == currentRow.getRowNum() &&
                            // anchor.getCol1() == currentCell.getColumnIndex()) {

                            // // Save the image
                            // byte[] imageBytes = xssfPictureData.getData();
                            // String extension = xssfPictureData.suggestFileExtension();
                            // String fileName = "image_cell_" + currentRow.getRowNum() + "_" +
                            // currentCell.getColumnIndex() + "_" + imagePaths.size() + "."
                            // + extension;

                            // Path outputPath = Paths.get("output/directory", fileName);
                            // Files.write(outputPath, imageBytes);

                            // // Add the file path to the list
                            // imagePaths.add(outputPath.toString());
                            // }
                            // }
                            // }
                            // }
                            // }
                            break;
                        case 3:
                            stu.setPrice(currentCell.getNumericCellValue());
                            break;
                        case 4:
                            stu.setColor(currentCell.getStringCellValue());
                            break;
                        case 5:
                            String category = currentCell.getStringCellValue();
                            Category entity = this.categoryRepository.findByName(category);
                            stu.setCategory(entity);
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                stuList.add(stu);
            }
            workbook.close();
            return stuList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
