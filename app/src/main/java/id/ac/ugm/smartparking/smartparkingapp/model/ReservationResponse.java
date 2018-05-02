package id.ac.ugm.smartparking.smartparkingapp.model;

import com.google.gson.annotations.SerializedName;

public class ReservationResponse {

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
			"ReservationResponse{" +
			"data = '" + data + '\'' + 
			"}";
		}

	public class Data{

		@SerializedName("leaving_time")
		private String leavingTime;

		@SerializedName("id_slot")
		private int idSlot;

		@SerializedName("id_user_park")
		private int idUserPark;

		@SerializedName("price")
		private int price;

		@SerializedName("arrive_time")
		private String arriveTime;

		public void setLeavingTime(String leavingTime){
			this.leavingTime = leavingTime;
		}

		public String getLeavingTime(){
			return leavingTime;
		}

		public void setIdSlot(int idSlot){
			this.idSlot = idSlot;
		}

		public int getIdSlot(){
			return idSlot;
		}

		public void setIdUserPark(int idUserPark){
			this.idUserPark = idUserPark;
		}

		public int getIdUserPark(){
			return idUserPark;
		}

		public void setPrice(int price){
			this.price = price;
		}

		public int getPrice(){
			return price;
		}

		public void setArriveTime(String arriveTime){
			this.arriveTime = arriveTime;
		}

		public String getArriveTime(){
			return arriveTime;
		}

		@Override
		public String toString(){
			return
					"Data{" +
							"leaving_time = '" + leavingTime + '\'' +
							",id_slot = '" + idSlot + '\'' +
							",id_user_park = '" + idUserPark + '\'' +
							",price = '" + price + '\'' +
							",arrive_time = '" + arriveTime + '\'' +
							"}";
		}
	}
}

