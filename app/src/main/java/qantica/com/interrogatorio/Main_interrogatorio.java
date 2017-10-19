package qantica.com.interrogatorio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

/**
 * Clase Interrogatorio. Actividad principal, el usuario responde las preguntas de acuerdo a los filtros seleccionados
 * @author Sebastián Malagón Pérez
 * Q-antica Ltda.
 * Bogotá, Colombia
 * 24/02/2017
 */

public class Main_interrogatorio extends AppCompatActivity {


    private TextView e;
    private Button r1;
    private Button r2;
    private Button r3;
    private Button r4;

    private TextView title;
    private Toolbar toolbar;

    private int numPreg = 0;
    private int numSubC = 0;
    public boolean backPressed = false;
    private ArrayList<String> filtered;


    private ArrayList<String> cList = new ArrayList<String>();
    private ArrayList<String> subList = new ArrayList<String>();
    private ArrayList<String> eList = new ArrayList<String>();
    private ArrayList<String> r1List = new ArrayList<String>();
    private ArrayList<String> r2List = new ArrayList<String>();
    private ArrayList<String> r3List = new ArrayList<String>();
    private ArrayList<String> r4List = new ArrayList<String>();
    private ArrayList<String> levelList = new ArrayList<String>();


    private String CorrectAnswer;
    private String ChoseAnswer;
    private ArrayList<String> arr = new ArrayList<>();
    private ArrayList<String> correctArray = new ArrayList<>();
    private ArrayList<String> selectionArray = new ArrayList<>();

    private Intent in;
    private ArrayList<String> selectedSub;
    private String selectedLv;
    private String categ;
    private LinearLayout header;
    private AlertDialog.Builder builder;
    private static final String ANALYTICS_ID = "UA-92126687-1";

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
        setContentView(R.layout.activity_main_interrogatorio);

        /**
         * Intent con el string de la categoría elegida, un ArrayList (selectedSub) con las subcategorías y selectedLv con el nivel.
         * Se remueven los espacios en blanco (" ")
         */
        in = getIntent();
        selectedSub = in.getStringArrayListExtra("SeleccionSub");
        selectedLv = in.getStringExtra("SeleccionLev");
        categ = in.getStringExtra("Categoria");
        selectedSub.removeAll(Arrays.asList(null,""));

