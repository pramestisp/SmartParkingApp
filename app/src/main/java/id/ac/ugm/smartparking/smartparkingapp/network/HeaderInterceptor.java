package id.ac.ugm.smartparking.smartparkingapp.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Shindy on 13-Mar-18.
 */

public class HeaderInterceptor implements Interceptor{
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = new Request.Builder()
                .header("Accept", "application/json").build();
        return chain.proceed(request);
    }
}
