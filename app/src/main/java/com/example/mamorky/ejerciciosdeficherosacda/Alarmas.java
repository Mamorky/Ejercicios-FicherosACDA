package com.example.mamorky.ejerciciosdeficherosacda;

import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Alarmas extends AppCompatActivity {

    EditText edtDurAlarma,edtMensajeAlarma;
    Button btnEmpezar,btnAniadir,btnVerAlarmas;
    CountDownTimer crono;
    TextView txvTiempo,txvAlarma;
    int tiempoAlarma;
    long tiempoMls;
    int numAlarmaLeida = 0;
    int numAlarmaEscrita = 0;
    int numAlarmasTotales = 0;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmas);

        edtDurAlarma = (EditText)findViewById(R.id.edtDuracion);
        edtMensajeAlarma = (EditText)findViewById(R.id.edtMensaje);
        btnEmpezar = (Button)findViewById(R.id.btnStartAlarma);
        btnAniadir = (Button) findViewById(R.id.btnAddAlarma);
        btnVerAlarmas = (Button) findViewById(R.id.btnVerAlarmas);
        txvTiempo = (TextView)findViewById(R.id.txvTiempo);
        txvAlarma = (TextView)findViewById(R.id.txvAlarma);
        file = new File(Environment.getExternalStorageDirectory() + "/" + "alarmas.txt");

        try {
            // Crea un fichero con todas las alarmas
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        inicializaAlarma();

        btnEmpezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    btnEmpezar.setEnabled(false);
                    sonar(tiempoMls);

            }
        });

        btnAniadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(edtMensajeAlarma.getText().toString() == "" || edtDurAlarma.getText().toString() == "")) {
                    tiempoAlarma = Integer.parseInt(edtDurAlarma.getText().toString());
                    tiempoMls = tiempoAlarma * 60 * 1000;
                    guardar();
                } else {
                    Toast.makeText(Alarmas.this, "Introduce una alarma", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnVerAlarmas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leeAlarmas();
            }
        });
    }

    private void guardar(){
        BufferedWriter bw;

        try{
            bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()));

            bw.append("Alarma: " + ++numAlarmaEscrita + ". Duración: " + edtDurAlarma.getText().toString() + ". Mensaje: " +   edtMensajeAlarma.getText().toString());
            bw.newLine();
            bw.flush();

            if (bw != null)
                bw.close();
        }
        catch (Exception e) {
            Log.e("ERROR WRITING", "Unable to write" + e.getMessage());
        }

        Toast.makeText(this,"Alarma "+edtMensajeAlarma.getText().toString()+" guardada",Toast.LENGTH_LONG).show();
        numAlarmasTotales++;
        txvAlarma.setText("Alarmas finalizadas: " + numAlarmaLeida + "/" + numAlarmasTotales);
    }

    private void leeAlarmas(){
        BufferedReader br;
        String texto = "";

        try {
            br = new BufferedReader(new FileReader(file.getAbsolutePath()));

            String linea = "";

            while((linea = br.readLine()) != null){
                texto += linea+"\n";
            }

            if(texto != "")
                Toast.makeText(this,texto,Toast.LENGTH_LONG*10).show();
            else
                Toast.makeText(this,"No hay alarmas",Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void inicializaAlarma(){
        BufferedWriter bw;

        try{
            bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()));

            for (int i = 1; i < 6; i++){
                bw.append("Alarma: " + ++numAlarmaLeida + ". Duración: " + edtDurAlarma.getText().toString() + ". Mensaje: Alarma de Prueba "+numAlarmaLeida);
                bw.newLine();
                bw.flush();
            }
            if (bw != null)
                bw.close();
        }
        catch (Exception e) {
            Log.e("ERROR WRITING", "Unable to write" + e.getMessage());
        }
    }

    private void sonar(long t){
        txvTiempo.setText(DateFormat.format("mm:ss", t));

        crono = new CountDownTimer(t, 1000) {
            @Override
            public void onTick(long l) {
                txvTiempo.setText(DateFormat.format("mm:ss", l));
            }

            @Override
            public void onFinish() {
                txvTiempo.setText("00:00");
                Toast.makeText(getApplicationContext(), "Fin alarma " + ++numAlarmaLeida + "   Mensaje: " + edtMensajeAlarma.getText().toString(), Toast.LENGTH_LONG).show();
                if(numAlarmaEscrita < 5) {
                    txvTiempo.setText(DateFormat.format("mm:ss", tiempoMls));
                    crono.start();
                }else
                    btnEmpezar.setEnabled(true);
            }
        };
        crono.start();
    }
}


