package com.example.aplicationandroidunlam.fragments;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.Toast;

import com.example.aplicationandroidunlam.R;
import com.example.aplicationandroidunlam.listAdapters.LuminosityListAdapter;
import com.example.aplicationandroidunlam.servicesHandlers.EventsHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.SENSOR_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LuminosityTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LuminosityTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LuminosityTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ArrayList<JSONObject> dataLuminosity;
    public RecyclerView luminosityRecycler;
    public SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEventListener;
    private float maxValue;
    private int lastValueRead;


    public LuminosityTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LuminosityTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LuminosityTabFragment newInstance(String param1, String param2) {
        LuminosityTabFragment fragment = new LuminosityTabFragment();
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
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_luminosity_tab, container, false);


        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null){
            Toast.makeText(getContext(), "Este dispositivo no tiene sensor de luminosidad", Toast.LENGTH_SHORT).show();
        }
        else{
            luminosityRecycler = (RecyclerView) root.findViewById(R.id.recicler_luminosity);
            luminosityRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

            dataLuminosity = new ArrayList<JSONObject>();
            maxValue = lightSensor.getMaximumRange();

            lightEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    float value = sensorEvent.values[0];
                    lastValueRead = (int)value;
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };

            sensorManager.registerListener(lightEventListener, lightSensor,sensorManager.SENSOR_DELAY_FASTEST);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(this, 5000);
                    try{
                        if(dataLuminosity.size() != 0 && lastValueRead == Integer.parseInt(dataLuminosity.get(dataLuminosity.size() - 1).getString("value")))
                            return;

                        if(dataLuminosity.size() > 8){
                            dataLuminosity = new ArrayList<JSONObject>();
                        }

                        dataLuminosity.add(new JSONObject().put("value", lastValueRead));
                        LuminosityListAdapter adapterLuminosity = new LuminosityListAdapter(dataLuminosity);
                        luminosityRecycler.setAdapter(adapterLuminosity);

                        EventsHandler eventsHandler = new EventsHandler(getContext());
                        eventsHandler.RegisterEvent("Proceso background - Sensor", "Se acaba de detectar un cambio en el nivel de brillo del dispositivo");
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }, 5000);
        }
        return root;
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
