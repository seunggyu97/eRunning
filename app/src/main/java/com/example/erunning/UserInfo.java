package com.example.erunning;

public class UserInfo {
    private String name;
    private int birthyear;
    private int birthmonth;
    private int birthday;
    public UserInfo(String name, int birthyear, int birthmonth, int birthday){
        this.name = name;
        this.birthyear = birthyear;
        this.birthmonth = birthmonth;
        this.birthday = birthday;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public int getBirthyear(){
        return this.birthyear;
    }
    public void setBirthyear(int birthyear){
        this.birthyear = birthyear;
    }
    public int getBirthmonth(){
        return this.birthmonth;
    }
    public void setBirthmonth(int birthmonth){
        this.birthmonth = birthmonth;
    }
    public int getBirthday(){
        return this.birthday;
    }
    public void setBirthday(int birthday){
        this.birthday = birthday;
    }
}
