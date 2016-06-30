package com.example.administrator.mytimedialog;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.util.TimeZone;
import java.util.logging.LogRecord;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

public class DateTimeDialog extends Dialog implements View.OnClickListener {
    private MyWheelView wvYear;
    private MyWheelView wvMonth;
    private MyWheelView wvDay;
    private MyWheelView wvHour;
    private List<String> listYear  = new ArrayList<>();
    private List<String> listMonth = new ArrayList<>();
    private List<String> listDay   = new ArrayList<>();
    private List<String> listHour  = new ArrayList<>();
    private Calendar       calendar;
    private Calendar       localCalendar;
    private String         mYear;
    private String         mMonth;
    private String         mDay;
    private String         mHour;
    private Context        mContext;
    private ResultCallBack onResultCallBack;
    private boolean isFirst = true;
    private Handler handler;

    public DateTimeDialog(Context context, ResultCallBack resultCallBack) {
        super(context, R.style.order_dialog);
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.onResultCallBack = resultCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_date_time);
        initData();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(wmlp); // 底部显示
        getWindow().setWindowAnimations(R.style.date_dialog_animstyle); // 启动关闭动画
        this.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                // TODO Auto-generated method stub

                if (isFirst) {

                    isFirst = false;
                }
            }
        });
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_ok).setOnClickListener(this);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                setLocalTime((Long) msg.obj);
            }
        };
        if (isNetWorkConnected(mContext)) {
            new WorkerThread().start();
        } else {
            setLocalTime(new Date().getTime());
        }
    }

    /***
     * 判断当前网络是否连接
     *
     * @param context
     * @return
     */
    public boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    class WorkerThread extends Thread {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            URL url;
            try {
                url = new URL("http://www.baidu.com");
                URLConnection uc = url.openConnection();
                uc.connect();
                long    ld  = uc.getDate();
                Message msg = handler.obtainMessage();
                msg.obj = ld;
                handler.sendMessage(msg);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private void initData() {
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        initYear();
        initMonth();
        initDay(31);
        initHour();
        initView();
    }

    private void initYear() {
        listYear.add("2016" + "");
        listYear.add(("2017") + "");
        mYear = listYear.get(0);
    }

    private void initDay(int maxDay) {
        for (int i = 1; i <= maxDay; i++) {
            String str = i < 10 ? ("0" + i) : (i + "");
            listDay.add(str);
        }
        mDay = listDay.get(0);
    }

    private void initMonth() {
        for (int i = 1; i <= 12; i++) {
            String str = i < 10 ? ("0" + i) : (i + "");
            listMonth.add(str);
        }
        mMonth = listMonth.get(0);
    }

    private void initHour() {
        for (int i = 0; i < 24; i++) {
            String str = i < 10 ? ("0" + i + ":00") : (i + ":00");
            listHour.add(str);
        }
        mHour = listHour.get(0);
    }

    public void setLocalTime(long time) {
        localCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        localCalendar.setTimeInMillis(time);
        int    month    = localCalendar.get(Calendar.MONTH) + 1;
        String strMonth = month < 10 ? ("0" + month) : (month + "");
        wvMonth.setSeletion(listMonth.indexOf(strMonth));
        refreshDayByMonth(strMonth);
        int    day    = localCalendar.get(Calendar.DATE);
        String strDay = day < 10 ? ("0" + day) : (day + "");
        wvDay.setSeletion(listDay.indexOf(strDay));
        int    hour    = localCalendar.get(Calendar.HOUR_OF_DAY);
        String strHour = hour < 10 ? ("0" + hour + ":00") : (hour + ":00");
        wvHour.setSeletion(listHour.indexOf(strHour));
        mMonth = strMonth;
        mDay = strDay;
        mHour = strHour;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        wvYear = (MyWheelView) findViewById(R.id.wv_year);
        wvMonth = (MyWheelView) findViewById(R.id.wv_month);
        wvDay = (MyWheelView) findViewById(R.id.wv_day);
        wvHour = (MyWheelView) findViewById(R.id.wv_hour);
        wvYear.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return true;
            }
        });

        wvYear.setItems(listYear);
        wvMonth.setItems(listMonth);
        wvDay.setItems(listDay);
        wvHour.setItems(listHour);
        wvYear.setOnWheelViewListener(new MyWheelView.OnWheelViewListener() {

            @Override
            public void onSelected(int selectedIndex, String item) {
                // TODO Auto-generated method stub
                mYear = item;
            }
        });
        wvMonth.setOnWheelViewListener(new MyWheelView.OnWheelViewListener() {

            @Override
            public void onSelected(int selectedIndex, String item) {
                // TODO Auto-generated method stub
                mMonth = item;
                refreshDayByMonth(item);
            }
        });
        wvDay.setOnWheelViewListener(new MyWheelView.OnWheelViewListener() {

            @Override
            public void onSelected(int selectedIndex, String item) {
                // TODO Auto-generated method stub
                mDay = item;
            }
        });
        wvHour.setOnWheelViewListener(new MyWheelView.OnWheelViewListener() {

            @Override
            public void onSelected(int selectedIndex, String item) {
                // TODO Auto-generated method stub
                mHour = item;
            }
        });
    }

    public void refreshDayByMonth(String month) {
        listDay = new ArrayList<>();
        if (month.equals("02")) {
            if (Integer.valueOf(mYear) % 4 == 0) {
                initDay(29);
            } else {
                initDay(28);
            }

        } else {
            if (month.equals("01") | month.equals("03") | month.equals("05") | month.equals("07") | month.equals("08")
                    | month.equals("10") | month.equals("12")) {
                initDay(31);
            } else {
                initDay(30);
            }
        }
        mDay = listDay.get(0);
        wvDay.setItems(listDay);
        wvDay.setSeletion(0);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_cancel:
                this.dismiss();
                break;
            case R.id.tv_ok:
                this.dismiss();
                if (judgeTime(mYear, mMonth, mDay, mHour)) {
                    onResultCallBack.onSelected(mYear, mMonth, mDay, mHour);
                } else {
                    Toast.makeText(mContext, "你选择的时间不能小于当前时间", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private boolean judgeTime(String year, String month, String day, String hour) {
        // TODO Auto-generated method stub
        hour = hour.split(":")[0];
        Boolean isTime = false;
        if (Integer.parseInt(year) == localCalendar.get(Calendar.YEAR)
                && Integer.parseInt(month) == (localCalendar.get(Calendar.MONTH) + 1)
                && Integer.parseInt(day) == localCalendar.get(Calendar.DATE)
                && Integer.parseInt(hour) > (localCalendar.get(Calendar.HOUR_OF_DAY))) {
            isTime = true;
        } else if (Integer.parseInt(year) > localCalendar.get(Calendar.YEAR)) {
            isTime = true;
        } else if (Integer.parseInt(year) == localCalendar.get(Calendar.YEAR)
                && Integer.parseInt(month) > (localCalendar.get(Calendar.MONTH) + 1)) {
            isTime = true;
        } else if (Integer.parseInt(year) == localCalendar.get(Calendar.YEAR)
                && Integer.parseInt(month) == (localCalendar.get(Calendar.MONTH) + 1)
                && Integer.parseInt(day) > localCalendar.get(Calendar.DATE)) {
            isTime = true;
        }
        return isTime;
    }

    public interface ResultCallBack {
        void onSelected(String year, String month, String day, String hour);
    }

}
