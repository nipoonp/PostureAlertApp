package com.posturealert.smartchair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.posturealert.smartchair.com.posturealert.smartchair.api.APIInterface;
import com.posturealert.smartchair.com.posturealert.smartchair.api.APIReturn;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_fname) EditText _fnameText;
    @Bind(R.id.input_lname) EditText _lnameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_weight) EditText _weightText;
    @Bind(R.id.input_height) EditText _heightText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;

    TextView txtStatus;
    LoginButton login_button;
    CallbackManager callbackManager;
    String firstname = "none";
    String lastname = "none";
    String email = "none";
    String id = "none";
    String gender = "none";
    GoogleApiClient mGoogleApiClient;
    int signedUp = 0;
    private static int RC_SIGN_IN = 420;


    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signup);
        initialiseControls();
        initialiseControlsGoogle();
        loginWithFB();
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() { // "CREATE ACCOUNT" button
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() { // "Already a member? Login" Button
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);

//                intent.putExtra("firstname", firstname);
//                intent.putExtra("lastname", lastname);
//                intent.putExtra("id", id);



                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }


    private void initialiseControls() {
        callbackManager = CallbackManager.Factory.create();
        login_button = (LoginButton) findViewById(R.id.login_button);
        login_button.setReadPermissions(Arrays.asList("email"));


    }

    private void initialiseControlsGoogle(){

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void loginWithFB() {


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("TAAAAAAAAGGGGGG", "SUCCESS");
                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {
                                    Log.d("this is it 1", object.toString());
                                    id = object.getString("id");
                                    firstname = object.getString("first_name");
                                    lastname = object.getString("last_name");
                                    gender = object.getString("gender");
                                    email = object.getString("email");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d("this is it", id + "   " + firstname + "   " + lastname + "   " + email + "   " + gender + "   ");
                                _fnameText.setText(firstname);
                                _lnameText.setText(lastname);
                                _emailText.setText(email);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,gender"); //email not working
                request.setParameters(parameters);
                request.executeAsync();

                LoginManager.getInstance().logOut();





            }

            @Override
            public void onCancel() {

                Log.d("TAAAAAAAAGGGGGG", "CANCEL");
            }

            @Override
            public void onError(FacebookException error) {

                Log.d("TAAAAAAAAGGGGGG", "ERROR");
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("TAG", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
             firstname = acct.getGivenName();
             email = acct.getEmail();
             lastname = acct.getFamilyName();
             id = acct.getId();

            _fnameText.setText(firstname);
            _lnameText.setText(lastname);
            _emailText.setText(email);



            Log.d("GOOGLE HERE", firstname + email + lastname + id);


        } else {
            // Signed out, show unauthenticated UI.
            Log.d("TAG", "signed out");
        }
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String fname = _fnameText.getText().toString();
        String lname = _lnameText.getText().toString();
        String email = _emailText.getText().toString();
        String weight = _weightText.getText().toString();
        String height = _heightText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        //  Call NodeJS API
        retrofit(fname,lname,email,weight,height,password);


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        if(signedUp == 1) {
                            onSignupSuccess();
                        } else{
                            onSignupFailed();
                        }
                        //
                        progressDialog.dismiss();
                    }
                }, 5000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Toast.makeText(getBaseContext(), "Registration Successful! :)", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        //finish(); // TODO Relocate back to login screen.
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Registration failed! You may already be registered! :(", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String fname = _fnameText.getText().toString();
        String lname = _lnameText.getText().toString();
        String email = _emailText.getText().toString();
        String weight = _weightText.getText().toString();
        String height = _heightText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (fname.isEmpty() || fname.length() < 3) {
            _fnameText.setError("At least 3 characters");
            valid = false;
        } else {
            _fnameText.setError(null);
        }



        if (lname.isEmpty() || lname.length() < 3) {
            _lnameText.setError("At least 3 characters");
            valid = false;
        } else {
            _lnameText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (weight.isEmpty()) {
            _weightText.setError("Enter valid weight in KG");
            valid = false;
        } else {
            _weightText.setError(null);
        }

        if (height.isEmpty()) {
            _heightText.setError("Enter valid height in Centimeters");
            valid = false;
        } else {
            _heightText.setError(null);
        }



        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                signOut();
                break;


            // ...
        }
    }

    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void retrofit(String fname, String lname, String email, String weight, String height, String password){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://13.55.201.70:8099/").addConverterFactory(GsonConverterFactory.create()).build();

        APIInterface service = retrofit.create(APIInterface.class);
        Call<APIReturn> call = service.registerUser(fname, lname, email, weight, height, password);


        call.enqueue(new Callback<APIReturn>() {
            @Override
            public void onResponse(Call<APIReturn> call, Response<APIReturn> response) {

                APIReturn s = response.body();


                //Toast.makeText(SignupActivity.this, "Success :) " + s.getStatus(), Toast.LENGTH_LONG).show();

                if(s.getStatus().equals("0")){
                    signedUp = 0;
                } else if( s.getStatus().equals("1")){
                    signedUp = 1;
                }
            }

            @Override
            public void onFailure(Call<APIReturn> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "error :(" + call + t, Toast.LENGTH_LONG).show();
            }
        });
    }

}