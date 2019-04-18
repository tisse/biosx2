package tisse.controller;

import tisse.service.mail.MailSender;
import tisse.service.mail.MailSettingHolder;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

@ManagedBean
@ViewScoped
public class MailTestBean extends BaseBean {

    @Inject
    private MailSender mailSender;

    @Inject
    private MailSettingHolder mailSettingHolder;

    public void mailTest() {
        boolean test = mailSender.test();
        if (test) {
            infoMessage("Отправлено успешно");
        } else {
            errorMessage("Не отправлено");
        }
    }

    public boolean isMailEnabled() {
        return mailSettingHolder.getMailSetting().isEnabled();
    }

    public void mailEnable() {
        mailSettingHolder.getMailSetting().setEnabled(true);
        mailSettingHolder.save();
        showMailState();
    }

    public void mailDisable() {
        mailSettingHolder.getMailSetting().setEnabled(false);
        mailSettingHolder.save();
        showMailState();
    }

    private void showMailState() {
        infoMessage(String.format("Mail send is %s", isMailEnabled() ? "ENABLED" : "DISABLED"));
    }

    @Override
    public String getTitle() {
        return "Настройки почты";
    }
}
