package com.sentayzo.app;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {


    TextInputLayout tilEmail, tilPassword, tilConfirmPassword;
    Button bSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        tilEmail = (TextInputLayout) findViewById(R.id.til_email);
        tilPassword = (TextInputLayout) findViewById(R.id.til_password);
        tilConfirmPassword = (TextInputLayout) findViewById(R.id.til_confirm_password);
        bSignUp = (Button) findViewById(R.id.b_sign_up);
        bSignUp.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == bSignUp) {

            Intent i = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(i);

        }
    }
}
