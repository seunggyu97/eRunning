package com.example.erunning;

public class Item {

    public enum ItemType {
        ONE_ITEM, TWO_ITEM;
    }

    private String content;
    private String name;
    private String time;
    private int viewType;

    public Item(String name, String content, String time, int viewType) {
        this.content = content;
        this.viewType = viewType;
        this.name = name;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public int getType() {
        return viewType;
    }
    public void setType(int viewType) {
        this.viewType = viewType;
    }
}