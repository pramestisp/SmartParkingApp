package id.ac.ugm.smartparking.smartparkingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import id.ac.ugm.smartparking.smartparkingapp.apihelper.BaseApiService;
import id.ac.ugm.smartparking.smartparkingapp.apihelper.UtilsApi;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterLoginActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputName;
    private EditText inputCarType;
    private EditText inputLicenseNo;
    private TextInputLayout inputNameContainer;
    private TextInputLayout inputLicenseNoContainer;
    private TextInputLayout inputCarTypeContainer;
    private Button loginButton;
    private TextView toggleButton;
//    private Spinner selectCarBrand;
//    private Spinner selectCarModel;

    //    private ImageView logo;
//    private TextView textLogo;
    private ProgressBar progressBar;

    private View.OnClickListener mContext;
    private BaseApiService mApiService;
    private ProgressDialog loading;

    private boolean isLogin = true;
    private String name, email, car_type, license_no, password = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputEmail = (EditText) findViewById(R.id.etLoginEmail);
        inputPassword = (EditText) findViewById(R.id.etLoginPassword);
        inputName = (EditText) findViewById(R.id.etLoginName);
        inputCarType = (EditText) findViewById(R.id.etLoginCarModel);
        inputCarTypeContainer = (TextInputLayout) findViewById(R.id.carModelWrapper);
        inputNameContainer = (TextInputLayout) findViewById(R.id.nameWrapper);
        inputLicenseNoContainer = (TextInputLayout) findViewById(R.id.licenseNoWrapper);
        loginButton = (Button) findViewById(R.id.bLogin);
        toggleButton = (TextView) findViewById(R.id.tvRegister);
        //logo = findViewById(R.id.login_logo);
        //textLogo = findViewById(R.id.login_text_logo);
        progressBar = (ProgressBar) findViewById(R.id.pbLogin);


        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleRegister();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin) {
                    //login with email
                    if (!inputEmail.getText().toString().isEmpty() && !inputPassword.getText().toString().isEmpty()) {
                        mContext = this;
                        mApiService = UtilsApi.getAPIService();

                        loading = ProgressDialog.show((Context) mContext, null, "Please wait", true, false);
                        requestLogin();
//                        toggleProgressBar();
//                        String email = inputEmail.getText().toString();
//                        String password = inputPassword.getText().toString();
//                        Toast.makeText(RegisterLoginActivity.this,
//                                "Login successful",
//                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(RegisterLoginActivity.this,
                                "Please check again",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //register with email
                    mContext = this;
                    mApiService = UtilsApi.getAPIService();

                    loading = ProgressDialog.show(RegisterLoginActivity.this, null, "Please wait", true, false);
                    requestRegister();
//                    try {
//                        //GetReg();
//                    }
//                    catch (Exception ex) {
//                        Toast.makeText(RegisterLoginActivity.this,
//                                "URL Exception",
//                                Toast.LENGTH_SHORT).show();
//                    }

//                    if (!inputEmail.getText().toString().isEmpty() && !inputPassword.getText().toString().isEmpty()) {
//                        submitRegisterForm();
////                        final String email = inputEmail.getText().toString();
////                        String password = inputPassword.getText().toString();
////                        if (!inputName.getText().toString().isEmpty()) {
////                            name = inputName.getText().toString();
////                        }
////                        if (!inputPhone.getText().toString().isEmpty()) {
////                            phone = inputPhone.getText().toString();
////                        }
//                        //toggleProgressBar();
//                        Toast.makeText(RegisterLoginActivity.this,
//                                "Please check again",
//                                Toast.LENGTH_SHORT).show();
//                    }
                }
            }

        });

    }

    private void requestLogin() {
        mApiService.loginRequest(inputEmail.getText().toString(),
                inputPassword.getText().toString()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    loading.dismiss();
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("error").equals("false")){
                            // Jika login berhasil maka data yang ada di response API
                            // akan diparsing ke activity selanjutnya.
                            Toast.makeText((Context) mContext, "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();
                            //String nama = jsonRESULTS.getJSONObject("user").getString("nama");
                            Intent intent = new Intent((Context) mContext, MainActivity.class);
                            //intent.putExtra("result_nama", nama);
                            startActivity(intent);
                        } else {
                            // Jika login gagal
                            String error_message = jsonRESULTS.getString("error_msg");
                            Toast.makeText((Context) mContext, error_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("debug", "onFailure: ERROR > " + t.toString());
                loading.dismiss();
            }
        });
    }

    private void requestRegister() {
        name = inputName.getText().toString();
        email = inputEmail.getText().toString();
        car_type = inputCarType.getText().toString();
        license_no = inputLicenseNo.getText().toString();
        password = inputPassword.getText().toString();

        mApiService.registerRequest(name, email, car_type, license_no, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Log.i("debug", "onResponse: REGISTRATION SUCCESS");
                    loading.dismiss();
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("error").equals("false")){
                            Toast.makeText((Context) mContext, "REGISTRATION SUCCESS", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent((Context) mContext, MainActivity.class));
                        } else {
                            String error_message = jsonRESULTS.getString("error_msg");
                            Toast.makeText((Context) mContext, error_message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i("debug", "onResponse: FAILED");
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.e("debug", "onFailure: ERROR > " + t.getMessage());
                Toast.makeText((Context) mContext, "CONNECTION ERROR", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public void GetReg() throws UnsupportedEncodingException {
//
//        //user defined values
//        String email = inputEmail.getText().toString();
//        String name = inputName.getText().toString();
//        String password = inputPassword.getText().toString();
//        String type = inputCarType.getText().toString();
//        String no = inputLicenseNo.getText().toString();
//
//        //data variable for sent values to server
//        String data = URLEncoder.encode("email", "UTF-8")
//                + "=" + URLEncoder.encode(email, "UTF-8");
//
//        data += "&" + URLEncoder.encode("name", "UTF-8") + "="
//                + URLEncoder.encode(name, "UTF-8");
//
//        data += "&" + URLEncoder.encode("pass", "UTF-8")
//                + "=" + URLEncoder.encode(password, "UTF-8");
//
//        data += "&" + URLEncoder.encode("type", "UTF-8")
//                + "=" + URLEncoder.encode(type, "UTF-8");
//
//        data += "&" + URLEncoder.encode("no", "UTF-8")
//                + "=" + URLEncoder.encode(no, "UTF-8");
//
//        String text = "";
//        BufferedReader reader = null;
//
//        //send data
//        try {
//            URL regURL = new URL("");
//
//            //send post data req
//            URLConnection conn = regURL.openConnection();
//            conn.setDoOutput(true);
//            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//            wr.write( data );
//            wr.flush();
//
//            //get server response
//            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//
//            //read server response
//            while((line = reader.readLine()) != null) {
//                // Append server response in string
//                sb.append(line + "\n");
//            }
//            text = sb.toString();
//        }
//
//        catch (Exception ex) {
//        }
//
//        finally {
//            try {
//                reader.close();
//            }
//            catch (Exception ex) {
//
//            }
//
//        }
//    }

//    @Override
//    public void onItemSelected(AdapterView<?> arg0, View arg1, int long arg3) {
//        String sp1 = String.valueOf(selectCarBrand.getSelectedItem());
//        Toast.makeText(this, sp1, Toast.LENGTH_SHORT).show();
//        if(sp1.contentEquals("Honda")) {
//            ArrayAdapter<CharSequence> brandAdapter = ArrayAdapter.createFromResource(this, R.array.car_brands, simple_spinner_item);
//            brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            selectCarBrand.setAdapter(brandAdapter);
//        }
//    }

//        public void addItemsOnModelSpinner() {
//            List<String> list = new ArrayList<String>();
//            list.add("list 1");
//            list.add("list 2");
//            list.add("list 3");
//            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//                    android.R.layout.simple_spinner_item, list);
//            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            selectCarModel.setAdapter(dataAdapter);
//
//    }
//
//    public void addListenerOnSpinnerItemSelection() {
//            selectCarBrand.setOnItemSelectedListener(new CustomOnItemSelectedListener());
//
//    }

    private void toggleRegister() {
        if (inputNameContainer.getVisibility() == View.GONE) {
            //logo.setVisibility(View.GONE);
            inputNameContainer.setVisibility(View.VISIBLE);
            inputCarTypeContainer.setVisibility(View.VISIBLE);
            inputLicenseNoContainer.setVisibility(View.VISIBLE);
            toggleButton.setText("Back to login");
            loginButton.setText("Register");
            isLogin = false;
        } else {
            inputNameContainer.setVisibility(View.GONE);
            inputCarTypeContainer.setVisibility(View.GONE);
            inputLicenseNoContainer.setVisibility(View.GONE);
            toggleButton.setText("Tap here to register");
            loginButton.setText("Login");
            //logo.setVisibility(View.VISIBLE);
            isLogin = true;
        }
    }
    private void toggleProgressBar() {
        if (progressBar.getVisibility() == View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }

//    private void carModel() {
//        List<String> honda = new ArrayList<String>();
//        honda.add("Brio");
//        honda.add("BR-V");
//        honda.add("Civic");
//        honda.add("Freed");
//        honda.add("Jazz");
//
//        ArrayAdapter<String> hondaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, honda);
//        hondaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        selectCarModel.setAdapter(hondaAdapter);
//        selectCarModel.setOnItemSelectedListener(new MyOnItemSelectedListener());
//
//    }






//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        if (parent.getId() == R.id.sCarBrand) {
//            ArrayAdapter<CharSequence> modelAdapter = ArrayAdapter.createFromResource(this, R.array.car_model_honda, android.R.layout.simple_spinner_item);
//            if(selectCarBrand.getSelectedItem().toString().equals("Honda")) {
//                selectCarModel.setAdapter(modelAdapter);
//            }
//            modelAdapter.clear();
//        }
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
}










