package com.posturealert.smartchair;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.posturealert.smartchair.com.posturealert.smartchair.api.APIInterface;
import com.posturealert.smartchair.com.posturealert.smartchair.api.APIReturn;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserInfo extends AppCompatActivity {

    final Context context = this;
    private Button mbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        retrofit();

    }

    public void retrofit(){
        final TextView textView = (TextView) findViewById(R.id.textView);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://13.55.201.70:8098/").addConverterFactory(GsonConverterFactory.create()).build();

        APIInterface service = retrofit.create(APIInterface.class);
        Call<APIReturn> call = service.getUserInfo("1001");

        call.enqueue(new Callback<APIReturn>() {
            @Override
            public void onResponse(Call<APIReturn> call, Response<APIReturn> response) {
                APIReturn s = response.body();
                textView.setText("UserID: " + s.getUserID() + "\n" + "First Name: " + s.getFirstName() + "\n" + "Last Name: " + s.getLastName() + "\n" + "ChairID: " + s.getChairID());


                    Button openDialogButton = (Button)findViewById(R.id.changeChairID);
                    openDialogButton.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View arg0) {
                            final Dialog openDialog = new Dialog(context);
                            openDialog.setContentView(R.layout.chairid_dialog);
                            openDialog.setTitle("Custom Dialog Box");
                            TextView dialogTextContent = (TextView)openDialog.findViewById(R.id.dialog_text);
                            ImageView dialogImage = (ImageView)openDialog.findViewById(R.id.dialog_image);
                            Button dialogCloseButton = (Button)openDialog.findViewById(R.id.dialog_button);
                            Button change_button = (Button) openDialog.findViewById(R.id.change_button);
                            final EditText text_change_chairID = (EditText) findViewById(R.id.text_change_chairID) ;


                            dialogCloseButton.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    openDialog.dismiss();
                                }
                            });

                            change_button.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub

                                    Toast.makeText(UserInfo.this, "Request to ChairID change sent", Toast.LENGTH_LONG).show();
                                    openDialog.dismiss();
                                }

                            });

                            openDialog.show();
                        }
                    });


            }

            @Override
            public void onFailure(Call<APIReturn> call, Throwable t) {
                Toast.makeText(UserInfo.this, "error :(" + call + t, Toast.LENGTH_LONG).show();
                textView.setText(call + "\n" + t);
            }
        });
    }

}
