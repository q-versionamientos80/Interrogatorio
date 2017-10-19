package qantica.com.interrogatorio;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
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

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


/**
 * Clase Categoría. Aquí el usuario puede elegir una de la cuatro categorías.
 * @author Sebastián Malagón Pérez
 * Q-antica Ltda.
 * Bogotá, Colombia
 * 24/02/2017
 */
public class Categoria extends AppCompatActivity {


    private Toolbar toolbar;
    private AlertDialog.Builder builder;
    private TextView title;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_categoria);

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
        title.setText("Derecho");

    }

    /**
     * Permite que el TOOLBAR tenga la opción de navegación hacia Atrás
     * El diseño del TOOLBAR se encuentra en main_toolbar.xml
     */
    public void customToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventTrackerGoogleAnalytics(getApplicationContext(),"Amateur","Atras","Atras Amateur");
                Intent intent = new Intent(getApplicationContext(),Intro.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_right_out);
            }
        });
    }

    /**
     * Libera el diálogo solo para la primera vez que la aplicación sea corrida
     * El diálogo está personalizado con el estilo MyDialogTheme
     */
    public void firstLaunch(){

        boolean isFirstRunCateg = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRunCateg", true);
        if (isFirstRunCateg){
            ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
            builder = new AlertDialog.Builder(ctw);
            builder.setTitle(Html.fromHtml("<b>Categoría</b>"));
            builder.setMessage("Elige sólo una categoría pulsando sobre la imagen");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRunCateg", false)
                    .apply();
        }
    }

    /**
     * Función onClick() que permite seguir hacía CivilView
     * @param view
     *          Por Default
     */
    public void ClickCivil(View view){
        ScreenTrackerGoogleAnalytics(this, "Civil");
        Intent intent = new Intent(this,CivilView.class);
        intent.putExtra("Categoria", "CIVIL");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
    }
    /**
     * Función onClick() que permite seguir hacía LaboralView
     * @param view
     *          Por Default
     */
    public void ClickLaboral(View view){
        ScreenTrackerGoogleAnalytics(this, "Laboral");
        Intent intent = new Intent(this,LaboralView.class);
        intent.putExtra("Categoria", "LABORAL");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
    }

    /**
     * Función onClick() que permite seguir hacía ComercialView
     * @param view
     *        Por Default
     */
    public void ClickComercial(View view){
        ScreenTrackerGoogleAnalytics(this, "Comercial");
        Intent intent = new Intent(this,ComercialView.class);
        intent.putExtra("Categoria", "COMERCIAL");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
    }

    /**
     * Función onClick() que permite seguir hacía PenalView
     * @param view
     *          Por Default
     */
    public void ClickPenal(View view){
        ScreenTrackerGoogleAnalytics(this, "Penal");
        Intent intent = new Intent(this,PenalView.class);
        intent.putExtra("Categoria", "PENAL");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
    }
    /**
     * Función onClick() que permite seguir hacía ProcesalView
     * @param view
     *          Por Default
     */
    public void ClickProcesal (View view){
        ScreenTrackerGoogleAnalytics(this, "Procesal");
        Intent intent = new Intent(this,ProcesalView.class);
        intent.putExtra("Categoria","PROCESAL");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
    }
    /**
     * Función onClick() que permite seguir hacía PublicoView
     * @param view
     *          Por Default
     */
    public void  ClickPublico (View view){
        ScreenTrackerGoogleAnalytics(this, "Público");
        Intent intent = new Intent(this,PublicoView.class);// Se declara un objeto para indicar hacia que activity (pantalla) vamos
        intent.putExtra("Categoria","PUBLICO");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
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
        EventTrackerGoogleAnalytics(this,"Amateur","Info","Info Amateur");
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
        builder = new AlertDialog.Builder(ctw);
        builder.setTitle(Html.fromHtml("<b>Categoría</b>"));
        builder.setMessage("Elige sólo una categoría pulsando sobre la imagen");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

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
        //Log.d("Categ","On Stop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d("Categ","On Restart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("Categ","On Start");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d("Categ","On Destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d("Categ","On Pause");
    }

}


