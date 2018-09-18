package com.sentayzo.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    Button bResetPassword;
    TextInputLayout tilEmail;
    RelativeLayout loadingPanel;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();

        tilEmail = findViewById(R.id.til_email);
        bResetPassword = findViewById(R.id.b_reset_password);
        loadingPanel = findViewById(R.id.loadingPanel);

        bResetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == bResetPassword) {

            showLoadingPanel();
            if (validEntries()) {
                sendResetEmail(tilEmail.getEditText().getText().toString().trim());
            }


        }
    }

    private void sendResetEmail(String emailAddress) {

        firebaseAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            hideLoadingPanel();
                            showDialogMessage(getString(R.string.email_sent));


                        }else{
                            hideLoadingPanel();
                            showDialogMessage(getString(R.string.oops));
                        }


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


    private void showLoadingPanel() {

        bResetPassword.setActivated(false);
        bResetPassword.setText(R.string.wait);
        loadingPanel.setVisibility(View.VISIBLE);

    }

    private void hideLoadingPanel() {

        bResetPassword.setActivated(true);
        bResetPassword.setText(R.string.login);
        loadingPanel.setVisibility(View.GONE);

    }

    public void showDialogMessage(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(i);
                finish();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }
}
