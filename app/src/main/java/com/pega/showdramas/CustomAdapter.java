package com.pega.showdramas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<Drama> dramasData;
    LayoutInflater layoutInflater;
    Drama drama;

    public CustomAdapter(Context context, ArrayList<Drama> data) {
        this.context = context;
        this.dramasData = data;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dramasData.size();
    }

    @Override
    public Object getItem(int i) {
        return dramasData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return dramasData.get(i).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View rowView = view;
        if (rowView==null) {
            rowView = layoutInflater.inflate(R.layout.row, null, true);
        }
        //link views
        //ImageView countryFlagIv = rowView.findViewById(R.id.countryFlagIv);
        TextView name_v = rowView.findViewById(R.id.name);
        TextView rating_v = rowView.findViewById(R.id.rating);
        TextView created_at_v = rowView.findViewById(R.id.created_at);

        drama = dramasData.get(position);

        //countryFlagIv.setImageResource(drama.getImage());
        name_v.setText(drama.getName());
        rating_v.setText("rating : " + drama.getRating());
        created_at_v.setText("created_at : " + drama.getCreatedAt());

        return rowView;
    }
}
