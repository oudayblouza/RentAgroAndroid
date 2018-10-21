package com.esprit.rentagro.Activities;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.esprit.rentagro.R;
import com.jaeger.library.StatusBarUtil;

import org.json.JSONException;
import org.json.JSONObject;


import Entities.Delegation;
import Util.AppSingleton;
import Util.BCrypt;
import Util.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    @BindView(R.id.link_signup)
    TextView linkSignup;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.input_password)
    EditText inputPassword;
    @BindView(R.id.input_email)
    EditText inputEmail;
    @BindView(R.id.linearLayout3)
    LinearLayout linearLayout3;


    String passworddb = "";
    String  emailput;
    RequestQueue rq;
    SweetAlertDialog pDialog;
    SweetAlertDialog alertdialog;

    SharedPreferences sharedpreferences;


    String requiredPermission1 = "android.permission.READ_EXTERNAL_STORAGE";
    String requiredPermission2 = "android.permission.WRITE_EXTERNAL_STORAGE";
    String requiredPermission3 = "android.permission.CAMERA";
    String requiredPermission4 = "android.permission.ACCESS_FINE_LOCATION";
    String requiredPermission5 = "android.permission.CALL_PHONE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        StatusBarUtil.setTransparent(LoginActivity.this);
        rq = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!validate()) {
                    onLoginFailed();
                    return;
                } else {
                    AlertLoading();
                    userLogin();

                }


            }
        });


        linkSignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int checkVal1 = getApplicationContext().checkCallingOrSelfPermission(requiredPermission1);
                int checkVal2 = getApplicationContext().checkCallingOrSelfPermission(requiredPermission2);
                int checkVal3 = getApplicationContext().checkCallingOrSelfPermission(requiredPermission3);
                int checkVal4 = getApplicationContext().checkCallingOrSelfPermission(requiredPermission4);
                int checkVal5 = getApplicationContext().checkCallingOrSelfPermission(requiredPermission5);
                if (checkVal1 != PackageManager.PERMISSION_GRANTED || checkVal2 != PackageManager.PERMISSION_GRANTED || checkVal3 != PackageManager.PERMISSION_GRANTED || checkVal4 != PackageManager.PERMISSION_GRANTED || checkVal5 != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(LoginActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA ,Manifest.permission.ACCESS_FINE_LOCATION ,Manifest.permission.CALL_PHONE},
                            1);
                    Toast.makeText(getApplicationContext(), "Please check all Permissions !", Toast.LENGTH_LONG).show();

                }else {
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivityForResult(intent, REQUEST_SIGNUP);
                }

            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }



    public void onLoginFailed() {

        Alert("Oups", "Login failed");
        btnLogin.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            inputPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        return valid;
    }

    private void userLogin() {
        System.out.println("he is here");
        emailput = inputEmail.getText().toString();
        String url = Constants.ROOT_URL + "/rentagro/login.php?username=" + inputEmail.getText().toString() + "&password=" + inputPassword.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                passworddb = response.toString();
                StringBuilder finalpassword = new StringBuilder(passworddb);
                finalpassword.setCharAt(2, 'a');
                String pass = finalpassword.toString();

                try {


                    if ((!passworddb.equals("")) & (BCrypt.checkpw(inputPassword.getText().toString(), pass))) {

                        FindUser(emailput);

                    } else {
                        System.out.println("erreur ici");

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                pDialog.dismissWithAnimation();
                                //Toast.makeText(getApplicationContext(), "Login ou password Incorrect", Toast.LENGTH_LONG).show();
                                Alert("Oups", "Login or password Incorrect");
                            }
                        });
                    }
                } catch (Exception e1) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.dismissWithAnimation();
                            // Toast.makeText(getApplicationContext(), "Login ou password Incorrect", Toast.LENGTH_LONG).show();
                            Alert("Oups", "Login or password Incorrect");
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismissWithAnimation();
                //  Toast.makeText(getApplicationContext(),"Erreur Connection",Toast.LENGTH_LONG).show();
                Alert("Error", " Connection Error");
                error.printStackTrace();
            }
        });


        rq.add(stringRequest);


    }


    public void FindUser(String email) {
        String url = Constants.ROOT_URL + "rentagro/User/user_by_email.php?email="+email;
        System.out.println(url);
        String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";
        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  Log.d(TAG, response.toString());

                        try {

                            Delegation delegation = new Delegation();
                            JSONObject v = (JSONObject) response.getJSONObject("users");
                            delegation.setId(v.getInt("delegation_id"));


                           sharedpreferences = getSharedPreferences("userpref", LoginActivity.this.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            editor.clear();

                            editor.putInt("id", v.getInt("id"));
                            editor.putString("username",v.getString("username"));
                            editor.putString("email",v.getString("email"));
                            editor.putInt("delegation",v.getInt("delegation_id"));
                            editor.putString("prenom",v.getString("prenom"));
                            editor.putInt("numtel",v.getInt("numtel"));
                            editor.putInt("voters",v.getInt("voters"));
                            editor.putFloat("rate", Float.parseFloat(v.getString("rate")));
                            editor.putString("roles",v.getString("roles"));
                            editor.putString("password",inputPassword.getText().toString());
                            editor.putLong("longitude",Double.doubleToRawLongBits(v.getDouble("longitude")));
                            editor.putLong("latitude",Double.doubleToRawLongBits(v.getDouble("latitude")));


                            editor.commit();

                            System.out.println( Double.longBitsToDouble(sharedpreferences.getLong("longitude", Double.doubleToLongBits(12))));

                            Intent i1 = new Intent(getApplicationContext(), test.class);
                            startActivity(i1);


                        } catch (JSONException e) {
                            e.printStackTrace();


                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);

    }


    public void Alert(String title, String text) {

        alertdialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        alertdialog.setTitleText(title);
        alertdialog.setContentText(text);
        alertdialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
            }
        });
        alertdialog.show();
    }

    public void AlertLoading() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Authenticating...");
        pDialog.setCancelable(false);
        pDialog.show();
    }





}
