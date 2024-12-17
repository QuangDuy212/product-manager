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
import com.quangduy.product_manager_for_arius.entity.Tag;
import com.quangduy.product_manager_for_arius.repository.CategoryRepository;
import com.quangduy.product_manager_for_arius.repository.TagRepository;
import com.quangduy.product_manager_for_arius.service.S3FileUploadService;
import com.quangduy.product_manager_for_arius.service.TagService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;

@Service
public class ProductExcelImport {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private S3FileUploadService s3FileUploadService;
    @Autowired
    private TagRepository tagRepository;
    @NonFinal
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    @NonFinal
    static String[] HEADERs = { "Name", "Thumbnail", "Sliders", "Price", "ShortDes", "Category", "Tags",
            "Quantity", "Discount" };
    @NonFinal
    static String SHEET = "Products";
    @NonFinal
    static String DIRECTORY = "D:/Workspace/project/images";

    public boolean hasExcelFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    public List<Tag> convertStringToListTag(String t) {
        String[] temp = t.split(",");
        List<Tag> tags = new ArrayList<>();
        for (String i : temp) {
            Tag tag = this.tagRepository.findByName(i);
            tags.add(tag);
        }
        return tags;
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
                            stu.setThumbnail(currentCell.getStringCellValue());
                            break;
                        case 2:
                            List<String> sliders = new ArrayList<String>();
                            sliders.add(currentCell.getStringCellValue());
                            stu.setSliders(sliders);
                            break;
                        case 3:
                            stu.setPrice(currentCell.getNumericCellValue());
                            break;
                        case 4:
                            stu.setShortDes(currentCell.getStringCellValue());
                            break;
                        case 5:
                            String category = currentCell.getStringCellValue();
                            Category entity = this.categoryRepository.findByName(category);
                            stu.setCategory(entity);
                            break;
                        case 6:
                            String t = currentCell.getStringCellValue();
                            List<Tag> tags = this.convertStringToListTag(t);
                            stu.setTags(tags);
                            break;
                        case 7:
                            stu.setQuantity((long) currentCell.getNumericCellValue());
                            break;
                        case 8:
                            stu.setDiscount(currentCell.getNumericCellValue());
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
