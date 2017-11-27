package com.example.mamorky.ejerciciosdeficherosacda;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nbsp.materialfilepicker.MaterialFilePicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class SubidaFichero extends AppCompatActivity implements View.OnClickListener{

    private static Uri rutaFich;
    private static final int ABRIRFICHERO_REQUEST_CODE = 1;
    public final static String WEB = "http://alumno.mobi/~alumno/superior/bujalance/upload.php";
    EditText mTexto;
    TextView mInfo;
    Button btnSubida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subida_fichero);
        mTexto = (EditText) findViewById(R.id.edt_subida);
        mTexto.setOnClickListener(this);

        btnSubida = (Button) findViewById(R.id.btn_subida);
        btnSubida.setOnClickListener(this);

        mInfo = (TextView) findViewById(R.id.txv_info);
    }

    public void subida() {
        final ProgressDialog progreso = new ProgressDialog(this);
        File myFile;
        Boolean existe = true;
        myFile = new File(mTexto.getText().toString());
        RequestParams params = new RequestParams();
        try {
            params.put("fileToUpload", myFile);
        } catch (FileNotFoundException e) {
            existe = false;
            mInfo.setText("Error en el fichero: " + e.getMessage());
            Toast.makeText(this, "Error en el fichero: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (existe) {
            RestClient.post(WEB, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    // called before request is started
                    progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progreso.setMessage("Conectando . . ."); //progreso.setCancelable(false);
                    progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            RestClient.cancelRequests(getApplicationContext(), true);
                        }
                    });
                    progreso.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) { // called when response HTTP status is "200 OK"
                    progreso.dismiss();
                    mInfo.setText(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable t) { // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    progreso.dismiss();
                    mInfo.setText(response);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if(v==mTexto){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("file/*");
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivityForResult(intent, ABRIRFICHERO_REQUEST_CODE);
            else
//informar que no hay ninguna aplicación para manejar ficheros
                Toast.makeText(this, "No hay aplicación para manejar ficheros", Toast.LENGTH_SHORT).show();
        }
        if(v==btnSubida)
            subida();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == ABRIRFICHERO_REQUEST_CODE)
            if (resultCode == RESULT_OK) {
                // Mostramos en la etiqueta la ruta del archivo seleccionado

                Uri ruta = data.getData();
                mTexto.setText(ruta.getPath());
            }
            else
                Toast.makeText(this, "Error: " + resultCode, Toast.LENGTH_SHORT).show();
    }
}
