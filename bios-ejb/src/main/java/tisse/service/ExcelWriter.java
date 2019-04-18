package tisse.service;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import tisse.dto.DtoExcel;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

@Stateless
public class ExcelWriter extends BaseReportWriter<DtoExcel> {
    private static final String HH_MM_SS = "HH:mm:ss";
    private static final String[] HEADERS = {"Подразделение", "Табельный номер", "Фамилия", "Имя", "Приход", "Уход", "Обед", "С обеда"};

    @Inject
    private Logger logger;

    @Override
    public byte[] process(List<DtoExcel> dtos) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("main");
            int i = 0;
            XSSFRow rowHeader = sheet.createRow(i++);
            for (int k = 0; k < HEADERS.length; k++) {
                XSSFCell cell = rowHeader.createCell(k);
                cell.setCellValue(HEADERS[k]);
            }
            for (DtoExcel dto : dtos) {
                XSSFRow row = sheet.createRow(i++);
                int col = 0;
                row.createCell(col++).setCellValue(dto.getDepartment());
                row.createCell(col++).setCellValue(dto.getTabNum());
                row.createCell(col++).setCellValue(dto.getLastName());
                row.createCell(col++).setCellValue(dto.getFirstName());
                row.createCell(col++).setCellValue((dto.getWorkIn()));
                row.createCell(col++).setCellValue((dto.getWorkOut()));
                row.createCell(col++).setCellValue((dto.getDinnerIn()));
                row.createCell(col).setCellValue((dto.getDinnerOut()));
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            workbook.write(stream);
            return stream.toByteArray();
        } catch (IOException e) {
            logger.error("", e);
        }
        return null;
    }

    private String format(Calendar calendar) {
        return null != calendar ? DateFormatUtils.format(calendar, HH_MM_SS) : "";
    }
}
