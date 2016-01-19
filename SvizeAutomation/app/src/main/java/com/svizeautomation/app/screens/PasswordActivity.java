package com.svizeautomation.app.screens;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.svizeautomation.app.R;
import com.svizeautomation.app.model.LocalModel;

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

    boolean correctPwd = false;

    private void registerEvents() {
        loginBtnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pwdEditText.getText().toString().trim().equals(pwd)) {
                    correctPwd = true;
                    onBackPressed();
                } else {
                    pwdEditText.setError("Incorrect Password, Please enter correct one.");
                }

            }
        });
    }


    @Override
    public void onBackPressed() {
        LocalModel.getInstance().hideKeyboard(this);
        if (correctPwd)
            setResult(Activity.RESULT_OK);
        else
            setResult(Activity.RESULT_CANCELED);

        super.onBackPressed();
        System.out.println(">>sid >>onBackPressed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println(">>sid >>onDestroy");
    }
}
