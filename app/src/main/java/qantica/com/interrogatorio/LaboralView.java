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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
/**
 * Clase LaboralView. El usuario puede elegir una o varias subcategorías de la categoría Laboral.
 * @author Sebastián Malagón Pérez
 * Q-antica Ltda.
 * Bogotá, Colombia
 * 24/02/2017
 */
public class LaboralView extends AppCompatActivity {

    private CheckBox individual;
    private CheckBox colectivo;
    private CheckBox seguridad;
    private CheckBox riesgos;

    private TextView title;
    private TextView subheader;

    private LinearLayout ind;
    private LinearLayout col;
    private LinearLayout seg;
    private LinearLayout ries;

    private Toolbar toolbar;

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
        setContentView(R.layout.activity_laboral_view);

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
        individual = (CheckBox) findViewById(R.id.lab_1);
        colectivo = (CheckBox) findViewById(R.id.lab_2);
        seguridad = (CheckBox) findViewById(R.id.lab_3);
        riesgos = (CheckBox) findViewById(R.id.lab_4);

        ind = (LinearLayout) findViewById(R.id.ly_individual);
        col = (LinearLayout) findViewById(R.id.ly_colectivo);
        seg = (LinearLayout) findViewById(R.id.ly_seguridad);
        ries = (LinearLayout) findViewById(R.id.ly_riesgos);

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
        title.setText("Laboral");

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
                EventTrackerGoogleAnalytics(getApplicationContext(),"Laboral","Atras","Atras Laboral");
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
        boolean isFirstRunLabor = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRunLabor", true);
        if (isFirstRunLabor){
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
                    .putBoolean("isFirstRunLabor", false)
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

        if(individual.isChecked()){
            individual.setTextColor(getResources().getColor(R.color.colorLaboraldark));
            ind.setBackground(getResources().getDrawable(R.drawable.individual_laboral_on));
        }else{
            individual.setTextColor(getResources().getColor(R.color.gray));
            ind.setBackground(getResources().getDrawable(R.drawable.individual_laboral_off));
        }

        if(colectivo.isChecked()){
            colectivo.setTextColor(getResources().getColor(R.color.colorLaboraldark));
            col.setBackground(getResources().getDrawable(R.drawable.colectivo_laboral_on));
        }else{
            colectivo.setTextColor(getResources().getColor(R.color.gray));
            col.setBackground(getResources().getDrawable(R.drawable.colectivo_laboral_off));
        }

        if(seguridad.isChecked()){
            seguridad.setTextColor(getResources().getColor(R.color.colorLaboraldark));
            seg.setBackground(getResources().getDrawable(R.drawable.seguridad_social_laboral_on));
        }else{
            seguridad.setTextColor(getResources().getColor(R.color.gray));
            seg.setBackground(getResources().getDrawable(R.drawable.seguridad_social_laboral_off));
        }

        if(riesgos.isChecked()){
            riesgos.setTextColor(getResources().getColor(R.color.colorLaboraldark));
            ries.setBackground(getResources().getDrawable(R.drawable.riesgos_laboral_on));
        }else{
            riesgos.setTextColor(getResources().getColor(R.color.gray));
            ries.setBackground(getResources().getDrawable(R.drawable.riesgos_laboral_off));
        }

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

        if(individual.isChecked()){
            selectedSub.add("INDIVIDUAL");
        }

        if(colectivo.isChecked()){
            selectedSub.add("COLECTIVO");
        }

        if(seguridad.isChecked()){
            selectedSub.add("SEGURIDAD SOCIAL");
        }

        if(riesgos.isChecked()){
            selectedSub.add("RIESGOS LABORALES");
        }

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
        EventTrackerGoogleAnalytics(this,"Laboral","Info","Info Laboral");
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
        //Log.d("Laboral","On Stop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d("Laboral","On Restart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("Laboral","On Start");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d("Laboral","On Destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d("Laboral","On Pause");
    }

}
