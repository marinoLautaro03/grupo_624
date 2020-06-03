package com.example.aplicationandroidunlam.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.aplicationandroidunlam.R;
import com.example.aplicationandroidunlam.listAdapters.GiroscopeListAdapter;
import com.example.aplicationandroidunlam.listAdapters.LuminosityListAdapter;
import com.example.aplicationandroidunlam.servicesHandlers.EventsHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.SENSOR_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GiroscopeTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GiroscopeTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GiroscopeTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public ArrayList<JSONObject> dataGiroscope;
    public RecyclerView giroscopeyRecycler;
    public SensorManager sensorManager;
    private Sensor giroscopeSensor;
    private SensorEventListener giroscopeEventListener;
    private float maxValue;
    private JSONObject lastValueRead;
    private Handler handler;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GiroscopeTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GiroscopeTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GiroscopeTabFragment newInstance(String param1, String param2) {
        GiroscopeTabFragment fragment = new GiroscopeTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_giroscope_tab, container, false);
        Button btnEvent = root.findViewById(R.id.btn_giroscope_event);

        handler = new Handler();

        btnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String newValue = "X: " + lastValueRead.getString("x") +
                            " Y: " + lastValueRead.getString("y") +
                            " Z: " + lastValueRead.getString("z");

                    EventsHandler eventsHandler = new EventsHandler(getContext());
                    eventsHandler.RegisterEvent("Registro de sensor - Giroscopio", "El último valor obtenido fue: " + newValue);
                    Toast.makeText(getContext(),"Evento registrado con exito", Toast.LENGTH_LONG).show();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        giroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (giroscopeSensor == null){
            Toast.makeText(getContext(), "Este dispositivo no tiene giroscopio", Toast.LENGTH_SHORT).show();
        }
        else{
            giroscopeyRecycler = (RecyclerView) root.findViewById(R.id.recicler_Giroscope);
            giroscopeyRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

            dataGiroscope = new ArrayList<JSONObject>();
            maxValue = giroscopeSensor.getMaximumRange();

            giroscopeEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    lastValueRead = new JSONObject();
                    try {
                        lastValueRead.put("x", sensorEvent.values[0]);
                        lastValueRead.put("y", sensorEvent.values[1]);
                        lastValueRead.put("z", sensorEvent.values[2]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };

            sensorManager.registerListener(giroscopeEventListener, giroscopeSensor,sensorManager.SENSOR_DELAY_FASTEST);

        }
        return root;
    }


    private void startValueHandler(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(this, 5000);
                try{
                    if(dataGiroscope.size() != 0 && SameValueAsBefore())
                        return;

                    if(dataGiroscope.size() > 6)
                        dataGiroscope = new ArrayList<JSONObject>();

                    String newValue = "X: " + lastValueRead.getString("x") +
                            " Y: " + lastValueRead.getString("y") +
                            " Z: " + lastValueRead.getString("z");

                    dataGiroscope.add(new JSONObject().put("value", newValue));
                    GiroscopeListAdapter adapterGiroscope = new GiroscopeListAdapter(dataGiroscope);
                    giroscopeyRecycler.setAdapter(adapterGiroscope);

                    EventsHandler eventsHandler = new EventsHandler(getContext());
                    eventsHandler.RegisterEvent("Proceso background - Sensor", "Se acaba de detectar un cambio en el giroscopio del dispositivo");
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, 5000);
    }

    private void stopValueHandler(){
        handler.removeCallbacksAndMessages(null);
    }

    public boolean SameValueAsBefore(){

        try {

            String newValue = "X: " + lastValueRead.getString("x") +
                    " Y: " + lastValueRead.getString("y") +
                    " Z: " + lastValueRead.getString("z");

            String previousValue = dataGiroscope.get(dataGiroscope.size() - 1).getString("value");

            if(newValue.equals(previousValue))
                return true;

            return false;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        //stopValueHandler();
        sensorManager.unregisterListener(giroscopeEventListener);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        startValueHandler();
        sensorManager.registerListener(giroscopeEventListener, giroscopeSensor,sensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onPause() {
        super.onPause();
        //stopValueHandler();
        sensorManager.unregisterListener(giroscopeEventListener);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
