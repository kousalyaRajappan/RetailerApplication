package com.za.toptitup.loginlibrary.model;

/*
 * Created by Sambhaji Karad on 04-Jan-18
 * Mobile 9423476192
 * Email sambhaji2134@gmail.com/
*/

public class Item {

    public String text;
    public int drawable;
    public String color;
    public String pos;
    public String bdis;
   

    public Item(String text,String pos, int drawable, String color,String bdis) {
        this.text = text;
        this.drawable = drawable;
        this.color = color;
        this.pos=pos;
        this.bdis=bdis;


    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getBdis() {
        return bdis;
    }

    public void setBdis(String bdis) {
        this.bdis = bdis;
    }
}
