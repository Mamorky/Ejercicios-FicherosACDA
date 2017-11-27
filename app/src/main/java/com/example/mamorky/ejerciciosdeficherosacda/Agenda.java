package com.example.mamorky.ejerciciosdeficherosacda;

import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Agenda extends AppCompatActivity implements View.OnClickListener{

    Memoria memoria;
    String FICHCONTACTOS = "contactos.txt";
    Resultado resultado;
    File file;

    EditText edtContactos;
    EditText edtNombre;
    EditText edtEmail;
    EditText edtTelefono;

    Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        memoria = new Memoria(this);
        edtContactos = (EditText)findViewById(R.id.edtContactos);
        edtNombre = (EditText)findViewById(R.id.edtNombre);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtTelefono = (EditText)findViewById(R.id.edtTlf);

        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
    }

    public void LeerContactos(){
        if(this.memoria.disponibleLectura()){
                resultado = memoria.leerInterna(FICHCONTACTOS,"utf-8");
                edtContactos.setText(resultado.getContenido());
        }
    }

    public void AnadirContacto(String nombre,String email,String telefono){
        if(this.memoria.disponibleEscritura()){
            if(comprobarCorrecto(nombre,email,telefono)){
                String contacto = "Nombre: "+nombre+"\nEmail: "+email+"\nTLF: "+telefono+"\n\n";
                memoria.escribirInterna(FICHCONTACTOS, contacto,true,"utf-8");
            }
        }
    }

    @Override
    public void onClick(View v) {
        AnadirContacto(edtNombre.getText().toString(),edtEmail.getText().toString(),edtTelefono.getText().toString());
        LeerContactos();
    }

    private boolean comprobarCorrecto(String nombre,String email,String tlf){
        if(!ValidatorUtil.validateName(nombre))
            Toast.makeText(this,"El nombre no tiene un formato correcto",Toast.LENGTH_LONG).show();
        else if(!ValidatorUtil.validateEmail(email))
            Toast.makeText(this,"El email no tiene un formato correcto",Toast.LENGTH_LONG).show();
        else if(!ValidatorUtil.validateTLF(tlf))
            Toast.makeText(this,"El teléfono no tiene un formato correcto",Toast.LENGTH_LONG).show();
        else
            return true;

        return false;
    }
}
