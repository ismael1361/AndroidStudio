package com.ismaeldev.calculadora;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.service.autofill.RegexValidator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    private HorizontalScrollView scrollDisplayResult, scrollDisplayForm;
    private TextView displayForm, displayResult, displayActivMemory;

    private String memory = "";

    private String formsCalcs;

    private boolean isResult = false;

    public void alert(String s){
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setMessage(s);
        dlg.setNeutralButton("OK", null);
        dlg.show();
    }

    private  boolean isSimbol(String v){
        return (searchString(v,"[\\*\\/\\+\\-\\%]") > 0 && v.length() == 1);
    }

    private void showDisplay(String v){
        if((v == "0" || (v.length() == 1 && isSimbol(v) && searchString(v,"\\-") <= 0)) && formsCalcs.length() <= 0){
        }else{
            if(isResult){
                if(isSimbol(v)){v = displayResult.getText().toString()+v;}
                clearDisplay();
            }
            if(formsCalcs == "0"){formsCalcs = "";}
            if(v.length() == 1) {
                if (formsCalcs.length() > 0) {
                    String s = String.valueOf(formsCalcs.charAt(formsCalcs.length() - 1));
                    if (isSimbol(s) && searchString(s, "\\%") <= 0 && isSimbol(v)) {
                        backForm();
                    } else if (searchString(s, "[0-9\\.]") <= 0 && searchString(v, "\\.") > 0) {
                        v = "0.";
                    }
                } else if (searchString(v, "\\.") > 0) {
                    v = "0.";
                }
            }
            formsCalcs = "" + formsCalcs + v;
            displayResult.setText(formsCalcs);
        }
    }

    private void clearDisplay(){
        formsCalcs = "";
        displayForm.setText("");
        displayResult.setText("0");
        displayResult.setTextColor(getResources().getColor(R.color.textColor_01));
        isResult = false;
    }

    private void backForm(){
        if(isResult){
            String temp = formsCalcs;
            clearDisplay();
            formsCalcs = temp;
            displayResult.setText(formsCalcs);
        }else{
            if(formsCalcs.length() > 1){
                formsCalcs = formsCalcs.substring(0, formsCalcs.length() - 1);
                displayResult.setText(formsCalcs);
            }else{
                formsCalcs = "";
                displayResult.setText("0");
            }
        }
    }

    private int searchString(String s, String reg){
        return s.length()-s.replaceAll(reg, "").length();
    }

    private String GetFormResult(String form, String reg){
        String r = form;
        Pattern p = null;
        Matcher m;

        int cam = 0;

        if(reg == "\\%"){
            p = Pattern.compile("((\\-)?\\d+(\\.\\d+)?)([\\*\\/\\+\\-])((\\-)?\\d+(\\.\\d+)?)\\%");
        }else{
            p = Pattern.compile("((\\-)?\\d+(\\.\\d+)?)"+reg+"((\\-)?\\d+(\\.\\d+)?)");
        }


        while(p.matcher(r).find()){
            m = p.matcher(r);
            while (m.find()) {
                boolean addPlus = (m.group(1).contains("-") || m.group(4).contains("-"));
                String result = "";

                DecimalFormat decimalFormat = new DecimalFormat("#0.00");

                if (reg == "\\*") {
                    result = decimalFormat.format(Float.parseFloat(m.group(1)) * Float.parseFloat(m.group(4)));
                } else if (reg == "\\/") {
                    result = decimalFormat.format(Float.parseFloat(m.group(1)) / Float.parseFloat(m.group(4)));
                } else if (reg == "\\+") {
                    result = decimalFormat.format(Float.parseFloat(m.group(1)) + Float.parseFloat(m.group(4)));
                } else if (reg == "\\-") {
                    result = decimalFormat.format(Float.parseFloat(m.group(1)) - Float.parseFloat(m.group(4)));
                } else if (reg == "\\%") {
                    result = decimalFormat.format(Float.parseFloat(m.group(1)) * (Float.parseFloat(m.group(5)) / 100.0f));
                }

                result = result.replaceAll(",", ".");
                if(reg == "\\%"){
                    r = r.replace(m.group(0),  m.group(1) + m.group(4) + result);
                }else{
                    r = r.replace(m.group(0),  (result.contains("-") == false && addPlus ? "+" : "") + result);
                }
            }
        }

        return r;
    }

    private String getPriority(String strForm){
        String str = "";
        int start = -1;
        if(searchString(strForm,"\\(") > 0){
            for(int s=0; s<strForm.length(); s++){
                String letter = Character.toString(strForm.charAt(s));
                if(searchString(letter,"\\(") > 0){
                    str = "";
                    start = s;
                }else if(searchString(letter,"\\)") > 0){
                    str = str+letter;
                    break;
                }
                str = str+letter;
            }
        }
        return start < 0 ? "" : str;
    }

    private String getResult(String strForm){
        String result = strForm;

        Pattern p;
        Matcher m;

        result = GetFormResult(result, "\\%");
        result = GetFormResult(result, "\\*");
        result = GetFormResult(result, "\\/");
        result = GetFormResult(result, "\\+");
        result = GetFormResult(result, "\\-");

        result = result.replace("+", "");

        while (Pattern.compile("([\\*\\/\\+\\%\\(\\)])").matcher(result).find()){
            result = "err:syntax";
        }

        if(
            (searchString(result,"\\-") > 1) ||
            (searchString(result,"\\.") > 1)
        ){
            result = "err:syntax";
        }

        result = result.replaceAll("(\\d+)\\.00", "$1");
        result = result.replaceAll("(\\d+\\.\\d)0", "$1");

        return result;
    }

    private void resolveForm(){
        String result = formsCalcs, priority = getPriority(result);

        result = result.replaceAll("([0-9]+)\\(", "$1*(");
        result = result.replaceAll("\\)([0-9]+)", ")*$1");

        while (priority.length() > 0) {
            String form = priority.replaceFirst("^\\(", "").replaceFirst("\\)$", "");
            result = result.replace(priority, getResult(form));
            //priority = getPriority(result);
            priority = getPriority(result);
        }

        result = getResult(result);
        displayForm.setText(formsCalcs);
        displayResult.setText(result);
        displayResult.setTextColor(getResources().getColor(R.color.textColor_02));
        isResult = true;
    }

    private void addMemory(String simbol){
        if(formsCalcs.length() > 0) {
            resolveForm();
            if (searchString(displayResult.getText().toString(), "err//:syntax") <= 0) {
                if (memory.length() > 0) {
                    formsCalcs = memory + simbol + displayResult.getText().toString();
                    resolveForm();
                    if (searchString(displayResult.getText().toString(), "err//:syntax") <= 0) {
                        memory = displayResult.getText().toString();
                    }
                } else {
                    memory = displayResult.getText().toString();
                }
                displayActivMemory.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showMemory(){
        if(memory.length() > 0) {
            showDisplay(memory);
        }
    }

    private void clearMemory(){
        memory = "";
        clearDisplay();
        displayActivMemory.setVisibility(View.INVISIBLE);
    }

    public void btnAnimation(View view) {
        Button btn = (Button) view;
        Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.blink_anim);
        btn.startAnimation(anim);
    }

    public void btnMemoryClick(View view) {
        Button btn = (Button) view;

        if(searchString(btn.getText().toString(), "mc") > 0){
            clearMemory();
        }else if(searchString(btn.getText().toString(), "m\\-") > 0){
            addMemory("-");
        }else if(searchString(btn.getText().toString(), "m\\+") > 0){
            addMemory("+");
        }else if(searchString(btn.getText().toString(), "mr") > 0){
            showMemory();
        }
        btnAnimation(view);
    }

    public void btnClearClick(View view) {
        Button btn = (Button) view;
        clearDisplay();
        btnAnimation(view);
    }

    public void btnBackClick(View view) {
        Button btn = (Button) view;
        backForm();
        btnAnimation(view);
    }

    public void btnOperadorClick(View view) {
        Button btn = (Button) view;
        if(searchString(btn.getText().toString(), "[\\×\\÷\\+\\-\\%]") > 0){
            String op = searchString(btn.getText().toString(), "[\\×]") > 0 ? "*" : btn.getText().toString();
            op = searchString(op, "[\\÷]") > 0 ? "/" : op;
            showDisplay(op);
        }else if(searchString(btn.getText().toString(), "\\=") > 0){
            resolveForm();
        }else if(searchString(btn.getText().toString(), "[\\(\\)]") > 0){
            showDisplay(btn.getText().toString());
        }
        btnAnimation(view);
    }

    public void btnNumberClick(View view) {
        Button btn = (Button) view;
        if(searchString(btn.getText().toString(), "[0-9]") > 0){
            showDisplay(btn.getText().toString());
        }else if(searchString(btn.getText().toString(), "\\.") > 0){
            showDisplay((String)".");
        }
        btnAnimation(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayActivMemory = (TextView) findViewById(R.id.isMemory);

        scrollDisplayForm = (HorizontalScrollView) findViewById(R.id.horizontalScrollDisplayFormCalc);

        displayForm = (TextView) findViewById(R.id.displayFormCalc);

        displayForm.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                scrollDisplayForm.fullScroll(scrollDisplayResult.FOCUS_RIGHT);
            }
        });

        scrollDisplayResult = (HorizontalScrollView) findViewById(R.id.horizontalScrollDisplayResultCalc);

        displayResult = (TextView) findViewById(R.id.displayResultCalc);

        displayResult.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                scrollDisplayResult.fullScroll(scrollDisplayResult.FOCUS_RIGHT);
            }
        });

        displayResult.setTextColor(getResources().getColor(R.color.textColor_01));

        clearDisplay();
    }
}
