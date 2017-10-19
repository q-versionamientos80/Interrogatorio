package qantica.com.interrogatorio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
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

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;
/**
 * Clase Confirmacion. El usuario puede verificar la selección de Categoría, Subcategorías y Nivel.
 * @author Sebastián Malagón Pérez
 * Q-antica Ltda.
 * Bogotá, Colombia
 * 24/02/2017
 */
public class Confirmacion extends AppCompatActivity{

    private LinearLayout header;
    private TextView cat;
    private TextView subcat;
    private TextView level;
    private Button confirmar;
    private TextView ready;
    private TextView categ_conf;
    private TextView subcateg_conf;
    private TextView level_cong;

    private TextView title;
    private Toolbar toolbar;

    private ArrayList<String> selectedSub;
    private String selectedLv;
    private String categ;
    private AlertDialog.Builder builder;
    private LinearLayout inicia;

    private GifImageView gif;
    private static final long GIF_DELAY = 1500;
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
        setContentView(R.layout.activity_confirmacion);

        /**
         * Intent con el string de la categoría elegida, un ArrayList (selectedSub) con las subcategorías elegidas
         *  y selectedLv con el nivel elegido
         */
        Intent in = getIntent();
        selectedSub = in.getStringArrayListExtra("SeleccionSub");
        selectedLv = in.getStringExtra("SeleccionLev");
        categ = in.getStringExtra("Categoria");

