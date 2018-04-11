package id.ac.ugm.smartparking.smartparkingapp.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

	@SerializedName("data")
	private Data data;

	@SerializedName("meta")
	private Meta meta;

	public void setData(Data data){
		this.data = data;
	}

	public Data getData(){
		return data;
	}

	public void setMeta(Meta meta){
		this.meta = meta;
	}

	public Meta getMeta(){
		return meta;
	}

	@Override
 	public String toString(){
		return 
			"LoginResponse{" +
			"data = '" + data + '\'' + 
			",meta = '" + meta + '\'' + 
			"}";
		}

	public class Data{

		@SerializedName("name")
		private String name;

		@SerializedName("activate")
		private String activate;

		@SerializedName("id user")
		private int idUser;

		@SerializedName("email")
		private String email;

		@SerializedName("car_type")
		private String carType;

		@SerializedName("license_plate_number")
		private String licensePlateNumber;

		public void setName(String name){
			this.name = name;
		}

		public String getName(){
			return name;
		}

		public void setActivate(String activate){
			this.activate = activate;
		}

		public String getActivate(){
			return activate;
		}

		public void setIdUser(int idUser){
			this.idUser = idUser;
		}

		public int getIdUser(){
			return idUser;
		}

		public void setEmail(String email){
			this.email = email;
		}

		public String getEmail(){
			return email;
		}

		public void setCarType(String carType){
			this.carType = carType;
		}

		public String getCarType(){
			return carType;
		}

		public void setLicensePlateNumber(String licensePlateNumber){
			this.licensePlateNumber = licensePlateNumber;
		}

		public String getLicensePlateNumber(){
			return licensePlateNumber;
		}

		@Override
		public String toString(){
			return
					"Data{" +
							"name = '" + name + '\'' +
							",activate = '" + activate + '\'' +
							",id user = '" + idUser + '\'' +
							",email = '" + email + '\'' +
							",car_type = '" + carType + '\'' +
							",license_plate_number = '" + licensePlateNumber + '\'' +
							"}";
		}
	}

	public class Meta{

		@SerializedName("token")
		private String token;

		public void setToken(String token){
			this.token = token;
		}

		public String getToken(){
			return token;
		}

		@Override
		public String toString(){
			return
					"Meta{" +
							"token = '" + token + '\'' +
							"}";
		}
	}


}