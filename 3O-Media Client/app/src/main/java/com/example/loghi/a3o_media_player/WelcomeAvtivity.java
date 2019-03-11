package com.example.loghi.a3o_media_player;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.blikoon.qrcodescanner.QrCodeActivity;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

public class WelcomeAvtivity extends AppCompatActivity {

    LinearLayout l1, l2;
    Animation uptodown, downtoup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_avtivity);
        l1 = (LinearLayout) findViewById(R.id.l1);
        l2 = (LinearLayout) findViewById(R.id.l2);
        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        l1.setAnimation(uptodown);
        l2.setAnimation(downtoup);
        startHeavyProcessing();

    }

    private void startHeavyProcessing() {
        new LongOperation().execute("");
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //some heavy processing resulting in a Data String

            for (int i = 0; i < 2; i++) {
                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            return "whatever result you have";
        }

        @Override
        protected void onPostExecute(String result) {
            Intent iny = new Intent(WelcomeAvtivity.this, MainActivity.class);
//            i.putExtra("data", result);
            startActivity(iny);
            finish();
        }
    }
}