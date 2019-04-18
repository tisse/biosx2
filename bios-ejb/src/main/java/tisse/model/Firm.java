package tisse.model;

public class Firm {

    private Integer id;
    private String name;
    private String departmentId;
    private String biosmartId;
    private String guid1c;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getBiosmartId() {
        return biosmartId;
    }

    public void setBiosmartId(String biosmartId) {
        this.biosmartId = biosmartId;
    }

    public String getGuid1c() {
        return guid1c;
    }

    public void setGuid1c(String guid1c) {
        this.guid1c = guid1c;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
