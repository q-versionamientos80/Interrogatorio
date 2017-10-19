package qantica.com.interrogatorio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Clase Intro. Activity donde se introducen las reglas del juego.
 * @author Sebastián Malagón Pérez
 * Q-antica Ltda.
 * Bogotá, Colombia
 * 24/02/2017
 */
public class Intro extends AppCompatActivity {

    private AlertDialog.Builder builder;
    private TextView title;
    public boolean isFirstRun;
    private Toolbar toolbar;
    public boolean backPressed = false;
    private static final String ANALYTICS_ID = "UA-92126687-1";


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
        /**
         * Una vez se borra el stack de Activities en onBackPressed() Intro.class se crea y se cierra
         */
        if(getIntent().getBooleanExtra("EXIT",false)){
            finish();
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro);



        /**
         * Llamado de métodos
         */
        setFont();
        customToolbar();
        firstLaunch();

    }

    /**
     * Coloca la fuente de letra del título del TOOLBAR
     */
    public void setFont(){
        AssetManager am = getAssets();
        Typeface caviarDreams = Typeface.createFromAsset(am,"font/CaviarDreams.ttf");
        title = (TextView) findViewById(R.id.tv_header);
        title.setTypeface(caviarDreams);
        title.setText("interrogatorio");

    }

    /**
     * Libera el diálogo solo para la primera vez que la aplicación sea corrida
     * El diálogo está personalizado con el estilo MyDialogTheme
     */
    public void firstLaunch(){
        isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun){
            ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
            builder = new AlertDialog.Builder(ctw);
            builder.setTitle(Html.fromHtml("<b>"+"¡Llegó el momento del Interrogatorio!"+"</b>"));
            builder.setMessage(Html.fromHtml(
                    "Tienes 2 opciones para validar tus conocimientos: Amateur y Máster." +
                            "<br><br><b>Amateur:</b> Personaliza tu interrogatorio por categoría, subcategorías y dificultad." +
                            "<br><b>Máster: </b>Encontrarás preguntas retadoras en varias áreas del derecho." +
                            "<br><br> Adelante, selecciona... y tú, <b>¿Cuánto sabes de derecho?</b>"));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    /**
     * Aunque setDisplayHomeAsUpEnabled() esté habilitado, el TOOLBAR no cuenta con la opción de navegar hacia Atrás
     * El diseño del TOOLBAR se encuentra en intro_toolbar.xml
     */
    public void customToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Función onClick() para iniciar el modo Amateur
     * @param view
     *          Por Default
     */
    public void AmateurSig(View view){
        ScreenTrackerGoogleAnalytics(this,"Amateur");
        Intent intent = new Intent(this,Categoria.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
    }

    /**
     * Función onClick() para iniciar el modo Máster
     * @param view
     *          Por Default
     */
    public void MasterSig(View view){
        ScreenTrackerGoogleAnalytics(this,"Máster");
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
        builder = new AlertDialog.Builder(ctw);
        builder.setMessage("¿Estás seguro que deseas iniciar en modo máster?");
        builder.setPositiveButton("INICIAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(),MasterGame.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Infla el header_menu.xml en la parte derecha del TOOLBAR.
     * Corresponde al botón de acción de Información
     * @param menu
     *          por Default
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.header_menu, menu);
        return true;
    }

    /**
     * Función onClick() del ícono en header_menu.xml
     * @param m
     *      Por Default
     */
    public void info(MenuItem m){
        EventTrackerGoogleAnalytics(this,"Intro","Info","Info Intro");
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
        builder = new AlertDialog.Builder(ctw);
        builder.setTitle(Html.fromHtml("<b>"+"¡Llegó el momento del Interrogatorio!"+"</b>"));
        builder.setMessage(Html.fromHtml(
                "Tienes 2 opciones para validar tus conocimientos: Amateur y Máster." +
                        "<br><br><b>Amateur:</b> Personaliza tu interrogatorio por categoría, subcategorías y dificultad." +
                        "<br><b>Máster: </b>Encontrarás preguntas retadoras en varias áreas del derecho." +
                        "<br><br> Adelante, selecciona... y tú, <b>¿Cuánto sabes de derecho?</b>"));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    /**
     * Cuando se oprime el Back Button por primera vez, advierte con un Toast que la app se va a cerrar si vuelve a oprimir.
     * Cuando e oprime se oprime por segunda vez, se limpia el stack de Activities y se crea de nuevo la Activity.
     */
    @Override
    public void onBackPressed() {

        if(backPressed){
            super.onBackPressed();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressed = false;
                    Intent i = new Intent(getApplicationContext(),Intro.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.putExtra("EXIT",true);
                    startActivity(i);
                }
            },0);
            return;
        }else{
            this.backPressed = true;
            Toast.makeText(this,"Presiona de nuevo para salir de la aplicación",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Medición de Screen para Google Analytics
     * @param context
     *          Contexto
     * @param ScreenName
     *          Nombre que identifica la pantalla (Screen) en Google Anañytics
     */
    private void ScreenTrackerGoogleAnalytics(Context context, String ScreenName){
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        Tracker tracker = analytics.newTracker(ANALYTICS_ID);
        tracker.setScreenName(ScreenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    /**
     * Seguimiento de eventos para Google Analytics
     * @param context
     *          Contexto
     * @param Categoria
     *          Objeto con el que se ha interactuado
     * @param Action
     *          Tipo de la interacción
     * @param label
     *          Etiqueta del evento
     */
    private void EventTrackerGoogleAnalytics(Context context, String Categoria, String Action, String label){
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        Tracker tracker = analytics.newTracker(ANALYTICS_ID);

        tracker.send(new HitBuilders.EventBuilder().setCategory(Categoria).setAction(Action).setLabel(label).build());
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.d("Intro","On Stop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d("Intro","On Restart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("Intro","On Start");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d("Intro","On Destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d("Intro","On Pause");
    }

}
