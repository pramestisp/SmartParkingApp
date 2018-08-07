package id.ac.ugm.smartparking.smartparkingapp.model;

import com.google.gson.annotations.SerializedName;


public class ArrivedCheckSlotResponse {

	@SerializedName("data")
	private Data data;

	public void setData(Data data){
		this.data = data;
	}

	public Data getData(){
		return data;
	}

	@Override
 	public String toString(){
		return 
			"ArrivedCheckSlotResponse{" +
			"data = '" + data + '\'' + 
			"}";
		}

	public class Data{

		@SerializedName("id_slot")
		private int idSlot;

		@SerializedName("id_sensor")
		private String idSensor;

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

		public void setIdSensor(String idSensor){
			this.idSensor = idSensor;
		}

		public String getIdSensor(){
			return idSensor;
		}

		public void setSlotName(String slotName){
			this.slotName = slotName;
		}

		public String getSlotName(){
			return slotName;
		}

		public void setStatus(String status){
			this.status = status;
		}

		public String getStatus(){
			return status;
		}

		@Override
		public String toString(){
			return
					"Data{" +
							"id_slot = '" + idSlot + '\'' +
							",id_sensor = '" + idSensor + '\'' +
							",slot_name = '" + slotName + '\'' +
							",status = '" + status + '\'' +
							"}";
		}
	}
}