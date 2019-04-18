package tisse.model;

import java.math.BigInteger;
import java.util.Calendar;

public class EventOld {

    private Calendar date;
    private Calendar dateLocal;
    private BigInteger subjectId;
    private BigInteger objectId;
    private BigInteger cardId;
    private Integer event;
    private BigInteger dept;
    private Calendar curTime;


    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Calendar getDateLocal() {
        return dateLocal;
    }

    public void setDateLocal(Calendar dateLocal) {
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

    public Integer getEvent() {
        return event;
    }

    public void setEvent(Integer event) {
        this.event = event;
    }

    public BigInteger getDept() {
        return dept;
    }

    public void setDept(BigInteger dept) {
        this.dept = dept;
    }

    public Calendar getCurTime() {
        return curTime;
    }

    public void setCurTime(Calendar curTime) {
        this.curTime = curTime;
    }
}
