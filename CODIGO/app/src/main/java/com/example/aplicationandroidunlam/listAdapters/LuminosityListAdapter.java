package com.example.aplicationandroidunlam.listAdapters;

import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicationandroidunlam.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class LuminosityListAdapter extends RecyclerView.Adapter<LuminosityListAdapter.ViewHolderData> {

    ArrayList<JSONObject> data;

    public LuminosityListAdapter(ArrayList<JSONObject> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.luminosity_item_list,null, false);
        return new ViewHolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderData holder, int position) {
        holder.assignData(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolderData extends RecyclerView.ViewHolder {

        TextView dato;

        public ViewHolderData(@NonNull View itemView) {
            super(itemView);
            dato = (TextView) itemView.findViewById(R.id.idDatoLuminosity);
        }

        public void assignData(JSONObject object) {
            String dataSensor = null;
            try {
                dataSensor = object.getString("value");

                Calendar calendar = Calendar.getInstance();

                int minutes = calendar.get(Calendar.MINUTE);
                int hour = calendar.get(Calendar.HOUR);

                dato.setText("Valor de luminosidad: " + dataSensor + "  ------------ " + hour + ":" + minutes);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
