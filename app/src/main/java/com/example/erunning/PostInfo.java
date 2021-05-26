package com.example.erunning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostInfo implements Serializable {
    private String title;
    private ArrayList<String> contents;
    private ArrayList<String> liker;
    private String publisher;
    private Date createdAt;
    private String id;
    private String publisherName;
    private String photoUrl;
    private String like;
    private String comment;

    public PostInfo(String title, ArrayList<String> contents, String publisher, Date createdAt, String id,String publisherName, String photoUrl, String like, String comment, ArrayList<String> liker){
        this.title = title;
        this.contents = contents;
        this.liker = liker;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.id = id;
        this.publisherName = publisherName;
        this.photoUrl = photoUrl;
        this.like = like;
        this.comment = comment;
    }

    public PostInfo(String title, ArrayList<String> contents, String publisher, Date createdAt, String publisherName, String photoUrl, String like, String comment, ArrayList<String> liker){
        this.title = title;
        this.contents = contents;
        this.liker = liker;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.publisherName = publisherName;
        this.photoUrl = photoUrl;
        this.like = like;
        this.comment = comment;
    }

    public PostInfo(String title, ArrayList<String> contents,String id, String publisher, Date createdAt, String publisherName, String like, String comment, ArrayList<String> liker){
        this.title = title;
        this.contents = contents;
        this.liker = liker;
        this.id = id;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.publisherName = publisherName;
        this.like = like;
        this.comment = comment;
    }

    public Map<String, Object> getPostInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("title",title);
        docData.put("contents",contents);
        //docData.put("formats",formats);
        docData.put("publisher",publisher);
        docData.put("createdAt",createdAt);

        docData.put("publisherName",publisherName);
        docData.put("photoUrl",photoUrl);
        docData.put("like",like);
        docData.put("comment",comment);
        return  docData;
    }

    public String getTitle(){ return this.title; }
    public void setTitle(String title){ this.title = title; }

    public ArrayList<String> getContents(){ return this.contents; }
    public void setContents(ArrayList<String> contents){ this.contents = contents; }

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

    public String getLike(){ return this.like; }
    public void setLike(String like){ this.like = like; }

    public String getComment(){ return this.comment; }
    public void setComment(String comment){ this.comment = comment; }

    public ArrayList<String> getLiker(){ return this.liker; }
    public void setLiker(ArrayList<String> liker){ this.liker = liker; }

}
