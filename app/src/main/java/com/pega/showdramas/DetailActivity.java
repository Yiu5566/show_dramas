package com.pega.showdramas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

        Intent intent = getIntent();
        Object obj = intent.getSerializableExtra(MainActivity.INTENT_PARAM_KEY_DRAMA);
        Drama drama = (Drama)obj;

        Picasso.get()
                .load(drama.getImageUrl())
                .into(thumb_v);
        name_v.setText(drama.getName());
        rating_v.setText("rating : " + drama.getRating());
        created_at_v.setText("created at : " + drama.getCreatedAt());
        total_views_v.setText("total views : " + drama.getTotalviews());
    }
}
