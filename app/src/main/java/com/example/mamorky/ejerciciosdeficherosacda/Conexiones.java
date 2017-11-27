package com.example.mamorky.ejerciciosdeficherosacda;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class Conexiones extends AppCompatActivity implements View.OnClickListener{

    EditText direccion,edtNombreF;
    RadioButton radioJava,radioAAHC,radioVolley;
    Button conectar,btnGuardar;
    WebView web;
    TextView tiempo;
    long inicio, fin;
    public static final String JAVA = "Java";
    public static final String AAHC = "AAHC";
    public static final String VOLLEY = "Volley";
    TareaAsincronaJava tareaAsincrona;
    RequestQueue mRequestQueue;
    public static final String TAG = "MyTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conexiones);
        iniciar();
    }

    private void iniciar() {
        direccion = (EditText) findViewById(R.id.direccion);
        radioJava = (RadioButton) findViewById(R.id.rdbJava);
        radioAAHC = (RadioButton) findViewById(R.id.rdbAAHC);
        radioVolley = (RadioButton) findViewById(R.id.rdbVolley);
        conectar = (Button) findViewById(R.id.btnConectar);
        conectar.setOnClickListener(this);
        web = (WebView) findViewById(R.id.web);
        tiempo = (TextView) findViewById(R.id.txvTiempo);
        edtNombreF = (EditText)findViewById(R.id.edtNomFichGuardar);
        btnGuardar = (Button)findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(this);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        mRequestQueue = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
    }

    @Override
    public void onClick(View v) {
        tareaAsincrona = new TareaAsincronaJava(this);
        if(v == conectar){
            if(isNetworkAvailable()) {
                Resultado resultado;
                if(radioJava.isChecked())
                    tareaAsincrona.execute(direccion.getText().toString(),JAVA);
                else if(radioAAHC.isChecked())
                    AAHC();
                else
                    makeRequest();
            }else
                Toast.makeText(this,"No hay conexión a internet",Toast.LENGTH_LONG).show();
        }
        if(v == btnGuardar){
            logLine(web,edtNombreF.getText().toString());
        }
    }

    public class TareaAsincronaJava extends AsyncTask<String, Integer, Resultado > {
        private ProgressDialog progreso;
        private Context context;
        public TareaAsincronaJava(Context context){
            this.context = context;
        }

        protected void onPreExecute() {
            progreso = new ProgressDialog(context);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Conectando . . .");
            progreso.setCancelable(
                    true
            );
            progreso.setOnCancelListener(new DialogInterface.OnCancelListener(){
                public void onCancel(DialogInterface dialog){
                    TareaAsincronaJava.this.cancel(true);
                }
            });
            progreso.show();
        }

        protected Resultado doInBackground(String... cadena) {
            Resultado resultado = new Resultado();
            int i = 1;
            try {
                inicio = System.currentTimeMillis();
                    resultado = conectarJava(cadena[0]);

                publishProgress(i++);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultado;
        }
        protected void onProgressUpdate(Integer... progress) {
            progreso.setMessage("Conectando " + Integer.toString(progress[0]));
        }
        protected void onPostExecute(Resultado resultado) {
            fin = System.currentTimeMillis();
            if (resultado.getCodigo())
                web.loadDataWithBaseURL(null, resultado.getContenido(),"text/html", "UTF-8", null);
            else
                web.loadDataWithBaseURL(null, resultado.getMensaje(),"text/html", "UTF-8", null);
            tiempo.setText("Duración: " + String.valueOf(fin - inicio) + " milisegundos");
            progreso.dismiss();
        }
        protected void onCancelled()
        {
            progreso.dismiss();
        }
    }

    public static Resultado conectarJava(String texto) {
        URL url;
        HttpURLConnection urlConnection = null;
        int respuesta;
        Resultado resultado = new Resultado();
        try {
            url = new URL(texto);
            urlConnection = (HttpURLConnection) url.openConnection();
            respuesta = urlConnection.getResponseCode();
            if (respuesta == HttpURLConnection.HTTP_OK){
                resultado.setCodigo(true);
                resultado.setContenido(leer(urlConnection.getInputStream()));
            }
            else {
                resultado.setCodigo(false);
                resultado.setMensaje("Error en el acceso a la web: " + String.valueOf(respuesta));
            }
        } catch (IOException e) {
            resultado.setCodigo(false);
            resultado.setMensaje("Excepción: " + e.getMessage());
        } finally {
            try {
                if (urlConnection != null)
                    urlConnection.disconnect();
            } catch (Exception e) {
                resultado.setCodigo(false);
                resultado.setMensaje("Excepción: " + e.getMessage());
            }
            return resultado;
        }
    }

    private void AAHC() {
        final String texto = direccion.getText().toString();;
        final ProgressDialog progreso = new ProgressDialog(Conexiones.this);
        inicio = System.currentTimeMillis();
        RestClient.get(texto, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Conectando . . .");
                //progreso.setCancelable(false);
                progreso.setOnCancelListener(new DialogInterface.OnCancelListener(){
                    public void onCancel(DialogInterface dialog){
                        RestClient.cancelRequests(getApplicationContext(), true);
                    }
                });
                progreso.show();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                // called when response HTTP status is "200 OK"
                fin = System.currentTimeMillis();
                progreso.dismiss();
                web.loadDataWithBaseURL(null, response,"text/html", "UTF-8", null);
                tiempo.setText("Duración: " + String.valueOf(fin - inicio) + " milisegundos");
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                fin = System.currentTimeMillis();
                progreso.dismiss();
                web.loadDataWithBaseURL(null, "Error "+ statusCode +" "+response+" "+t.getMessage().toString(),"text/html", "UTF-8", null);
                tiempo.setText("Duración: " + String.valueOf(fin - inicio) + " milisegundos");
            }
        });
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


    public void logLine(WebView view,String nombreF) {
        File ruta_sd = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File f = new File(ruta_sd.getAbsolutePath(), nombreF);
        OutputStreamWriter out;
        FileOutputStream fos;

        try {
            fos = view.getContext().openFileOutput(nombreF, Context.MODE_APPEND);
            out = new OutputStreamWriter(new FileOutputStream(f));
            out.write((new Date()).toString()  + "\n");
            out.close();
            fos.close();
            Toast.makeText(this,"Fichero guardado en "+f.getAbsolutePath(),Toast.LENGTH_LONG).show();
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void makeRequest() {
        final String enlace = direccion.getText().toString();
        // Instantiate the RequestQueue.
        // mRequestQueue = Volley.newRequestQueue(this);
        final ProgressDialog progreso = new ProgressDialog(Conexiones.this);

        progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progreso.setMessage("Conectando . . .");
        //progreso.setCancelable(false);
        progreso.setOnCancelListener(new DialogInterface.OnCancelListener(){
            public void onCancel(DialogInterface dialog){
                mRequestQueue.cancelAll(TAG);
            }
        });
        progreso.show();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, enlace,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fin = System.currentTimeMillis();
                        progreso.dismiss();
                        web.loadDataWithBaseURL(enlace,response,"text/html","utf-8",null);
                        tiempo.setText("Duración: " + String.valueOf(fin - inicio) + " milisegundos");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        fin = System.currentTimeMillis();
                        String mensaje = "Error";
                        if (error instanceof TimeoutError || error instanceof NoConnectionError)
                            mensaje = "Timeout Error: " + error.getMessage();
                        else {
                            NetworkResponse errorResponse = error.networkResponse;
                            if (errorResponse != null && errorResponse.data != null)
                                try {
                                    mensaje = "Error: " + errorResponse.statusCode + " " + "\n" + new
                                            String(errorResponse.data, "UTF-8");
                                    Log.e("Error", mensaje);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                    mensaje = "Error sin informacion";
                                }
                        }
                        web.loadDataWithBaseURL(null,mensaje,"text/html","UTF-8",null);
                        tiempo.setText("Duración: " + String.valueOf(fin - inicio) + " milisegundos");
                        progreso.dismiss();
                    }
                });
        // Set the tag on the request.
        stringRequest.setTag(TAG);
        // Set retry policy
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 1, 1));
        // Add the request to the RequestQueue.
        mRequestQueue.add(stringRequest);
    }

    private static String leer(InputStream entrada) throws IOException{
        BufferedReader in;
        String linea;
        StringBuilder miCadena = new StringBuilder();
        in = new BufferedReader(new InputStreamReader(entrada), 32000);
        while ((linea = in.readLine()) != null)
            miCadena.append(linea);
        //miCadena.append(linea).append('\n');
        in.close();
        return miCadena.toString();
    }
}
