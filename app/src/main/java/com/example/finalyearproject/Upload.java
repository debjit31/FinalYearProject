package com.example.finalyearproject;

public class Upload {
    private String mName;
    private String mImageURl;

    public Upload(){
        // empty constructor
    }
    public Upload(String name, String imageURL){
        this.mName = "profilepicture";
        this.mImageURl = imageURL;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImageURl() {
        return mImageURl;
    }

    public void setmImageURl(String mImageURl) {
        this.mImageURl = mImageURl;
    }
}
