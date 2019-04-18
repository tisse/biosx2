package tisse.dto;

import java.util.Calendar;

public class DtoExcel implements Dto {

    private String department;
    private String tabNum;
    private String lastName;
    private String firstName;
    private String workIn;
    private String workOut;
    private String dinnerIn;
    private String dinnerOut;


    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTabNum() {
        return tabNum;
    }

    public void setTabNum(String tabNum) {
        this.tabNum = tabNum;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getWorkIn() {
        return workIn;
    }

    public void setWorkIn(String workIn) {
        this.workIn = workIn;
    }

    public String getWorkOut() {
        return workOut;
    }

    public void setWorkOut(String workOut) {
        this.workOut = workOut;
    }

    public String getDinnerIn() {
        return dinnerIn;
    }

    public void setDinnerIn(String dinnerIn) {
        this.dinnerIn = dinnerIn;
    }

    public String getDinnerOut() {
        return dinnerOut;
    }

    public void setDinnerOut(String dinnerOut) {
        this.dinnerOut = dinnerOut;
    }
}
