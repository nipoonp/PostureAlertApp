package com.posturealert.smartchair;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.posturealert.smartchair.com.posturealert.smartchair.api.APIInterface;
import com.posturealert.smartchair.com.posturealert.smartchair.api.APIReturn;

import butterknife.ButterKnife;
import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;

    String value2 = "";
    String value1 = "";
    String value = "";

    String fnameDb, lnameDb, idDb, emailDb, weightDb, heightDb, passwordDb;

    int status = 2; //default user does not exist
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity

                Intent intent = new Intent(getApplicationContext(), Main.class);

                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            Toast.makeText(getBaseContext(), "Invalid Email or Password!", Toast.LENGTH_LONG).show();
            return;
        }

        _loginButton.setEnabled(false);

//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//             value = extras.getString("firstname");
//             value1 = extras.getString("lastname");  // When you click login AFTER REGISTER SCREEN, values are recevied.
//             value2 = extras.getString("id");
//
//        }
//
//        Log.d("firstname", value);
//        Log.d("lastname", value1);
//        Log.d("id", value2);


        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        retrofit(email,password);



        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(status == 0){
                            onLoginSuccess();
                        }else{
                            onLoginFailed();
                        }

                        //
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);

        Toast.makeText(getBaseContext(), "Signed In!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(),Main.class);

        intent.putExtra("firstname", fnameDb);
        intent.putExtra("lastname", lnameDb);
        intent.putExtra("id", idDb);
        intent.putExtra("email", emailDb);
        intent.putExtra("weight", weightDb);
        intent.putExtra("height", heightDb);
        intent.putExtra("password", passwordDb); //Dont think we need this.

        startActivity(intent);






        finish();
    }

    public void onLoginFailed() {

        if(status == 2) {
            Toast.makeText(getBaseContext(), "Login failed, Email Doesn't Exist. Please Register.", Toast.LENGTH_LONG).show();
        } else if (status == 1){
            Toast.makeText(getBaseContext(), "Login failed, Incorrect Password.", Toast.LENGTH_LONG).show();
        }

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void retrofit(String email, String password){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://13.55.201.70:8099/").addConverterFactory(GsonConverterFactory.create()).build();

        APIInterface service = retrofit.create(APIInterface.class);
        Call<APIReturn> call = service.loginUser(email, password);


        call.enqueue(new Callback<APIReturn>() {
            @Override
            public void onResponse(Call<APIReturn> call, Response<APIReturn> response) {

                APIReturn s = response.body();


                //Toast.makeText(SignupActivity.this, "Success :) " + s.getStatus(), Toast.LENGTH_LONG).show();

                if(s.getStatus().equals("0")){
                    status = 0;
                    fnameDb = s.getFname();
                    lnameDb = s.getLname();
                    idDb = s.getId();
                    emailDb = s.getEmail();
                    weightDb = s.getWeight();
                    heightDb = s.getHeight();
                    passwordDb = s.getPassword();

                } else if( s.getStatus().equals("1")){
                    status = 1;
                }else if (s.getStatus().equals("2")){
                    status = 2;
                }
            }

            @Override
            public void onFailure(Call<APIReturn> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "error :(" + call + t, Toast.LENGTH_LONG).show();
            }
        });
    }



}
