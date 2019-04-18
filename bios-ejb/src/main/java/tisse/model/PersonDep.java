package tisse.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "workerdepart", schema = "public")
@Cacheable
public class PersonDep implements Serializable{

    @Id
    @Column(name = "worker_id")
    private Integer workerId;
    @Column(name = "dep_id")
    private Integer depId;

    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public Integer getDepId() {
        return depId;
    }

    public void setDepId(Integer depId) {
        this.depId = depId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("workerId", workerId)
                .append("depId", depId)
                .toString();
    }
}
