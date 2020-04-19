package com.pega.showdramas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CustomAdapter extends BaseAdapter implements Filterable{
    Context context;
    ArrayList<Drama> dramasData;
    LayoutInflater layoutInflater;
    Drama drama;

    //new variable for filter
    private ArrayList<Drama> mOriginalValues;
    private MyFilter filter;

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
        ImageView thumb_v = rowView.findViewById(R.id.thumb);
        TextView name_v = rowView.findViewById(R.id.name);
        TextView rating_v = rowView.findViewById(R.id.rating);
        TextView created_at_v = rowView.findViewById(R.id.created_at);

        drama = dramasData.get(position);
        Picasso.get()
                .load(drama.getImageUrl())
                .into(thumb_v);
        name_v.setText(drama.getName());
        rating_v.setText("rating : " + drama.getRating());
        created_at_v.setText("created_at : " + drama.getCreatedAt());

        return rowView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new MyFilter();
        }
        return filter;
    }
    
    private class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString();
            FilterResults result = new FilterResults();
            if (mOriginalValues == null) {
                synchronized (this) {
                    mOriginalValues = new ArrayList<Drama>(dramasData);
                }
            }
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<Drama> filteredItems = new ArrayList<Drama>();
                for (int i = 0, l = mOriginalValues.size(); i < l; i++) {
                    Drama m = mOriginalValues.get(i);
                    if (m.getName().contains(constraint)) {
                        //filteredItems.add(mOriginalValues.get((i/4)*4));
                        filteredItems.add(m);
                    }
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    ArrayList<Drama> list = new ArrayList<Drama>(mOriginalValues);
                    result.values = list;
                    result.count = list.size();
                }
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            dramasData = (ArrayList<Drama>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
