package com.example.aplicationandroidunlam.listAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicationandroidunlam.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GiroscopeListAdapter extends RecyclerView.Adapter<GiroscopeListAdapter.ViewHolderData> {

    ArrayList<JSONObject> data;

    public GiroscopeListAdapter(ArrayList<JSONObject> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.giroscope_item_list,null, false);
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
            dato = (TextView) itemView.findViewById(R.id.idDatoGiroscope);
        }

        public void assignData(JSONObject object) {
            String dataSensor = null;
            try {
                dataSensor = object.getString("value");

                dato.setText(dataSensor);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
