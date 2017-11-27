package com.example.mamorky.ejerciciosdeficherosacda;

import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by mamorky on 25/11/17.
 */

public class ManejaFechas {
    public static void CrearFicheroFestivos(File fechasFestivas) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fechasFestivas,true)));

        bw.write("02-01-2017\n06-01-2017\n28-02-2017\n13-04-2017\n14-04-2017\n15-08-2017\n19-08-2017\n08-09-2017\n12-10-2017\n01-11-2017\n06-12-2017\n08-12-2017\n25-12-2017\n");

        bw.close();
    }

    public static ArrayList<Date> margenDeFechas(Date fechaIni,Date fechaFin){
        ArrayList<Date> margenFechas = new ArrayList<Date>();
        Calendar calendarFComp = new GregorianCalendar();
        Calendar calendarFFin = new GregorianCalendar();

        Date fechaComp = fechaIni;
        calendarFComp.set(fechaComp.getYear(),fechaComp.getMonth(),fechaComp.getDate());
        calendarFFin.set(fechaFin.getYear(),fechaFin.getMonth(),fechaFin.getDate());

        while (calendarFComp.compareTo(calendarFFin) <= 0){
            margenFechas.add(fechaComp);
            calendarFComp.add(Calendar.DAY_OF_YEAR,1);
            fechaComp = calendarFComp.getTime();
        }

        return margenFechas;
    }

    public static void ComparaFechasFichero(File fechasFestivas,ArrayList<Date> margenFechas) throws IOException, ParseException {

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fechasFestivas)));

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        String linea;

        while((linea = br.readLine())!=null){
            Date fechaFes = sdf.parse(linea);

            for (int i = 0; i < margenFechas.size(); i++) {
                Date fechaComp = sdf.parse(new SimpleDateFormat("dd-MM-yyyy").format(margenFechas.get(i)));
                if(fechaComp.equals(fechaFes))
                    margenFechas.remove(i);
            }
        }

        br.close();
    }
}
