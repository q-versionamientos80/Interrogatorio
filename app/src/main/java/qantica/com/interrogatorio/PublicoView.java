package qantica.com.interrogatorio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

/**
 * Clase CivilView. El usuario puede elegir una o varias subcategorías de la categoría Civil.
 * @author Sebastián Malagón Pérez
 * Q-antica Ltda.
 * Bogotá, Colombia
 * 24/02/2017
 */
public class PublicoView extends AppCompatActivity {

    private CheckBox constitucional_colombiano;
    private CheckBox constitucional_general;
    private CheckBox DDHH;
    private CheckBox contratacion_estatal;
    private CheckBox administrativo;

    private TextView title;
    private TextView subheader;

    private LinearLayout consti;
    private LinearLayout constigene;
    private LinearLayout ddhh;
    private LinearLayout contra;
    private LinearLayout admin;

    private Toolbar toolbar;


    private FloatingActionButton fab;
    private AlertDialog.Builder builder;
    private Intent intent;
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
        setContentView(R.layout.activity_publico_view);

        /**
         * Llamado de métodos
         */
        setControls();
        setFont();
        customToolbar();
        firstLaunch();
        setTextSize();

        /**
         * Intent con el string de la categoría elegida
         */
        intent = getIntent();
        categ = (String) intent.getSerializableExtra("Categoria");
    }

    /**
     * Relaciona las clases View con su correspondiente id en el XML
     */
    public void setControls(){

        //se desactiva el correspondiente el id del xml con la clase, se debe habilitar la siguiente línea
        //constitucional_general = (CheckBox) findViewById(R.id.publi_1);

        constitucional_colombiano = (CheckBox) findViewById(R.id.publi_2);
        DDHH = (CheckBox) findViewById(R.id.publi_3);

        //se desactiva el correspondiente el id del xml con la clase, se debe habilitar la siguiente línea
        //administrativo = (CheckBox) findViewById(R.id.publi_4);

        //se desactiva el correspondiente el id del xml con la clase, se debe habilitar la siguiente línea
        //contratacion_estatal = (CheckBox) findViewById(R.id.publi_5);

        //se desactiva el correspondiente el id del xml con la clase, se debe habilitar la siguiente línea
        //constigene = (LinearLayout) findViewById(R.id.ly_constitucional_general_publico);

        consti = (LinearLayout) findViewById(R.id.ly_constitucional_colombiano_publico);
        ddhh   = (LinearLayout) findViewById(R.id.ly_ddhh);

        //se desactiva el correspondiente el id del xml con la clase, se debe habilitar la siguiente línea
        //admin = (LinearLayout) findViewById(R.id.ly_administrativo);

        //se desactiva el correspondiente el id del xml con la clase, se debe habilitar la siguiente línea
        //contra = (LinearLayout) findViewById(R.id.ly_contratacion_estatal);


        fab = (FloatingActionButton) findViewById(R.id.fab_next);
        subheader = (TextView) findViewById(R.id.tv_subheader);
    }

    /**
     * Coloca la fuente de letra del título del TOOLBAR
     */
    public void setFont(){
        AssetManager am = getAssets();
        Typeface caviarDreams = Typeface.createFromAsset(am,"font/CaviarDreams.ttf");
        title = (TextView) findViewById(R.id.tv_header);
        title.setTypeface(caviarDreams);
        title.setText("Público");
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
                EventTrackerGoogleAnalytics(getApplicationContext(),"Publico","Atras","Atras Publico");
                Intent next = new Intent(getApplicationContext(),Categoria.class);
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
        boolean isFirstRunPublico = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRunPublico", true);
        if (isFirstRunPublico){
            ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
            builder = new AlertDialog.Builder(ctw);
            builder.setTitle(Html.fromHtml("<b>Subcategorías</b>"));
            builder.setMessage("Selecciona una o varias sub-categorías y avanza con el botón en la parte inferior");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRunPublico", false)
                    .apply();
        }
    }

    /**
     * Coloca el tamaño del texto de acuerdo con la densidad de la pantalla
     */
    public void setTextSize(){
        float screenDensity = getResources().getDisplayMetrics().density;

        if(screenDensity==1.5){
            subheader.setTextSize(14);
        }
        if(screenDensity==3.0){
            subheader.setTextSize(15);
        }

    }

    /**
     * Si el CheckBox es seleccionado cambia el color de la letra y el ícono correspondiente.
     * De lo contrario la letra y el ícono correspodiente permanecen grises
     * @param v
     *       Por Default
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onCheckBoxClicked(View v){
       /* if(constitucional_general.isChecked()){
            constitucional_general.setTextColor(getResources().getColor(R.color.colorPublicodark));
            constigene.setBackground(getResources().getDrawable(R.drawable.constitucional_general_publico_on));
        }else{
            constitucional_general.setTextColor(getResources().getColor(R.color.gray));
            constigene.setBackground(getResources().getDrawable(R.drawable.constitucional_general_publico_off));
        }*/
        if(constitucional_colombiano.isChecked()){
            constitucional_colombiano.setTextColor(getResources().getColor(R.color.colorPublicodark));
            consti.setBackground(getResources().getDrawable(R.drawable.constitucional_colombiano_publico_on));
        }else{
            constitucional_colombiano.setTextColor(getResources().getColor(R.color.gray));
            consti.setBackground(getResources().getDrawable(R.drawable.constitucional_colombiano_publico_off));
        }
        if(DDHH.isChecked()){
            DDHH.setTextColor(getResources().getColor(R.color.colorPublicodark));
           ddhh.setBackground(getResources().getDrawable(R.drawable.ddhh_publico_on));
        }else{
            DDHH.setTextColor(getResources().getColor(R.color.gray));
            ddhh.setBackground(getResources().getDrawable(R.drawable.ddhh_publico_off));
        }

        /*if(contratacion_estatal.isChecked()){
            contratacion_estatal.setTextColor(getResources().getColor(R.color.colorPublicodark));
            contra.setBackground(getResources().getDrawable(R.drawable.contratacion_estatal_publico_on));
        }else{
            contratacion_estatal.setTextColor(getResources().getColor(R.color.gray));
            contra.setBackground(getResources().getDrawable(R.drawable.contratacion_estatal_publico_off));
        }*/
        /*if(administrativo.isChecked()){
            administrativo.setTextColor(getResources().getColor(R.color.colorPublicodark));
            admin.setBackground(getResources().getDrawable(R.drawable.administrativo_publico_on));
        }else{
            administrativo.setTextColor(getResources().getColor(R.color.gray));
            admin.setBackground(getResources().getDrawable(R.drawable.administrativo_publico_off));
        }*/
    }

    /**
     * Función onClick del Float Action Button.
     * Agrega a selectedSub los strings de las subcategorías seleccionadas en onCheckBoxClicked(View v)
     *  y las envía a la siguiente Activity (LevelView). También envía el string de la categoría seleccionada en categ
     * Si no hay selección de subcategorías, es decir, selectedSub está vacio, libera un Toast advirtiendo
     * @param view
     *       Por Default
     */
    public void Sig(View view){

        selectedSub = new ArrayList<>();
        selectedSub.add("");
        /*
        if(constitucional_general.isChecked()){
            selectedSub.add("CONSTITUCIONAL GENERAL");
        }*/
        if(constitucional_colombiano.isChecked()){
            selectedSub.add("CONSTITUCIONAL COLOMBIANO");
        }
        if(DDHH.isChecked()){
            selectedSub.add("DERECHOS HUMANOS");
        }
        /*if(contratacion_estatal.isChecked()){
            selectedSub.add("CONTRATACION ESTATAL");
        }*/
        /*if(administrativo.isChecked()){
            selectedSub.add("ADMINISTRATIVO");
        }*/

        if(selectedSub.size()>1){
            Intent next = new Intent(this,LevelView.class);
            next.putStringArrayListExtra("SeleccionSub",selectedSub);
            next.putExtra("Categoria",categ);
            startActivity(next);
            overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
        }else{
            Toast.makeText(this,"Selecciona una o más subcategorías",Toast.LENGTH_SHORT).show();
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
        EventTrackerGoogleAnalytics(this,"Publico","Info","Info Publico");
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
        builder = new AlertDialog.Builder(ctw);
        builder.setTitle(Html.fromHtml("<b>Subcategorías</b>"));
        builder.setMessage("Selecciona una o varias sub-categorías y avanza con el botón en la parte inferior");
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
        //Log.d("Publico","On Stop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d("Publico","On Restart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("Publico","On Start");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d("Publico","On Destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d("Publico","On Pause");
    }
}
