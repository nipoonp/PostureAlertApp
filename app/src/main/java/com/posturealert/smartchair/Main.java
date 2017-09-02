package com.posturealert.smartchair;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.ImageView;
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
    public boolean s0_saturated, s1_saturated, s2_saturated, s3_saturated, s4_saturated;
    public boolean notifyFlag = false;
    public int SATURATION_LIMIT = 1000;

    Thread posture_thread;

    NotificationCompat.Builder notfication;
    String fnameDb, lnameDb, idDb, emailDb, weightDb, heightDb, passwordDb;
    int notification_counter = 0;
    boolean bad = true;
    int posture_value = 0;
    private static final int uniqueID = 45612;



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
                            if (notifyFlag) {
                                AsyncHttpClient client2 = new AsyncHttpClient();
                                client2.get("http://13.55.201.70:8099/getNotifications/" + idDb, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        if (responseBody != null) {

                                            try {
                                                JSONObject jsonObj = new JSONObject(new String(responseBody));
                                                posture_value = jsonObj.getInt("Posture");
                                                Log.d("Posture", Integer.toString(posture_value));
                                                //TODO check which ones are the correct posture

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            if (bad) {
                                                notification_counter = notification_counter + 1;
                                            } else {
                                                notification_counter = 0;
                                            }

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

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        Log.d("ERROR", "ERROR HAS OCCURED");
                                    }
                                });


                            }
                        }
                    });
                }
            }
        };


        posture_thread = new Thread(runnable);


        b0 = (Button) findViewById(R.id.b0);
        b1 = (Button) findViewById(R.id.b1);
        b2 = (Button) findViewById(R.id.b2);
        b3 = (Button) findViewById(R.id.b3);
        b4 = (Button) findViewById(R.id.b4);

        posture_thread.start();

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

    public void ToggleNotifications(View v) {
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
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.get("http://13.55.201.70:8099/sensorReadings", new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    if (responseBody != null) {

                                        TextView txtResponse = (TextView) findViewById(R.id.txtResponse);
                                        TextView txtResponse2 = (TextView) findViewById(R.id.txtResponse2);
                                        assert txtResponse != null;
                                        txtResponse.setText(new String(responseBody));

                                        try {
                                            JSONObject jsonObj = new JSONObject(new String(responseBody));
                                            int s0 = jsonObj.getInt("s0");
                                            int s1 = jsonObj.getInt("s1");
                                            int s2 = jsonObj.getInt("s2");
                                            int s3 = jsonObj.getInt("s3");
                                            int s4 = jsonObj.getInt("s4");

                                            b0.setBackgroundColor(Color.rgb(255, 255 - s0 / 4, 255 - s0 / 4));
                                            b1.setBackgroundColor(Color.rgb(255, 255 - s1 / 4, 255 - s1 / 4));
                                            b2.setBackgroundColor(Color.rgb(255, 255 - s2 / 4, 255 - s2 / 4));
                                            b3.setBackgroundColor(Color.rgb(255, 255 - s3 / 4, 255 - s3 / 4));
                                            b4.setBackgroundColor(Color.rgb(255, 255 - s4 / 4, 255 - s4 / 4));

                                            s0_saturated = (s0 < SATURATION_LIMIT) ? true : false;
                                            s1_saturated = (s1 < SATURATION_LIMIT) ? true : false;
                                            s2_saturated = (s2 < SATURATION_LIMIT) ? true : false;
                                            s3_saturated = (s3 < SATURATION_LIMIT) ? true : false;
                                            s4_saturated = (s4 < SATURATION_LIMIT) ? true : false;

                                            if (s0_saturated == false && s1_saturated == false && s2_saturated == false && s3_saturated == false) {
                                                txtResponse2.setText(new String("1. Seat not in use..."));
                                            } else if (s0_saturated == false && s1_saturated == false && s2_saturated == true && s3_saturated == true) {
                                                txtResponse2.setText(new String("2. User is in front of chair with legs raised..."));
                                            } else if (s0_saturated == true && s1_saturated == true && s2_saturated == false && s3_saturated == false) {
                                                txtResponse2.setText(new String("6. Front of chair, leaning backward..."));
                                            } else if (s0_saturated == true && s1_saturated == true && s2_saturated == true && s3_saturated == false) {
                                                txtResponse2.setText(new String("7. Leaning left or forward left..."));
                                            } else if (s0_saturated == true && s1_saturated == true && s2_saturated == false && s3_saturated == true) {
                                                txtResponse2.setText(new String("8. Leaning right or forward right..."));
                                            } else if (s0_saturated == false && s1_saturated == true && s2_saturated == true && s3_saturated == true) {
                                                txtResponse2.setText(new String("9. Left leg crossed"));
                                            } else if (s0_saturated == true && s1_saturated == false && s2_saturated == true && s3_saturated == true) {
                                                txtResponse2.setText(new String("10. Right leg crossed..."));
                                            } else if (s0_saturated == false && s1_saturated == false && s2_saturated == true && s3_saturated == true) {
                                                txtResponse2.setText(new String("11. Torso and legs are correctly aligned..."));
                                            } else {
                                                txtResponse2.setText(new String("Suhan is gay..."));
                                            }


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    v.setEnabled(true);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    v.setEnabled(true);
                                }
                            });
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();


    }
}
