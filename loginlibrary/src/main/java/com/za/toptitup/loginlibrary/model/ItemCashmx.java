package com.za.toptitup.loginlibrary.model;

/*
 * Created by Sambhaji Karad on 04-Jan-18
 * Mobile 9423476192
 * Email sambhaji2134@gmail.com/
*/

public class ItemCashmx {

    public String text;
    public int drawable;
    public String color;
    public Integer pos;
    public String bdis;
    public String activate;

    public ItemCashmx(String text, int pos, int drawable, String color, String bdis, String activate ) {
        this.text = text;
        this.drawable = drawable;
        this.color = color;
        this.pos=pos;
        this.bdis=bdis;
        this.activate=activate;

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

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getBdis() {
        return bdis;
    }

    public void setBdis(String bdis) {
        this.bdis = bdis;
    }

    public String getActivate() {
        return activate;
    }

    public void setActivate(String activate) {
        this.activate = activate;
    }
}
