package com.za.toptitup.loginlibrary.model;

/*
 * Created by manoj on 04-Jan-18
 * Mobile 0782222143
 * Email manojecdvg@gmail.com/
*/

public class ItemSPI {

    public String text;
    public String deno;
    public int drawable;
    public String color;
    public int pos;
    public String bmenu;
    public String item_barcode;

    public boolean visible_cashier;
    public boolean visible_admin;
    public String social_type=null;
    public String period_label=null;


    public ItemSPI(String text, int pos, int drawable, String color, String bmenu, String item_barcode, boolean visible_admin, boolean visible_cashier,String deno,String social_type,String period_label ) {
        this.text = text;
        this.drawable = drawable;
        this.color = color;
        this.pos=pos;
        this.bmenu=bmenu;
        this.item_barcode=item_barcode;
        this.visible_cashier=visible_cashier;
        this.visible_admin=visible_admin;
        this.deno=deno;
        this.social_type=social_type;
        this.period_label=period_label;
    }

    public ItemSPI() {
    }

    public enum SOCIAL_KEY {
        //0=whatsapp,1=youtube,2=twitter,3=facebook,4=instagram,5=tiktok
        WHATSAPP("0"), YOUTUBE("1"),
        TWITTER("2"), FACEBOOK("3"), INSTAGRAM("4"),
        TIKTOK("5");
        public final String KEY;

        SOCIAL_KEY(String key) {
            this.KEY = key;
        }
    }
    public enum PERIOD_LABEL {
        //  0=standard,1=daily,2=monthly,3=hourly,4=weekly,5=special,6=social
        STANDARD("0"), DAILY("1"),
        MONTHLY("2"), HOURLY("3"), WEEKLY("4"),
        SPECIAL("5"),SOCIAL("6");
        public final String KEY;

        PERIOD_LABEL(String key) {
            this.KEY = key;
        }
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

    public String getBmenu() {
        return bmenu;
    }

    public void setBmenu(String bmenu) {
        this.bmenu = bmenu;
    }

    public String getItem_barcode() {
        return item_barcode;
    }

    public void setItem_barcode(String item_barcode) {
        this.item_barcode = item_barcode;
    }

    public void setVisible_cashier(boolean visible_cashier) {
        this.visible_cashier = visible_cashier;
    }

    public void setVisible_admin(boolean visible_admin) {
        this.visible_admin = visible_admin;
    }

    public String getDeno() {
        return deno;
    }

    public void setDeno(String deno) {
        this.deno = deno;
    }
}
