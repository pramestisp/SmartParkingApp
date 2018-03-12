package id.ac.ugm.smartparking.smartparkingapp.apihelper;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shindy on 10-Mar-18.
 */

public class RegisterRequestModel {
    @SerializedName("name")
    String name;
    @SerializedName("email")
    String email;
    @SerializedName("password")
    String password;
    @SerializedName("car_type")
    String car_type;
    @SerializedName("license_plate_number")
    String license_no;

    public RegisterRequestModel(String name, String email, String password, String car_type, String license_no) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.car_type = car_type;
        this.license_no = license_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCar_type() {
        return car_type;
    }

    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }

    public String getLicense_no() {
        return license_no;
    }

    public void setLicense_no(String license_no) {
        this.license_no = license_no;
    }
}
