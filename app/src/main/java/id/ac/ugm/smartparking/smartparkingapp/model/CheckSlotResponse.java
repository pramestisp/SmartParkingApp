package id.ac.ugm.smartparking.smartparkingapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shindy on 09-May-18.
 */

public class CheckSlotResponse {

    @SerializedName("data")
    public CheckSlot data;

    public void setData(CheckSlot data){
        this.data = data;
    }

    public CheckSlot getData(){
        return data;
    }

    @Override
    public String toString(){
        return
                "CheckSlotResponse{" +
                        "data = '" + data + '\'' +
                        "}";
    }
}


