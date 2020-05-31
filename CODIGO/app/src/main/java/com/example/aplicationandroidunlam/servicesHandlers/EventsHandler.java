package com.example.aplicationandroidunlam.servicesHandlers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class EventsHandler  {

    public Context context;

    public EventsHandler(Context context){
        this.context = context;
    }
    public void RegisterEvent(String type, String description){
        JSONObject obj = new JSONObject();
        try{
            obj.put("env", "DEV");
            obj.put("type_events", type);
            obj.put("state", "ACTIVO");
            obj.put("description", description);
            obj.put("action", "EVENT_REGISTER_ACTION");
            Intent i = new Intent(context, ServicesHttp_POST.class);
            i.putExtra("uri", "http://so-unlam.net.ar/api/api/event");
            i.putExtra("jsonData", obj.toString());

            context.startService(i);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
