package tisse.service.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import tisse.dto.MailData;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Stateless
public class FTPBusiness {

    @Inject
    private Logger logger;

    public String write(MailData mailData) throws FTPConnectException, FTPAuthException, IOException {
        FTPClient client = new FTPClient();

        try {
            FTPAuthInfo authInfo = produceFtpAuthInfo();
            client.connect(authInfo.getUrl(), authInfo.getPort());

            if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
                throw new FTPConnectException(String.format("Failed to connect to %s FTP-server [ftp://%s:%d]. Reason: %s",
                        authInfo.getServerName(), authInfo.getUrl(), authInfo.getPort(), client.getReplyString()));
            } else {
                if (client.login(authInfo.getLogin(), authInfo.getPass())) {
                    return write(client, mailData);
                } else {
                    throw new FTPAuthException(String.format("Authorization on  %s failed [ftp://%s:%d, %s, %s]. Reason: %s",
                            authInfo.getServerName(), authInfo.getUrl(), authInfo.getPort(), authInfo.getLogin(), authInfo.getPass(), client.getReplyString()));
                }
            }
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                logger.error("", e);
            }
        }
    }

    private String write(FTPClient client, MailData mailData) {
        try {
            String workDir = System.getProperty("bios.ftp.workdir");
            String remote = workDir.concat("/").concat(mailData.getName());
            logger.info(remote);
            client.storeFile(remote, new ByteArrayInputStream(mailData.getContent()));
            return remote;
        } catch (IOException e) {
            logger.error("", e);
        }
        return null;
    }

    private FTPAuthInfo produceFtpAuthInfo() {
        return new FTPAuthInfo("bios",
                System.getProperty("bios.ftp.host"),
                Integer.valueOf(System.getProperty("bios.ftp.port")),
                System.getProperty("bios.ftp.login"),
                System.getProperty("bios.ftp.pass"));

    }


}
