package id.ac.ugm.smartparking.smartparkingapp.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Shindy on 11-Apr-18.
 */

public class AuthInterceptor implements Interceptor {

    private String token;


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + token).build();
        return chain.proceed(request);
    }
}
