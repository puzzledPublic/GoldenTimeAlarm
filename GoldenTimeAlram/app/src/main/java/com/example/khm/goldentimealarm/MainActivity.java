package com.example.khm.goldentimealarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    Button startBtn;
    Button stopBtn;
    AlarmManager alarmManager;
    PendingIntent servicePendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = (Button)findViewById(R.id.start);
        stopBtn  = (Button)findViewById(R.id.stop);

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        final Intent serviceIntent = new Intent(getApplicationContext(), AlarmService.class);
        servicePendingIntent = PendingIntent.getService(getApplicationContext(), AlarmService.REQUESTCODE, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmStart();
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alarmManager.cancel(servicePendingIntent);
                stopService(serviceIntent);
            }
        });
    }

    public void alarmStart(){
        //20분마다 알림 서비스 호출
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() ,1000*60*20,servicePendingIntent);

        //테스트용 알림
        //alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),servicePendingIntent);
    }
}
