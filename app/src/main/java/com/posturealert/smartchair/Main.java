package com.posturealert.smartchair;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.MenuItem;
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
    NotificationCompat.Builder notfication2;
    String fnameDb, lnameDb, idDb, emailDb, weightDb, heightDb, passwordDb;
    int notification_counter = 0;
    int notification2_counter = 0;

    int posture_value_good = 0;
    int posture_value_bad = 0;
    int recent_posture = 0;
    int old_posture_value_good = 99999;
    int Entries = 0;
    int Bad_posture = 0;
    String text_to_show = "TEMP LKNSDNLKSLK  KLDNSFNLKSDKFLN SLK FLSKN DLKNSL DKLKSD LKFNSDLKF NLKSD LFSLKDFN SLS DLKFNSLKD FLKSDLK FSLKN DLKNSD LKS FNDSLSLKD FLKNSLDKFKSDFLK SDLKFNS DLKNL FSLKDSFLK LKSFDSLK FKNLSKLDNFNLSDKF LKDSFFL DSNLKS DLKN SKDK FDSLKNS FDLKS FL SFLKNSDFNLK SLKDNFDSLKFN";

    private static final int uniqueID = 45612;

    //Timer stuff
    TextView textGoesHere;
    TextView textGoesHere2;
    TextView totalTime;
    Button button2;

    TextView currentPosture;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_notification:
                    ToggleNotifications();
                    return true;
                case R.id.navigation_train:

                    Intent intent = new Intent(getApplicationContext(),Train.class);
                    intent.putExtra("firstname", fnameDb);
                    intent.putExtra("lastname", lnameDb);
                    intent.putExtra("id", idDb);
                    intent.putExtra("email", emailDb);
                    intent.putExtra("weight", weightDb);
                    intent.putExtra("height", heightDb);
                    intent.putExtra("password", passwordDb); //Dont think we need this.

                    startActivity(intent);
                    return true;
                case R.id.navigation_Profile:
                    Intent nIntent = new Intent(Main.this, Profile.class);

                    nIntent.putExtra("firstname", fnameDb);
                    nIntent.putExtra("lastname", lnameDb);
                    nIntent.putExtra("id", idDb);
                    nIntent.putExtra("email", emailDb);
                    nIntent.putExtra("weight", weightDb);
                    nIntent.putExtra("height", heightDb);
                    nIntent.putExtra("password", passwordDb); //Dont think we need this.

                    startActivity(nIntent);
                    return true;
                case R.id.navigation_reports:
                    Intent newIntent = new Intent(Main.this, Dashboard.class);

                    newIntent.putExtra("firstname", fnameDb);
                    newIntent.putExtra("lastname", lnameDb);
                    newIntent.putExtra("id", idDb);
                    newIntent.putExtra("email", emailDb);
                    newIntent.putExtra("weight", weightDb);
                    newIntent.putExtra("height", heightDb);
                    newIntent.putExtra("password", passwordDb); //Dont think we need this.

                    startActivity(newIntent);
                    return true;
                case R.id.navigation_Summary:
                    Intent neIntent = new Intent(Main.this, Summary.class);

                    neIntent.putExtra("firstname", fnameDb);
                    neIntent.putExtra("lastname", lnameDb);
                    neIntent.putExtra("id", idDb);
                    neIntent.putExtra("email", emailDb);
                    neIntent.putExtra("weight", weightDb);
                    neIntent.putExtra("height", heightDb);
                    neIntent.putExtra("password", passwordDb); //Dont think we need this.

                    startActivity(neIntent);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        textGoesHere = (TextView) findViewById(R.id.txtResponse);
        textGoesHere2 = (TextView) findViewById(R.id.txtResponse2);
        totalTime = (TextView) findViewById(R.id.totalTime);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Roboto-Regular.ttf");
        textGoesHere.setTypeface(custom_font);
        textGoesHere2.setTypeface(custom_font);
        totalTime.setTypeface(custom_font);

        currentPosture = (TextView) findViewById(R.id.textView3);
        currentPosture.setTypeface(custom_font);

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


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://13.55.201.70:8099/popup/" + idDb, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody != null) {

                    try {
                        JSONObject jsonObj = new JSONObject(new String(responseBody));
                        Entries = jsonObj.getInt("Entries");
                        Bad_posture = jsonObj.getInt("Bad_posture");
                        Log.d("THIS1", Integer.toString(Entries));
                        Log.d("THIS2", Integer.toString(Bad_posture));

                        switch(Bad_posture) {
                            case 1 :
                                text_to_show = "Your legs are raised all the time. Try to have them more parallel to the ground.";
                                break; // optional

                            case 2 :
                                text_to_show = "Your always sitting on the edge. Sit back.";
                                break; // optional

                            case 3 :
                                text_to_show = "Your back is not supported enough. Relax by leaning further back.";
                                break; // optional

                            case 4 :
                                text_to_show = "Your upper back is not supported enough. Relax by leaning further back.";
                                break; // optional

                            case 5 :
                                text_to_show = "Your sitting awkwardly a lot of the time. Try sitting further back.";
                                break; // optional

                            case 6 :
                                text_to_show = "You've been leaning too much to the right lately. Try sitting more evenly.";
                                break; // optional

                            case 7 :
                                text_to_show = "You've been leaning too much to the left lately. Try sitting more evenly.";
                                break; // optional

                            case 8 :
                                text_to_show = "Your right leg is crossed too often. Try crossing your legs less.";
                                break; // optional

                            case 9 :
                                text_to_show = "Your left leg is crossed too often. Try crossing your legs less.";
                                break; // optional

                            // You can have any number of case statements.
                            default : // Optional
                                text_to_show = "NONE";
                        }

                        if(Entries > 100){

                            AlertDialog.Builder builder  = new AlertDialog.Builder(Main.this);

                            builder.setCancelable(true);
                            builder.setTitle("Posture Alert");
                            builder.setMessage(text_to_show);


                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i){
                                    dialogInterface.cancel();
                                }
                            });

                            TextView Message = (TextView) builder.show().findViewById(android.R.id.message); // This shows the dialogue box
                            Message.setTextSize(20);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("ERROR", "ERROR HAS OCCURED");
            }
        });

        //probably dont need this anymore. This was the get button stuff.
