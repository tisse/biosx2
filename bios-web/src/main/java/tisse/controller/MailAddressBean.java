package tisse.controller;

import tisse.dto.MailAddress;
import tisse.service.holder.MailAddressHolder;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@ManagedBean
public class MailAddressBean {

    @Inject
    private MailAddressHolder mailAddressHolder;

    private Map<String, List<String>> mailMap;

    private List<MailAddress> mailAddresses;

    @PostConstruct
    private void init() {
        refresh();
    }

    private void refresh() {
        mailMap = mailAddressHolder.getMailMap();
        mailAddresses = mailAddressHolder.getMailAddresses();
    }

    public Map<String, List<String>> getMailMap() {
        return mailMap;
    }

    public List<MailAddress> getMailAddresses() {
        return mailAddresses;
    }

    public void setMailAddresses(List<MailAddress> mailAddresses) {
        this.mailAddresses = mailAddresses;
    }
}

