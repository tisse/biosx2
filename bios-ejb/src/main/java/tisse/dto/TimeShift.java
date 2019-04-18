package tisse.dto;

public class TimeShift {

    private String name;
    private String depGuId;
    private Integer timeShift;

    public Integer getTimeShift() {
        return timeShift;
    }

    public void setTimeShift(Integer timeShift) {
        this.timeShift = timeShift;
    }

    public String getDepGuId() {
        return depGuId;
    }

    public void setDepGuId(String depGuId) {
        this.depGuId = depGuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
