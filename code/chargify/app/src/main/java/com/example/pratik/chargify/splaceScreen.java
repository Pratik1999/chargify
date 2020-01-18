package com.example.pratik.chargify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class splaceScreen extends AppCompatActivity {

    ImageView splaceIcon;
    ImageView splaceCar;
    Animation appear,rightToLeft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splace_screen);
    splaceIcon=findViewById(R.id.splaceIcon);
    splaceCar=findViewById(R.id.splaceCar);
    appear= AnimationUtils.loadAnimation(this,R.anim.appear);
    rightToLeft=AnimationUtils.loadAnimation(this,R.anim.fromrighttoleft);
    splaceIcon.startAnimation(appear);
    appear.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

            splaceCar.startAnimation(rightToLeft);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            finish();
            Intent i = new Intent(getBaseContext(),MainActivity.class);
            startActivity(i);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    });

    }
}
