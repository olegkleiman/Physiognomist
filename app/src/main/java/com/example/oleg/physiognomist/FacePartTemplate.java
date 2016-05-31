package com.example.oleg.physiognomist;

/**
 * Created by Oleg on 31-May-16.
 */
public class FacePartTemplate {

    public FacePartTemplate(String text, int imageId) {
        this.text = text;
        this.imageId = imageId;
    }

    private String text;
    public void setText(String val) {
        text = val;
    }
    public String getText() {
        return  text;
    }

    private int imageId;
    public void setImageId(int val){
        imageId = val;
    }
    public int getImageId() {
        return imageId;
    }

}
