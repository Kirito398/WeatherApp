package com.bg.biozz.weather;

import android.app.ActionBar;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.Gravity.CENTER_HORIZONTAL;

public class DetailActivity extends AppCompatActivity {

    TextView cityName, temp, weatherMain, description, windspeed, pressure, humidity, tempA;
    ImageView icon;
    ConstraintLayout mainLayout;
    String city = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        getSupportActionBar().hide();

        city = getIntent().getStringExtra("City");

        cityName = findViewById(R.id.cityName);
        temp = findViewById(R.id.temp);
        weatherMain = findViewById(R.id.weatherMain);
        mainLayout = findViewById(R.id.detailLayout);
        icon = findViewById(R.id.icon);
        description = findViewById(R.id.description);
        windspeed = findViewById(R.id.windSpeed);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humidity);
        tempA = findViewById(R.id.tempA);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MessageAPI messageAPI = retrofit.create(MessageAPI.class);

        Call<Message> messages = messageAPI.messages(city, "c63805d4c51f376eeb0b0c242ffef6eb", "metric");

        messages.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.isSuccessful()) {
                    Message data = response.body();
                    Weather weather = data.getWeather().get(0);
                    cityName.setText(data.getName());
                    temp.setText(""+(int)data.getMain().getTemp());
                    weatherMain.setText(weather.getMain());
                    Glide.with(DetailActivity.this).load("http://openweathermap.org/img/w/" + weather.getIcon() + ".png").into(icon);
                    description.setText(weather.getDescription());
                    windspeed.setText("Wind speed: "+(int)data.getWind().getSpeed());
                    pressure.setText("Pressure: "+(int)data.getMain().getPressure());
                    humidity.setText("Humidity: "+(int)data.getMain().getHumidity());
                    tempA.setText("Temperature: "+(int)data.getMain().getTempMin() + " / " + (int)data.getMain().getTempMax());
                }else{
                    try {
                        Log.e("OneDay: ",response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("OneDay: ", t.toString());
            }
        });

        final Call<ForeCast> forecast = messageAPI.forecast(city, "c63805d4c51f376eeb0b0c242ffef6eb", "metric");

        forecast.enqueue(new Callback<ForeCast>() {
            @Override
            public void onResponse(Call<ForeCast> call, Response<ForeCast> response) {
                if(response.isSuccessful()){
                    ForeCast data = response.body();

                    for(Message day : data.getItems()){
                        if(day.getDate().get(Calendar.HOUR_OF_DAY) == 3){
                            String date = String.format("%d.%d.%d %d:%d %d",
                                    day.getDate().get(Calendar.DAY_OF_MONTH),
                                    day.getDate().get(Calendar.WEEK_OF_MONTH),
                                    day.getDate().get(Calendar.YEAR),
                                    day.getDate().get(Calendar.HOUR_OF_DAY),
                                    day.getDate().get(Calendar.MINUTE),
                                    day.getDate().get(Calendar.HOUR_OF_DAY));
                            Log.e("Forecast", date);
                            Log.e("Forecast", ""+day.getMain().getTemp());

                            LinearLayout view = findViewById(R.id.viewLayout);
                            LinearLayout item = new LinearLayout(DetailActivity.this);
                            item.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
                            item.setOrientation(LinearLayout.VERTICAL);
                            item.setWeightSum(0.2f);

                            SimpleDateFormat formatDayOfWeek = new SimpleDateFormat("E");
                            TextView day_view = new TextView(DetailActivity.this);
                            String day_txt = formatDayOfWeek.format(day.getDate().getTime());
                            day_view.setText(day_txt);
                            day_view.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
                            day_view.setGravity(CENTER_HORIZONTAL);
                            item.addView(day_view);

                            ImageView icon = new ImageView(DetailActivity.this);
                            icon.setLayoutParams(new LinearLayout.LayoutParams(convertDPtoPX(60,DetailActivity.this), convertDPtoPX(60,DetailActivity.this)));
                            Glide.with(DetailActivity.this).load("http://openweathermap.org/img/w/" + day.getWeather().get(0).getIcon() + ".png").into(icon);
                            item.addView(icon);

                            TextView temp_view = new TextView(DetailActivity.this);
                            temp_view.setText(""+(int)day.getMain().getTemp());
                            temp_view.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
                            temp_view.setGravity(CENTER_HORIZONTAL);
                            item.addView(temp_view);

                            view.addView(item);
                        }
                    }
                }else{
                    Log.e("Forecast: ", "onResponse fail");
                }
            }

            @Override
            public void onFailure(Call<ForeCast> call, Throwable t) {
                Log.e("Forecast: ", "Fail");
                Log.e("Forecast t: ", t.toString());
            }
        });
    }

    public int convertDPtoPX(int dp, Context ctx){
        float density = ctx.getResources().getDisplayMetrics().density;
        int px = (int)(dp*density);
        return px;
    }
}
