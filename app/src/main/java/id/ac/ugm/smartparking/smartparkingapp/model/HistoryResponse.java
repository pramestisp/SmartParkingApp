package id.ac.ugm.smartparking.smartparkingapp.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class HistoryResponse {

	@SerializedName("data")
	private List<DataItem> data;

	public void setData(List<DataItem> data){
		this.data = data;
	}

	public List<DataItem> getData(){
		return data;
	}

	@Override
 	public String toString(){
		return 
			"HistoryResponse{" +
			"data = '" + data + '\'' + 
			"}";
		}

	public class DataItem{

		@SerializedName("slot name")
		private String slotName;

		@SerializedName("price")
		private String price;

		@SerializedName("time")
		private String time;

		public void setSlotName(String slotName){
			this.slotName = slotName;
		}

		public String getSlotName(){
			return slotName;
		}

		public void setPrice(String price){
			this.price = price;
		}

		public String getPrice(){
			return price;
		}

		public void setTime(String time){
			this.time = time;
		}

		public String getTime(){
			return time;
		}

		@Override
		public String toString(){
			return
					"DataItem{" +
							"slot name = '" + slotName + '\'' +
							",price = '" + price + '\'' +
							",time = '" + time + '\'' +
							"}";
		}
	}
}