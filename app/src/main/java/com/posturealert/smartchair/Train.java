package com.posturealert.smartchair;

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
    private int pos = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

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


        train.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch(pos) {
                    case 0 :
                        sw.setImageResource(R.drawable.b1);
                        break;
                    case 1 :
                        sw.setImageResource(R.drawable.b2);
                        break;
                    case 2 :
                        sw.setImageResource(R.drawable.b3);
                        break;
                    case 3 :
                        sw.setImageResource(R.drawable.b4);
                        break;
                    case 4 :
                        sw.setImageResource(R.drawable.b5);
                        break;
                    case 5 :
                        sw.setImageResource(R.drawable.b6);
                        break;
                    case 6 :
                        sw.setImageResource(R.drawable.a7);
                        break;
                    case 7 :
                        sw.setImageResource(R.drawable.a8);
                        break;
                    case 8 :
                        sw.setImageResource(R.drawable.a9);
                        break;
                    case 9 :
                        sw.setImageResource(R.drawable.a10);
                        break;
                    case 10 :
                        sw.setImageResource(R.drawable.a11);
                        break;
                    case 11 :
                        sw.setImageResource(R.drawable.a12);
                        break;
                    default : // Optional
                        // Statements
                }

                retrofit(String.valueOf(pos));


            }
        });



    }


    public void retrofit(String posture){
        final TextView textView = (TextView) findViewById(R.id.textView);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://13.55.201.70:8099/").addConverterFactory(GsonConverterFactory.create()).build();

        long seconds = System.currentTimeMillis() / 1000;


        APIInterface service = retrofit.create(APIInterface.class);
        Call<APIReturn> call = service.trainData("7",posture,String.valueOf(seconds));

        call.enqueue(new Callback<APIReturn>() {
            @Override
            public void onResponse(Call<APIReturn> call, Response<APIReturn> response) {

                APIReturn s = response.body();
                pos++;
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
