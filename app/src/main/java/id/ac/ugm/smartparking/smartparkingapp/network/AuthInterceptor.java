package id.ac.ugm.smartparking.smartparkingapp.network;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;

import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Shindy on 11-Apr-18.
 */

public class AuthInterceptor implements Interceptor {

    private SmartParkingSharedPreferences prefManager;

    AuthInterceptor(Context context) {
        prefManager = new SmartParkingSharedPreferences(context);
    }

    public Response intercept(@NonNull Chain chain) throws IOException {
        String token = prefManager.getString(SmartParkingSharedPreferences.PREF_TOKEN);
        Request request = chain.request().newBuilder()
                .header("Authorization", "Bearer " + token).build();
        return chain.proceed(request);
    }
}
