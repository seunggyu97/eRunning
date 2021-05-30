package com.example.erunning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FlagCommentInfo implements Serializable {
    private String title;
    private String publisher;
    private Date createdAt;
    private String id;
    private String publisherName;
    private String photoUrl;

    public FlagCommentInfo(String title, String publisher, Date createdAt, String id,String publisherName, String photoUrl){
        this.title = title;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.id = id;
        this.publisherName = publisherName;
        this.photoUrl = photoUrl;
    }

    public FlagCommentInfo(String title, String publisher, Date createdAt, String publisherName, String photoUrl){
        this.title = title;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.publisherName = publisherName;
        this.photoUrl = photoUrl;
    }

    public FlagCommentInfo(String title, String publisher, Date createdAt, String publisherName){
        this.title = title;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.publisherName = publisherName;
    }

    public Map<String, Object> getFlagCommentInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("title",title);
        docData.put("publisher",publisher);
        docData.put("createdAt",createdAt);
        docData.put("publisherName",publisherName);
        docData.put("photoUrl",photoUrl);
        return  docData;
    }

    public String getTitle(){ return this.title; }
    public void setTitle(String title){ this.title = title; }

    public String getPublisher(){ return this.publisher; }
    public void setPublisher(String publisher){ this.publisher = publisher; }

    public Date getCreatedAt(){ return this.createdAt; }
    public void setCreatedAt(Date createdAt){ this.createdAt = createdAt; }

    public String getId(){ return this.id; }
    public void setId(String id){ this.id = id; }

    public String getPublisherName(){ return this.publisherName; }
    public void setPublisherName(String publisherName){ this.publisherName = publisherName; }

    public String getPhotoUrl(){ return this.photoUrl; }
    public void setPhotoUrl(String photoUrl){ this.photoUrl = photoUrl; }

}