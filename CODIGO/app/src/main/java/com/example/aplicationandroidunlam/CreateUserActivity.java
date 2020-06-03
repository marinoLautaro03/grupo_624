package com.example.aplicationandroidunlam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplicationandroidunlam.servicesHandlers.EventsHandler;
import com.example.aplicationandroidunlam.servicesHandlers.ServicesHttp_POST;
import com.example.aplicationandroidunlam.ui.login.LoginActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CreateUserActivity extends AppCompatActivity {

    private TextView notConnectedMessage;
    private IntentFilter filter;
    private ReceptorOperacion receiver = new ReceptorOperacion();
    private String URI_CREATE = "http://so-unlam.net.ar/api/api/register";
    private Map<String,EditText> fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_create_user);
        }catch (Exception e){
            e.printStackTrace();
        }

        notConnectedMessage = findViewById(R.id.text_no_internet);

        final Button cancelButton = findViewById(R.id.btn_cancel);
        final Button acceptButton = findViewById(R.id.btn_accept);
        fields = new HashMap<String, EditText>();

        fields.put("email", (EditText) findViewById(R.id.txt_email));
        fields.put("password", (EditText) findViewById(R.id.txt_password));
        fields.put("name", (EditText) findViewById(R.id.txt_nombre));
        fields.put("lastname", (EditText) findViewById(R.id.txt_apellido));
        fields.put("dni", (EditText) findViewById(R.id.txt_dni));
        fields.put("comission", (EditText) findViewById(R.id.txt_comision));
        fields.put("group", (EditText) findViewById(R.id.txt_grupoAsignado));
        fields.put("repeticion", (EditText) findViewById(R.id.txt_repetirContraseña));

        setConnectionChecker();
        setBroadcastReciever();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(CreateUserActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Confirmación");
                builder.setMessage("¿Desea cancelar la operación?");
                builder.setPositiveButton("Si",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent myIntent = new Intent(getBaseContext(), LoginActivity.class);
                                startActivity(myIntent);
                            }
                        });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String errors = validateForm();
                if(errors != ""){
                    Toast.makeText(getApplicationContext(), errors, Toast.LENGTH_LONG).show();
                    return;
                }


                Log.i("Mensaje", "Llego al boton");
                JSONObject obj = new JSONObject();
                try{
                    obj.put("env", "DEV");
                    obj.put("email", fields.get("email").getText().toString());
                    obj.put("password", fields.get("password").getText().toString());
                    obj.put("name", fields.get("name").getText().toString());
                    obj.put("lastname", fields.get("lastname").getText().toString());
                    obj.put("dni", Integer.parseInt(fields.get("dni").getText().toString()));
                    obj.put("commission", Integer.parseInt(fields.get("comission").getText().toString()));
                    obj.put("group", Integer.parseInt(fields.get("group").getText().toString()));
                    obj.put("action", "CREATE_USER");


                    Intent i = new Intent(CreateUserActivity.this, ServicesHttp_POST.class);
                    i.putExtra("uri", URI_CREATE);
                    i.putExtra("jsonData", obj.toString());

                    startService(i);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
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

    private void setBroadcastReciever(){
        filter = new IntentFilter("CREATE_USER");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);
    }

    private String validateForm(){


        for (Map.Entry<String, EditText> entry: fields.entrySet()) {
            String value = entry.getValue().getText().toString();
            if(TextUtils.isEmpty(value))
                return "Debe completar el campo " + entry.getKey() + ". ";
        }

        String password = fields.get("password").getText().toString();
        String confirmation = fields.get("repeticion").getText().toString();

        if(password.length() < 8)
            return "La contraseña debe tener un mínimo de 8 caracteres.";

        if(!password.equals(confirmation))
            return "Las contraseñas ingresadas no coinciden.";

        return "";
    }

    public class ReceptorOperacion extends BroadcastReceiver {

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
                    EventsHandler eventsHandler = new EventsHandler(CreateUserActivity.this);
                    eventsHandler.RegisterEvent("Creación de usuario", "El usuario se creo correctamente");
                    Toast.makeText(getApplicationContext(),"Se completo el registro con exito.", Toast.LENGTH_LONG).show();
                    Intent myIntent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(myIntent);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }
    }



}
