package id.ac.ugm.smartparking.smartparkingapp.apihelper;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Shindy on 25-Dec-17.
 */

public interface BaseApiService {

    //call login api
    @FormUrlEncoded
    @POST("login.php")
    Call<ResponseBody> loginRequest(@Field("email") String email,
                                    @Field("password") String password);

    //call register api
    @FormUrlEncoded
    @POST("register.php")
    Call<ResponseBody> registerRequest(@Field("name") String name,
                                       @Field("email") String email,
                                       @Field("car_type") String car_type,
                                       @Field("license_no") String license_no,
                                       @Field("password") String password);
}