        /**
         * Llamado de métodos
         */
        setControls();
        setFont();
        customToolbar();
        firstLaunch();
        setTextSize();
        changeCategFeatures();
        setTextChoose();

    }

    /**
     * Relaciona las clases View con su correspondiente id en el XML
     */
    public void setControls(){
        cat = (TextView) findViewById(R.id.tv_categoria);
        subcat = (TextView) findViewById(R.id.tv_subcategoria);
        level = (TextView) findViewById(R.id.tv_nivel);
        header = (LinearLayout) findViewById(R.id.header_conf);
        gif = (GifImageView) findViewById(R.id.gv_gif);
        inicia = (LinearLayout) findViewById(R.id.inicio_juego);

        ready = (TextView) findViewById(R.id.tv_ready);
        categ_conf = (TextView) findViewById(R.id.tv_categ_conf);
        subcateg_conf = (TextView) findViewById(R.id.tv_subcateg_conf);
        level_cong = (TextView) findViewById(R.id.tv_level_conf);
        confirmar = (Button) findViewById(R.id.btn_confirmar);

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
                EventTrackerGoogleAnalytics(getApplicationContext(),"Confirmacion","Atras","Atras Confirmacion");
                Intent next = new Intent(getApplicationContext(),LevelView.class);
                next.putStringArrayListExtra("SeleccionSub",selectedSub);
                next.putExtra("Categoria",categ);
                startActivity(next);
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_right_out);
            }
        });
    }

    /**
     * Libera el diálogo solo para la primera vez que la aplicación sea corrida
     * El diálogo está personalizado con el estilo MyDialogTheme
     */
    public void firstLaunch(){
        boolean isFirstRunConfir = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRunConfir", true);
        if (isFirstRunConfir){
            ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
            builder = new AlertDialog.Builder(ctw);
            builder.setTitle(Html.fromHtml("<b>¿Qué tan fuerte eres?</b>"));
            builder.setMessage(Html.fromHtml("Verifica la configuración que personalizaste y oprime " +
                    "<b>¡Que empiece el juicio!</b> Utiliza el botón Atrás para ajustar tus cambios."));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRunConfir", false)
                    .apply();
        }
    }

    /**
     * Coloca el tamaño del texto de acuerdo con la densidad de la pantalla
     */
    public void setTextSize(){
        float screenDensity = getResources().getDisplayMetrics().density;

        if(screenDensity==1.5){
            categ_conf.setTextSize(14);
            subcateg_conf.setTextSize(14);
            level_cong.setTextSize(14);
        }
        if(screenDensity==3.0){
            categ_conf.setTextSize(15);
            subcateg_conf.setTextSize(15);
            level_cong.setTextSize(14);
        }

    }

    /**
     * Se configura los colores de los diferentes elementos de la pantalla dependiendo de la Categoría elegida
     */
    public void changeCategFeatures(){
        switch (categ){
            case "CIVIL":
                header.setBackgroundResource(R.drawable.gradiente_civil);
                gif.setBackgroundResource(R.drawable.gif_blue_img);
                inicia.setBackgroundResource(R.drawable.rounded_civil);
                ready.setTextColor(getResources().getColor(R.color.colorCivildark));
                categ_conf.setTextColor(getResources().getColor(R.color.colorCivildark));
                subcateg_conf.setTextColor(getResources().getColor(R.color.colorCivildark));
                level_cong.setTextColor(getResources().getColor(R.color.colorCivildark));
                break;
            case "LABORAL":
                header.setBackgroundResource(R.drawable.gradiente_laboral);
                gif.setBackgroundResource(R.drawable.gif_blue_img);
                inicia.setBackgroundResource(R.drawable.rounded_laboral);
                ready.setTextColor(getResources().getColor(R.color.colorLaboraldark));
                categ_conf.setTextColor(getResources().getColor(R.color.colorLaboraldark));
                subcateg_conf.setTextColor(getResources().getColor(R.color.colorLaboraldark));
                level_cong.setTextColor(getResources().getColor(R.color.colorLaboraldark));
                break;
            case "COMERCIAL":
                header.setBackgroundResource(R.drawable.gradiente_comercial);
                gif.setBackgroundResource(R.drawable.gif_red_img);
                inicia.setBackgroundResource(R.drawable.rounded_comercial);
                ready.setTextColor(getResources().getColor(R.color.colorComercialdark));
                categ_conf.setTextColor(getResources().getColor(R.color.colorComercialdark));
                subcateg_conf.setTextColor(getResources().getColor(R.color.colorComercialdark));
                level_cong.setTextColor(getResources().getColor(R.color.colorComercialdark));
                break;
            case "PENAL":
                header.setBackgroundResource(R.drawable.gradiente_penal);
                gif.setBackgroundResource(R.drawable.gif_red_img);
                inicia.setBackgroundResource(R.drawable.rounded_penal);
                ready.setTextColor(getResources().getColor(R.color.colorPenaldark));
                categ_conf.setTextColor(getResources().getColor(R.color.colorPenaldark));
                subcateg_conf.setTextColor(getResources().getColor(R.color.colorPenaldark));
                level_cong.setTextColor(getResources().getColor(R.color.colorPenaldark));
                break;
            case "PROCESAL":
                header.setBackgroundResource(R.drawable.gradiente_procesal);
                gif.setBackgroundResource(R.drawable.gif_purple_img);
                inicia.setBackgroundResource(R.drawable.rounded_procesal);
                ready.setTextColor(getResources().getColor(R.color.colorProcesaldark));
                categ_conf.setTextColor(getResources().getColor(R.color.colorProcesaldark));
                subcateg_conf.setTextColor(getResources().getColor(R.color.colorProcesaldark));
                level_cong.setTextColor(getResources().getColor(R.color.colorProcesaldark));
                break;
            case "PUBLICO":
                header.setBackgroundResource(R.drawable.gradiente_publico);
                gif.setBackgroundResource(R.drawable.gif_purple_img);
                inicia.setBackgroundResource(R.drawable.rounded_publico);
                ready.setTextColor(getResources().getColor(R.color.colorPublicodark));
                categ_conf.setTextColor(getResources().getColor(R.color.colorPublicodark));
                subcateg_conf.setTextColor(getResources().getColor(R.color.colorPublicodark));
                level_cong.setTextColor(getResources().getColor(R.color.colorPublicodark));
                break;
        }
    }

    /**
     * En subcategorias se concatenan y se separan por comas las diferentes subcategorías elegidas
     * Se colocan los textos de Categoría, Sub-cat y Nivel de acuerdo al contenido de categ, subcategorias y selectedLv
     */
    public void setTextChoose(){
        String subcategorias = "";
        for(int i = 0;i<selectedSub.size();i++){
            if(i<selectedSub.size()-1){
                subcategorias+=selectedSub.get(i);
                subcategorias+=", ";
            }else{
                subcategorias+=selectedSub.get(i);
            }
        }
        cat.setText(categ);
        subcat.setText(subcategorias);
        switch (selectedLv){
            case "1":
                level.setText("JUDICANTE");
                break;
            case "2":
                level.setText("JUEZ");
                break;
            case "3":
                level.setText("MAGISTRADO");
                break;
        }
    }

    /**
     * Reproduce el gif de acuerdo al color asignado por categoria
     * Se inicia un Thread con un tiempo de GIF_DELAY. Posteriormente se pasa a la siguiente Activity
     * Intent next almacena selectedSub, selectedLv y categ
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void confirmar(View view){
        EventTrackerGoogleAnalytics(this,"Confirmacion","Confirmar","Confirmar pressed");

        confirmar.setBackground(getResources().getDrawable(R.color.white));

        switch (categ){
            case "CIVIL":
                gif.setBackgroundResource(R.drawable.gif_azul);
                break;
            case "LABORAL":
                gif.setBackgroundResource(R.drawable.gif_azul);
                break;
            case "COMERCIAL":
                gif.setBackgroundResource(R.drawable.gif_rojo);
                break;
            case "PENAL":
                gif.setBackgroundResource(R.drawable.gif_rojo);
                break;
            case "PROCESAL":
                gif.setBackgroundResource(R.drawable.gif_morado);
                break;
            case "PUBLICO":
                gif.setBackgroundResource(R.drawable.gif_morado);
                break;

        }
        final Thread launchTime = new Thread(){
            public void run(){
                try{

                    sleep(GIF_DELAY);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{

                    Intent next = new Intent(Confirmacion.this,Main_interrogatorio.class);
                    next.putStringArrayListExtra("SeleccionSub",selectedSub);
                    next.putExtra("SeleccionLev",selectedLv);
                    next.putExtra("Categoria",categ);
                    startActivity(next);
                    overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
                }
            }
        };
        launchTime.start();

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
        EventTrackerGoogleAnalytics(this,"Confirmacion","Info","Info Confirmacion");
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
        builder = new AlertDialog.Builder(ctw);
        builder.setTitle(Html.fromHtml("<b>¿Qué tan fuerte eres?</b>"));
        builder.setMessage(Html.fromHtml("Verifica la configuración que personalizaste y oprime " +
                "<b>¡Que empiece el juicio!</b> Utiliza el botón Atrás para ajustar tus cambios."));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
        //Log.d("Confirmacion","On Stop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d("Confirmacion","On Restart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("Confirmacion","On Start");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d("Confirmacion","On Destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d("Confirmacion","On Pause");
    }


}
