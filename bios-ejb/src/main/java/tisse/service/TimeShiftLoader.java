package tisse.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import tisse.dto.DepInfo;
import tisse.dto.TimeShift;
import tisse.util.TimeShiftUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class TimeShiftLoader {

    @Inject
    private Logger logger;

    public List<TimeShift> load(ByteArrayInputStream byteArrayInputStream) {
        return loadDepInfos(byteArrayInputStream)
                .stream().map(TimeShiftUtils::convert).collect(Collectors.toList());
    }

    public List<DepInfo> loadDepInfos(ByteArrayInputStream byteArrayInputStream) {
        List<DepInfo> depInfos = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (Row currentRow : sheet) {
                if (currentRow.getRowNum() > 0)
                    if (null != currentRow.getCell(1) && null != currentRow.getCell(2)) {
                        DepInfo depInfo = new DepInfo();
                        Cell cell = currentRow.getCell(0);
                        if (cell.getCellType() == CellType.STRING) {
                            depInfo.setName(cell.getStringCellValue());
                        }
                        depInfo.setUuid(currentRow.getCell(1).getStringCellValue());
                        depInfo.setTimeShift(Double.valueOf(currentRow.getCell(2).getNumericCellValue()).intValue());
                        depInfos.add(depInfo);
                    }
            }
        } catch (IOException e) {
            logger.error("", e);
        }

        return depInfos;
    }

}
