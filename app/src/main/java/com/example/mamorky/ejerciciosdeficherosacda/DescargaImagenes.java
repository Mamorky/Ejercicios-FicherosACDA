package com.example.mamorky.ejerciciosdeficherosacda;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by mamorky on 25/11/17.
 */

public class DescargaImagenes extends AppCompatActivity implements View.OnClickListener{

    private Button btnDescarga,btnSig,btnAnt;
    private EditText edtFichDesc;
    private ImageView imgIMagen;
    private static String mLink;
    private static ArrayList<String> images;
    private static int flag = 0;

    private static final int MAX_TIMEOUT = 2000;
    private static final int RETRIES = 1;
    private static final int TIMEOUT_BETWEEN_RETRIES = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descarga_imagenes);
        //http://i.imgur.com/hlWzRAQ.jpg
        edtFichDesc = (EditText) findViewById(R.id.edtFichDesc);
        imgIMagen = (ImageView) findViewById(R.id.imgImagen);
        images = new ArrayList<String>();

        btnDescarga = (Button)findViewById(R.id.btnDescargarImg);
        btnDescarga.setOnClickListener(this);
        btnSig = (Button)findViewById(R.id.btnSig);
        btnSig.setOnClickListener(this);
        btnAnt = (Button)findViewById(R.id.btnAnt);
        btnAnt.setOnClickListener(this);
    }

    private void getContenidoUrl(String url) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(MAX_TIMEOUT);
        client.setMaxRetriesAndTimeout(RETRIES, TIMEOUT_BETWEEN_RETRIES);

        if (URLUtil.isValidUrl(url)) {
            client.get(url, new FileAsyncHttpResponseHandler(this) {
                @Override
                public void onStart() {
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("Descargando ...");
                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            client.cancelAllRequests(true);
                        }
                    });
                    progressDialog.show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    progressDialog.dismiss();
                    Toast.makeText(DescargaImagenes.this, "Fallo al conseguir el archivo. Error: " + statusCode, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, File file) {
                    String linea;
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                        BufferedReader br = new BufferedReader(inputStreamReader);
                        while ((linea = br.readLine()) != null) {
                            images.add(linea);
                        }
                    } catch (FileNotFoundException e) {
                        Toast.makeText(DescargaImagenes.this, "No se encontró el archivo", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(DescargaImagenes.this, "Error I/O", Toast.LENGTH_SHORT).show();
                    } catch (IllegalArgumentException e){
                        avanzar();
                    }

                    progressDialog.dismiss();
                    avanzar();
                }
            });

        } else {
            Toast.makeText(DescargaImagenes.this, "URL no Válida", Toast.LENGTH_SHORT).show();
        }
    }
    public void descargar(String imagenStr){
        if(!TextUtils.isEmpty(imagenStr)) {
            picasso(imagenStr);
            Toast.makeText(this, "Imagen descargada correctamente.", Toast.LENGTH_SHORT).show();
        }
    }

    private void picasso(String link){
        Picasso.with(getApplicationContext()).load(link) .placeholder(R.drawable.preloader) .error(R.drawable.error)
                .resize(300, 300).into(imgIMagen);
    }

    public void avanzar(){
        try{
        String nextImage = images.get(flag++ % images.size());
        if(nextImage.isEmpty())
            imgIMagen.setImageResource(R.drawable.error);
        else
            picasso(nextImage);
        }catch (Exception e){
            Toast.makeText(this,"No se pudo avanzar la posición. "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void retroceder (){
        try{
        String nextImage = images.get(flag++ % images.size());
        if(nextImage.isEmpty())
            imgIMagen.setImageResource(R.drawable.error);
        else
            picasso(nextImage);
        }catch (Exception e){
            Toast.makeText(this,"No se pudo retroceder la posición. "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btnDescarga){
            getContenidoUrl(edtFichDesc.getText().toString());
        }
        if(v == btnSig)
            avanzar();
        if(v == btnAnt)
            retroceder();
    }
}
