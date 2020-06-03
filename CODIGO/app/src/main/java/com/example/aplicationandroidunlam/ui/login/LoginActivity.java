package com.example.aplicationandroidunlam.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplicationandroidunlam.CreateUserActivity;
import com.example.aplicationandroidunlam.R;
import com.example.aplicationandroidunlam.SensorTabActivity;
import com.example.aplicationandroidunlam.servicesHandlers.EventsHandler;
import com.example.aplicationandroidunlam.servicesHandlers.ServicesHttp_POST;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class LoginActivity extends AppCompatActivity {
    private TextView notConnectedMessage;
    private IntentFilter filter;
    private LoginViewModel loginViewModel;
    private ReceptorOperacion receiver = new ReceptorOperacion();
    private String URI_LOGIN = "http://so-unlam.net.ar/api/api/login";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.inp_username);
        final EditText passwordEditText = findViewById(R.id.inp_password);
        final Button loginButton = findViewById(R.id.btn_login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final Button newUserButton = findViewById(R.id.btn_new_user);

        notConnectedMessage = findViewById(R.id.text_no_internet);
        notConnectedMessage.setVisibility(View.INVISIBLE);

        setConnectionChecker();
        setBroadcastReciever();

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject obj = new JSONObject();
                try{
                    obj.put("email", usernameEditText.getText());
                    obj.put("password", passwordEditText.getText());
                    obj.put("env", "DEV");
                    obj.put("name", "");
                    obj.put("lastname", "");
                    obj.put("dni", 00000000);
                    obj.put("commission", 0);
                    obj.put("group", 0);
                    obj.put("action", "LOGIN_ACTION");


                    Intent i = new Intent(LoginActivity.this, ServicesHttp_POST.class);
                    i.putExtra("uri", URI_LOGIN);
                    i.putExtra("jsonData", obj.toString());

                    startService(i);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent myIntent = new Intent(getBaseContext(), CreateUserActivity.class);
                    startActivity(myIntent);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void setBroadcastReciever(){
        filter = new IntentFilter ("LOGIN_ACTION");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);
    }


    private void setConnectionChecker(){
        Timer timer = new Timer();
        TimerTask connectionTask = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(connectedToInternet())
                            notConnectedMessage.setVisibility(View.INVISIBLE);
                        else
                            notConnectedMessage.setVisibility(View.VISIBLE);
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(connectionTask,0, 1000);
    }


    private boolean connectedToInternet(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            connected = true;

        return connected;
    }



    public class ReceptorOperacion extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                String result = intent.getStringExtra("result");
                JSONObject dataJSON = new JSONObject(result);


                if(dataJSON.get("state").equals("error")){
                    Toast.makeText(getApplicationContext(),"Surgio un error en la solicitud. Mensaje: " + dataJSON.get("msg"), Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    EventsHandler eventsHandler = new EventsHandler(LoginActivity.this);
                    eventsHandler.RegisterEvent("Login", "El usuario se logueo correctamente");
                    Toast.makeText(getApplicationContext(),"Bienvenido", Toast.LENGTH_LONG).show();
                    Intent myIntent = new Intent(getBaseContext(), SensorTabActivity.class);
                    startActivity(myIntent);
                }
            }
            catch(Exception e){

            }

        }
    }


}
