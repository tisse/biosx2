package tisse.service.manager;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import tisse.dto.MailAddress;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class MailAddressManager {

    @Inject
    private Logger logger;

    public List<MailAddress> load(ByteArrayInputStream byteArrayInputStream) {
        List<MailAddress> mailAddresses = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (Row currentRow : sheet) {
                if (null != currentRow && null != currentRow.getCell(0) && null != currentRow.getCell(2)) {
                    MailAddress mailAddress = new MailAddress();
                    mailAddress.setDepId(currentRow.getCell(0).getStringCellValue());
                    mailAddress.setMail(currentRow.getCell(2).getStringCellValue());
                    mailAddresses.add(mailAddress);
                }
            }
            logger.info("{} items loaded", mailAddresses.size());
        } catch (IOException e) {
            logger.error("", e);
        }
        return mailAddresses;
    }


}
