package com.sentayzo.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvForgotPassword, tvSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        tvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);
        tvSignUp = (TextView) findViewById(R.id.tv_sign_up);

        tvForgotPassword.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == tvForgotPassword){

            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);


        }

        if(v == tvSignUp){

            Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(i);


        }
    }
}
