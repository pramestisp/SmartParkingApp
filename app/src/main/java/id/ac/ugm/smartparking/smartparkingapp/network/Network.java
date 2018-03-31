package id.ac.ugm.smartparking.smartparkingapp.network;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import id.ac.ugm.smartparking.smartparkingapp.model.LoginRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.RegisterRequestModel;
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
    public static final String BASE_URL_API = "http://10.72.42.229:8000/api/";
    private NetworkService service;

    public Network() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //HeaderInterceptor headerInterceptor = new HeaderInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);
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

    public void Login (final LoginRequestModel request, final MyCallback<String> callback) {
        service.login(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    callback.onSuccess("Login success");
                }

                else {
                    callback.onError("Login failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                callback.onError(t.getLocalizedMessage());
            }
        });
    }


}


