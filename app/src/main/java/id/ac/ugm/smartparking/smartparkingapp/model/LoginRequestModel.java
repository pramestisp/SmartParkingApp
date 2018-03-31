package id.ac.ugm.smartparking.smartparkingapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shindy on 10-Mar-18.
 */

public class LoginRequestModel {
    @SerializedName("email")
    String email;
    @SerializedName("password")
    String password;

    public LoginRequestModel(String email, String password) {
        this.email = email;
        this.password = password;
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
}
