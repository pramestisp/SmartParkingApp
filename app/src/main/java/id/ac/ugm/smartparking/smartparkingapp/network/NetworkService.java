package id.ac.ugm.smartparking.smartparkingapp.network;

import id.ac.ugm.smartparking.smartparkingapp.model.BalanceResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlotResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlotStatusResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.GetAllSlotsResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.HistoryResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.LoginRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.LoginResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.RegisterRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationNewRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Shindy on 10-Mar-18.
 */

public interface NetworkService {

    @Headers("Accept: application/json")
    @POST("auth/user/register-user")
    Call<ResponseBody> register(@Body RegisterRequestModel request);

    @Headers("Accept: application/json")
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequestModel request);

    @GET("carparkslot/{hour}")
    Call<CheckSlotResponse> getSlot(@Path("hour") String hour);

    @Headers("Accept: application/json")
    @POST("reservation/add")
    Call<ReservationResponse> reservation(@Body ReservationRequestModel request);

    @Headers("Accept: application/json")
    @GET("carparkslot")
    Call<GetAllSlotsResponse> getAllSlot();

    @GET("history/{id_user}")
    Call<HistoryResponse> getHistory(@Path("id_user") int id_user);

    @GET("carparkslot/{id_reservation}")
    Call<CheckSlotStatusResponse> getSlotStatus(@Path("id_reservation") int id_reservation);

    @Headers("Accept: application/json")
    @DELETE("reservation/cancel/{id_reservation}")
    Call<ResponseBody> cancel(@Path("id_reservation") int id_reservation);

    @GET("balance/{id_user}")
    Call<BalanceResponse> getBalance(@Path("id_user") int id_user);

    @Headers("Accept: application/json")
    @GET("balance/updatecharge/{id_user}")
    Call<ResponseBody> balanceCharged(@Path("id_user") int id_user);

}
