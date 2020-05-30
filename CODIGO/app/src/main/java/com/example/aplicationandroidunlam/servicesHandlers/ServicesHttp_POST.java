package com.example.aplicationandroidunlam.servicesHandlers;

import android.app.IntentService;
import android.content.Intent;
import android.util.JsonReader;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class ServicesHttp_POST extends IntentService {


    private Exception mException = null;

    private HttpURLConnection httpURLConnection;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ServicesHttp_POST() {
        super("ServicesHttp_POST");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try{
            Log.d("Mensaje", "Llego al handler");
            String uri = intent.getExtras().getString("uri");
            JSONObject jsonData = new JSONObject(intent.getExtras().getString("jsonData"));

            executePost(uri, jsonData);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void executePost(String uri, JSONObject data){

        String result = POST(uri, data);

        try{
            Intent i = new Intent((String)data.get("action"));
            i.putExtra("result", result);
            sendBroadcast(i);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String POST(String uri, JSONObject data){

        HttpURLConnection urlConnection = null;

        String result = "";

        try{
            URL mUrl = new URL(uri);
            urlConnection = (HttpURLConnection) mUrl.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("POST");

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

            wr.write(data.toString().getBytes("UTF-8"));

            wr.flush();
            wr.close();

            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            InputStream output;

            if((responseCode == HttpURLConnection.HTTP_OK) || (responseCode == httpURLConnection.HTTP_CREATED)){
                output = urlConnection.getInputStream();
            }
            else
                output = urlConnection.getErrorStream();


            BufferedReader in = new BufferedReader(new InputStreamReader(output));

            StringBuilder response = new StringBuilder();
            String currentLine;

            while ((currentLine = in.readLine()) != null)
                response.append(currentLine);

            mException = null;

            return response.toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }
}
