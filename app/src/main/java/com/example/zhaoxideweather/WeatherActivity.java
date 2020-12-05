package com.example.zhaoxideweather;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhaoxideweather.db.County;
import com.example.zhaoxideweather.gson.Lives;
import com.example.zhaoxideweather.gson.Weather;
import com.example.zhaoxideweather.util.HttpUtil;
import com.example.zhaoxideweather.util.Utility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    private Button navButton;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView humidityText;


    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final EditText inputServer = new EditText(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Button guanzhubutton = (Button)findViewById(R.id.county_guanzhu);
        SharedPreferences preferences = getSharedPreferences("guanzhu", MODE_PRIVATE);
        String findcountyweather = getIntent().getStringExtra("findcounty");
        String weatherCountyname = getIntent().getStringExtra("weather_countyname");
        if (findcountyweather!=null) {
            String guanzhucounty = preferences.getString(findcountyweather, null);
            if(guanzhucounty!=null){
                guanzhubutton.setText("已关注");
            }
        }
        else if(weatherCountyname!=null){
            String guanzhucounty1 = preferences.getString(weatherCountyname, null);
            if(guanzhucounty1!=null){
                guanzhubutton.setText("已关注");
            }
        }
        guanzhubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(guanzhubutton.getText().equals("关注")) {
                    String findcountyweather = getIntent().getStringExtra("findcounty");
                    String weatherCountyname = getIntent().getStringExtra("weather_countyname");
                    SharedPreferences.Editor editor = getSharedPreferences("guanzhu", MODE_PRIVATE).edit();
                    if (findcountyweather != null) {
                        editor.putString(findcountyweather, findcountyweather);
                        editor.apply();
                        guanzhubutton.setText("已关注");
                    } else if (weatherCountyname != null) {
                        editor.putString(weatherCountyname, weatherCountyname);
                        editor.apply();
                        guanzhubutton.setText("已关注");
                    }
                }
                else if (guanzhubutton.getText().equals("已关注")){
                    String findcountyweather = getIntent().getStringExtra("findcounty");
                    String weatherCountyname = getIntent().getStringExtra("weather_countyname");
                    SharedPreferences.Editor editor = getSharedPreferences("guanzhu", MODE_PRIVATE).edit();
                    if (findcountyweather != null) {
                        editor.remove(findcountyweather);
                        editor.apply();
                        guanzhubutton.setText("关注");
                    } else if (weatherCountyname != null) {
                        editor.remove(weatherCountyname);
                        editor.apply();
                        guanzhubutton.setText("关注");
                    }
                }
            }
        });
        FloatingActionButton refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String findcountyweather = getIntent().getStringExtra("findcounty");
                if(findcountyweather!=null){
                    requestWeather(findcountyweather);
                }
                String weatherCountyname = getIntent().getStringExtra("weather_countyname");
                if(weatherCountyname!=null) {
                    weatherLayout.setVisibility(View.INVISIBLE);
                    requestWeather(weatherCountyname);
                }
            }
        });
        FloatingActionButton findcounty = findViewById(R.id.find_county);
        findcounty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputServer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);
                builder.setTitle("请输入你需要查询的城市名称").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reload();
                            }
                        });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        String countyname = inputServer.getText().toString();
                            Intent intent = new Intent(WeatherActivity.this, WeatherActivity.class);
                            intent.putExtra("findcounty", countyname);
                            startActivity(intent);
                    }
                });
                builder.show();
            }
        });
        FloatingActionButton fanhui = findViewById(R.id.fanhui);
        fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button)findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        humidityText = (TextView)findViewById(R.id.humidity_text);
        if(findcountyweather!=null){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String weatherString = prefs.getString(findcountyweather, null);
            if(weatherString !=null){
                 Weather weather = Utility.handleWeatherResponse(weatherString);
                 showWeatherInfo(weather);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
            }
            else {
                requestWeather(findcountyweather);
            }
        }
            if(weatherCountyname!=null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherString = prefs.getString(findcountyweather, null);
                if(weatherString !=null) {
                    Weather weather = Utility.handleWeatherResponse(weatherString);
                    showWeatherInfo(weather);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.apply();
                }
                else {
                    weatherLayout.setVisibility(View.INVISIBLE);
                    requestWeather(weatherCountyname);
                }
            }


    }
    public void requestWeather(final String countyName) {
        String weatherUrl = "https://restapi.amap.com/v3/weather/weatherInfo?key=b240a268a4203381ee87374fb1103bf6" + "&city=" + countyName ;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather!=null) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString(countyName, responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void showWeatherInfo(Weather weather) {
            aqiText.setText(weather.windpower);
            pm25Text.setText(weather.winddirection);
            String cityName = weather.city;
            titleCity.setText(cityName);
            String updateTime = weather.reporttime.split(" ")[1];
            titleUpdateTime.setText(updateTime);
            String degree = weather.temperature + "℃";
            degreeText.setText(degree);
            String weatherInfo = weather.weather;
            weatherInfoText.setText(weatherInfo);
            humidityText.setText(weather.humidity);

        weatherLayout.setVisibility(View.VISIBLE);
    }
    
}