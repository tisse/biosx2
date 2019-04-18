package tisse.model;

import tisse.dto.EventKind;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

import static tisse.dto.EventKind.*;

@Entity
@Table(name = "log_indexed", schema = "public")
public class SimpleEvent extends BaseEntity {

    @Column(name = "date")
    private BigInteger date;

    @Column(name = "date_local")
    private BigInteger dateLocal;

    @Column(name = "subject_id", insertable = false, updatable = false)
    private BigInteger subjectId;

    @Column(name = "object_id")
    private BigInteger objectId;

    @Column(name = "card_id")
    private BigInteger cardId;

    @Column(name = "event")
    private Integer eventId;

    @Column(name = "firm")
    private BigInteger firm;

    @Column(name = "internal_id")
    private BigInteger internal;

    @Column(name = "curtime")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar curTime;

    @Transient
    private LocalDateTime eventDateLocal;

    @Transient
    private LocalDateTime eventDate;

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

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    @PostLoad
    private void onLoad() {
        eventDate = Instant.ofEpochSecond(this.date.longValue()).atZone(ZoneId.of("UTC")).toLocalDateTime();
        eventDateLocal = Instant.ofEpochSecond(this.dateLocal.longValue()).atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    @Transient
    public EventType getEventType() {
        EventType byCode = null;
        if (null != eventId) {
            byCode = EventType.getByCode(eventId);
        }
        return byCode;
    }

    @Transient
    public EventKind getEventTypeValue() {
        switch (getEventType()) {
            case EVENT_IN:
            case EVENT_CARD_IN:
                return WI;
            case EVENT_OUT:
            case EVENT_CARD_OUT:
                return WO;
            case EVENT_TO_LAUNCH:
                return DI;
            case EVENT_FROM_LAUNCH:
                return DO;
            default:
                return null;
        }
    }

    public BigInteger getFirm() {
        return firm;
    }

    public void setFirm(BigInteger firm) {
        this.firm = firm;
    }

    public LocalDateTime getEventDateLocal() {
        return eventDateLocal;
    }

    public void setEventDateLocal(LocalDateTime eventDateLocal) {
        this.eventDateLocal = eventDateLocal;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }


}
