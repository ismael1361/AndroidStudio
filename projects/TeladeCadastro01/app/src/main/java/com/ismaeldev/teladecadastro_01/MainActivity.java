package com.ismaeldev.teladecadastro_01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private EditText inputName, inputIdade;
    private Spinner spinnerSexo;
    private ImageView iconSexo;
    private Button btnConfirmar;
    private TextView textResult;

    private void closeKeyboard(){
        View v = this.getCurrentFocus();
        if(v != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputName = (EditText) findViewById(R.id.inputName);
        inputIdade = (EditText) findViewById(R.id.inputIdade);
        spinnerSexo = (Spinner) findViewById(R.id.spinnerSexo);

        spinnerSexo.setAdapter((ArrayAdapter) ArrayAdapter.createFromResource(
                this,
                R.array.sexoList,
                R.layout.support_simple_spinner_dropdown_item
        ));

        iconSexo = (ImageView) findViewById(R.id.iconSexo);
        btnConfirmar = (Button) findViewById(R.id.btnConfirmar);
        textResult = (TextView) findViewById(R.id.textResult);

        spinnerSexo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    iconSexo.setImageResource(R.drawable.gender_male);
                }else if(position == 1){
                    iconSexo.setImageResource(R.drawable.gender_female);
                }
                closeKeyboard();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
                closeKeyboard();
            }
        });

        /*
        spinnerSexo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
            }
        });*/

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = "Bem Vindo ";
                int pos = spinnerSexo.getSelectedItemPosition();
                result += pos == 0 ? "Senhor" : "Senhora";
                result += " "+inputName.getText().toString();
                textResult.setText(result);

                closeKeyboard();
            }
        });
    }
}
