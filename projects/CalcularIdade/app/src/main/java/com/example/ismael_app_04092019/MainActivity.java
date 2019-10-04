package com.example.ismael_app_04092019;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.Principal;
import java.util.Calendar;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textViewResult;
    private EditText inputDate;
    private Button buttonCheck;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private int yearNow, monthNow, dayNow, year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputDate = (EditText) findViewById(R.id.inputDate);
        buttonCheck = (Button) findViewById(R.id.buttonCheck);
        textViewResult = (TextView) findViewById(R.id.textViewResult);
        imageView = (ImageView) findViewById(R.id.imageView);

        Calendar cal = Calendar.getInstance();
        yearNow = year = cal.get(Calendar.YEAR);
        monthNow = month = cal.get(Calendar.MONTH) + 1;
        dayNow = day = cal.get(Calendar.DAY_OF_MONTH);

        inputDate.setText(day + "/" + month + "/" + year);

        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month - 1,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d){
                year = y;
                month = m + 1;
                day = d;
                inputDate.setText(day + "/" + month + "/" + year);
            }
        };

        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int y = yearNow-year;
                String type;

                if(y <= 12){
                    imageView.setImageResource(R.drawable.child);
                    type = "uma criança";
                }else if(y <= 18){
                    imageView.setImageResource(R.drawable.teenage);
                    type = "um adolescente";
                }else{
                    imageView.setImageResource(R.drawable.adult);
                    type = "um adulto";
                }

                if(monthNow >= month && dayNow >= day){
                    textViewResult.setText("Você é "+ type +" e tem "+ y + " anos");
                }else {
                    textViewResult.setText("Você é  " + type + " e fará " + y + " anos");
                }
            }
        });
    }
}
