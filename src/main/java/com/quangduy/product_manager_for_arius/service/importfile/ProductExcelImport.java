package com.quangduy.product_manager_for_arius.service.importfile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.quangduy.product_manager_for_arius.entity.Category;
import com.quangduy.product_manager_for_arius.entity.Product;
import com.quangduy.product_manager_for_arius.repository.CategoryRepository;
import com.quangduy.product_manager_for_arius.service.S3FileUploadService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;

@Service
public class ProductExcelImport {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private S3FileUploadService s3FileUploadService;
    @NonFinal
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    @NonFinal
    static String[] HEADERs = { "Name", "Thumbnail", "Sliders", "Price", "Price", "Color", "Category", "Tags" };
    @NonFinal
    static String SHEET = "Products";
    @NonFinal
    static String DIRECTORY = "D:/Workspace/project/images";

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
                                    String fileName = "product_" + UUID.randomUUID().toString();
                                    File tempFile = File.createTempFile(fileName,
                                            "." + extension,
                                            new File(DIRECTORY));
                                    Files.write(tempFile.toPath(), imageBytes);
                                    String imageUrl = this.s3FileUploadService.uploadFile(tempFile,
                                            "quangduy/thumbnail", fileName);

                                    // Set the path as a property for the Product
                                    imagePath = imageUrl;
                                }
                            }

                            stu.setThumbnail(imagePath);
                            break;
                        case 2:
                            Drawing<?> drawing1 = currentRow.getSheet().createDrawingPatriarch();
                            List<? extends PictureData> pictures1 = workbook.getAllPictures();
                            String imagePath1 = null;

                            for (PictureData pictureData : pictures1) {
                                if (pictureData instanceof XSSFPictureData) {
                                    XSSFPictureData xssfPictureData = (XSSFPictureData) pictureData;

                                    // Save or return the image bytes
                                    byte[] imageBytes = xssfPictureData.getData();
                                    String extension = xssfPictureData.suggestFileExtension();
                                    String fileName = "product_" + UUID.randomUUID().toString();
                                    File tempFile = File.createTempFile(fileName,
                                            "." + extension,
                                            new File(DIRECTORY));
                                    Files.write(tempFile.toPath(), imageBytes);
                                    String imageUrl = this.s3FileUploadService.uploadFile(tempFile,
                                            "quangduy/sliders", fileName);

                                    // Set the path as a property for the Product
                                    imagePath1 = imageUrl;
                                }
                            }
                            List<String> sliders = new ArrayList<String>();
                            sliders.add(imagePath1);
                            stu.setSliders(sliders);
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
                        case 6:

                            stu.setTags(null);
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