        /**
         * Llamado de métodos
         */
        setControls();
        setFont();
        customToolbar();
        firstLaunch();
        ReadExcel();
        changeCategFeatures();
        randomSort(filter(selectedSub,selectedLv,categ));
        printText();
    }

    /**
     * Relaciona las clases View con su correspondiente id en el XML
     */
    public void setControls(){
        e = (TextView) findViewById(R.id.tv_enunciado);
        r1 = (Button) findViewById(R.id.b_resp1);
        r2 = (Button) findViewById(R.id.b_resp2);
        r3 = (Button) findViewById(R.id.b_resp3);
        r4 = (Button) findViewById(R.id.b_resp4);
        header = (LinearLayout) findViewById(R.id.header_q);
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
                ContextThemeWrapper ctw = new ContextThemeWrapper(Main_interrogatorio.this, R.style.MyDialogTheme);
                builder = new AlertDialog.Builder(ctw);
                builder.setMessage("¿Quieres abandonar el interrogatorio?");
                builder.setPositiveButton("ABANDONAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventTrackerGoogleAnalytics(getApplicationContext(),"Main Amateur","Atras","Atras Main Amateur");
                        Intent intent = new Intent(getApplicationContext(),Intro.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_right_out);
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
        });
    }
    /**
     * Libera el diálogo solo para la primera vez que la aplicación sea corrida
     * El diálogo está personalizado con el estilo MyDialogTheme
     */
    public void firstLaunch(){
        boolean isFirstRunMain = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRunMain", true);
        if (isFirstRunMain){
            ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
            builder = new AlertDialog.Builder(ctw);
            builder.setTitle(Html.fromHtml("<b>¡Ya inició el juicio!</b>"));
            builder.setMessage("Elige sólo una respuesta. Una vez que selecciones no podrás volver");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRunMain", false)
                    .apply();
        }
    }
    /**
     * Lee, celda por celda, la hoja de càlculo de Excel, guardada en formato .xls en la carpeta Assets
     * cList almacena la categoria
     * subList almacena la subcategoría
     * eList almacena el enunciado
     * r1List, r2List, r3List, r4List almacenan las respuestas, respectivamente
     * levelList almacena el nivel
     * Cada ArrayList almacena el número total de preguntas, es decir, 1350.
     */
    public void ReadExcel(){
        AssetManager am = getAssets();
        InputStream is = null;
        try {
            is = am.open("consolidado.xls");
            WorkbookSettings workbookSettings = new WorkbookSettings();
            workbookSettings.setEncoding( "Cp1252" );
            Workbook wb =  Workbook.getWorkbook(is, workbookSettings );
            Sheet sh = wb.getSheet(0);
            int row = sh.getRows();
            int col = sh.getColumns();
            String temp;
            for(int i = 0;i<row;i++){
                for(int j = 0;j<col;j++){
                    Cell c = sh.getCell(j,i);
                    temp = c.getContents();
                    if(j == 0) cList.add(i,temp);
                    if(j == 1) subList.add(i,temp);
                    if(j == 2) eList.add(i,temp);
                    if(j == 3) r1List.add(i,temp);
                    if(j == 4) r2List.add(i,temp);
                    if(j == 5) r3List.add(i,temp);
                    if(j == 6) r4List.add(i,temp);
                    if(j == 7) levelList.add(i,temp);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }

    }
    /**
     * Se hace un filtro de acuerdo a la Categoría, Subcategorías y Nivel elegidos y se almacenan las posiciones de las preguntas que cumplen
     *      con este filtro
     * Por cada subcategoría se suma 1 a numSubC, que servirá para el método randomSort()
     * El identificador que separa cada subcategoría en findPos es un /
     * @param sub ArrayList con el nombre de las subcategorías elegidas
     * @param lv String con el nivel elegido
     * @param cat ArrayList con el nombre de la categoría elegida
     * @return findPos
     *          ArrayList con las posiciones de las preguntas que cumplen el filtro
     */
    public ArrayList<String> filter(ArrayList<String> sub, String lv, String cat){

        ArrayList<String> findPos = new ArrayList<>();

        for(int i = 0; i<sub.size(); i++){
            numSubC+=1;
            for(int j = 0;j<subList.size();j++){
                if(subList.get(j).contains(sub.get(i)) && cList.get(j).contains(cat) && levelList.get(j).contains(lv)){
                    findPos.add(String.valueOf(j));
                }
            }
            findPos.add(String.valueOf('/'));
        }
        return findPos;
    }
    /**
     * Se agrega a filtered cada posición de findPos. Cuando encuentra el "/" hace una mezcla aleatoria, esto garantiza que las preguntas por cada
     *      juego siempre serán aleatorias
     * Si sólo se seleccionó 1 subcategoría se agregan 10 preguntas. Si se seleccionó más de una, se agregan 5 preguntas por subcategoría
     * Se agrega un espacio en blanco al final que servirá en printText()
     * @param a ArrayList con las posiciones de las preguntas que cumplen con el filtro
     */
    public void randomSort(ArrayList<String> a){
        ArrayList<String> aux = new ArrayList<>();
        filtered = new ArrayList<>();
        for(int i = 0;i<a.size();i++){
            if(a.get(i).equals("/")){
                Collections.shuffle(aux, new Random());

                if(numSubC<2){
                    for(int j = 0;j<5;j++){
                        filtered.add(aux.get(j));
                    }
                }else{
                    for(int j = 0;j<3;j++){
                        filtered.add(aux.get(j));
                    }
                }
                aux.clear();
            }else{
                aux.add(a.get(i));
            }
        }
        filtered.add("");
    }
    /**
     * Coloca los textos de las respuestas y el enunciado en los botones y la vista de texto, respectivamente.
     * En arr se agregan las respuestas y después se mezclan de forma aleatoria, siendo que r4List tiene la respuesta correcta siempre
     * Cuando termina, Intent next envía correctArray con las respuestas correctas, y selectionArray con las respuestas seleccionadas.
     */
    public void printText(){

        if(numPreg<filtered.size()-1) {
            arr.add(r1List.get(Integer.parseInt(filtered.get(numPreg))));
            arr.add(r2List.get(Integer.parseInt(filtered.get(numPreg))));
            arr.add(r3List.get(Integer.parseInt(filtered.get(numPreg))));
            arr.add(r4List.get(Integer.parseInt(filtered.get(numPreg))));
            CorrectAnswer = r4List.get(Integer.parseInt(filtered.get(numPreg)));

            Collections.shuffle(arr, new Random());

            e.setText(eList.get(Integer.parseInt(filtered.get(numPreg))));


            //Toast.makeText(this, eList.get(Integer.parseInt(filtered.get(numPreg))), Toast.LENGTH_LONG).show();
           /* Toast.makeText(this, r1List.get(Integer.parseInt(filtered.get(numPreg))), Toast.LENGTH_LONG).show();
            Toast.makeText(this, r2List.get(Integer.parseInt(filtered.get(numPreg))), Toast.LENGTH_LONG).show();
            Toast.makeText(this, r3List.get(Integer.parseInt(filtered.get(numPreg))), Toast.LENGTH_LONG).show();
            Toast.makeText(this, r4List.get(Integer.parseInt(filtered.get(numPreg))), Toast.LENGTH_LONG).show();*/
            //Log.e("Integorratorio",eList.get(Integer.parseInt(filtered.get(numPreg))));
            r1.setText(arr.get(0));
            r2.setText(arr.get(1));
            r3.setText(arr.get(2));
            r4.setText(arr.get(3));

            numPreg++;
        }else{

            Intent next = new Intent(this,Statistics.class);
            next.putStringArrayListExtra("correctArray",correctArray);
            next.putStringArrayListExtra("selectionArray",selectionArray);
            next.putStringArrayListExtra("SeleccionSub",selectedSub);
            next.putExtra("SeleccionLev",selectedLv);
            next.putExtra("Categoria",categ);
            next.putStringArrayListExtra("filtered",filtered);
            next.putStringArrayListExtra("enunciado",eList);
           /* next.putStringArrayListExtra("respuesta1",r1List);
            next.putStringArrayListExtra("respuesta2",r2List);
            next.putStringArrayListExtra("respuesta3",r3List);
            next.putStringArrayListExtra("respuesta4",r4List);*/
            startActivity(next);
            overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
        }
    }

    /**
     * Función onClick() de los checkbox.
     * Dependiendo de la respuesta seleccionada, la guarda y la imprime en pantalla.
     * correctArray repuestas correctas
     * @param view
     *      Por Default
     */
    public void SetAnswer(View view){

        if(numPreg<filtered.size()) {

            switch(view.getId()) {
                case R.id.b_resp1:
                    if (r1.isPressed()){
                        ChoseAnswer = arr.get(0);
                        correctArray.add(CorrectAnswer);
                        selectionArray.add(ChoseAnswer);
                        arr.clear();
                        printText();
                    }
                    break;

                case R.id.b_resp2:
                    if (r2.isPressed()){
                        ChoseAnswer = arr.get(1);
                        correctArray.add(CorrectAnswer);
                        selectionArray.add(ChoseAnswer);
                        arr.clear();
                        printText();
                    }
                    break;

                case R.id.b_resp3:
                    if (r3.isPressed()){
                        ChoseAnswer = arr.get(2) ;
                        correctArray.add(CorrectAnswer);
                        selectionArray.add(ChoseAnswer);
                        arr.clear();
                        printText();
                    }
                    break;

                case R.id.b_resp4:
                    if (r4.isPressed()){
                        ChoseAnswer = arr.get(3);
                        correctArray.add(CorrectAnswer);
                        selectionArray.add(ChoseAnswer);
                        arr.clear();
                        printText();
                    }
                    break;
            }
            /*for (int i = 0; i < correctArray.size(); i++){
                //correctArray.get(0);
                //Toast.makeText(this, correctArray.get(i), Toast.LENGTH_SHORT).show();

                Log.e("Integorratorio",correctArray.get(i));
                Log.e("Integorratorio",selectionArray.get(i));

            }*/
        }
    }

    /**
     * Se configura los colores de los diferentes elementos de la pantalla dependiendo de la Categoría elegida
     */
    public void changeCategFeatures(){
        switch (categ){
            case "CIVIL":
                header.setBackgroundResource(R.drawable.gradiente_civil);
                e.setTextColor(getResources().getColor(R.color.colorCivildark));
                r1.setTextColor(getResources().getColor(R.color.colorCivildark));
                r2.setTextColor(getResources().getColor(R.color.colorCivildark));
                r3.setTextColor(getResources().getColor(R.color.colorCivildark));
                r4.setTextColor(getResources().getColor(R.color.colorCivildark));
                break;
            case "LABORAL":
                header.setBackgroundResource(R.drawable.gradiente_laboral);
                e.setTextColor(getResources().getColor(R.color.colorCivildark));
                r1.setTextColor(getResources().getColor(R.color.colorCivildark));
                r2.setTextColor(getResources().getColor(R.color.colorCivildark));
                r3.setTextColor(getResources().getColor(R.color.colorCivildark));
                r4.setTextColor(getResources().getColor(R.color.colorCivildark));
                break;
            case "COMERCIAL":
                header.setBackgroundResource(R.drawable.gradiente_comercial);
                e.setTextColor(getResources().getColor(R.color.colorPenaldark));
                r1.setTextColor(getResources().getColor(R.color.colorPenaldark));
                r2.setTextColor(getResources().getColor(R.color.colorPenaldark));
                r3.setTextColor(getResources().getColor(R.color.colorPenaldark));
                r4.setTextColor(getResources().getColor(R.color.colorPenaldark));
                break;
            case "PENAL":
                header.setBackgroundResource(R.drawable.gradiente_penal);
                e.setTextColor(getResources().getColor(R.color.colorPenaldark));
                r1.setTextColor(getResources().getColor(R.color.colorPenaldark));
                r2.setTextColor(getResources().getColor(R.color.colorPenaldark));
                r3.setTextColor(getResources().getColor(R.color.colorPenaldark));
                r4.setTextColor(getResources().getColor(R.color.colorPenaldark));
                break;
            case "PROCESAL":
                header.setBackgroundResource(R.drawable.gradiente_procesal);
                e.setTextColor(getResources().getColor(R.color.colorProcesaldark));
                r1.setTextColor(getResources().getColor(R.color.colorProcesaldark));
                r2.setTextColor(getResources().getColor(R.color.colorProcesaldark));
                r3.setTextColor(getResources().getColor(R.color.colorProcesaldark));
                r4.setTextColor(getResources().getColor(R.color.colorProcesaldark));
                break;
            case "PUBLICO":
                header.setBackgroundResource(R.drawable.gradiente_publico);
                e.setTextColor(getResources().getColor(R.color.colorPublicodark));
                r1.setTextColor(getResources().getColor(R.color.colorPublicodark));
                r2.setTextColor(getResources().getColor(R.color.colorPublicodark));
                r3.setTextColor(getResources().getColor(R.color.colorPublicodark));
                r4.setTextColor(getResources().getColor(R.color.colorPublicodark));
                break;
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
        EventTrackerGoogleAnalytics(getApplicationContext(),"Main Amateur","Info","Info Main Amateur");
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
        builder = new AlertDialog.Builder(ctw);
        builder.setTitle(Html.fromHtml("<b>¡Ya inició el juicio!</b>"));
        builder.setMessage("Elige sólo una respuesta. Una vez que selecciones no podrás volver");
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
        //Log.d("Interr","On Stop");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d("Interr","On Restart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("Interr","On Start");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d("Interr","On Pause");
    }

}
