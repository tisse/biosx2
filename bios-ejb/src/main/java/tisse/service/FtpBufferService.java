package tisse.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import tisse.dto.MailData;
import tisse.service.ftp.FTPAuthException;
import tisse.service.ftp.FTPBusiness;
import tisse.service.ftp.FTPConnectException;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
@TransactionAttribute(TransactionAttributeType.NEVER)
public class FtpBufferService {

    @Inject
    private Logger logger;

    @Inject
    private FTPBusiness ftpBusiness;

    @Schedule(minute = "*/5", hour = "*")
    public void process() {
        logger.info("FtpBufferService process start");
        String dirName = System.getProperty("bios.ftp.buffer");
        List<Path> paths = new ArrayList<>();
        try (Stream<Path> list = Files.list(new File(dirName).toPath())) {
            paths = list
                    .filter(path -> path.toString().endsWith("csv"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("", e);
        }
        if (CollectionUtils.isNotEmpty(paths)) {
            paths.forEach(path -> {
                try {
                    logger.info("process {}", path);
                    String remote = ftpBusiness.write(createMailData(dirName, path));
                    if (StringUtils.isNotEmpty(remote)) {
                        Files.delete(path);
                    }
                } catch (IOException | FTPAuthException | FTPConnectException e) {
                    logger.error("", e);
                }
            });
        }
        logger.info("FtpBufferService process finish");
    }

    private MailData createMailData(String dirName, Path path) throws IOException {
        MailData data = new MailData();
        data.setName(path.toString().replace(dirName, ""));
        data.setContent(Files.readAllBytes(path));
        data.setType("text/csv");
        return data;
    }

}
