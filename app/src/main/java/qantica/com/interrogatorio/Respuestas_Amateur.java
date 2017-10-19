package qantica.com.interrogatorio;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;


public class Respuestas_Amateur extends AppCompatActivity {

    public TextView vista_enunciado_pregunta;
    public TextView vista_respuesta_correcta;
    public TextView vista_respuesta_seleccionada;
    public boolean backPressed = false;
    public Button r1;
    public Button r2;
    public Button r3;
    public Button r4;

    private LinearLayout header;
    private Toolbar toolbar;
    private AlertDialog.Builder builder;
    private TextView title;
    private Intent in;

    public ArrayList<String> respuesta1;
    public ArrayList<String> respuesta2;
    public ArrayList<String> respuesta3;
    public ArrayList<String> respuesta4;
    public ArrayList<String> statement;
    public ArrayList<String> filtered;
    public ArrayList<String> correctAns;
    public ArrayList<String> selectedAns;

    private static final String ANALYTICS_ID = "UA-92126687-1";
    private Context context;
    private String screenName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Permite que STATUS BAR sea traslúcida y no esté en modo FULL SCREEN
         */
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        w.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_respuestas_amateur);

        /**
         * Intent con el string de la categoría elegida, un ArrayList (selectedSub) con las subcategorías y selectedLv con el nivel.
         * Se remueven los espacios en blanco (" ")
         */
        in = getIntent();
        correctAns = in.getStringArrayListExtra("respuestas_correctas");
        selectedAns =in.getStringArrayListExtra("respuestas_seleccionadas");
        statement= in.getStringArrayListExtra("enunciado");
        filtered=in.getStringArrayListExtra("filtered");
        //respuesta1 =in.getStringArrayListExtra("respuesta1");
        //respuesta2 =in.getStringArrayListExtra("respuesta2");
        //respuesta3 =in.getStringArrayListExtra("respuesta3");
        //respuesta4 =in.getStringArrayListExtra("respuesta4");

        /**
         * Llamado de métodos
         */
        setControls();
        comparaciónRespuestas();
        setFont();
        customToolbar();
        firstLaunch();
        //changeCategFeatures();
        //r2.setText(String.valueOf(respuesta2.get(Integer.parseInt(filtered.get(0)))));
        //r3.setText(String.valueOf(respuesta2.get(Integer.parseInt(filtered.get(0)))));
        //r4.setText(String.valueOf(respuesta2.get(Integer.parseInt(filtered.get(0)))));
    }


    /**
     * Relaciona las clases View con su correspondiente id en el XML
     */
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
        title.setText("Respuestas Amateur");
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
                EventTrackerGoogleAnalytics(getApplicationContext(),"Respuestas Amateur","Atras","Atras Respuestas Amateur");
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
            builder.setTitle(Html.fromHtml("<b>Respuestas Amateur</b>"));
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
        EventTrackerGoogleAnalytics(this,"Respuestas Amateur","Info","Info Respuestas Amateur");
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
        builder = new AlertDialog.Builder(ctw);
        builder.setTitle(Html.fromHtml("<b>Respuestas Amateur</b>"));
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
     *          Nombre que identifica la pantalla (Screen) en Google Anañytics
     * @param Action
     *          Tipo de la interacción
     * @param label
     *          Etiqueta del evento
     */
    private void EventTrackerGoogleAnalytics(Context context, String ScreenName, String Action, String label){

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        Tracker tracker = analytics.newTracker(ANALYTICS_ID);
        tracker.send(new HitBuilders.EventBuilder().setCategory(ScreenName).setAction(Action).setLabel(label).build());
        tracker.setScreenName(ScreenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.d("MasterStat","On Stop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d("MasterStat","On Restart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("MasterStat","On Start");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d("MasterStat","On Destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d("MasterStat","On Pause");
    }

}