package com.pega.showdramas;

public class Drama {
    private int mId;
    private String mImageUrl;
    private String mName;
    private String mRating;
    private String mCreatedAt;
    private String mTotalviews;


    public Drama(int id, String imageUrl, String name, String rating, String created_at, String total_views) {
        mId = id;
        mImageUrl = imageUrl;
        mName = name;
        mRating = rating;
        mCreatedAt = created_at;
        mTotalviews = total_views;
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

    public String getTotalviews() {
        return mTotalviews;
    }
}
