package id.ac.ugm.smartparking.smartparkingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlot;
import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlotResponse;
import id.ac.ugm.smartparking.smartparkingapp.network.Network;
import id.ac.ugm.smartparking.smartparkingapp.utils.Constants;

/**
 * Created by Shindy on 19-Apr-18.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private Context context;
    private List<CheckSlot> slotList;
    private LayoutInflater mInflater;

    public RecyclerViewAdapter(Context context, List<CheckSlot> slotList) {
        this.mInflater = LayoutInflater.from(context);
        this.slotList = slotList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CheckSlot slot = slotList.get(position);
        if(slot.getStatus().equals(Constants.AVAILABLE)) {
            holder.slotLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.slotLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout slotLayout;

        public ViewHolder(View view) {
            super(view);
            slotLayout = view.findViewById(R.id.layoutSlot);
        }
    }



}
