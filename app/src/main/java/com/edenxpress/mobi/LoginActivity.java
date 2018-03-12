package com.edenxpress.mobi;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * Created by HP 15-P038 on Jan 2 17.
 */

public class LoginActivity extends BaseActivity implements OnConnectionFailedListener {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";
    private CallbackManager callbackManager;
    private boolean isPasswordVisible;
    protected MenuItem itemCart;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    protected Menu menu;
    private String personEmail;
    private String personName;
    private Uri personPhoto;
    ProgressDialog progress;
    private ProgressDialog progressDialog;
    protected JSONObject responseObject;
    public SharedPreferences shared;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOnline();
        if (this.isInternetAvailable) {
            this.shared = getSharedPreferences("customerData", MODE_PRIVATE);
            //If logged in redirect to dashboard
            if (this.shared.getBoolean("isLoggedIn", false)) {
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            }

            //Implementing FB Login button
            FacebookSdk.sdkInitialize(getApplicationContext());
            //AppEventsLogger.activateApp(this);

            setContentView(R.layout.activity_login);

            //itemCart = (MenuItem) findViewById(R.id.action_cart);
            LoginButton fbLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
            fbLoginButton.setReadPermissions("email");
            this.callbackManager = CallbackManager.Factory.create();
            fbLoginButton.registerCallback(this.callbackManager, new FacebookCallback<LoginResult>() {
                public void onSuccess(LoginResult loginResult) {
                    Log.d("FBToken", "" + loginResult);
                    Log.d("Token", "userid==" + loginResult.getAccessToken().getUserId());
                    LoginActivity.this.RequestData();
                }

                public void onCancel() {
                    Log.d("FBToken", "cancel");
                }

                public void onError(FacebookException exception) {
                    exception.printStackTrace();
                }
            });

            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            this.mGoogleApiClient = new Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (v.getId() == R.id.sign_in_button) {
                        signIn();
                    }
                }
            });

            ((TextInputLayout) findViewById(R.id.passLayout11)).setPasswordVisibilityToggleEnabled(true);

            //@TODO Delete these lines on completion
            ((EditText) findViewById(R.id.et_username)).setText(getString(R.string.demo_username));
            ((EditText) findViewById(R.id.et_password)).setText(getString(R.string.demo_password));

            this.toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(this.toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            return;
        } else {
            showRetryDialog(this);
        }

        this.isPasswordVisible = false;
    }

    public void RequestData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject json = response.getJSONObject();
                JSONObject jsonObject = new JSONObject();
                try {
                    LoginActivity.this.personName = json.getString("first_name") + " " + json.getString("last_name");
                    LoginActivity.this.personEmail = json.getString("email");
                    LoginActivity.this.personPhoto = Uri.parse(json.getJSONObject("picture").getJSONObject("data").getString("url"));
                    jsonObject.put("firstname", json.getString("first_name"));
                    jsonObject.put("lastname", json.getString("last_name"));
                    jsonObject.put("email", json.getString("email"));
                    jsonObject.put("personId", json.getString("id"));
                    LoginManager.getInstance().logOut();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showProgressDialog();
                new Connection(LoginActivity.this).execute("addSocialCustomer", jsonObject.toString());
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,link,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void loginPost(View v) {
        try {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        EditText userNameField = (EditText) findViewById(R.id.et_username);
        EditText passwordField = (EditText) findViewById(R.id.et_password);
        String username = userNameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        Boolean isFormValidated = true;
        if (username.matches("")) {
            userNameField.setError("Email " + getResources().getString(R.string.is_require_text));
            isFormValidated = false;
        }
        if (password.matches("")) {
            passwordField.setError("Passsword " + getResources().getString(R.string.is_require_text));
            isFormValidated = false;
        } else if (password.length() < 4) {
            passwordField.setError("Passsword " + getResources().getString(R.string.alert_password_length));
            isFormValidated = false;
        }
        if (isFormValidated) {
            this.progress = ProgressDialog.show(this, getResources().getString(R.string.please_wait), getResources().getString(R.string.processing_request_response), true);
            this.progress.setCanceledOnTouchOutside(false);
            JSONObject jo = new JSONObject();
            try {
                jo.put("username", username);
                jo.put("password", password);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
            new Connection(this).execute("customerLogin", jo.toString());
        }
    }

    public void openForgotPasswordDialog(View v) {
        View dialogView = getLayoutInflater().inflate(R.layout.forgot_password_dialog_layout, null);
        TextView email = (TextView) dialogView.findViewById(R.id.email);
        final TextView user_email = (TextView) dialogView.findViewById(R.id.user_email);
        email.setText(Html.fromHtml(email.getText() + " " + "<font color=\"#FF2107\">" + "*" + "</font>"));
        final AlertDialog forgotPasswordDialog = new AlertDialog.Builder(this).setView(dialogView).setPositiveButton(getResources().getString(R.string._submit), null).setNegativeButton(R.string._cancel, null).create();
        forgotPasswordDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                forgotPasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (LoginActivity.EMAIL_PATTERN.matcher(user_email.getText().toString()).matches()) {
                            LoginActivity.this.progressDialog = ProgressDialog.show(LoginActivity.this, "", LoginActivity.this.getResources().getString(R.string.please_wait) + LoginActivity.this.getResources().getString(R.string.processing_request_response), true, false);
                            JSONObject jo = new JSONObject();
                            try {
                                jo.put("email", user_email.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            new Connection(LoginActivity.this).execute("forgotPassword", jo.toString());
                            forgotPasswordDialog.dismiss();
                            return;
                        }
                        user_email.setError(LoginActivity.this.getResources().getString(R.string.enter_valid_email));
                    }
                });
            }
        });
        forgotPasswordDialog.show();
    }


    public void revealPassword(View v) {
        EditText passwordField = (EditText) findViewById(R.id.et_password);
        if (this.isPasswordVisible) {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordField.setSelection(passwordField.length());
            this.isPasswordVisible = false;
            return;
        }
        passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        passwordField.setSelection(passwordField.length());
        this.isPasswordVisible = true;
    }

    private void signIn() {
        startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(this.mGoogleApiClient), RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            this.personName = acct.getDisplayName();
            this.personEmail = acct.getEmail();
            String personId = acct.getId();
            this.personPhoto = acct.getPhotoUrl();
            Log.d("SignInActivity", "personName:" + this.personName + "");
            Log.d("SignInActivity", "personEmail:" + this.personEmail + "");
            Log.d("SignInActivity", "personPhoto:" + this.personPhoto + "");
            Log.d("SignInActivity", "personId:" + personId + "");
            String[] name = this.personName.split(" ");
            JSONObject jo = new JSONObject();
            try {
                jo.put("firstname", name[0]);
                jo.put("lastname", name[1]);
                jo.put("email", this.personEmail);
                jo.put("personId", personId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            showProgressDialog();
            new Connection(this).execute("addSocialCustomer", jo.toString());
        } else {
            Log.d(",", result.isSuccess() + "");
        }
        Auth.GoogleSignInApi.signOut(this.mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            public void onResult(Status status) {
            }
        });
    }

    private void showProgressDialog() {
        if (this.mProgressDialog == null) {
            this.mProgressDialog = new ProgressDialog(this);
            this.mProgressDialog.setMessage(getString(R.string.loading));
            this.mProgressDialog.setIndeterminate(true);
            this.mProgressDialog.setCanceledOnTouchOutside(false);
        }
        this.mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (this.mProgressDialog != null && this.mProgressDialog.isShowing()) {
            this.mProgressDialog.dismiss();
        }
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    //API RESPONSES
    public void addSocialCustomerResponse(String backresult) {
        Log.d("addSocialCustomer", backresult + "");
        hideProgressDialog();
        try {
            JSONObject mainObject = new JSONObject(backresult);
            if (!mainObject.has("status")) {
                new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.Error)).setPositiveButton(getResources().getString(R.string._ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setMessage(mainObject.getString("message")).show();
            } else if (mainObject.getString("status").equalsIgnoreCase("1")) {
                String customerId = mainObject.getString("customer_id");
                Editor editor = getApplicationContext().getSharedPreferences("customerData", 0).edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("customerEmail", this.personEmail);
                editor.putString("customerName", this.personName);
                editor.putString("customerId", customerId);
                editor.putString("customerPic", this.personPhoto.toString());
                editor.apply();
                Intent intent_name = new Intent();
                intent_name.setClass(getApplicationContext(), DashboardActivity.class);
                startActivity(intent_name);
                finish();
                Toast.makeText(getApplicationContext(), "Welcome, " + mainObject.getString("firstname") + " " + mainObject.getString("lastname"), Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (JSONException e) {
            Log.d("postError", "1");
            e.printStackTrace();
        }
    }

    public void forgotPasswordResponse(String output) {
        try {
            this.responseObject = new JSONObject(output);
            this.progressDialog.dismiss();
            new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.message)).setPositiveButton(getResources().getString(R.string._ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setMessage(this.responseObject.getString("message")).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void customerLoginResponse(String backresult) {
        try {
            this.responseObject = new JSONObject(backresult);
            if (!this.responseObject.has("status")) {
                new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.Error)).setPositiveButton(getResources().getString(R.string._ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setMessage(this.responseObject.getString("warning")).show();
            } else if (this.responseObject.getString("status").equalsIgnoreCase("1")) {
                String customerName = this.responseObject.getString("firstname") + " " + this.responseObject.getString("lastname");
                String customerEmail = this.responseObject.getString("email");
                String customerId = this.responseObject.getString("customer_id");
                Editor editor = getApplicationContext().getSharedPreferences("customerData", MODE_PRIVATE).edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("customerEmail", customerEmail);
                editor.putString("customerName", customerName);
                editor.putString("customerId", customerId);
                editor.putString("cartItems", this.responseObject.getString("cart_total"));
                if (this.responseObject.has("partner")) {
                    editor.putString("isSeller", this.responseObject.getString("partner"));
                }
                editor.apply();
                Intent intent_name = new Intent();
                intent_name.setClass(getApplicationContext(), DashboardActivity.class);
                startActivity(intent_name);
                finish();
                try {
                    Toast.makeText(getApplicationContext(), "Welcome, " + this.responseObject.getString("firstname") + " " + this.responseObject.getString("lastname"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            this.progress.dismiss();
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }


}
