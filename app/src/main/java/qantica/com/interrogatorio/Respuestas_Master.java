package qantica.com.interrogatorio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import java.util.ArrayList;

/**
 * Created by Q-antica IT on 04/10/2017.
 */

public class Respuestas_Master extends AppCompatActivity {

    private Intent in;
    public ArrayList<String> statement;
    public ArrayList<String> filtered;
    public ArrayList<String> correctAns;
    public ArrayList<String> selectedAns;
    public TextView vista_enunciado_pregunta;
    public TextView vista_respuesta_correcta;
    public TextView vista_respuesta_seleccionada;
    private TextView title;
    private Toolbar toolbar;
    public boolean backPressed = false;
    private AlertDialog.Builder builder;
    public LinearLayout header;
    public static final String ANALYTICS_ID = "UA-92126687-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        w.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_respuestas_master);


        in = getIntent();
        correctAns = in.getStringArrayListExtra("respuestas_correctas");
        selectedAns =in.getStringArrayListExtra("respuestas_seleccionadas");
        statement= in.getStringArrayListExtra("enunciado");
        filtered=in.getStringArrayListExtra("filtered");

        /**
         * Llamado de métodos
         */
        setControls();
        comparaciónRespuestas();
        setFont();
        customToolbar();
        firstLaunch();

    }
    public void setControls(){
        header = (LinearLayout) findViewById(R.id.header_resp);
        vista_enunciado_pregunta = (TextView) findViewById(R.id.tv_enunciado);
        vista_respuesta_correcta = (TextView) findViewById(R.id.b_resp1);
        vista_respuesta_seleccionada = (TextView) findViewById(R.id.b_resp2);
    }
    public void comparaciónRespuestas(){

        for (int i=0; i<correctAns.size();) {
            if(correctAns.get(i).equals(selectedAns.get(i))){
                i++;
            }
            else {
                vista_enunciado_pregunta.setText(String.valueOf(statement.get(Integer.parseInt(filtered.get(i)))));
                vista_respuesta_seleccionada.setText(selectedAns.get(i));
                vista_respuesta_correcta.setText(correctAns.get(i));
                i=correctAns.size()+1;
            }
        }
    }

    /**
     * Coloca la fuente de letra del título del TOOLBAR
     */
    public void setFont(){
        AssetManager am = getAssets();
        Typeface caviarDreams = Typeface.createFromAsset(am,"font/CaviarDreams.ttf");
        title = (TextView) findViewById(R.id.tv_header);
        title.setTypeface(caviarDreams);
        title.setText("Respuestas Máster");
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
                EventTrackerGoogleAnalytics(getApplicationContext(),"Respuestas Máster","Atras","Atras Respuestas Máster");
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

        boolean isFirstRunResp = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRunResp", true);
        if (isFirstRunResp){
            ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
            builder = new AlertDialog.Builder(ctw);
            builder.setTitle(Html.fromHtml("<b>Respuestas Máster</b>"));
            builder.setMessage("Podras visualizar una de las respuestas correctas.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRunResp", false)
                    .apply();
        }
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
        EventTrackerGoogleAnalytics(this,"Respuestas Master","Info","Info Respuestas Master");
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
        builder = new AlertDialog.Builder(ctw);
        builder.setTitle(Html.fromHtml("<b>Respuestas Máster</b>"));
        builder.setMessage("Podras visualizar una de las respuestas correctas.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    public void fallasteDialog(View view){
        EventTrackerGoogleAnalytics(this,"Resultados Amateur","Porque fallaste","Porque fallaste Amateur");
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
        builder = new AlertDialog.Builder(ctw);
        builder.setMessage("Espera nuestra versión Premium\nSiguenos en nuestras redes sociales\nFB @interrogatorio\nTW @iinterrogatorio");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /**
     * Cuando se oprime el Back Button por primera vez, advierte con un Toast que se va a cerrar la Activity si vuelve a oprimir.
     * Cuando se oprime por segunda vez, pasa a la Activity Intro.class.
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
                    startActivity(i);
                }
            },0);
            return;
        }else{
            this.backPressed = true;
            Toast.makeText(this,"Presiona de nuevo para ir al menú inicial",Toast.LENGTH_SHORT).show();
        }

    }
    /**
     * Seguimiento de eventos para Google Analytics
     * @param context
     *          Contexto
     * @param ScreenName
     *          nombre de la pantalla en analytics
     */
    public void pantalla(Context context, String ScreenName){
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        Tracker tracker = analytics.newTracker(ANALYTICS_ID);
        tracker.setScreenName(ScreenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    /**
     * Seguimiento de eventos para Google Analytics
     * @param context
     *          Contexto
     * @param ScreenName
     *          Objeto con el que se ha interactuado
     * @param Action
     *          Tipo de la interacción
     * @param label
     *          Etiqueta del evento
     */
    private void EventTrackerGoogleAnalytics(Context context, String ScreenName, String Action, String label){
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        Tracker tracker = analytics.newTracker(ANALYTICS_ID);
        tracker.setScreenName(ScreenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        tracker.send(new HitBuilders.EventBuilder().setCategory(ScreenName).setAction(Action).setLabel(label).build());
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.d("Nivel","On Stop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d("Nivel","On Restart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("Nivel","On Start");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d("Nivel","On Destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d("Nivel","On Pause");
    }

}
