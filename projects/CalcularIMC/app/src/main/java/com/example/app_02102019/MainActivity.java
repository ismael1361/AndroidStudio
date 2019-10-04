package com.example.app_02102019;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView resultInput;
    private EditText peso, altura;

    public float IMC(float p, float a){return p/(a*a);}

    public float pesoIdeal(float imc, float a){return imc*(a*a);}

    public void calc(){
        if(peso.getText().toString().length() > 0 && altura.getText().toString().length() >0){
            float pesoF = Float.parseFloat(peso.getText().toString()), alturaF = Float.parseFloat(altura.getText().toString());
            float imc = IMC(pesoF, alturaF);

            String situacao = "Situação: ";

            if(imc < 17){
                situacao += "Muito abaixo do peso ideal";
                resultInput.setTextColor(getResources().getColor(R.color.alert_3));
            }else if(imc < 18.5){
                situacao += "Abaixo do peso ideal";
                resultInput.setTextColor(getResources().getColor(R.color.alert_2));
            }else if(imc < 25){
                situacao += "Peso normal";
                resultInput.setTextColor(getResources().getColor(R.color.alert_1));
            }else if(imc < 30){
                situacao += "Acima do peso ideal";
                resultInput.setTextColor(getResources().getColor(R.color.alert_2));
            }else if(imc < 35){
                situacao += "Obesidade I";
                resultInput.setTextColor(getResources().getColor(R.color.alert_3));
            }else if(imc < 40){
                situacao += "Obesidade II (severa)";
                resultInput.setTextColor(getResources().getColor(R.color.alert_4));
            }else{
                situacao += "Obesidade III (mórbida)";
                resultInput.setTextColor(getResources().getColor(R.color.alert_5));
            }

            situacao += "\nPeso ideal: "+String.format("%.2f", pesoIdeal((float) 18.5, alturaF))+"kg ~ "+String.format("%.2f", pesoIdeal((float) 24.99, alturaF))+"kg";

            resultInput.setText(situacao);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        peso = (EditText) findViewById(R.id.inputPeso);
        altura = (EditText) findViewById(R.id.inputAltura);

        resultInput = (TextView) findViewById(R.id.textViewResult);

        peso.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                calc();
                return false;
            }
        });

        altura.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                calc();
                return false;
            }
        });
    }
}
