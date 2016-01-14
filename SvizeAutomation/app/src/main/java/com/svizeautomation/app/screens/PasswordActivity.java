package com.svizeautomation.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.svizeautomation.app.R;

/**
 * Created by sid on 15/01/2016.
 */
public class PasswordActivity extends Activity {

    private TextView loginBtnTextView;
    private EditText pwdEditText;

    private String pwd = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pwd_layout);
        initViews();
        registerEvents();

    }

    private void initViews() {
        loginBtnTextView = (TextView) findViewById(R.id.loginBtnTextView);
        pwdEditText = (EditText) findViewById(R.id.pwdEditText);
        pwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (pwdEditText.getText().toString().trim().length() > 0) {
                    pwdEditText.setError(null);
                }
            }
        });
    }

    private void registerEvents() {
        loginBtnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pwdEditText.getText().toString().trim().equals(pwd)) {
                    finish();
                } else {
                    pwdEditText.setError("Incorrect Password, Please enter correct one.");
                }

            }
        });
    }
    

}
