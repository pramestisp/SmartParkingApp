package id.ac.ugm.smartparking.smartparkingapp.network;

import id.ac.ugm.smartparking.smartparkingapp.apihelper.LoginRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.apihelper.RegisterRequestModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Shindy on 10-Mar-18.
 */

public interface NetworkService {

    @Headers("Accept: application/json")
    @POST("auth/user/register")
    Call<ResponseBody> register(@Body RegisterRequestModel request);

    @Headers("Accept: application/json")
    @POST("auth/login")
    Call<ResponseBody> login(@Body LoginRequestModel request);

}
