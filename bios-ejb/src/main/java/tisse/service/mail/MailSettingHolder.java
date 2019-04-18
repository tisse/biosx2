package tisse.service.mail;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Singleton
@Startup
public class MailSettingHolder {

    @Inject
    private Logger logger;

    private MailSetting mailSetting;

    public MailSetting getMailSetting() {
        return mailSetting;
    }

    @PostConstruct
    private void init() {
        if (exists()) {
            read();
        } else {
            mailSetting = new MailSetting();
            mailSetting.setEnabled(false);
            mailSetting.setBcc(Arrays.asList("markuszugrau@gmail.com", "6391589@gmail.com"));
            save();
        }
        logger.info("mailSetting init complete");
    }

    private String getFileName() {
        String biosDepDir = System.getProperty("bios.dep.path");
        return biosDepDir.concat("mail.setting").concat(".json");
    }

    private boolean exists() {
        Path path = Paths.get(getFileName());
        return path.toFile().exists();
    }

    public void save() {
        try {
            String fileName = getFileName();
            Files.write(Paths.get(fileName), new GsonBuilder().setPrettyPrinting().create().toJson(mailSetting).getBytes());
            logger.info("mailSetting saved to {}", fileName);
        } catch (IOException e) {
            logger.error("{}", e);
        }
    }

    private void read() {
        if (exists()) {
            try {
                Gson gson = new Gson();
                String fileName = getFileName();
                mailSetting = gson.fromJson(new FileReader(fileName), MailSetting.class);
                logger.info("mailSetting have read from {}", fileName);
            } catch (FileNotFoundException e) {
                logger.error("", e);
            }
        }

    }


}
