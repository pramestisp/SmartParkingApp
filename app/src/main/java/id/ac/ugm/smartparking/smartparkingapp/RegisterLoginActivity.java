package id.ac.ugm.smartparking.smartparkingapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import id.ac.ugm.smartparking.smartparkingapp.model.LoginRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.LoginResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.LoginResponse.Meta;
import id.ac.ugm.smartparking.smartparkingapp.model.RegisterRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.network.Network;
import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;


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
    private TextView toggleButton, tvForgot;

    private ProgressBar progressBar;

    private SmartParkingSharedPreferences prefManager;
    private Intent intentMain;
    private ProgressDialog loading;
    private boolean isLogin = true;
    private boolean logged = true;
    private Network network;
    private String name, email, car_type, license_no, password, confirm_pass, authToken;

    public String profile_name, profile_email, profile_car_type, profile_car_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        prefManager = new SmartParkingSharedPreferences(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        progressBar = findViewById(R.id.pbLogin);
        tvForgot = findViewById(R.id.tvForgotPW);
        tvForgot.setMovementMethod(LinkMovementMethod.getInstance());

        inputEmail.setText("bebek@ayam.com");
        inputPassword.setText("sayaarina");

        network = new Network(this);

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

        getText();

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(RegisterLoginActivity.this,
                    "Please check again",
                    Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }

        else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            Toast.makeText(RegisterLoginActivity.this,
                    "Invalid email format",
                    Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }

        else {
            final LoginRequestModel request = new LoginRequestModel(email, password);

            network.Login(request, new Network.MyCallback<LoginResponse>() {
                @Override
                public void onSuccess(LoginResponse response) {
                    Toast.makeText(RegisterLoginActivity.this,
                            "Login success",
                            Toast.LENGTH_SHORT).show();

                    Meta meta = response.getMeta();
                    LoginResponse.Data data = response.getData();

                    int user_id = data.getIdUser();
                    profile_name = data.getName();
                    profile_email = data.getEmail();
                    profile_car_type = data.getCarType();
                    profile_car_no = data.getLicensePlateNumber();
                    authToken = meta.getToken();

                    prefManager.setInt(SmartParkingSharedPreferences.PREF_USER_ID, user_id);
                    prefManager.setString(SmartParkingSharedPreferences.PREF_USER_NAME, profile_name);
                    prefManager.setString(SmartParkingSharedPreferences.PREF_EMAIL, profile_email);
                    prefManager.setString(SmartParkingSharedPreferences.PREF_CAR_TYPE, profile_car_type);
                    prefManager.setString(SmartParkingSharedPreferences.PREF_CAR_NO, profile_car_no);
                    prefManager.setString(SmartParkingSharedPreferences.PREF_TOKEN, authToken);
                    prefManager.setBoolean(SmartParkingSharedPreferences.PREF_LOGGED, logged);

                    loading.dismiss();

                    if(prefManager.getBoolean(SmartParkingSharedPreferences.PREF_LOGGED)) {
                        startActivity(intentMain);
                    }
                    else {
                        return;
                    }
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

    private void requestRegister() {

        if (name.isEmpty() || password.isEmpty() || email.isEmpty() || car_type.isEmpty() || license_no.isEmpty() || confirm_pass.isEmpty()
                || !password.equals(confirm_pass)) {
            Toast.makeText(RegisterLoginActivity.this,
                    "Please check again",
                    Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }

        else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            Toast.makeText(RegisterLoginActivity.this,
                    "Email format is invalid",
                    Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }

        else {
            final RegisterRequestModel request = new RegisterRequestModel(name, email, password, car_type, license_no);

            network.Register(request, new Network.MyCallback<String>() {
                @Override
                public void onSuccess(String response) {

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

    }

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
            tvForgot.setVisibility(View.GONE);
            toggleButton.setText("Back to login");
            loginButton.setText("Register");
            isLogin = false;
        } else {
            inputConfirmPasswordContainer.setVisibility(View.GONE);
            inputNameContainer.setVisibility(View.GONE);
            inputCarTypeContainer.setVisibility(View.GONE);
            inputLicenseNoContainer.setVisibility(View.GONE);
            tvForgot.setVisibility(View.VISIBLE);
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

    @Override
    public void onBackPressed() {
        Intent close = new Intent(Intent.ACTION_MAIN);
        close.addCategory(Intent.CATEGORY_HOME);
        close.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(close);
    }
}










