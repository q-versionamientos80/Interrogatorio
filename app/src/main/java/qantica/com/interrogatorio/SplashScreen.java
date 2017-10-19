package qantica.com.interrogatorio;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import pl.droidsonroids.gif.GifImageView;
/**
 * Clase Splashscreen. Pantalla inicial de carga
 * @author Sebastián Malagón Pérez
 * Q-antica Ltda.
 * Bogotá, Colombia
 * 24/02/2017
 */
public class SplashScreen extends AppCompatActivity {


    private static final long SPLASH_SCREEN_DELAY = 3000;
    private GifImageView h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Permite que STATUS BAR sea traslúcida y no esté en modo FULL SCREEN
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);

        h = (GifImageView) findViewById(R.id.gif_1);


        /**
         * Thread con duración de SPLASH_SCREEN_DELAY, que coloca como background el gif_interrogatorio
         */
        Thread launchTime = new Thread(){
            @Override
            public void run(){
                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            h.setBackgroundResource(R.drawable.gif_interrogatorio);
                        }
                    });

                    sleep(SPLASH_SCREEN_DELAY);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashScreen.this,Intro.class);
                    startActivity(intent);
                }
            }
        };
        launchTime.start();

    }



}


