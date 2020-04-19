package com.pega.showdramas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Context context;
    ArrayList<Drama> dramasData;
    CustomAdapter customAdapter;

    private TestHandler handler = new TestHandler(this);
    final static int MSG_GET_JSON = 0;
    final static int MSG_UPDATE_LISTVIEW = 1;
    final static int MSG_UPDATE_DB = 2;

    //for Serializable
    public static final String INTENT_PARAM_KEY_DRAMA = "INTENT_PARAM_KEY_DRAMA";

    static class TestHandler extends Handler{

        private WeakReference<Activity> mActivity;

        TestHandler(Activity activity){
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = (MainActivity) mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case MSG_GET_JSON:
                        activity.handle_msg_get_json(msg);
                        break;
                    case MSG_UPDATE_LISTVIEW:
                        activity.handle_msg_update_listview(msg);
                        break;
                    case MSG_UPDATE_DB:
                        activity.handle_msg_update_db(msg);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void handle_msg_update_db(Message msg){
        Log.i(TAG, "yiu start handle_msg_update_db");
    }

        private void handle_msg_update_listview(Message msg){
        String response = (String) msg.obj;
        if(response != null) {
            JSONObject json = null;
            try {
                json = new JSONObject(response);
                JSONArray dramas = json.getJSONArray("data");
                for (int i = 0; i < dramas.length(); i++) {
                    JSONObject jsonObject = dramas.getJSONObject(i);

                    int id = jsonObject.getInt("drama_id");
                    String imageurl = jsonObject.getString("thumb");
                    String name = jsonObject.getString("name");
                    //String total_views = jsonObject.getString("total_views");
                    String rating = jsonObject.getString("rating");
                    String created_at = jsonObject.getString("created_at");
                    String total_views = jsonObject.getString("total_views");


                    Drama tmp = new Drama(id, imageurl, name, rating, created_at, total_views);
                    dramasData.add(tmp);
                }
                customAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

        private void handle_msg_get_json(Message msg){
        Thread thread = new Thread(){
            @Override
            public void run() {
                ArrayList<Object> result = getJson();
                String state = (String) result.get(0);
                //Log.i(TAG,"yiu Thread:"+Thread.currentThread().getName());
                if (state.equals("200")){
                    Message message;
                    String response = (String) result.get(1);
                    message = handler.obtainMessage(MSG_UPDATE_LISTVIEW, response);
                    handler.sendMessage(message);
                }else{
                    Log.i(TAG, "yiu wrong handle："+ state);
                }
            }
        };
        thread.start();
    }

    private ArrayList<Object> getJson(){
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        //Log.i(TAG,"yiu  ui Thread:"+Thread.currentThread().getName());

        //getviews
        listView = findViewById(R.id.listView);
        dramasData = new ArrayList<>();
        customAdapter = new CustomAdapter(context,dramasData);
        listView.setAdapter(customAdapter);
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Toast.makeText(context,dramasData.get(position).getName(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(MainActivity.INTENT_PARAM_KEY_DRAMA, dramasData.get(position));
                startActivity(intent);
            }
        });


        EditText edt;
        edt = findViewById(R.id.EditText01);
        edt.addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged( CharSequence arg0, int arg1, int arg2, int
                    arg3){}
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int
                    arg3){}
            @Override
            public void afterTextChanged(Editable arg0)
            {
                customAdapter.getFilter().filter(arg0);
            }
        });

        Message msg = new Message();
        msg.what = MSG_GET_JSON;
        handler.sendMessage(msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}