package id.ac.ugm.smartparking.smartparkingapp;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlot;
import id.ac.ugm.smartparking.smartparkingapp.utils.Constants;
import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;

/**
 * Created by Shindy on 18-May-18.
 */

class RecyclerViewSlotAdapter extends RecyclerView.Adapter<RecyclerViewSlotAdapter.ViewHolder> {
    private Context context;
    private List<CheckSlot> slotList;
    private LayoutInflater inflater;

    public RecyclerViewSlotAdapter(Context context, List<CheckSlot> slotList) {
        this.inflater = LayoutInflater.from(context);
        this.slotList = slotList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_recyclerview, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout slotLayout;
        TextView tvSlot;
        SmartParkingSharedPreferences pref;
        String slotName;

        public ViewHolder(View view) {
            super(view);
            slotLayout = view.findViewById(R.id.layoutSlot);
            tvSlot = view.findViewById(R.id.tvInfo);
        }

        public void bindData(CheckSlot slot) {
            pref = new SmartParkingSharedPreferences(context);
            slotName = slot.getSlotName();
            if (slotName.equals(pref.getString(SmartParkingSharedPreferences.PREF_SLOT_NAME))) {
                slotLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
                tvSlot.setText(slotName);
            } else {
                slotLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
            }
        }
    }
}
