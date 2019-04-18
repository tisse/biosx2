package tisse.service;

import com.opencsv.CSVWriter;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import tisse.dto.Dto1C;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.List;

@Stateless
public class Csv1CWriter extends BaseReportWriter<Dto1C> {

    @Inject
    private Logger logger;

    @Override
    public byte[] process(List<Dto1C> dtos) {
        StringWriter writer = new StringWriter();
        try (CSVWriter csvWriter = new CSVWriter(writer,
                ';',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {
            for (Dto1C dto : dtos) {
                String[] strings = new String[]{dto.getDepartment(),
                        dto.getTabNum(),
                        dto.getLastName(),
                        dto.getFirstName(),
                        dto.getDate(),
                        dto.getDateFirst(),
                        dto.getDateNext(),
                        String.valueOf(dto.getMinutes()),
                        dto.getMark(),
                        dto.getState(),
                        dto.getJobGuid(),
                        dto.getStatus(),
                        dto.getDescription(),
                };
                csvWriter.writeNext(strings);
            }
            return writer.toString().getBytes("UTF-8");
        } catch (IOException e) {
            logger.error("", e);
        }

        return null;
    }

}
