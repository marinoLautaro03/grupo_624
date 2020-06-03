package com.example.aplicationandroidunlam;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.aplicationandroidunlam.fragments.GiroscopeTabFragment;
import com.example.aplicationandroidunlam.fragments.LuminosityTabFragment;
import com.example.aplicationandroidunlam.listAdapters.LuminosityListAdapter;
import com.example.aplicationandroidunlam.servicesHandlers.EventsHandler;
import com.example.aplicationandroidunlam.ui.login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.aplicationandroidunlam.ui.main.SectionsPagerAdapter;

import org.json.JSONObject;

import java.util.ArrayList;

public class SensorTabActivity extends AppCompatActivity implements LuminosityTabFragment.OnFragmentInteractionListener, GiroscopeTabFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_tab);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());


        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);


        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(SensorTabActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Confirmación");
                builder.setMessage("¿Desea cerrar la sesión?");
                builder.setPositiveButton("Si",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EventsHandler eventsHandler = new EventsHandler(SensorTabActivity.this);
                                eventsHandler.RegisterEvent("Cierre de sesión", "El usuario cerro su sesión");
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
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}