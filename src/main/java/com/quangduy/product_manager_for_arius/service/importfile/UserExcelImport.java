package com.quangduy.product_manager_for_arius.service.importfile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.quangduy.product_manager_for_arius.entity.Role;
import com.quangduy.product_manager_for_arius.entity.User;
import com.quangduy.product_manager_for_arius.service.RoleService;

@Service
public class UserExcelImport {
  private PasswordEncoder passwordEncoder;
  private RoleService roleService;

  public UserExcelImport(PasswordEncoder passwordEncoder, RoleService roleService) {
    this.passwordEncoder = passwordEncoder;
    this.roleService = roleService;
  }

  public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  static String[] HEADERs = { "Username", "Password", "Name", "Roles", "Address" };
  static String SHEET = "Users";

  public boolean hasExcelFormat(MultipartFile file) {
    var test = file.getContentType();
    if (!TYPE.equals(file.getContentType())) {
      return false;
    }
    return true;
  }

  public List<User> excelToStuList(InputStream is) {
    try {
      Workbook workbook = new XSSFWorkbook(is);
      Sheet sheet = workbook.getSheet(SHEET);
      Iterator<Row> rows = sheet.iterator();
      List<User> stuList = new ArrayList<User>();
      int rowNumber = 0;
      while (rows.hasNext()) {
        Row currentRow = rows.next();
        // skip header
        if (rowNumber == 0) {
          rowNumber++;
          continue;
        }
        Iterator<Cell> cellsInRow = currentRow.iterator();
        User stu = new User();
        int cellIdx = 0;
        while (cellsInRow.hasNext()) {
          Cell currentCell = cellsInRow.next();
          switch (cellIdx) {
            case 0:
              stu.setUsername(currentCell.getStringCellValue());
              break;
            case 1:
              String pass = passwordEncoder.encode(String.valueOf(currentCell.getNumericCellValue()));
              stu.setPassword(pass);
              break;
            case 2:
              stu.setName(currentCell.getStringCellValue());
              break;
            case 3:
              Role role = this.roleService.findByName(currentCell.getStringCellValue());
              stu.setRole(role);
              break;
            case 4:
              stu.setAddress(currentCell.getStringCellValue());
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
