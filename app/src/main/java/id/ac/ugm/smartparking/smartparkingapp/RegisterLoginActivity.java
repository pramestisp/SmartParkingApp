package id.ac.ugm.smartparking.smartparkingapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import id.ac.ugm.smartparking.smartparkingapp.model.LoginRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.RegisterRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.network.Network;


public class RegisterLoginActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputConfirmPassword;
    private EditText inputName;
    private EditText inputCarType;
    private EditText inputLicenseNo;
    private TextInputLayout inputConfirmPasswordContainer;
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

    private Intent intentMain;

//    private View.OnClickListener mContext;
//    private BaseApiService mApiService;
    private ProgressDialog loading;

    private boolean isLogin = true;
    //private String name, email, car_type, license_no, password = "";

    private Network network;

    private String name, email, car_type, license_no, password, confirm_pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputEmail = findViewById(R.id.etLoginEmail);
        inputPassword = findViewById(R.id.etLoginPassword);
        inputConfirmPassword = findViewById(R.id.etConfirmPassword);
        inputName = findViewById(R.id.etLoginName);
        inputCarType = findViewById(R.id.etLoginCarModel);
        inputLicenseNo = findViewById(R.id.etLicenseNo);
        inputConfirmPasswordContainer = findViewById(R.id.confirmPasswordWrapper);
        inputCarTypeContainer = findViewById(R.id.carModelWrapper);
        inputNameContainer = findViewById(R.id.nameWrapper);
        inputLicenseNoContainer = findViewById(R.id.licenseNoWrapper);
        loginButton = findViewById(R.id.bLogin);
        toggleButton = findViewById(R.id.tvRegister);
        //logo = findViewById(R.id.login_logo);
        //textLogo = findViewById(R.id.login_text_logo);
        progressBar = findViewById(R.id.pbLogin);



        network = new Network();

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleRegister();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentMain = new Intent(view.getContext(), MainActivity.class);
                getText();
                if (isLogin) {
                    //login with email
                    loading = ProgressDialog.show(RegisterLoginActivity.this, null, "Please wait", true, false);
                    requestLogin();
//                    if (!inputEmail.getText().toString().isEmpty() || !inputPassword.getText().toString().isEmpty()) {
//
////                        toggleProgressBar();
////                        String email = inputEmail.getText().toString();
////                        String password = inputPassword.getText().toString();
////                        Toast.makeText(RegisterLoginActivity.this,
////                                "Login successful",
////                                Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        Toast.makeText(RegisterLoginActivity.this,
//                                "Please check again",
//                                Toast.LENGTH_SHORT).show();
//                    }
                }
                else {
                    //register with email
                    loading = ProgressDialog.show(RegisterLoginActivity.this, null, "Please wait", true, false);
                    requestRegister();


                }
            }

        });

    }


    private void requestLogin() {
//        String email = inputEmail.getText().toString();
//        String password = inputPassword.getText().toString();
        getText();

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(RegisterLoginActivity.this,
                    "Please check again",
                    Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }

        else {
            final LoginRequestModel request = new LoginRequestModel(email, password);

            network.Login(request, new Network.MyCallback<String>() {
                @Override
                public void onSuccess(String response) {
                    Toast.makeText(RegisterLoginActivity.this,
                            response,
                            Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    startActivity(intentMain);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(RegisterLoginActivity.this,
                            error,
                            Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            });
        }

    }

//        mApiService.loginRequest(inputEmail.getText().toString(),
//                inputPassword.getText().toString()).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()){
//                    loading.dismiss();
//                    try {
//                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
//                        if (jsonRESULTS.getString("error").equals("false")){
//                            // Jika login berhasil maka data yang ada di response API
//                            // akan diparsing ke activity selanjutnya.
//                            Toast.makeText((Context) mContext, "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();
//                            //String nama = jsonRESULTS.getJSONObject("user").getString("nama");
//                            Intent intent = new Intent((Context) mContext, MainActivity.class);
//                            //intent.putExtra("result_nama", nama);
//                            startActivity(intent);
//                        } else {
//                            // Jika login gagal
//                            String error_message = jsonRESULTS.getString("error_msg");
//                            Toast.makeText((Context) mContext, error_message, Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    loading.dismiss();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e("debug", "onFailure: ERROR > " + t.toString());
//                loading.dismiss();
//            }
//        });


    private void requestRegister() {
//        String name = inputName.getText().toString();
//        String email = inputEmail.getText().toString();
//        String car_type = inputCarType.getText().toString();
//        String license_no = inputLicenseNo.getText().toString();
//        String password = inputPassword.getText().toString();
//        String confirm_pass = inputConfirmPassword.getText().toString();



//        if (!name.isEmpty() || !email.isEmpty() || !car_type.isEmpty() || !license_no.isEmpty() || !password.isEmpty() ||
//                !confirm_pass.isEmpty() || !password.equals(confirm_pass)) {

        if (name.isEmpty() || password.isEmpty() || email.isEmpty() || car_type.isEmpty() || license_no.isEmpty() || confirm_pass.isEmpty()
                || !password.equals(confirm_pass)) {
            Toast.makeText(RegisterLoginActivity.this,
                    "Please check again",
                    Toast.LENGTH_SHORT).show();
            loading.dismiss();

        } else {
            final RegisterRequestModel request = new RegisterRequestModel(name, email, password, car_type, license_no);

            network.Register(request, new Network.MyCallback<String>() {
                @Override
                public void onSuccess(String response) {
//                    Toast.makeText(RegisterLoginActivity.this,
//                            response,
//                            Toast.LENGTH_SHORT).show();

                    final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterLoginActivity.this);
                    builder.setMessage("Register success! Please check your email to verify your account")
                            .setPositiveButton("OK", null).show();
                    loading.dismiss();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(RegisterLoginActivity.this,
                            error,
                            Toast.LENGTH_SHORT).show();
                    loading.dismiss();

                }
            });

        }



//        } else {
//
//            Toast.makeText(RegisterLoginActivity.this,
//                    "Please check again",
//                    Toast.LENGTH_SHORT).show();
//            loading.dismiss();
//
//        }
    }



//        mApiService.registerRequest(name, email, car_type, license_no, password).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()){
//                    Log.i("debug", "onResponse: REGISTRATION SUCCESS");
//                    loading.dismiss();
//                    try {
//                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
//                        if (jsonRESULTS.getString("error").equals("false")){
//                            Toast.makeText((Context) mContext, "REGISTRATION SUCCESS", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent((Context) mContext, MainActivity.class));
//                        } else {
//                            String error_message = jsonRESULTS.getString("error_msg");
//                            Toast.makeText((Context) mContext, error_message, Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.i("debug", "onResponse: FAILED");
//                    loading.dismiss();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                Log.e("debug", "onFailure: ERROR > " + t.getMessage());
//                Toast.makeText((Context) mContext, "CONNECTION ERROR", Toast.LENGTH_SHORT).show();
//            }
//        });

    private void getText() {
        name = inputName.getText().toString();
        email = inputEmail.getText().toString();
        car_type = inputCarType.getText().toString();
        license_no = inputLicenseNo.getText().toString();
        password = inputPassword.getText().toString();
        confirm_pass = inputConfirmPassword.getText().toString();
    }


    private void toggleRegister() {
        if (inputConfirmPasswordContainer.getVisibility() == View.GONE) {
            //logo.setVisibility(View.GONE);
            inputConfirmPasswordContainer.setVisibility(View.VISIBLE);
            inputNameContainer.setVisibility(View.VISIBLE);
            inputCarTypeContainer.setVisibility(View.VISIBLE);
            inputLicenseNoContainer.setVisibility(View.VISIBLE);
            toggleButton.setText("Back to login");
            loginButton.setText("Register");
            isLogin = false;
        } else {
            inputConfirmPasswordContainer.setVisibility(View.GONE);
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

}










