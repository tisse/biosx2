package tisse.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "param_type", schema = "public")
public class ParamType extends BaseEntity{

    @Column(name = "obj_type")
    private Integer objType;

    @Column(name = "id_name")
    private String idName;

    @Column(name = "param_name")
    private String paramName;

    @Column(name = "param_type")
    private String paramType;

    @Column(name = "properties")
    private String properties;

    @Column(name = "pkey")
    private Boolean pkey;


    public Integer getObjType() {
        return objType;
    }

    public void setObjType(Integer objType) {
        this.objType = objType;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public Boolean getPkey() {
        return pkey;
    }

    public void setPkey(Boolean pkey) {
        this.pkey = pkey;
    }
}
