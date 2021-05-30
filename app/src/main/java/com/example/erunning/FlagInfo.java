package com.example.erunning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FlagInfo implements Serializable {
    private String title;
    private ArrayList<String> contents;
    private ArrayList<String> flager;
    private String publisher;
    private Date createdAt;
    private String id;
    private String publisherName;
    private String photoUrl;
    private String flag;
    private String comment;
    private String starthour;
    private String startminute;
    private String description;
    private String address;
    private String maxpeople;
    private String currentmember;
    private String sportCode;
    private String sportText;
    private String latitude;
    private String longitude;

    public FlagInfo(String title, String publisher, Date createdAt,String id,String publisherName, String photoUrl, String flag, ArrayList<String> flager,
                    String starthour, String startminute, String description, String address, String maxpeople, String currentmember, String sportCode,
                    String sportText, String latitude, String longitude,String comment){
        this.title = title;
        this.flager = flager;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.id = id;
        this.publisherName = publisherName;
        this.photoUrl = photoUrl;
        this.flag = flag;
        this.starthour = starthour;
        this.startminute = startminute;
        this.description = description;
        this.address = address;
        this.maxpeople = maxpeople;
        this.currentmember = currentmember;
        this.sportCode = sportCode;
        this.sportText = sportText;
        this.latitude = latitude;
        this.longitude = longitude;
        this.comment = comment;
    }

    public FlagInfo(String title, String publisher, Date createdAt, String publisherName, String photoUrl, String flag, ArrayList<String> flager,
                    String starthour, String startminute, String description, String address, String maxpeople, String currentmember, String sportCode,
                    String sportText, String latitude, String longitude, String comment){
        this.title = title;
        this.flager = flager;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.publisherName = publisherName;
        this.photoUrl = photoUrl;
        this.flag = flag;
        this.starthour = starthour;
        this.startminute = startminute;
        this.description = description;
        this.address = address;
        this.maxpeople = maxpeople;
        this.currentmember = currentmember;
        this.sportCode = sportCode;
        this.sportText = sportText;
        this.latitude = latitude;
        this.longitude = longitude;
        this.comment = comment;
    }


    public Map<String, Object> getFlagInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("title",title);
        docData.put("publisher",publisher);
        docData.put("createdAt",createdAt);
        docData.put("publisherName",publisherName);
        docData.put("photoUrl",photoUrl);
        docData.put("flag",flag);
        docData.put("comment",comment);
        docData.put("starthour",starthour);
        docData.put("startminute",startminute);
        docData.put("description",description);
        docData.put("address",address);
        docData.put("maxpeople",maxpeople);
        docData.put("currentmember",currentmember);
        docData.put("sportCode",sportCode);
        docData.put("sportText",sportText);
        docData.put("latitude",latitude);
        docData.put("longitude",longitude);
        return  docData;
    }

    public String getTitle(){ return this.title; }
    public void setTitle(String title){ this.title = title; }

    public ArrayList<String> getContents(){ return this.contents; }
    public void setContents(ArrayList<String> contents){ this.contents = contents; }

    /*public ArrayList<String> getFormats(){ return this.formats; }
    public void setFormats(ArrayList<String> formats){ this.formats = formats; }*/

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

    public String getComment(){ return this.comment; }
    public void setComment(String comment){ this.comment = comment; }

    public String getFlag(){ return this.flag; }
    public void setFlag(String flag){ this.flag = flag; }

    public ArrayList<String> getFlager(){ return this.flager; }
    public void setFlager(ArrayList<String> flager){ this.flager = flager; }

    public String getStarthour(){ return this.starthour; }
    public void setStarthour(String starthour){ this.starthour = starthour; }

    public String getStartminute(){ return this.startminute; }
    public void setStartminute(String startminute){ this.startminute = startminute; }

    public String getDescription(){ return this.description; }
    public void setDescription(String description){ this.description = description; }

    public String getAddress(){ return this.address; }
    public void setAddress(String address){ this.address = address; }

    public String getMaxpeople(){ return this.maxpeople; }
    public void setMaxpeople(String maxpeople){ this.maxpeople = maxpeople; }

    public String getCurrentmember(){ return this.currentmember; }
    public void setCurrentmember(String currentmember){ this.currentmember = currentmember; }

    public String getSportCode(){ return this.sportCode; }
    public void setSportCode(String sportCode){ this.sportCode = sportCode; }

    public String getSportText(){ return this.sportText; }
    public void setSportText(String sportText){ this.sportText = sportText; }

    public String getLatitude(){ return this.latitude; }
    public void setLatitude(String latitude){ this.latitude = latitude; }

    public String getLongitude(){ return this.longitude; }
    public void setLongitude(String longitude){ this.longitude = longitude; }

}
