package id.ac.ugm.smartparking.smartparkingapp.network;

import android.content.Context;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import id.ac.ugm.smartparking.smartparkingapp.model.ArrivedCheckSlotResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.BalanceResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlotResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.GetAllSlotsResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.HistoryResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.LoginRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.LoginResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.RegisterRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationResponse;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Shindy on 10-Mar-18.
 */

public class Network {
    public static final String BASE_URL_API = "http://smartagriculture.wg.ugm.ac.id/api/";
    private NetworkService service;

    public Network(Context context) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        AuthInterceptor authInterceptor = new AuthInterceptor(context);
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);
        builder.addInterceptor(authInterceptor);
        OkHttpClient client = builder
                .connectTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .build();

        Gson gson = new Gson();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_API)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        service = retrofit.create(NetworkService.class);
    }

    public NetworkService getService() {
        return service;
    }

    public interface MyCallback<T> {
        void onSuccess(T response);
        void onError(String error);

    }

    public void Register (final RegisterRequestModel request, final MyCallback<String> callback) {
        service.register(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    callback.onSuccess("Register success");
                }

                else {
                    callback.onError("Register failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                callback.onError(t.getLocalizedMessage());
            }
        });
    }

    public void Login (final LoginRequestModel request, final MyCallback<LoginResponse> callback) {
        service.login(request).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()) {
                    callback.onSuccess(response.body());
                }
                else {
                    if (response.code() == 422) {
                        callback.onError("Please verify your email first");
                    }
                    callback.onError("Login failed");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
                callback.onError(t.getLocalizedMessage());
            }

        });
    }

    public void getSlot(final String hour, final MyCallback<CheckSlotResponse> callback) {
        service.getSlot(hour).enqueue(new Callback<CheckSlotResponse>() {
            @Override
            public void onResponse(Call<CheckSlotResponse> call, Response<CheckSlotResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    if (response.code() == 404) {
                        callback.onError("Full booked");
                    }
                    callback.onError("Please choose another time");
                }
            }

            @Override
            public void onFailure(Call<CheckSlotResponse> call, Throwable t) {
                t.printStackTrace();
                callback.onError(t.getLocalizedMessage());
            }
        });
    }

    public void Reservation(final ReservationRequestModel request, final MyCallback<ReservationResponse> callback) {
        service.reservation(request).enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(Call<ReservationResponse> call, Response<ReservationResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    if(response.code() == 401) {
                        callback.onError("Token expired");
                    } else if (response.code() == 402) {
                        callback.onError("Insufficient balance");
                    } else {
                        callback.onError("Reservation failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<ReservationResponse> call, Throwable t) {
                t.printStackTrace();
                callback.onError(t.getLocalizedMessage());
            }
        });
    }

    public void getAllSlot(final MyCallback<GetAllSlotsResponse> callback) {
        service.getAllSlot().enqueue(new Callback<GetAllSlotsResponse>() {
            @Override
            public void onResponse(Call<GetAllSlotsResponse> call, Response<GetAllSlotsResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Get Car Park Slot Error");
                }
            }

            @Override
            public void onFailure(Call<GetAllSlotsResponse> call, Throwable t) {
                callback.onError(t.getLocalizedMessage());
            }
        });
    }

    public void getHistory(final int id_user, final MyCallback<HistoryResponse> callback) {
        service.getHistory(id_user).enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Get History Error");
                }
            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {
                callback.onError(t.getLocalizedMessage());
            }
        });
    }


    public void cancel(final int id_reservation, final MyCallback<ResponseBody> callback) {
        service.cancel(id_reservation).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    callback.onSuccess(response.body());
                }
                else callback.onError("Cancel booking failed");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError(t.getLocalizedMessage());
            }
        });
    }

    public void getBalance(final int id_user, final MyCallback<BalanceResponse> callback) {
        service.getBalance(id_user).enqueue(new Callback<BalanceResponse>() {
            @Override
            public void onResponse(Call<BalanceResponse> call, Response<BalanceResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Get balance error");
                }
            }

            @Override
            public void onFailure(Call<BalanceResponse> call, Throwable t) {
                callback.onError(t.getLocalizedMessage());
            }
        });
    }

    public void penaltyCharge(final int id_user, final MyCallback<ResponseBody> callback) {
        service.penaltyCharge(id_user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Update charge error");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError(t.getLocalizedMessage());
            }
        });
    }

    public void addCharge(final int id_reservation, final ReservationRequestModel request, final MyCallback<ReservationResponse> callback) {
        service.addCharge(id_reservation, request).enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(Call<ReservationResponse> call, Response<ReservationResponse> response) {
                if(response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Add charge error");
                }
            }

            @Override
            public void onFailure(Call<ReservationResponse> call, Throwable t) {
                callback.onError(t.getLocalizedMessage());

            }
        });
    }

    public void arrivedCheckSlot(final int id_reservation, final MyCallback<ArrivedCheckSlotResponse> callback) {
        service.arrivedCheck(id_reservation).enqueue(new Callback<ArrivedCheckSlotResponse>() {
            @Override
            public void onResponse(Call<ArrivedCheckSlotResponse> call, Response<ArrivedCheckSlotResponse> response) {
                if(response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Check slot error");
                }
            }

            @Override
            public void onFailure(Call<ArrivedCheckSlotResponse> call, Throwable t) {
                callback.onError(t.getLocalizedMessage());
            }
        });
    }

    public void changeSlot(final int id_reservation, final ReservationRequestModel request, final MyCallback<ReservationResponse> callback) {
        service.changeSlot(id_reservation, request).enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(Call<ReservationResponse> call, Response<ReservationResponse> response) {
                if(response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else if (response.code() == 402) {
                    callback.onError("Insufficient balance");
                } else {
                    callback.onError("Change slot failed");
                }
            }

            @Override
            public void onFailure(Call<ReservationResponse> call, Throwable t) {
                callback.onError(t.getLocalizedMessage());
            }
        });
    }

    public void resExpired(final int id_reservation, final MyCallback<ResponseBody> callback) {
        service.resExpired(id_reservation).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    callback.onSuccess(response.body());
                }
                else {
                    callback.onError("Cancelling reservation failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError(t.getLocalizedMessage());
            }
        });
    }


}
