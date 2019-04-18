package tisse.controller;

import org.primefaces.model.UploadedFile;
import tisse.dto.DepInfo;
import tisse.dto.MailAddress;
import tisse.service.manager.DepInfoManager;
import tisse.service.TimeShiftLoader;
import tisse.service.holder.MailAddressHolder;
import tisse.service.manager.MailAddressManager;
import tisse.service.holder.TimeShiftHolder;
import tisse.util.TimeShiftUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ViewScoped
@ManagedBean
public class DepBean extends BaseBean {

    private UploadedFile file;

    @Inject
    private TimeShiftLoader timeShiftLoader;
    @Inject
    private DepInfoManager depInfoManager;
    @Inject
    private TimeShiftHolder timeShiftHolder;
    @Inject
    private MailAddressHolder mailAddressHolder;
    @Inject
    private MailAddressManager mailAddressManager;

    private List<DepInfo> depInfos;

    @Override
    public String getTitle() {
        return "Загруженные подразделения";
    }

    @PostConstruct
    private void init() {
        depInfos = timeShiftHolder.getTimeShifts().stream().map(TimeShiftUtils::convert).collect(Collectors.toList());
        fillMailAddress();
    }

    private void fillMailAddress() {
        Map<String, List<String>> mailMap = mailAddressHolder.getMailMap();
        depInfos.forEach(depInfo -> depInfo.setMails(mailMap.get(depInfo.getUuid())));
    }


    public List<DepInfo> getDepInfos() {
        return depInfos;
    }

    public void setDepInfos(List<DepInfo> depInfos) {
        this.depInfos = depInfos;
    }

    public void upload() {
        if (file != null) {
            depInfos = timeShiftLoader.loadDepInfos(new ByteArrayInputStream(file.getContents()));
            fillMailAddress();
            infoMessage(String.format("%s is uploaded. %d items", file.getFileName(), depInfos.size()));
        }
    }

    public void uploadMails() {
        if (file != null) {
            clearMails();
            List<MailAddress> mailAddressList = mailAddressManager.load(new ByteArrayInputStream(file.getContents()));
            Map<String, List<String>> mailMap = new HashMap<>();
            mailAddressList.forEach(mailAddress -> mailMap.computeIfAbsent(mailAddress.getDepId(), key -> new ArrayList<>()).add(mailAddress.getMail()));
            depInfos.forEach(depInfo -> depInfo.setMails(mailMap.get(depInfo.getUuid())));
            infoMessage(String.format("%s is uploaded. %d items", file.getFileName(), depInfos.size()));
        }
    }

    public void clearMails() {
        depInfos.forEach(depInfo -> depInfo.setMails(null));
        infoMessage("mails is cleanes.");
    }

    public void save() {
        depInfoManager.save(depInfos);
        infoMessage(String.format("depInfos is saved. %d items", depInfos.size()));
    }

    public void restore() {
        depInfos = depInfoManager.load();
        fillMailAddress();
        infoMessage(String.format("depInfos is restored. %d items", depInfos.size()));
    }

    public void assign() {
        timeShiftHolder.update(depInfos.stream().map(TimeShiftUtils::convert).collect(Collectors.toList()));
        infoMessage(String.format("timeshifts is updated. %d items", depInfos.size()));
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
}
