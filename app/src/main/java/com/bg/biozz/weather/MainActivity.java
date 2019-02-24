package com.bg.biozz.weather;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    LinearLayout mainLayout;
    List<String> cityNames;
    Retrofit retrofit;
    MessageAPI messageAPI;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.mainLayout);

        cityNames = new ArrayList<String>();
        cityNames.add("Naberezhnyye Chelny");
        cityNames.add("Yelabuga");
        cityNames.add("Kazan");

        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_CITYS, null, null, null,null,null,null,null);

        if(cursor.moveToFirst()){
            do{
                cityNames.add(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NAME)));
            }while(cursor.moveToNext());
        }else{
            Log.e("DBase", "0 rows");
        }
        cursor.close();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        messageAPI = retrofit.create(MessageAPI.class);

        for(final String city : cityNames) {
            Call<Message> messages = messageAPI.messages(city, "c63805d4c51f376eeb0b0c242ffef6eb", "metric");
            messages.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    if (response.isSuccessful()) {
                        final Message data = response.body();
                        Weather weather = data.getWeather().get(0);

                        View view = getLayoutInflater().inflate(R.layout.item_layout, null);
                        TextView text = view.findViewById(R.id.cityName);
                        text.setText(data.getName());
                        text = view.findViewById(R.id.weatherMain);
                        text.setText(weather.getMain());
                        text = view.findViewById(R.id.temp);
                        text.setText((int) data.getMain().getTempMin() + " / " + (int) data.getMain().getTempMax());
                        ImageView icon = view.findViewById(R.id.cityIcon);
                        Glide.with(MainActivity.this).load("http://openweathermap.org/img/w/" + weather.getIcon() + ".png").into(icon);

                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RelativeLayout layout = v.findViewById(R.id.itemLayout);
                                //layout.setBackgroundColor(Color.rgb(230, 198, 163));
                                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                                intent.putExtra("City", data.getName());
                                startActivity(intent);
                            }
                        });

                        mainLayout.addView(view);
                    } else {
                        Snackbar.make(mainLayout, "City " +city+ ": not found!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        try {
                            Log.e("OneDay: ", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
                    Snackbar.make(mainLayout, "Connection error!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Log.e("OneDay: ", t.toString());
                }
            });
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddCityActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                final String cityName = data.getStringExtra("NewCity");

                Call<Message> messages = messageAPI.messages(cityName, "c63805d4c51f376eeb0b0c242ffef6eb", "metric");
                messages.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if (response.isSuccessful()) {
                            final Message data = response.body();
                            Weather weather = data.getWeather().get(0);

                            for(String name : cityNames){
                                if(name.equals(data.getName())){
                                    Snackbar.make(mainLayout, "City "+ cityName + " has already been added", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    return;
                                }
                            }

                            View view = getLayoutInflater().inflate(R.layout.item_layout, null);
                            TextView text = view.findViewById(R.id.cityName);
                            text.setText(data.getName());
                            text = view.findViewById(R.id.weatherMain);
                            text.setText(weather.getMain());
                            text = view.findViewById(R.id.temp);
                            text.setText((int) data.getMain().getTempMin() + " / " + (int) data.getMain().getTempMax());
                            ImageView icon = view.findViewById(R.id.cityIcon);
                            Glide.with(MainActivity.this).load("http://openweathermap.org/img/w/" + weather.getIcon() + ".png").into(icon);

                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    RelativeLayout layout = v.findViewById(R.id.itemLayout);
                                    //layout.setBackgroundColor(Color.rgb(230, 198, 163));
                                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                                    intent.putExtra("City", data.getName());
                                    startActivity(intent);
                                }
                            });

                            cityNames.add(cityName);

                            SQLiteDatabase database = dbHelper.getWritableDatabase();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DBHelper.KEY_NAME, cityName);
                            database.insert(DBHelper.TABLE_CITYS, null, contentValues);

                            mainLayout.addView(view);
                            Snackbar.make(mainLayout, "New City: " + cityName + " added!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            Snackbar.make(mainLayout, "City " +cityName+ ": not found! City not added!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            try {
                                Log.e("OneDay: ", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Snackbar.make(mainLayout, "Connection error! City not added!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        Log.e("OneDay: ", t.toString());
                    }
                });
            }
        }
    }
}
