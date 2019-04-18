package tisse.dto;

import org.apache.commons.lang3.time.DateFormatUtils;
import tisse.model.EventType;

import java.math.BigInteger;
import java.util.Calendar;

public class EventDto {

    private Long id;
    private BigInteger date;
    private BigInteger dateLocal;
    private BigInteger subjectId;
    private BigInteger objectId;
    private BigInteger cardId;
    private Integer eventId;
    private BigInteger firm;
    private BigInteger dept;
    private BigInteger internal;
    private Calendar curTime;

    private String firstName;
    private String lastName;
    private String depName;

    private String dateLocalValue;
    private String dateValue;
    private String dateShortValue;
    private EventType eventType;
    private EventKind eventKind;
    private String curTimeValue;

    public String getDateLocalValue() {
        return dateLocalValue;
    }

    public void setDateLocalValue(String dateLocalValue) {
        this.dateLocalValue = dateLocalValue;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public BigInteger getDate() {
        return date;
    }

    public void setDate(BigInteger date) {
        this.date = date;
    }

    public BigInteger getDateLocal() {
        return dateLocal;
    }

    public void setDateLocal(BigInteger dateLocal) {
        this.dateLocal = dateLocal;
    }

    public BigInteger getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(BigInteger subjectId) {
        this.subjectId = subjectId;
    }

    public BigInteger getObjectId() {
        return objectId;
    }

    public void setObjectId(BigInteger objectId) {
        this.objectId = objectId;
    }

    public BigInteger getCardId() {
        return cardId;
    }

    public void setCardId(BigInteger cardId) {
        this.cardId = cardId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public BigInteger getInternal() {
        return internal;
    }

    public void setInternal(BigInteger internal) {
        this.internal = internal;
    }

    public Calendar getCurTime() {
        return curTime;
    }

    public void setCurTime(Calendar curTime) {
        this.curTime = curTime;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public BigInteger getFirm() {
        return firm;
    }

    public void setFirm(BigInteger firm) {
        this.firm = firm;
    }

    public String getCurTimeValue() {
        return curTimeValue;
    }

    public void setCurTimeValue(String curTimeValue) {
        this.curTimeValue = curTimeValue;
    }

    public BigInteger getDept() {
        return dept;
    }

    public void setDept(BigInteger dept) {
        this.dept = dept;
    }

    public String getDateValue() {
        return dateValue;
    }

    public void setDateValue(String dateValue) {
        this.dateValue = dateValue;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EventDto{");
        sb.append("date=").append(date);
        sb.append(", dateLocal=").append(dateLocal);
        sb.append(", subjectId=").append(subjectId);
        sb.append(", objectId=").append(objectId);
        sb.append(", cardId=").append(cardId);
        sb.append(", eventId=").append(eventId);
        sb.append(", firm=").append(firm);
        sb.append(", dept=").append(dept);
        sb.append(", internal=").append(internal);
        sb.append(", curTime=").append(DateFormatUtils.format(curTime, "dd.MM.yyyy HH:mm:ss"));
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", depName='").append(depName).append('\'');
        sb.append(", dateLocalValue='").append(dateLocalValue).append('\'');
        sb.append(", dateValue='").append(dateValue).append('\'');
        sb.append(", eventType=").append(eventType);
        sb.append(", eventKind='").append(eventKind).append('\'');
        sb.append(", curTimeValue='").append(curTimeValue).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDateShortValue() {
        return dateShortValue;
    }

    public void setDateShortValue(String dateShortValue) {
        this.dateShortValue = dateShortValue;
    }

    public EventKind getEventKind() {
        return eventKind;
    }

    public void setEventKind(EventKind eventKind) {
        this.eventKind = eventKind;
    }
}
