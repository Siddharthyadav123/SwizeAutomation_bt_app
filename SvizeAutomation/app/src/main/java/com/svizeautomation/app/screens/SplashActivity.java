package com.svizeautomation.app.screens;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.svizeautomation.app.BluetoothViewer;
import com.svizeautomation.app.R;

/**
 * Created by sid on 08/12/2015.
 */
public class SplashActivity extends Activity {

    private ImageView logoImageview;
    private TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        initViews();
        registerEvents();
        animateLogoImageview();
        boolean isBluetoothEnabled = enableBluetooth();

        if (isBluetoothEnabled) {
            finishAfterSomeTime("Bluetooth Connected");
        }

    }

    private void sayConnectionOkayBeforeFinish(final String infoText) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingText.setText(infoText);
            }
        }, 2000);
    }

    private boolean enableBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BluetoothViewer.REQUEST_ENABLE_BT);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case BluetoothViewer.REQUEST_ENABLE_BT: {
                if (resultCode == Activity.RESULT_OK) {
                    finishAfterSomeTime("Bluetooth Connected.");
                } else {
                    finishAfterSomeTime("Bluetooth Connection Denied.");
                }
            }
        }
    }

    private void initViews() {
        logoImageview = (ImageView) findViewById(R.id.logoImageview);
        loadingText = (TextView) findViewById(R.id.loadingText);
    }

    private void animateLogoImageview() {
        Animation fadeInFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadein_fadeout_reversible);
        logoImageview.startAnimation(fadeInFadeOut);
    }

    private void registerEvents() {

    }


    private void finishAfterSomeTime(final String msgInfo) {
        sayConnectionOkayBeforeFinish(msgInfo);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                launchRoomSettingActivity();
            }
        }, 4000);
    }

    private void launchRoomSettingActivity() {
        Intent i = new Intent(this, HomeScreenActivity.class);
        startActivity(i);
    }
}
