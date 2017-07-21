package com.example.khm.goldentimealarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Created by khm on 2017-07-20.
 */

public class AlarmService extends Service{
    public final static int REQUESTCODE = 1001;
    Handler handler;
    HttpURLConnection conn;
    URL url;
    StringBuilder output = new StringBuilder();

    Document doc;
    //Elements item;
    String time;
    Date now;
    Date then;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        Log.d("Service","onStartCommand is called");
        Toast.makeText(getApplicationContext(), "service started",Toast.LENGTH_SHORT).show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    doc = Jsoup.connect("http://www.kyobobook.co.kr/prom/2016/general/160323_nonstop.jsp?orderClick=c0z")
                            .userAgent("Mozilla/5.0")
                            .get();
                    time = doc.getElementsByClass("goldentime off").html();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try {
            thread.join();
            if(!time.contains("??:??")) {
                now = new Date();   //현재 날짜
                //1900 + a값, month - 1, date, time
                then = new Date(2017-1900, now.getMonth(), now.getDate(), 22, 0);
                int Total_sec = (int)Math.floor((then.getTime() - now.getTime())/1000) - 13;
                String remainTime = Day_counter(Total_sec); //남은 시간
                //Notification 발생
                notificationActivate(intent, remainTime);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        Log.d("Service","onDestroy is called");

        Toast.makeText(getApplicationContext(), "service destroyed",Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private void notificationActivate(Intent intent, String remainTime){
        Intent notiIntent = new Intent(AlarmService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(AlarmService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("골든타임 알람")
                .setContentText("골든타임이 시작됐습니다." + remainTime +" 남음")
                .setSmallIcon(R.drawable.favicon)
                .setTicker("골든타임")
                .setContentIntent(pendingIntent)
                .build();

        notification.defaults = Notification.DEFAULT_VIBRATE;
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(777, notification);
    }

    private String Day_counter(int Total_sec){
        int Remain_days = (int)Math.floor(Total_sec / 86400);
        int Remain_tot_sec = Total_sec - 86400 * Remain_days;
        int Remain_hour = (int)Math.floor(Remain_tot_sec / 3600);
        int tmp = Remain_tot_sec - Remain_hour * 3600;
        int Remain_minute =(int)Math.floor(tmp / 60);
        int Remain_sec = (int)Math.floor(tmp % 60);

        String strRemain_sec;
        String strRemain_minute;
        String strRemain_hour;

        strRemain_sec = (Remain_sec < 10 ? "0" : "") + Remain_sec;
        strRemain_minute = (Remain_minute < 10 ? "0" : "") + Remain_minute;

        return  strRemain_minute + " : " + strRemain_sec;
    }
}
