package com.example.zhaoxideweather;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zhaoxideweather.db.County;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.litepal.crud.DataSupport;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final EditText inputServer = new EditText(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();
            }
        });
        FloatingActionButton findcounty = findViewById(R.id.find_county);
        findcounty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputServer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                            intent.putExtra("findcounty", countyname);
                            startActivity(intent);
                    }
                });
                builder.show();
            }
        });
        /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("weather", null) != null){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }
        */
    }
}