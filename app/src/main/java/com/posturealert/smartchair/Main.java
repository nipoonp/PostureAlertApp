package com.posturealert.smartchair;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import android.os.Vibrator;


public class Main extends AppCompatActivity implements View.OnClickListener {

    public Button b0, b1, b2, b3, b4;
    public boolean notifyFlag = false;

    Thread posture_thread;
    NotificationCompat.Builder notfication;
    String fnameDb, lnameDb, idDb, emailDb, weightDb, heightDb, passwordDb;
    int notification_counter = 0;
    int posture_value_good = 0;
    int posture_value_bad = 0;
    int old_posture_value_good = 99999;

    private static final int uniqueID = 45612;

    //Timer stuff
    TextView textGoesHere;
    TextView textGoesHere2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fnameDb = extras.getString("firstname");
            lnameDb = extras.getString("lastname");  // When you click login AFTER REGISTER SCREEN, values are recevied.
            idDb = extras.getString("id");
            emailDb = extras.getString("email");
            weightDb = extras.getString("weight");  // When you click login AFTER REGISTER SCREEN, values are recevied.
            heightDb = extras.getString("height");
            passwordDb = extras.getString("password");
        }



        Button train = (Button)findViewById(R.id.train);

        train.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {



                Intent intent = new Intent(getApplicationContext(),Train.class);
                intent.putExtra("firstname", fnameDb);
                intent.putExtra("lastname", lnameDb);
                intent.putExtra("id", idDb);
                intent.putExtra("email", emailDb);
                intent.putExtra("weight", weightDb);
                intent.putExtra("height", heightDb);
                intent.putExtra("password", passwordDb); //Dont think we need this.

                startActivity(intent);
            }
        });

        Button btnGet = (Button) findViewById(R.id.btnGet);
        assert btnGet != null;
        btnGet.setOnClickListener(this);

        notfication = new NotificationCompat.Builder(this);
        notfication.setAutoCancel(true);

        Intent intent = new Intent(this, Main.class);
        final PendingIntent pendingintent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notfication.setSmallIcon(R.mipmap.ic_launcher);
        notfication.setTicker("This is the ticker");
        notfication.setWhen(System.currentTimeMillis());
        notfication.setContentTitle("Posture Alert");
        notfication.setContentText("Bad Posture Detected!");
        notfication.setContentIntent(pendingintent);

        //Check the most recent posture value. EVERY 2 SECONDS.
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        public void run() {

                            AsyncHttpClient client2 = new AsyncHttpClient();
                            client2.get("http://13.55.201.70:8099/getNotifications/" + idDb, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    if (responseBody != null) {

                                        try {
                                            JSONObject jsonObj = new JSONObject(new String(responseBody));
                                            posture_value_good = jsonObj.getInt("good_posture_time");
                                            posture_value_bad = jsonObj.getInt("bad_posture_time");
                                            textGoesHere = (TextView) findViewById(R.id.txtResponse);
                                            textGoesHere2 = (TextView) findViewById(R.id.txtResponse2);
                                            Log.d("Posture", Integer.toString(posture_value_good));
                                            Log.d("Posture2", Integer.toString(posture_value_bad));
                                            textGoesHere.setText("GOOD: " + convertTime(2* posture_value_good));
                                            textGoesHere2.setText("BAD: " + convertTime(2* posture_value_bad));


                                            // check which ones are the correct posture

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        if(notifyFlag) {
                                            if (posture_value_good == old_posture_value_good) {
                                                notification_counter++;
                                            } else {
                                                notification_counter = 0;
                                            }

                                            old_posture_value_good = posture_value_good;

                                            if (notification_counter == 5) { // change this to get a different time for notfications
                                                notification_counter = 0;
                                                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                nm.notify(uniqueID, notfication.build());
                                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                v.vibrate(500);
                                                MakeSound();
                                                ScreenOn();

                                                Log.d("Tag1", "got notification");
                                            }

                                        }
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    Log.d("ERROR", "ERROR HAS OCCURED");
                                }
                            });



                        }
                    });
                }
            }
        };


        posture_thread = new Thread(runnable);
        posture_thread.start();
    }

    private String convertTime(int totalSecs){

        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        if (hours == 0){
            return minutes + ":" + seconds;
        } else {
            return hours + ":" + minutes + ":" + seconds;
        }



    }

    public void ScreenOn(){

        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if(isScreenOn==false)
        {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");

            wl_cpu.acquire(10000);
        }


    }

    public void MakeSound(){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void ToggleNotifications(View v) { //on click
        Log.d("Tag2", "ran this code");
        notifyFlag = !notifyFlag;
        if(notifyFlag){
            Toast.makeText(getBaseContext(), "Notifications ON", Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(getBaseContext(), "Notifications OFF", Toast.LENGTH_LONG).show();


        }
//        try {
//            notifyThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public void getReport(View v) {
        Intent newIntent = new Intent(Main.this, Dashboard.class);

        newIntent.putExtra("firstname", fnameDb);
        newIntent.putExtra("lastname", lnameDb);
        newIntent.putExtra("id", idDb);
        newIntent.putExtra("email", emailDb);
        newIntent.putExtra("weight", weightDb);
        newIntent.putExtra("height", heightDb);
        newIntent.putExtra("password", passwordDb); //Dont think we need this.

        startActivity(newIntent);
    }

    @Override
    public void onClick(final View v) {


        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        public void run() {
                            v.setEnabled(false);

                        }
                    });
                }
            }
        };
        new Thread(runnable).start();


    }
}
