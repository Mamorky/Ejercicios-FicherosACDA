package com.example.mamorky.ejerciciosdeficherosacda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnEj1,btnEj2,btnEj3,btnEj4,btnEj5,btnEj6,btnEj7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEj1 = (Button)findViewById(R.id.btnEj1);
        btnEj1.setOnClickListener(this);

        btnEj2 = (Button)findViewById(R.id.btnEj2);
        btnEj2.setOnClickListener(this);

        btnEj3 = (Button)findViewById(R.id.btnEj3);
        btnEj3.setOnClickListener(this);

        btnEj4 = (Button)findViewById(R.id.btnEj4);
        btnEj4.setOnClickListener(this);

        btnEj5 = (Button)findViewById(R.id.btnEj5);
        btnEj5.setOnClickListener(this);

        btnEj6 = (Button)findViewById(R.id.btnEj6);
        btnEj6.setOnClickListener(this);

        btnEj7 = (Button)findViewById(R.id.btnEj7);
        btnEj7.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v== btnEj1){
            Intent intent = new Intent(this,Agenda.class);
            startActivity(intent);
        }
        if(v==btnEj2){
            Intent intent = new Intent(this,Alarmas.class);
            startActivity(intent);
        }
        if(v==btnEj3){
            Intent intent = new Intent(this,DiasLectivos.class);
            startActivity(intent);
        }
        if(v==btnEj4){
            Intent intent = new Intent(this,Conexiones.class);
            startActivity(intent);
        }
        if(v==btnEj5){
            Intent intent = new Intent(this,DescargaImagenes.class);
            startActivity(intent);
        }
        if(v==btnEj6){
            Intent intent = new Intent(this,ConversorPrincipal.class);
            startActivity(intent);
        }

        if(v==btnEj7){
            Intent intent = new Intent(this,SubidaFichero.class);
            startActivity(intent);
        }
    }
}
