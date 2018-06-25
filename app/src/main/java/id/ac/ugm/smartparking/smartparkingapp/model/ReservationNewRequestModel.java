package id.ac.ugm.smartparking.smartparkingapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shindy on 30-May-18.
 */

public class ReservationNewRequestModel {
    @SerializedName("id_slot")
    int idSlot;

    public ReservationNewRequestModel(int idSlot) {
        this.idSlot = idSlot;
    }

    public int getIdSlot() {
        return idSlot;
    }

    public void setIdSlot(int idSlot) {
        this.idSlot = idSlot;
    }
}
