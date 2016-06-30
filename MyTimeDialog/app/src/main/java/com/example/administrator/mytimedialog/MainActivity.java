package com.example.administrator.mytimedialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements DateTimeDialog.ResultCallBack {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DateTimeDialog dateTimeDialog = new DateTimeDialog(this,this);
        dateTimeDialog.show();
    }

    @Override
    public void onSelected(String year, String month, String day, String hour) {

    }
}
