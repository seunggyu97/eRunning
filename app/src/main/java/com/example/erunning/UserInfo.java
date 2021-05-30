package com.example.erunning;

import java.util.ArrayList;

public class UserInfo {
    private String item_id;
    private String name;
    private int birthyear;
    private int birthmonth;
    private int birthday;
    private String photoUrl;
    private String bio;
    private int post;
    private int follower;
    private int following;
    private ArrayList<String> followerlist;
    private ArrayList<String> followinglist;
    private int user_step;


    public UserInfo(String name, int birthyear, int birthmonth, int birthday,ArrayList<String> followerlist, ArrayList<String> followinglist, int user_step){
        this.name = name;
        this.birthyear = birthyear;
        this.birthmonth = birthmonth;
        this.birthday = birthday;
        this.photoUrl = null;
        this.bio = null;
        this.post = 0;
        this.follower = 0;
        this.following = 0;
        this.followerlist = followerlist;
        this.followinglist = followinglist;
        this.user_step = 0;
    }

    public UserInfo() { }


//    public UserInfo(String name, int birthyear, int birthmonth, int birthday){
//        this.name = name;
//        this.birthyear = birthyear;
//        this.birthmonth = birthmonth;
//        this.birthday = birthday;
//        this.photoUrl = null;
//        this.bio = null;
//        this.post = 0;
//        this.follower = 0;
//        this.following = 0;
//
//    }

    /*
    public UserInfo(String name, int birthyear, int birthmonth, int birthday,String photoUrl){
        this.name = name;
        this.birthyear = birthyear;
        this.birthmonth = birthmonth;
        this.birthday = birthday;
        this.photoUrl = photoUrl;
    }*/
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
    public String getPhotoUrl(){
        return this.photoUrl;
    }
    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }
    public String getBio(){
        return this.bio;
    }
    public void setBio(String bio){
        this.bio = bio;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public int getPost() {return this.post; }
    public void setPost(int post) {this.post = post; }

    public int getFollower() {return this.follower; }
    public void setFollower(int follower) {this.follower = follower; }

    public int getFollowing() {return this.following; }
    public void setFollowing(int following) {this.following = following; }

    public ArrayList<String> getFollowerlist(){ return this.followerlist; }
    public void setFollowerlist(ArrayList<String> followerlist){ this.followerlist = followerlist; }

    public ArrayList<String> getFollowinglist(){ return this.followinglist; }
    public void setFollowinglist(ArrayList<String> followerlist){ this.followinglist = followinglist; }

    public int getUser_step() {
        return this.user_step;
    }
    public void setUser_step(int user_step) {
        this.user_step = user_step;
    }

}
