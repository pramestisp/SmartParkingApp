package id.ac.ugm.smartparking.smartparkingapp.network;

import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlotResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.GetAllSlotsResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.LoginRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.LoginResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.RegisterRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Shindy on 10-Mar-18.
 */

public interface NetworkService {

    @Headers("Accept: application/json")
    @POST("auth/user/register")
    Call<ResponseBody> register(@Body RegisterRequestModel request);

    @Headers("Accept: application/json")
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequestModel request);

    @GET("carparkslot/{hour}")
    Call<CheckSlotResponse> getSlot(@Path("hour") String hour);

    @Headers("Accept: application/json")
    @POST("addreservation")
    Call<ReservationResponse> reservation(@Body ReservationRequestModel request);

    @GET("carparkslot")
    Call<GetAllSlotsResponse> getAllSlot();

}
