package com.sentayzo.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.oob.SignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {


    TextInputLayout tilEmail, tilPassword, tilConfirmPassword;
    Button bSignUp;
    FirebaseAuth firebaseAuth;
    String TAG = "SignUpActivity";
    RelativeLayout loadingPanel;
    SharedPreferences userSharedPrefs;
    SharedPreferences.Editor editor;
    AlertDialog connectingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        View v = LayoutInflater.from(SignUpActivity.this).inflate(R.layout.dialog_connecting, null);
        builder.setView(v);
        builder.setCancelable(false);
        connectingDialog = builder.create();

        firebaseAuth = FirebaseAuth.getInstance();
        userSharedPrefs = getSharedPreferences("USER_DETAILS",
                Context.MODE_PRIVATE);
        editor = userSharedPrefs.edit();

        tilEmail = (TextInputLayout) findViewById(R.id.til_email);
        tilPassword = (TextInputLayout) findViewById(R.id.til_password);
        tilConfirmPassword = (TextInputLayout) findViewById(R.id.til_confirm_password);
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);

        bSignUp = (Button) findViewById(R.id.b_sign_up);
        bSignUp.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == bSignUp) {

            showLoadingPanel();
            if (validEntries()) {
                createAccount(tilEmail.getEditText().getText().toString().trim(), tilPassword.getEditText().getText().toString().trim());
            }

        }
    }

    private boolean validEntries() {
        //method to validate that the input fields are valid eg if email field then it user should have entered a valid email

        if (tilEmail.getEditText().getText().toString().trim().isEmpty()) {

            tilEmail.getEditText().setError(getString(R.string.enter_email));
            hideLoadingPanel();

            return false;
        }

        if (isValidEmaillId(tilEmail.getEditText().getText().toString().trim()) == false) {
            tilEmail.getEditText().setError(getString(R.string.valid_email));
            hideLoadingPanel();
            return false;

        }

        if (tilPassword.getEditText().getText().toString().trim().isEmpty()) {

            tilPassword.getEditText().setError(getString(R.string.enter_password));
            hideLoadingPanel();
            return false;
        }

        if (tilConfirmPassword.getEditText().getText().toString().trim().isEmpty()) {

            tilConfirmPassword.getEditText().setError(getString(R.string.passwords_no_match));
            hideLoadingPanel();
            return false;
        }

        if (!tilPassword.getEditText().getText().toString().trim().equals(tilConfirmPassword.getEditText().getText().toString().trim())) {

            tilPassword.getEditText().setError(getString(R.string.passwords_no_match));
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


    public void createAccount(String email, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //   Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //  Toast.makeText(SignUpActivity.this, "Authentication failed.",
                            //        Toast.LENGTH_SHORT).show();
                            hideLoadingPanel();
                            showDialogMessage(getString(R.string.authentication_failed));
                        }

                        // ...
                    }
                });

    }


    private void goToMainActivity() {
        Intent i = new Intent(SignUpActivity.this, MainActivity.class);
        i.putExtra("zero", 0);
        i.putExtra("new_login", 1);
        startActivity(i);
        finish();
    }

    public void updateUI(FirebaseUser user) {

        // Toast.makeText(this, user.getEmail(), Toast.LENGTH_LONG).show();
        saveUserDetailsInSharedPrefs(user.getEmail());
        goToMainActivity();

    }

    private void showLoadingPanel() {

        bSignUp.setActivated(false);
        bSignUp.setText(R.string.wait);
      //  loadingPanel.setVisibility(View.VISIBLE);
        connectingDialog.show();

    }

    private void hideLoadingPanel() {

        bSignUp.setActivated(true);
        bSignUp.setText(R.string.sign_up);
     //   loadingPanel.setVisibility(View.GONE);
        connectingDialog.cancel();

    }

    public void showDialogMessage(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    public void saveUserDetailsInSharedPrefs(String email) {

        editor.putString("email", email);
        editor.putBoolean("logged_in", true);
        editor.apply();

    }
}
