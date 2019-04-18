package tisse.service.holder;

import org.slf4j.Logger;
import tisse.dto.MailAddress;
import tisse.service.manager.MailAddressManager;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@TransactionAttribute(TransactionAttributeType.NEVER)
public class MailAddressHolder {

    private Map<String, List<String>> mailMap;
    private List<MailAddress> mailAddresses;

    @Inject
    private Logger logger;

    @EJB
    private MailAddressManager mailAddressManager;

    @PostConstruct
    private void init() {
        refresh();
    }

    public void refresh() {
        mailMap = new HashMap<>();
        mailAddresses = new ArrayList<>();
        String path = System.getProperty("bios.mail.file");
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            mailAddresses = mailAddressManager.load(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            logger.error("", e);
        }
        mailAddresses.forEach(mailAddress -> mailMap.computeIfAbsent(mailAddress.getDepId(), key -> new ArrayList<>()).add(mailAddress.getMail()));
    }

    public List<String> getMail(String depId) {
        List<String> mail = mailMap.get(depId);
        logger.info("mail {}", mail);
        return mail;
    }

    public Map<String, List<String>> getMailMap() {
        return mailMap;
    }

    public List<MailAddress> getMailAddresses() {
        return mailAddresses;
    }
}
