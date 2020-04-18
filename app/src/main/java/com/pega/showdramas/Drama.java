package com.pega.showdramas;

public class Drama {
    private int mId;
    private String mImageUrl;
    private String mName;
    private String mRating;
    private String mCreatedAt;


    public Drama(int id, String imageUrl, String name, String rating, String created_at) {
        mId = id;
        mImageUrl = imageUrl;
        mName = name;
        mRating = rating;
        mCreatedAt = created_at;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getName() {
        return mName;
    }

    public String getRating() {
        return mRating;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public int getId() {
        return mId;
    }
}
