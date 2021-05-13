package com.example.erunning;

public class PostInfo {
    private String contents;
    private String publisher;

    public PostInfo(String contents, String publisher){
        this.contents = contents;
        this.publisher = publisher;
    }
    /*
    public UserInfo(String name, int birthyear, int birthmonth, int birthday,String photoUrl){
        this.name = name;
        this.birthyear = birthyear;
        this.birthmonth = birthmonth;
        this.birthday = birthday;
        this.photoUrl = photoUrl;
    }*/
    public String getContents(){
        return this.contents;
    }
    public void setContents(String contents){
        this.contents = contents;
    }
    public String getPublisher(){
        return this.publisher;
    }
    public void setPublisher(String publisher){
        this.publisher = publisher;
    }

}
