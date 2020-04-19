package com.pega.showdramas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        ImageView thumb_v = findViewById(R.id.thumb);
        TextView name_v = findViewById(R.id.name);
        TextView rating_v = findViewById(R.id.rating);
        TextView created_at_v = findViewById(R.id.created_at);
        TextView total_views_v = findViewById(R.id.total_views);


//        drama = dramasData.get(position);
        String testurl = "https://i.pinimg.com/originals/61/d4/be/61d4be8bfc29ab2b6d5cab02f72e8e3b.jpg";
        Picasso.get()
                .load(testurl)
                .into(thumb_v);
//        name_v.setText(drama.getName());
//        rating_v.setText("rating : " + drama.getRating());
//        created_at_v.setText("created at : " + drama.getCreatedAt());
//        total_views_v.setText("total views : " + drama.getTotalviews());

        name_v.setText("致我們單純的小美好");
        rating_v.setText("rating : " + "test 123");
        created_at_v.setText("created_at : " + "test 456");
        total_views_v.setText("total views : " + "23562274");
    }
}
