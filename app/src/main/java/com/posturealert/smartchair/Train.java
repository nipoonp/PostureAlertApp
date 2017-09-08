package com.posturealert.smartchair;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.posturealert.smartchair.com.posturealert.smartchair.api.APIInterface;
import com.posturealert.smartchair.com.posturealert.smartchair.api.APIReturn;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Train extends AppCompatActivity {

    private ImageSwitcher sw;
    private Button train;
    private int pos = 11;

    String fnameDb, lnameDb, idDb, emailDb, weightDb, heightDb, passwordDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

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

        sw = (ImageSwitcher) findViewById(R.id.imgsw);
        train = (Button) findViewById(R.id.train);

        sw.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                return imageView;
            }
        });

        sw.setImageResource(R.drawable.a12);

        train.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                retrofit();
            }
        });

    }


    public void retrofit(){
        final TextView textView = (TextView) findViewById(R.id.textView);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://13.55.201.70:8099/").addConverterFactory(GsonConverterFactory.create()).build();

        long seconds = System.currentTimeMillis()/1000;


        APIInterface service = retrofit.create(APIInterface.class);
        Call<APIReturn> call = service.trainData(idDb,String.valueOf(pos),String.valueOf(seconds));

        call.enqueue(new Callback<APIReturn>() {
            @Override
            public void onResponse(Call<APIReturn> call, Response<APIReturn> response) {

                APIReturn s = response.body();

                switch(pos) {
                    case 5 :
//                        sw.setImageResource(R.drawable.b6);
                        Intent intent = new Intent(getApplicationContext(),Main.class);
                        intent.putExtra("firstname", fnameDb);
                        intent.putExtra("lastname", lnameDb);
                        intent.putExtra("id", idDb);
                        intent.putExtra("email", emailDb);
                        intent.putExtra("weight", weightDb);
                        intent.putExtra("height", heightDb);
                        intent.putExtra("password", passwordDb); //Dont think we need this.

                        startActivity(intent);
                        break;
                    case 6 :
                        sw.setImageResource(R.drawable.b6);
                        pos = 5;
                        break;
                    case 11 :
                        sw.setImageResource(R.drawable.a7);
                        pos = 6;
                        break;
                    default : // Optional
                        // Statements
                }


                Toast.makeText(Train.this, "Success :) " + s.getStatus(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<APIReturn> call, Throwable t) {
                Toast.makeText(Train.this, "error :(" + call + t, Toast.LENGTH_LONG).show();
                textView.setText(call + "\n" + t);
            }
        });
    }

}
