package id.ac.ugm.smartparking.smartparkingapp.apihelper;

/**
 * Created by Shindy on 25-Dec-17.
 */

public class UtilsApi {
    public static final String BASE_URL_API = "http://10.72.47.26:80/api/auth/";

    //declare BaseApiService interface
    public static BaseApiService getAPIService() {
        return RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }

}
