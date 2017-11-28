package com.example.mamorky.ejerciciosdeficherosacda;

import android.app.DialogFragment;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.DateRangeLimiter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DiasLectivos extends AppCompatActivity implements View.OnClickListener,DatePickerDialog.OnDateSetListener{

    static EditText etPlannedDateIni,etPlannedDateFin,edtFechasFestivas;
    static Date dateCalendar;
    static Date dateIni;
    static Date dateEnd;
    static DatePickerDialog dpIni;
    static DatePickerDialog dpFin;
    static Button btnVerFestivos;
    static ArrayList<Date> margenFechas;
    static ArrayList<Date> diasLectivos;
    static File fechasFestivasFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dias_lectivos);
        File ruta_sd = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        new File(ruta_sd.getAbsolutePath(), "fechasFestivasFile.txt").delete();
        fechasFestivasFile = new File(ruta_sd.getAbsolutePath(), "fechasFestivasFile.txt");

        etPlannedDateIni = (EditText)findViewById(R.id.etPlannedDateIni);
        etPlannedDateIni.setOnClickListener(this);

        etPlannedDateFin = (EditText)findViewById(R.id.etPlannedDateFin);
        etPlannedDateFin.setOnClickListener(this);

        edtFechasFestivas = (EditText)findViewById(R.id.edtFechasFestivas);

        btnVerFestivos = (Button)findViewById(R.id.btnVerFectivos);
        btnVerFestivos.setOnClickListener(this);

        try {
            ManejaFechas.CrearFicheroFestivos(fechasFestivasFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(v== etPlannedDateIni){
            Calendar calendar = new GregorianCalendar();
            dpIni = DatePickerDialog.newInstance(
                    this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dpIni.show(getFragmentManager(), "Datepickerdialog");
        }

        if(v== etPlannedDateFin){
            Calendar calendar = new GregorianCalendar();
            dpFin = DatePickerDialog.newInstance(
                    this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dpFin.show(getFragmentManager(), "Datepickerdialog");
        }

        if(v==btnVerFestivos) {

            try {
                margenFechas = ManejaFechas.margenDeFechas(dateIni, dateEnd);
                if(dateIni == null || dateEnd == null)
                    Toast.makeText(this,"Debes introducir las dos fechas",Toast.LENGTH_LONG).show();
                else if(margenFechas.size() == 0){
                    Toast.makeText(this,"El margen de fechas no es válido",Toast.LENGTH_LONG).show();

                }
                else{
                    String texto = "";
                    ManejaFechas.ComparaFechasFichero(fechasFestivasFile,margenFechas);
                    for (int i = 0; i < margenFechas.size(); i++) {
                        texto += (new SimpleDateFormat("dd-MM-yyyy").format(margenFechas.get(i))+"\n");
                    }
                    edtFechasFestivas.setText(texto);
                }
            } catch (IOException e) {
                Toast.makeText(this,"Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
            } catch (ParseException e) {
                Toast.makeText(this,"Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
            } catch (NullPointerException e){
                Toast.makeText(this,"Debes introducir un margen de fechas",Toast.LENGTH_LONG).show();
            } catch (Exception e){
                Toast.makeText(this,"Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        dateCalendar = new Date(year,monthOfYear,dayOfMonth);
        if(view == dpIni){
            dateIni = dateCalendar;
            etPlannedDateIni.setText(new SimpleDateFormat("dd-MM-yyyy").format(dateIni));
        }
        if(view == dpFin){
            dateEnd = dateCalendar;
            etPlannedDateFin.setText(new SimpleDateFormat("dd-MM-yyyy").format(dateEnd));
        }
    }
}
