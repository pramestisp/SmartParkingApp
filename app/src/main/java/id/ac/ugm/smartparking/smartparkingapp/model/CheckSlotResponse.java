package id.ac.ugm.smartparking.smartparkingapp.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CheckSlotResponse{

	@SerializedName("data")
	public List<CheckSlot> data;

	public void setData(List<CheckSlot> data){
		this.data = data;
	}

	public List<CheckSlot> getData(){
		return data;
	}

	@Override
 	public String toString(){
		return 
			"CheckSlotResponse{" + 
			"data = '" + data + '\'' + 
			"}";
		}

	public class CheckSlot {

		@SerializedName("id_slot")
		private int idSlot;

		@SerializedName("slot_name")
		private String slotName;

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

		@Override
		public String toString(){
			return
					"CheckSlot{" +
							"id_slot = '" + idSlot + '\'' +
							",slot_name = '" + slotName + '\'' +
							"}";
		}
	}
}