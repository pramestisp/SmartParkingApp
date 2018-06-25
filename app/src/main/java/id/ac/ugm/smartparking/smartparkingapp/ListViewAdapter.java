package id.ac.ugm.smartparking.smartparkingapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import id.ac.ugm.smartparking.smartparkingapp.model.HistoryResponse;

/**
 * Created by Shindy on 21-May-18.
 */

public class ListViewAdapter extends BaseAdapter {
    private Context context;
    private List<HistoryResponse.DataItem> historyList;

    public ListViewAdapter(Context context, List<HistoryResponse.DataItem> historyList) {
        this.context = context;
        this.historyList = historyList;
    }


    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int position) {
        return historyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tvDate, tvSlot, tvPrice;

        View v = View.inflate(context, R.layout.item_listview, null);
        tvDate = v.findViewById(R.id.tvDate);
        tvSlot = v.findViewById(R.id.tvSlot);
        tvPrice = v.findViewById(R.id.tvPrice);

        HistoryResponse.DataItem data = historyList.get(position);

        Locale localeID = new Locale("in", "ID");
        NumberFormat RpFormat = NumberFormat.getCurrencyInstance(localeID);

        tvDate.setText(data.getTime());
        tvSlot.setText("Slot: " + data.getSlotName());
        tvPrice.setText(RpFormat.format(data.getPrice()));

        return v;
    }

}
