package id.ac.ugm.smartparking.smartparkingapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shindy on 06-Apr-18.
 */

public class ReservationRequestModel {
    @SerializedName("id_slot")
    int idSlot;
    @SerializedName("arrive_time")
    String df_string_from;
    @SerializedName("leaving_time")
    String df_string_to;
    @SerializedName("price")
    float price;

    public ReservationRequestModel(int idSlot, String df_string_from, String df_string_to, float price) {
        this.idSlot = idSlot;
        this.df_string_from = df_string_from;
        this.df_string_to = df_string_to;
        this.price = price;
    }

    public ReservationRequestModel(int idSlot) {
        this.idSlot = idSlot;
    }


    public int getIdSlot() {
        return idSlot;
    }

    public void setIdSlot(int idSlot) {
        this.idSlot = idSlot;
    }

    public String getDf_string_from() {
        return df_string_from;
    }

    public void setDf_string_from(String df_string_from) {
        this.df_string_from = df_string_from;
    }

    public String getDf_string_to() {
        return df_string_to;
    }

    public void setDf_string_to(String df_string_to) {
        this.df_string_to = df_string_to;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}

