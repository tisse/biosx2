package tisse.filter;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Set;

public class EventFilter {

    private BigInteger subjectId;

    private Set<BigInteger> subjectIds;

    private BigInteger objectId;

    private BigInteger depId;

    private Set<BigInteger> depIds;

    private Calendar dateLocal;

    private Integer limit;

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

    public Calendar getDateLocal() {
        return dateLocal;
    }

    public void setDateLocal(Calendar dateLocal) {
        this.dateLocal = dateLocal;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public BigInteger getDepId() {
        return depId;
    }

    public void setDepId(BigInteger depId) {
        this.depId = depId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EventFilter{");
        sb.append("subjectId=").append(subjectId);
        sb.append("subjectIds=").append(subjectIds);
        sb.append(", objectId=").append(objectId);
        sb.append(", depId=").append(depId);
        sb.append(", depIds=").append(depIds );
        sb.append(", dateLocal=").append(DateFormatUtils.format(dateLocal, "dd.MM.yyyy"));
        sb.append(", limit=").append(limit);
        sb.append('}');
        return sb.toString();
    }

    public Set<BigInteger> getDepIds() {
        return depIds;
    }

    public void setDepIds(Set<BigInteger> depIds) {
        this.depIds = depIds;
    }

    public Set<BigInteger> getSubjectIds() {
        return subjectIds;
    }

    public void setSubjectIds(Set<BigInteger> subjectIds) {
        this.subjectIds = subjectIds;
    }
}