//        Button btnGet = (Button) findViewById(R.id.btnGet);
//        assert btnGet != null;
//        btnGet.setOnClickListener(this);

        /////// Bad Posture Notfication stuff/////////////////////////////
        notfication = new NotificationCompat.Builder(this);
        notfication.setAutoCancel(true);

        Intent intent = new Intent(this, Main.class);
        final PendingIntent pendingintent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notfication.setSmallIcon(R.mipmap.ic_launcher); //change notfication image here
        notfication.setTicker("This is the ticker");
        notfication.setWhen(System.currentTimeMillis());
        notfication.setContentTitle("Posture Alert");
        notfication.setContentText("Bad Posture Detected!");
        notfication.setContentIntent(pendingintent);
        ///////Bad Posture Notfication stuff END/////////////////////////////


        ////////Sitting Too Long Notfication Stuff////////////////////////
        notfication2 = new NotificationCompat.Builder(this);
        notfication2.setAutoCancel(true);

        Intent intent2 = new Intent(this, Main.class);
        final PendingIntent pendingintent2 = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notfication2.setSmallIcon(R.mipmap.ic_launcher); //change notfication image here
        notfication2.setTicker("This is the ticker");
        notfication2.setWhen(System.currentTimeMillis());
        notfication2.setContentTitle("Posture Alert");
        notfication2.setContentText("You've been sitting for a while. Maybe take a break.");
        notfication2.setContentIntent(pendingintent);
        ////////Sitting Too Long Notification Stuff END////////////////////

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
                                            recent_posture = jsonObj.getInt("recent_posture");
                                            textGoesHere = (TextView) findViewById(R.id.txtResponse);
                                            textGoesHere2 = (TextView) findViewById(R.id.txtResponse2);
                                            Log.d("Posture", Integer.toString(posture_value_good));
                                            Log.d("Posture2", Integer.toString(posture_value_bad));
                                            Log.d("recent_posture", Integer.toString(recent_posture));
                                            textGoesHere.setText(convertTime(2* posture_value_good));
                                            textGoesHere2.setText(convertTime(2* posture_value_bad));
                                            totalTime.setText(convertTime((2*posture_value_bad) + (2*posture_value_good)));


                                            if (recent_posture == 0){
                                                currentPosture.setText("Unoccupied");
                                            } else if(recent_posture == 1){
                                                currentPosture.setText("Legs raised");
                                            }else if(recent_posture == 2){
                                                currentPosture.setText("Sitting forward");
                                            }else if(recent_posture == 3){
                                                currentPosture.setText("Leaning forward");
                                            }else if(recent_posture == 4){
                                                currentPosture.setText("Lightly forward");
                                            }else if(recent_posture == 5){
                                                currentPosture.setText("Leaning backwards");
                                            }else if(recent_posture == 6){
                                                currentPosture.setText("Leaning right");
                                            }else if(recent_posture == 7){
                                                currentPosture.setText("Leaning left");
                                            }else if(recent_posture == 8){
                                                currentPosture.setText("Right leg crossed");
                                            }else if(recent_posture == 9){
                                                currentPosture.setText("Left leg crossed");
                                            }else if(recent_posture == 10){
                                                currentPosture.setText("Good posture");
                                            }else if(recent_posture == 11){
                                                currentPosture.setText("Perfect posture");
                                            }





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

                                        //Alert users to take a break regardless of setting.
                                        if( recent_posture != 0){
                                            notification2_counter = notification2_counter + 1;
                                        } else{
                                            notification2_counter = 0;
                                        }

                                        if(notification2_counter == 60 ){ //i.e sitting down for a 2-minutes

                                            notification2_counter = 0;
                                            NotificationManager nm2 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                            nm2.notify(uniqueID, notfication2.build());
                                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            v.vibrate(500);
                                            MakeSound();
                                            ScreenOn();

                                            Log.d("Tag2", "got notification2");
                                        }
                                        //Take a break notifications ends here///////
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
        String secondsO;
        String mintuesO;

        if(seconds < 9){
            secondsO = "0";
        } else{
            secondsO = "";
        }

        if(minutes < 9){
            mintuesO = "0";

        }else{

            mintuesO = "";
        }






        if (hours == 0){
            return mintuesO + minutes + ":" + secondsO + seconds;
        } else {
            return hours + ":" + mintuesO + minutes + ":" + secondsO + seconds;
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


    public void ToggleNotifications() { //on click
        Log.d("Tag2", "ran this code");
        notifyFlag = !notifyFlag;
        if(notifyFlag){
            Toast.makeText(getBaseContext(), "Notifications ON", Toast.LENGTH_LONG).show();
//            button2.setBackgroundResource(R.drawable.ic_notifications_active_black_36dp);


        }else{
            Toast.makeText(getBaseContext(), "Notifications OFF", Toast.LENGTH_LONG).show();
//            button2.setBackgroundResource(R.drawable.ic_notifications_off_black_36dp);
        }
//        try {
//            notifyThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
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