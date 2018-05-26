package id.ac.ugm.smartparking.smartparkingapp.model;

/**
 * Created by Shindy on 25-May-18.
 */

import com.google.gson.annotations.SerializedName;


public class CheckSlotStatusResponse {

    @SerializedName("status")
    private String status;

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

    @Override
    public String toString(){
        return
                "CheckSlotStatusResponse{" +
                        "status = '" + status + '\'' +
                        "}";
    }
}