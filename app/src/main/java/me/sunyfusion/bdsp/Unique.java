package me.sunyfusion.bdsp;

/**
 * Created by deisingj1 on 11/7/2016.
 */

public class Unique {
    private String text = "";
    private String type;
    private String[] sArray;
    public Unique(String s) {
        type = s;
    }
    public void setText(String s) {
        text = s;
    }
    public String getText() {
        return text;
    }
    public String getType() {
        return type;
    }
    public String[] getArray() {
        return sArray;
    }
    public void setArray(String[] array) {
        sArray = array;
    }
}
