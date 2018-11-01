package com.sentayzo.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_GOOGLE_SIGN_IN = 50;
    private static final String EMAIL = "email";
    private static final String AUTH_TYPE = "rerequest";
    String CHECK_IF_PREMIUM_USER = "http://moneycradle.plexosys-consult.com/checkIfUserPremiumByEmail.php";


    TextView tvForgotPassword, tvSignUp;
    FirebaseAuth mAuth;
    Button bLogin;
    ApplicationClass applicationClass = ApplicationClass.getInstance();
    TextInputLayout tilEmail, tilPassword;
    RelativeLayout loadingPanel;
    SharedPreferences userSharedPrefs;
    SharedPreferences.Editor editor;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    CallbackManager callbackManager;

    AlertDialog connectingDialog;

    String TAG = "LoginActivity";

    SignInButton bGoogleLogin;
    LoginButton bFbLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View v = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_connecting, null);
        builder.setView(v);
        builder.setCancelable(false);
        connectingDialog = builder.create();

        //FirebaseApp.initializeApp(this);
        mAuth = applicationClass.getFirebaseAuth();
        userSharedPrefs = getSharedPreferences("USER_DETAILS",
                Context.MODE_PRIVATE);
        editor = userSharedPrefs.edit();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);


        callbackManager = CallbackManager.Factory.create();


        tvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);
        tvSignUp = (TextView) findViewById(R.id.tv_sign_up);
        bFbLogin = findViewById(R.id.b_fb);
        bLogin = (Button) findViewById(R.id.b_login);
        tilEmail = (TextInputLayout) findViewById(R.id.til_email);
        tilPassword = (TextInputLayout) findViewById(R.id.til_password);
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        bGoogleLogin = findViewById(R.id.b_google);


        bFbLogin.setReadPermissions("email", "public_profile");

        bFbLogin.setAuthType(AUTH_TYPE);


        bFbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(LoginActivity.this, "Login cancelled.",
                        Toast.LENGTH_SHORT).show();
                showDialogMessage(getString(R.string.login_cancelled));
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(LoginActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
                showDialogMessage(getString(R.string.authentication_failed));
            }
        });


        setGooglePlusButtonText(bGoogleLogin, getString(R.string.sign_with_google));

        tvForgotPassword.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        bFbLogin.setOnClickListener(this);
        bLogin.setOnClickListener(this);
        bGoogleLogin.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == bLogin) {

            showLoadingPanel();
            if (validEntries()) {
                signInUser(tilEmail.getEditText().getText().toString().trim(), tilPassword.getEditText().getText().toString().trim());
            }

        }


        if (v == tvForgotPassword) {

            Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(i);

        }

        if (v == tvSignUp) {

            Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(i);

        }
/*
        if (v == bFbLogin) {
            FirebaseUser currentUser = mAuth.getCurrentUser();


            Toast.makeText(this, currentUser.getDisplayName(), Toast.LENGTH_LONG).show();

        }
        */

        if (v == bGoogleLogin) {

            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);

        }
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
//this is for facebook login


    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            firebaseAuthWithGoogle(account);


            // Signed in successfully, show authenticated UI.
            //  saveUserDetailsInSharedPrefs(account.getEmail(), account.getDisplayName());


            //    goToMainActivity();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Login Activity", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(LoginActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
            showDialogMessage(getString(R.string.authentication_failed));

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //   Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //  Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUserDetailsInSharedPrefs(user.getEmail(), user.getDisplayName());
                            goToMainActivity();
                            //  checkOnlineIfUserIsPremium(user.getEmail());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //  hideLoadingPanel();
                            showDialogMessage(getString(R.string.authentication_failed));
                        }

                        // ...
                    }
                });
    }


    private void signInUser(String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //   Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUserDetailsInSharedPrefs(user.getEmail(), user.getDisplayName());
                            // checkOnlineIfUserIsPremium(user.getEmail());
                            goToMainActivity();


                        } else {
                            // If sign in fails, display a message to the user.
                            //   Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            hideLoadingPanel();
                            showDialogMessage(getString(R.string.authentication_failed));
                        }

                        // ...
                    }
                });


    }

    private void handleFacebookAccessToken(AccessToken token) {
        // Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUserDetailsInSharedPrefs(user.getEmail(), user.getDisplayName());
                            goToMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //  hideLoadingPanel();
                            showDialogMessage(getString(R.string.authentication_failed));
                        }

                        // ...
                    }
                });
    }


    private boolean validEntries() {
        //method to validate that the input fields are valid eg if email field then it user should have entered a valid email

        if (tilEmail.getEditText().getText().toString().trim().isEmpty()) {

            tilEmail.getEditText().setError(getString(R.string.enter_email));
            hideLoadingPanel();
            return false;
        }

        if (!isValidEmaillId(tilEmail.getEditText().getText().toString().trim())) {
            tilEmail.getEditText().setError(getString(R.string.valid_email));
            hideLoadingPanel();
            return false;

        }

        if (tilPassword.getEditText().getText().toString().trim().isEmpty()) {

            tilPassword.getEditText().setError(getString(R.string.enter_password));
            hideLoadingPanel();
            return false;
        }


        return true;
    }

    private boolean isValidEmaillId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }


    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //  FirebaseUser currentUser = mAuth.getCurrentUser();
        //  updateUI(currentUser);
    }
    // [END on_start_check_user]


    private void showLoadingPanel() {

        bLogin.setActivated(false);
        bLogin.setText(R.string.wait);
        // loadingPanel.setVisibility(View.VISIBLE);
        connectingDialog.show();
    }

    private void hideLoadingPanel() {

        bLogin.setActivated(true);
        bLogin.setText(R.string.login);
        //  loadingPanel.setVisibility(View.GONE);
        connectingDialog.cancel();

    }

    public void showDialogMessage(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    public void saveUserDetailsInSharedPrefs(String email, String displayname) {

        editor.putString("email", email);
        editor.putString("display_name", displayname);
        editor.putBoolean("logged_in", true);
        editor.apply();

    }

    private void goToMainActivity() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.putExtra("zero", 0);
        i.putExtra("new_login", 1);
        startActivity(i);
        finish();
    }
}
