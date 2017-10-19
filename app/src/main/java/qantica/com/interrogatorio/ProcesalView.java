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
public class ProcesalView extends AppCompatActivity {

    private CheckBox civil_general;
    private CheckBox civil_especial;
    private CheckBox constitucional;
    private CheckBox administrativo;
    private CheckBox laboral;
    private CheckBox penal;
    private CheckBox probatorio;

    private TextView title;
    private TextView subheader;

    private LinearLayout civi_gene;
    private LinearLayout civi_espe;
    private LinearLayout consti;
    private LinearLayout admin;
    private LinearLayout labo;
    private LinearLayout pen;
    private LinearLayout proba;

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
        setContentView(R.layout.activity_procesal_view);

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
        civil_general = (CheckBox) findViewById(R.id.pro_1);
        civil_especial = (CheckBox) findViewById(R.id.pro_2);
        //laboral = (CheckBox) findViewById(R.id.pro_3);
        //penal = (CheckBox) findViewById(R.id.pro_4);
        //probatorio = (CheckBox) findViewById(R.id.pro_5);
        //constitucional=(CheckBox) findViewById(R.id.pro_6);
        //administrativo =(CheckBox) findViewById(R.id.pro_7);

        civi_gene = (LinearLayout) findViewById(R.id.ly_civil_general_procesal);
        civi_espe = (LinearLayout) findViewById(R.id.ly_civil_especial_procesal);
        //labo = (LinearLayout) findViewById(R.id.ly_laboral_procesal);
        //pen = (LinearLayout) findViewById(R.id.ly_penal_procesal);
        //proba = (LinearLayout) findViewById(R.id.ly_probatorio_procesal);
        //consti = (LinearLayout) findViewById(R.id.ly_constitucional_procesal);
        //admin = (LinearLayout) findViewById(R.id.ly_administrativo_procesal);




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
        title.setText("Procesal");
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
                EventTrackerGoogleAnalytics(getApplicationContext(),"Procesal","Atras","Atras Procesal");
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
        boolean isFirstRunCivil = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRunProcesal", true);
        if (isFirstRunCivil){
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
                    .putBoolean("isFirstRunProcesal", false)
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
        if(civil_general.isChecked()){
            civil_general.setTextColor(getResources().getColor(R.color.colorProcesaldark));
            civi_gene.setBackground(getResources().getDrawable(R.drawable.civil_general_procesal_on));
        }else{
            civil_general.setTextColor(getResources().getColor(R.color.gray));
            civi_gene.setBackground(getResources().getDrawable(R.drawable.civil_general_procesal_off));
        }

        if(civil_especial.isChecked()){
            civil_especial.setTextColor(getResources().getColor(R.color.colorProcesaldark));
            civi_espe.setBackground(getResources().getDrawable(R.drawable.civil_especial_procesal_on));
        }else{
            civil_especial.setTextColor(getResources().getColor(R.color.gray));
            civi_espe.setBackground(getResources().getDrawable(R.drawable.civil_especial_procesal_off));
        }
        /*if(laboral.isChecked()){
            laboral.setTextColor(getResources().getColor(R.color.colorProcesaldark));
            labo.setBackground(getResources().getDrawable(R.drawable.laboral_procesal_on));
        }else{
            laboral.setTextColor(getResources().getColor(R.color.gray));
            labo.setBackground(getResources().getDrawable(R.drawable.laboral_procesal_off));
        }*/
        /*if(penal.isChecked()){
            penal.setTextColor(getResources().getColor(R.color.colorProcesaldark));
            pen.setBackground(getResources().getDrawable(R.drawable.penal_procesal_on));
        }else{
            penal.setTextColor(getResources().getColor(R.color.gray));
            pen.setBackground(getResources().getDrawable(R.drawable.penal_procesal_off));
        }*/
        /*if(probatorio.isChecked()){
            probatorio.setTextColor(getResources().getColor(R.color.colorProcesaldark));
            proba.setBackground(getResources().getDrawable(R.drawable.probatorio_procesal_on));
        }else{
            probatorio.setTextColor(getResources().getColor(R.color.gray));
            proba.setBackground(getResources().getDrawable(R.drawable.probatorio_procesal_off));
        }*/
        /*if(constitucional.isChecked()){
            constitucional.setTextColor(getResources().getColor(R.color.colorProcesaldark));
            consti.setBackground(getResources().getDrawable(R.drawable.constitucional_procesal_on));
        }else{
            constitucional.setTextColor(getResources().getColor(R.color.gray));
            consti.setBackground(getResources().getDrawable(R.drawable.constitucional_procesal_off));
        }*/
       /* if(administrativo.isChecked()){
            administrativo.setTextColor(getResources().getColor(R.color.colorProcesaldark));
            admin.setBackground(getResources().getDrawable(R.drawable.administrativo_procesal_on));
        }else{
            administrativo.setTextColor(getResources().getColor(R.color.gray));
            admin.setBackground(getResources().getDrawable(R.drawable.administrativo_procesal_off));
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

        if(civil_general.isChecked()){
            selectedSub.add("CIVIL GENERAL");
        }

        if(civil_especial.isChecked()){
            selectedSub.add("CIVIL ESPECIAL");
        }

       /* if(constitucional.isChecked()){
            selectedSub.add("LABORAL");
        }

        if(administrativo.isChecked()){
            selectedSub.add("PROBATORIO");
        }

        if(laboral.isChecked()){
            selectedSub.add("CONSTITUCIONAL");
        }
        if(penal.isChecked()){
            selectedSub.add("ADMINISTRATIVO");
        }
        if(probatorio.isChecked()){
            selectedSub.add("OBLIGACIONES");
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
        EventTrackerGoogleAnalytics(this,"Procesal","Info","Info Procesal");
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
        //Log.d("Civil","On Stop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d("Civil","On Restart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("Civil","On Start");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d("Civil","On Destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d("Civil","On Pause");
    }
}