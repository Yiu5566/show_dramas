package com.pega.showdramas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Context context;
    ArrayList<Drama> dramasData;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);

        //getviews
        listView = findViewById(R.id.listView);
        dramasData = new ArrayList<>();
        customAdapter = new CustomAdapter(context,dramasData);
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(context,dramasData.get(position).getName(),Toast.LENGTH_SHORT).show();
            }
        });

        //get drama data from an url
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getData() {
        new retrievedata(customAdapter, dramasData).execute();
    }
}

class retrievedata extends AsyncTask<Void, Void, ArrayList<Object>> {
    final static String TAG = "retrievedata";
    ArrayList<Drama> mDatalist;
    CustomAdapter mAdapter;

    public retrievedata(CustomAdapter adapter, ArrayList<Drama> datalist) {
        super();
        mAdapter = adapter;
        mDatalist = datalist;
    }

    @Override
    protected ArrayList<Object> doInBackground(Void... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        URL url;
        ArrayList<Object>  result = new ArrayList<Object>();
        try {
            url = new URL("https://static.linetv.tw/interview/dramas-sample.json");
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET"); //Your method here
            urlConnection.connect();


            int statusCode = urlConnection.getResponseCode();


            switch (statusCode) {
                case 200:
                    result.add("200");
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        result.add(null);
                        return result;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                    String line;
                    while ((line = reader.readLine()) != null)
                        buffer.append(line + "\n");

                    if (buffer.length() == 0) {
                        result.add(null);
                        return result;
                    }

                    result.add(buffer.toString());
            }

            return result;
        } catch (IOException e) {
            Log.e(TAG, "IO Exception", e);
            result.add(e);
            return result;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Object> result) {
        String state = (String) result.get(0);
        Log.i(TAG, "yiu use ArrayList to store state and data");
        if (state.equals("200")){
            String response = (String) result.get(1);
            if(response != null) {
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                    JSONArray dramas = json.getJSONArray("data");
                    int id = dramas.getJSONObject(0).getInt("drama_id");
                    String imageurl = dramas.getJSONObject(0).getString("thumb");
                    String name = dramas.getJSONObject(0).getString("name");
                    //iString total_views = dramas.getJSONObject(0).getString("total_views");
                    String rating = dramas.getJSONObject(0).getString("rating");
                    String created_at = dramas.getJSONObject(0).getString("created_at");
                    Drama tmp = new Drama(id, imageurl, name, rating, created_at);
                    mDatalist.add(tmp);
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else{
            Log.i(TAG, "yiu wrong handle："+ state);
        }
    }
}