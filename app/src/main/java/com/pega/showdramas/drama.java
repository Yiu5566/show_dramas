package com.pega.showdramas;

public class drama {
    private String mImageUrl;
    private String mName;
    private String mRating;
    private String mCreatedAt;


    public drama(String imageUrl, String name, String rating, String created_at) {
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
}
