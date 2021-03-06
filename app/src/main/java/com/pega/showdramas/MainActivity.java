package com.pega.showdramas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "ShowDrama";
    ListView listView;
    Context context;
    ArrayList<Drama> dramasData;
    CustomAdapter customAdapter;
    private DBHelper DH = null;

    private TestHandler handler = new TestHandler(this);
    final static int MSG_GET_JSON = 0;
    final static int MSG_UPDATE_LISTVIEW = 1;
    final static int MSG_UPDATE_DB_FROM_LIST = 2;
    final static int MSG_SHOW_NORMAL_UI = 3;
    final static int MSG_SHOW_ERROR_UI = 4;
    final static int MSG_UPDATE_LIST_FROM_DB = 5;

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
                    case MSG_UPDATE_DB_FROM_LIST:
                        activity.handle_msg_update_db_from_list(msg);
                        break;
                    case MSG_UPDATE_LIST_FROM_DB:
                        activity.handle_msg_update_list_from_db(msg);
                        break;
                    case MSG_SHOW_NORMAL_UI:
                        activity.handle_msg_show_normal_ui(msg);
                        break;
                    case MSG_SHOW_ERROR_UI:
                        activity.handle_msg_show_error_ui(msg);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void handle_msg_show_normal_ui(Message msg){
        EditText edit_v = this.findViewById(R.id.EditText01);
        edit_v.setVisibility(View.VISIBLE);

        TextView error_v = this.findViewById(R.id.error_msg);
        String err_info = (String) msg.obj;
        error_v.setText(err_info);
        error_v.setVisibility(View.GONE);

        Button btn_v = this.findViewById(R.id.error_retry_btn);
        btn_v.setVisibility(View.GONE);
    }

    private void handle_msg_show_error_ui(Message msg){
        EditText edit_v = this.findViewById(R.id.EditText01);
        edit_v.setVisibility(View.INVISIBLE);

        TextView error_v = this.findViewById(R.id.error_msg);
        String err_info = (String) msg.obj;
        error_v.setText(err_info);
        error_v.setVisibility(View.VISIBLE);

        Button btn_v = this.findViewById(R.id.error_retry_btn);
        btn_v.setVisibility(View.VISIBLE);
    }

    private void handle_msg_update_db_from_list(Message msg){
        SQLiteDatabase db = DH.getWritableDatabase();
        for (int i=0; i< dramasData.size();i++){
            Drama tmp = dramasData.get(i);
            ContentValues values = new ContentValues();
            values.put("_DRAMA_ID", tmp.getId());
            values.put("_IMG_URL", tmp.getImageUrl());
            values.put("_NAME", tmp.getName());
            values.put("_RATING", tmp.getRating());
            values.put("_CREATED_AT", tmp.getCreatedAt());
            values.put("_TOTAL_VIEWS", tmp.getTotalviews());
            db.insert("MySample", null, values);
        }
    }

    private void handle_msg_update_list_from_db(Message msg) {
        DH = new DBHelper(this);
        SQLiteDatabase db = DH.getReadableDatabase();
        Cursor cursor = db.query("MySample",
                new String[]{"_DRAMA_ID","_IMG_URL","_NAME","_RATING", "_CREATED_AT", "_TOTAL_VIEWS"},
                null, null, null, null, null);
        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String imageurl = cursor.getString(1);
            String name = cursor.getString(2);
            String rating = cursor.getString(3);
            String created_at = cursor.getString(4);
            String total_views = cursor.getString(5);

            Drama tmp = new Drama(id, imageurl, name, rating, created_at, total_views);
            dramasData.add(tmp);
        }
        cursor.close();
        customAdapter.notifyDataSetChanged();
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

                //construct db from arraylist
                Message message;
                message = handler.obtainMessage(MSG_UPDATE_DB_FROM_LIST);
                handler.sendMessage(message);
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
                if (state.equals("200")){
                    // enable normal ui
                    Message msg_show_ui;
                    msg_show_ui = handler.obtainMessage(MSG_SHOW_NORMAL_UI);
                    handler.sendMessage(msg_show_ui);

                    //put data to listview and edittext in normal ui
                    Message message;
                    String response = (String) result.get(1);
                    message = handler.obtainMessage(MSG_UPDATE_LISTVIEW, response);
                    handler.sendMessage(message);
                }else{
                    //enable error ui
                    Message message;
                    message = handler.obtainMessage(MSG_SHOW_ERROR_UI, state);
                    handler.sendMessage(message);
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
                    break;
                    default:
                        result.add(statusCode);
            }

            return result;
        } catch (IOException e) {
            result.add("Error: "+e);
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

        DH = new DBHelper(this);
        SQLiteDatabase db = DH.getReadableDatabase();
        Cursor cursor = db.query("MySample", new String[]{"_DRAMA_ID","_NAME"}, null, null, null, null, null);

        // error ui: button to retry getting json from internet
        Button btn_v = findViewById(R.id.error_retry_btn);
        btn_v.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = MSG_GET_JSON;
                handler.sendMessage(msg);
            }
        });

        // normal ui: listview
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

        // normal ui: for searching drama name on listview
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

        if (cursor.getCount() == 0) {
            Log.i(TAG, "onCreate use data from internet");
            Message msg = new Message();
            msg.what = MSG_GET_JSON;
            handler.sendMessage(msg);
        }else{
            Log.i(TAG, "onCreate use data from db");
            Message msg = new Message();
            msg.what = MSG_UPDATE_LIST_FROM_DB;
            handler.sendMessage(msg);
        }
        cursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DH.close();
    }
}