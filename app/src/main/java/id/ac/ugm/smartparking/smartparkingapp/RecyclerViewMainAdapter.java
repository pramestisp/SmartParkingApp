package id.ac.ugm.smartparking.smartparkingapp;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlot;
import id.ac.ugm.smartparking.smartparkingapp.utils.Constants;

/**
 * Created by Shindy on 19-Apr-18.
 */

public class RecyclerViewMainAdapter extends RecyclerView.Adapter<RecyclerViewMainAdapter.ViewHolder>{
    private Context context;
    private List<CheckSlot> slotList;
    private LayoutInflater inflater;

    public RecyclerViewMainAdapter(Context context, List<CheckSlot> slotList) {
        this.inflater = LayoutInflater.from(context);
        this.slotList = slotList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(slotList.get(position));
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout slotLayout;

        ViewHolder(View view) {
            super(view);
            slotLayout = view.findViewById(R.id.layoutSlot);
        }

        private void bindData(CheckSlot slot) {
            if (slot.getStatus().equals(Constants.AVAILABLE)) {
                slotLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
            } else {
                slotLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            }
        }
    }



}
