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
 * Clase ComercialView. El usuario puede elegir una o varias subcategorías de la categoría Comercial.
 * @author Sebastián Malagón Pérez
 * Q-antica Ltda.
 * Bogotá, Colombia
 * 24/02/2017
 */
public class ComercialView extends AppCompatActivity {

    private CheckBox contratos;
    private CheckBox general;
    private CheckBox sociedades;
    private CheckBox titulos;

    private TextView title;
    private TextView subheader;

    private LinearLayout contr;
    private LinearLayout gene;
    private LinearLayout soci;
    private LinearLayout titu;

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
        setContentView(R.layout.activity_comercial_view);

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
        contratos = (CheckBox) findViewById(R.id.com_1);
        general = (CheckBox) findViewById(R.id.com_2);
        sociedades = (CheckBox) findViewById(R.id.com_3);
        titulos = (CheckBox) findViewById(R.id.com_4);

        contr = (LinearLayout) findViewById(R.id.ly_contratos_comercial);
        gene = (LinearLayout) findViewById(R.id.ly_general_comercial);
        soci = (LinearLayout) findViewById(R.id.ly_sociedades);
        titu = (LinearLayout) findViewById(R.id.ly_titulos);

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
        title.setText("Comercial");

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
                EventTrackerGoogleAnalytics(getApplicationContext(),"Comercial","Atras","Atras Comercial");
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
        boolean isFirstRunComer = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRunComer", true);
        if (isFirstRunComer){
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
                    .putBoolean("isFirstRunComer", false)
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

        if(contratos.isChecked()){
            contratos.setTextColor(getResources().getColor(R.color.colorComercialdark));
            contr.setBackground(getResources().getDrawable(R.drawable.contratos_comercial_on));
        }else{
            contratos.setTextColor(getResources().getColor(R.color.gray));
            contr.setBackground(getResources().getDrawable(R.drawable.contratos_comercial_off));
        }

        if(general.isChecked()){
            general.setTextColor(getResources().getColor(R.color.colorComercialdark));
            gene.setBackground(getResources().getDrawable(R.drawable.general_comercial_on));
        }else{
            general.setTextColor(getResources().getColor(R.color.gray));
            gene.setBackground(getResources().getDrawable(R.drawable.general_comercial_off));
        }

        if(sociedades.isChecked()){
            sociedades.setTextColor(getResources().getColor(R.color.colorComercialdark));
            soci.setBackground(getResources().getDrawable(R.drawable.sociedades_comercial_on));
        }else{
            sociedades.setTextColor(getResources().getColor(R.color.gray));
            soci.setBackground(getResources().getDrawable(R.drawable.sociedades_comercial_off));
        }

        if(titulos.isChecked()){
            titulos.setTextColor(getResources().getColor(R.color.colorComercialdark));
            titu.setBackground(getResources().getDrawable(R.drawable.titulos_valores_comercial_on));
        }else{
            titulos.setTextColor(getResources().getColor(R.color.gray));
            titu.setBackground(getResources().getDrawable(R.drawable.titulos_valores_comercial_off));
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

        if(contratos.isChecked()){
            selectedSub.add("CONTRATOS");
        }

        if(general.isChecked()){
            selectedSub.add("GENERAL");
        }

        if(sociedades.isChecked()){
            selectedSub.add("SOCIEDADES");
        }

        if(titulos.isChecked()){
            selectedSub.add("TITULOS VALORES");
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
        EventTrackerGoogleAnalytics(this,"Comercial","Info","Info Comercial");
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
        //Log.d("Comercial","On Stop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d("Comercial","On Restart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("Comercial","On Start");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d("Comercial","On Destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d("Comercial","On Pause");
    }

}

