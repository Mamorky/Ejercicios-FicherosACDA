package com.example.mamorky.ejerciciosdeficherosacda;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.icu.text.DecimalFormat;
import android.preference.PreferenceActivity;
import android.renderscript.Double2;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

/**
 * @author mamorky
 * @version 1.0
 * */

public class ConversorPrincipal extends AppCompatActivity implements View.OnClickListener{
    Button convertirBtn;
    RadioButton dolarEuroRd;
    RadioButton euroDolarRd;
    EditText dolarTexto;
    EditText euroTexto;
    EditText cambioEdit;

    double cambio;
    static final double cambioOficial = 0.852784;

    private static final int MAX_TIMEOUT = 2000;
    private static final int RETRIES = 1;
    private static final int TIMEOUT_BETWEEN_RETRIES = 5000;

    private static final String URLFICHCONVER = "http://alumno.mobi/~alumno/superior/bujalance/conversion.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversor);
        convertirBtn = (Button) findViewById(R.id.convertirBtn);
        dolarEuroRd = (RadioButton) findViewById(R.id.dolarEuroRd);
        euroDolarRd = (RadioButton) findViewById(R.id.eurodolarRd);
        dolarTexto = (EditText) findViewById(R.id.DolaresEdit);
        euroTexto = (EditText) findViewById(R.id.EurosEdit);

        convertirBtn.setOnClickListener(this);
        cambio = cambioOficial;
    }

    @Override
    public void onClick(View view) {
        Conversor.TipoCambio tipoCambio;

        if(view == convertirBtn)
        {
            try{
                try{
                    obtenerConversion();
                }catch (Exception e){
                    Toast.makeText(this, "El cambio introducido no es válido.Se establecerá el cambio oficial", Toast.LENGTH_SHORT).show();
                    cambio = cambioOficial;
                    cambioEdit.setText(String.valueOf(cambio));
                    Toast.makeText(ConversorPrincipal.this,mostrarContenido("http://alumno.mobi/~alumno/superior/bujalance/direcciones.txt"),Toast.LENGTH_LONG).show();
                }

            if(dolarEuroRd.isChecked())
            {
                tipoCambio = Conversor.TipoCambio.dolarEuro;
                Conversor conver = new Conversor(Double.parseDouble(dolarTexto.getText().toString()),tipoCambio,cambio);
                euroTexto.setText(String.valueOf(conver.getEuros()));
            }

            if(euroDolarRd.isChecked())
            {
                tipoCambio = Conversor.TipoCambio.euroDolar;
                Conversor conver = new Conversor(Double.parseDouble(euroTexto.getText().toString()),tipoCambio,cambio);
                dolarTexto.setText(String.valueOf(conver.getDolares()));
            }}catch (Exception e){};
        }
    }

    public static String mostrarContenido(String url) throws Exception {
        URL ficheroUrl = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(ficheroUrl.openStream()));

        String linea;
        String texto = "";
        while ((linea = in.readLine()) != null){
            texto += linea+"\n";
        }

        in.close(); // Cerramos la conexión

        return texto;
    }

    private void obtenerConversion() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(MAX_TIMEOUT);
        client.setMaxRetriesAndTimeout(RETRIES, TIMEOUT_BETWEEN_RETRIES);

        client.get(URLFICHCONVER, new FileAsyncHttpResponseHandler(this) {

            @Override
            public void onStart() {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Obteniendo datos necesarios...");
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        client.cancelAllRequests(true);
                    }
                });
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    InputStreamReader isr = new InputStreamReader(fileInputStream);
                    BufferedReader br = new BufferedReader(isr);
                    cambio = Double.parseDouble(br.readLine());
                } catch (FileNotFoundException e) {
                    Toast.makeText(ConversorPrincipal.this, "No se ha encontrado el archivo en la red", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(ConversorPrincipal.this, "Error de entrada/salida", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                progressDialog.dismiss();
                Toast.makeText(ConversorPrincipal.this, "No se ha podido obtener el valor del cambio", Toast.LENGTH_SHORT).show();
                cambio = cambioOficial;
            }
        });
    }
}
