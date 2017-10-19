package qantica.com.interrogatorio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;


/**
 * Clase MasterStatistics. El usuario recibe los resultados de acuerdo a las respuestas seleccionadas en MasterGame; además puede compartirlos en redes sociales.
 * @author Sebastián Malagón Pérez
 * Q-antica Ltda.
 * Bogotá, Colombia
 * 24/02/2017
 */
public class MasterStatistics extends AppCompatActivity {

    private ArrayList<String> correctAns;
    private ArrayList<String> selectedAns;
    private LinearLayout header;
    private LinearLayout fallaste;
    private TextView text1;
    private TextView text2;
    private TextView title;
    private Bitmap image;
    private AlertDialog.Builder builder;
    private Toolbar toolbar;
    public boolean backPressed = false;
    private ShareButton shareButton;
    private int results;
    private String message;
    private  ArrayList<String> statement;
    private ArrayList<String> filtered;
    private ArrayList<String> correctArray = new ArrayList<>();
    private ArrayList<String> selectionArray = new ArrayList<>();

    private static final String TWITTER_KEY = "zhuLPYFQ7paMt1Uf7S6CAqd1o";
    private static final String TWITTER_SECRET = "fmkSDSjKqbHoU5fvrDJrNIR480YqWoUL52bnN1U4WZODbY3xDr";
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
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_master_statistics);

        /**
         * Intent con el string de la categoría elegida, un ArrayList (selectedSub) con las subcategorías elegidas
         *  y selectedLv con el nivel elegido.
         *  También con las respuestas correctas (correctAns) y las seleccionadas (seletedAns)
         */
        Intent in = getIntent();
        correctAns = in.getStringArrayListExtra("correctArray");
        selectedAns = in.getStringArrayListExtra("selectionArray");
        statement = in.getStringArrayListExtra("enunciado");
        filtered = in.getStringArrayListExtra("filtered");


        /**
         * Llamado de métodos
         */
        setControls();
        setFont();
        customToolbar();
        firstLaunch();
        results = GetResults(correctAns,selectedAns);
        setTextChoose();
        ValidacionTotalidadRespuestasCorrectas();


        String temp = "";
        for(int i = 0; i<correctAns.size();i++){
            temp+=correctAns.get(i)+selectedAns.get(i);
            temp+='\n';
        }
    }

    /**
     * Relaciona las clases View con su correspondiente id en el XML
     */
    public void setControls(){
        header = (LinearLayout) findViewById(R.id.header_stat);
        fallaste = (LinearLayout) findViewById(R.id.ly_fallaste);
        text1 = (TextView) findViewById(R.id.tv_stat1);
        text2 = (TextView) findViewById(R.id.tv_resultado);
        shareButton = (ShareButton) findViewById(R.id.share_btn);
    }

    /**
     * Coloca la fuente de letra del título del TOOLBAR
     */
    public void setFont(){
        AssetManager am = getAssets();
        Typeface caviarDreams = Typeface.createFromAsset(am,"font/CaviarDreams.ttf");
        title = (TextView) findViewById(R.id.tv_header);
        title.setTypeface(caviarDreams);
        title.setText("Máster");

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
                EventTrackerGoogleAnalytics(getApplicationContext(),"Resultados Master","Atras","Atras Resultados Master");
                Intent intent = new Intent(getApplicationContext(),Intro.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_right_out);
            }
        });
        header.setBackgroundResource(R.drawable.gradient);
    }

    /**
     * Libera el diálogo solo para la primera vez que la aplicación sea corrida
     * El diálogo está personalizado con el estilo MyDialogTheme
     */
    public void firstLaunch(){
        boolean isFirstRunMasterStat = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRunMasterStat", true);
        if (isFirstRunMasterStat){
            ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
            builder = new AlertDialog.Builder(ctw);
            builder.setTitle(Html.fromHtml("<b>Resultado</b>"));
            builder.setMessage("Comparte tu resultado en redes sociales");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRunMasterStat", false)
                    .apply();
        }
    }

    /**
     * Se realiza la comparación entre las respuestas seleccionadas y las respuestas correctas
     * @param correctas
     *          ArrayList con los enunciados de las respuestas correctas
     * @param selecciones
     *          ArrayList con los enunciados de las respuestas seleccionadas
     * @return cont
     *          contador con la cantidad de respuestas correctas
     */
    public int GetResults(ArrayList<String> correctas, ArrayList<String> selecciones){
        int cont = 0;
        for(int j = 0;j<correctas.size();j++){
            if(correctas.get(j).contains(selecciones.get(j))){
                cont+=1;
            }

        }
        return cont;
    }

    /**
     * En message se configura el mensaje que va a aparecer en Twitter
     */
    public void setTextChoose(){

        text2.setText("Máster: " + results + "/" + String.valueOf(correctAns.size()));
        message = "¿Cuánto sabes de derecho? Yo soy Máster: " + String.valueOf(results) + "/" + String.valueOf(correctAns.size()) +
                " ¡Ahora inténtalo! Busca Interrogatorio en";
    }

    public void ValidacionTotalidadRespuestasCorrectas(){
        if (results==correctAns.size()){
            ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
            builder = new AlertDialog.Builder(ctw);
            builder.setTitle(Html.fromHtml("<b>Felicitaciones</b>"));
            builder.setMessage("Has alcanzado la puntuación mas alta. Intenta en el siguiente nivel");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else {
            ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
            builder = new AlertDialog.Builder(ctw);
            builder.setTitle(Html.fromHtml("<b>Por Halloween</b>"));
            builder.setMessage("Podrás visualizar una de las respuestas correctas");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            fallaste.setVisibility(fallaste.VISIBLE);
        }
    }

    /**
     * Se compone el contenido del tweet a compartir. Se coloca en text el mensaje (message)
     * @param view
     *          Por Default
     */
    /**/public void setTweet(View view) throws MalformedURLException {
        URL myUrl = new URL("https://goo.gl/5mJHQJ");
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text(message).url(myUrl);
        builder.show();
    }

    /**
     * Se toma un screenshot, se guarda en image y se borra, después se comparte en Facebook con shareButton
     * @param view
     *          Por Default
     */
    public void shareFacebook(View view) {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        image = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.destroyDrawingCache();

        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
        builder = new AlertDialog.Builder(ctw);
        builder.setTitle("Compartir resultados");
        builder.setMessage("¿Deseas compartir tus resultados en Facebook?");
        builder.setPositiveButton("Compartir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


                SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
                SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
                shareButton.setShareContent(content);
                shareButton.performClick();

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();


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
        EventTrackerGoogleAnalytics(this,"Resultados Master","Info","Info Resultados Master");
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.MyDialogTheme);
        builder = new AlertDialog.Builder(ctw);
        builder.setTitle(Html.fromHtml("<b>Resultado</b>"));
        builder.setMessage("Comparte tu resultado en redes sociales");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Función onClick() del botón "¿Por què fallaste?"
     * @param view
     *        Por Default
     */
    public void fallasteDialog(View view){

        Intent next = new Intent(MasterStatistics.this,Respuestas_Master.class);
        next.putStringArrayListExtra("correctArray",correctArray);
        next.putStringArrayListExtra("selectionArray",selectionArray);
        next.putStringArrayListExtra("enunciado",statement);
        next.putStringArrayListExtra("filtered",filtered);
        next.putStringArrayListExtra("respuestas_correctas",correctAns);
        next.putStringArrayListExtra("respuestas_seleccionadas",selectedAns);
        startActivity(next);
        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
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
