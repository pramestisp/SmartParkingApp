package id.ac.ugm.smartparking.smartparkingapp.model;

import com.google.gson.annotations.SerializedName;

public class BalanceResponse {

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
			"BalanceResponse{" +
			"data = '" + data + '\'' + 
			"}";
		}

	public class Data{

		@SerializedName("balance")
		private float balance;

		public void setBalance(float balance){
			this.balance = balance;
		}

		public float getBalance(){
			return balance;
		}

		@Override
		public String toString(){
			return
					"Data{" +
							"balance = '" + balance + '\'' +
							"}";
		}
	}
}
