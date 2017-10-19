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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Arrays;
/**
 * Clase LevelView. El usuario elige uno de los tres niveles propuestos.
 * @author Sebastián Malagón Pérez
 * Q-antica Ltda.
 * Bogotá, Colombia
 * 24/02/2017
 */
public class LevelView extends AppCompatActivity {

    private LinearLayout header;
    private TextView titulo;
    private TextView header_lvl;
    private Button nivel1;
    private Button nivel2;
    private Button nivel3;
    private AlertDialog.Builder builder;
    private Toolbar toolbar;

    private LinearLayout rounded;
    private ImageView dot1;
    private ImageView dot2;

    private String selectedLv;
    private ArrayList<String> selectedSub;
    private String categ;
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
        setContentView(R.layout.activity_level_view);



        /**
         * Intent con el string de la categoría elegida y un ArrayList (selectedSub) con las subcategorías elegidas.
         * Se remueven los espacios en blanco (" ")
         */
        Intent in = getIntent();
        selectedSub = in.getStringArrayListExtra("SeleccionSub");
        selectedSub.removeAll(Arrays.asList(null,""));
        categ = in.getStringExtra("Categoria");


        /**
         * Llamado de métodos
         */
        setControls();
        setFont();
        customToolbar();
        firstLaunch();
        changeCategFeatures();

    }


    /**
     * Relaciona las clases View con su correspondiente id en el XML
     */
    public void setControls(){
        titulo = (TextView) findViewById(R.id.tv_header);
        header = (LinearLayout) findViewById(R.id.header_lvl);
        nivel1 = (Button) findViewById(R.id.lvl_1);
        nivel2 = (Button) findViewById(R.id.lvl_2);
        nivel3 = (Button) findViewById(R.id.lvl_3);
        rounded = (LinearLayout) findViewById(R.id.ly_rounded);
        dot1 = (ImageView) findViewById(R.id.dotted_1);
        dot2 = (ImageView) findViewById(R.id.dotted_2);
        header_lvl = (TextView) findViewById(R.id.tv_header_lvl);


    }

    /**
     * Coloca la fuente de letra del título del TOOLBAR
     */
    public void setFont(){
        AssetManager am = getAssets();
        Typeface caviarDreams = Typeface.createFromAsset(am,"font/CaviarDreams.ttf");
        titulo = (TextView) findViewById(R.id.tv_header);
        titulo.setTypeface(caviarDreams);

    }

    /**
     * Permite que el TOOLBAR tenga la opción de navegación hacia Atrás
     * El diseño del TOOLBAR se encuentra en main_toolbar.xml
     * Se ocnfigura el botón de Atrás dependiendo de la Categoría elegida
     */
    public void customToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventTrackerGoogleAnalytics(getApplicationContext(),"Nivel","Atras","Atras Nivel");
                Intent next;
                switch (categ){
                    case "CIVIL":
                        next = new Intent(getApplicationContext(),CivilView.class);
                        next.putExtra("Categoria",categ);
                        startActivity(next);
                        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_right_out);
                        break;
                    case "LABORAL":
                        next = new Intent(getApplicationContext(), LaboralView.class);
                        next.putExtra("Categoria",categ);
                        startActivity(next);
                        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_right_out);
                        break;
                    case "COMERCIAL":
                        next = new Intent(getApplicationContext(),ComercialView.class);
                        next.putExtra("Categoria",categ);
                        startActivity(next);
                        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_right_out);
                        break;
                    case "PENAL":
                        next = new Intent(getApplicationContext(),PenalView.class);
                        next.putExtra("Categoria",categ);
                        startActivity(next);
                        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_right_out);
                        break;
                    case "PROCESAL":
                        next = new Intent(getApplicationContext(),ProcesalView.class);
                        next.putExtra("Categoria",categ);
                        startActivity(next);
                        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_right_out);
                        break;
                    case "PUBLICO":
                        next = new Intent(getApplicationContext(),PublicoView.class);
                        next.putExtra("Categoria",categ);
                        startActivity(next);
                        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_right_out);
                        break;

                }
            }
        });
    }

    /**
     * Libera el diálogo solo para la primera vez que la aplicación sea corrida
     * El diálogo está personalizado con el estilo MyDialogTheme
     */
    public void firstLaunch(){
        boolean isFirstRunLvl = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRunLvl", true);
        if (isFirstRunLvl){
            ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
            builder = new AlertDialog.Builder(ctw);
            builder.setTitle(Html.fromHtml("<b>Nivel</b>"));
            builder.setMessage("Elige uno de los niveles. Recuerda que Judicante es el nivel con menor complejidad y Magistrado el de mayor.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRunLvl", false)
                    .apply();
        }
    }

    /**
     * Se configura los colores de los diferentes elementos de la pantalla dependiendo de la Categoría elegida
     */
    public void changeCategFeatures(){
        switch (categ){
            case "CIVIL":
                titulo.setText("Civil");
                header.setBackgroundResource(R.drawable.gradiente_civil);
                rounded.setBackgroundResource(R.drawable.rounded_civil);
                dot1.setBackgroundResource(R.drawable.dotted_civil);
                dot2.setBackgroundResource(R.drawable.dotted_civil);
                header_lvl.setTextColor(getResources().getColor(R.color.colorCivildark));
                nivel1.setTextColor(getResources().getColor(R.color.colorCivildark));
                nivel2.setTextColor(getResources().getColor(R.color.colorCivildark));
                nivel3.setTextColor(getResources().getColor(R.color.colorCivildark));
                break;
            case "LABORAL":
                titulo.setText("Laboral");
                header.setBackgroundResource(R.drawable.gradiente_laboral);
                rounded.setBackgroundResource(R.drawable.rounded_laboral);
                dot1.setBackgroundResource(R.drawable.dotted_laboral);
                dot2.setBackgroundResource(R.drawable.dotted_laboral);
                header_lvl.setTextColor(getResources().getColor(R.color.colorLaboraldark));
                nivel1.setTextColor(getResources().getColor(R.color.colorLaboraldark));
                nivel2.setTextColor(getResources().getColor(R.color.colorLaboraldark));
                nivel3.setTextColor(getResources().getColor(R.color.colorLaboraldark));
                break;
            case "COMERCIAL":
                titulo.setText("Comercial");
                header.setBackgroundResource(R.drawable.gradiente_comercial);
                rounded.setBackgroundResource(R.drawable.rounded_comercial);
                dot1.setBackgroundResource(R.drawable.dotted_comercial);
                dot2.setBackgroundResource(R.drawable.dotted_comercial);
                header_lvl.setTextColor(getResources().getColor(R.color.colorComercialdark));
                nivel1.setTextColor(getResources().getColor(R.color.colorComercialdark));
                nivel2.setTextColor(getResources().getColor(R.color.colorComercialdark));
                nivel3.setTextColor(getResources().getColor(R.color.colorComercialdark));
                break;
            case "PENAL":
                titulo.setText("Penal");
                header.setBackgroundResource(R.drawable.gradiente_penal);
                rounded.setBackgroundResource(R.drawable.rounded_penal);
                dot1.setBackgroundResource(R.drawable.dotted_penal);
                dot2.setBackgroundResource(R.drawable.dotted_penal);
                header_lvl.setTextColor(getResources().getColor(R.color.colorPenaldark));
                nivel1.setTextColor(getResources().getColor(R.color.colorPenaldark));
                nivel2.setTextColor(getResources().getColor(R.color.colorPenaldark));
                nivel3.setTextColor(getResources().getColor(R.color.colorPenaldark));
                break;
            case "PROCESAL":
                titulo.setText("Procesal");
                header.setBackgroundResource(R.drawable.gradiente_procesal);
                rounded.setBackgroundResource(R.drawable.rounded_procesal);
                dot1.setBackgroundResource(R.drawable.dotted_procesal);
                dot2.setBackgroundResource(R.drawable.dotted_procesal);
                header_lvl.setTextColor(getResources().getColor(R.color.colorProcesaldark));
                nivel1.setTextColor(getResources().getColor(R.color.colorProcesaldark));
                nivel2.setTextColor(getResources().getColor(R.color.colorProcesaldark));
                nivel3.setTextColor(getResources().getColor(R.color.colorProcesaldark));
                break;
            case "PUBLICO":
                titulo.setText("Público");
                header.setBackgroundResource(R.drawable.gradiente_publico);
                rounded.setBackgroundResource(R.drawable.rounded_publico);
                dot1.setBackgroundResource(R.drawable.dotted_publico);
                dot2.setBackgroundResource(R.drawable.dotted_publico);
                header_lvl.setTextColor(getResources().getColor(R.color.colorPublicodark));
                nivel1.setTextColor(getResources().getColor(R.color.colorPublicodark));
                nivel2.setTextColor(getResources().getColor(R.color.colorPublicodark));
                nivel3.setTextColor(getResources().getColor(R.color.colorPublicodark));
                break;
        }
    }

    /**
     * Se asigna a selectedLv un string con el número correspondiente a cada nivel, dependiendo de la selección del usuario
     * Judicante (1) ; Juez (2) ; Magistrado (3)
     * @param view
     */
    public void onCheckBoxClicked(View view){

        switch(view.getId()) {
            case R.id.lvl_1:
                if (nivel1.isPressed()){
                    selectedLv = "1";
                    ScreenTrackerGoogleAnalytics(getApplicationContext(),"Judicante");
                }
                Next();
                break;
            case R.id.lvl_2:
                if (nivel2.isPressed()){
                    selectedLv = "2";
                    ScreenTrackerGoogleAnalytics(getApplicationContext(),"Juez");
                }
                Next();
                break;
            case R.id.lvl_3:
                if (nivel3.isPressed()){
                    selectedLv = "3";
                    ScreenTrackerGoogleAnalytics(getApplicationContext(),"Magistrado");
                }
                Next();
                break;
        }
    }

    /**
     * Intent que contiene la información de Categoría (Categoria), subcategorias (selectedSub) y nivel (selectedLv)
     */
    public void Next(){
        Intent next = new Intent(this,Confirmacion.class);
        next.putStringArrayListExtra("SeleccionSub",selectedSub);
        next.putExtra("SeleccionLev",selectedLv);
        next.putExtra("Categoria",categ);
        startActivity(next);
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
        EventTrackerGoogleAnalytics(this,"Nivel","Info","Info Nivel");
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
        builder = new AlertDialog.Builder(ctw);
        builder.setTitle(Html.fromHtml("<b>Nivel</b>"));
        builder.setMessage("Elige uno de los niveles. Recuerda que Judicante es el nivel con menor complejidad y Magistrado el de mayor.");
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

