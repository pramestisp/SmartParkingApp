package id.ac.ugm.smartparking.smartparkingapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shindy on 15-Apr-18.
 */

public class CheckSlot {

    @SerializedName("id_slot")
    private int idSlot;

    @SerializedName("slot_name")
    private String slotName;

    @SerializedName("status")
    private String status;

    public void setIdSlot(int idSlot){
        this.idSlot = idSlot;
    }

    public int getIdSlot(){
        return idSlot;
    }

    public void setSlotName(String slotName){
        this.slotName = slotName;
    }

    public String getSlotName(){
        return slotName;
    }

    public void setStatus(String status) { this.status = status; }

    public String getStatus() { return status; }


    @Override
    public String toString(){
        return
                "CheckSlot{" +
                        "id_slot = '" + idSlot + '\'' +
                        ",slot_name = '" + slotName + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}
