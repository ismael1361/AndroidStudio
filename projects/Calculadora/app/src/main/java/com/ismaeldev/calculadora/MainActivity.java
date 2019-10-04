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
    private Button btn_mc, btn_mm, btn_mp, btn_mr, btn_c, btn_perc, btn_div, btn_mult, btn_min, btn_add, btn_back, btn_result, btn_np, btn_n0, btn_n1, btn_n2, btn_n3, btn_n4, btn_n5, btn_n6, btn_n7, btn_n8, btn_n9;

    private HorizontalScrollView scrollDisplayResult, scrollDisplayForm;
    private TextView displayForm, displayResult, displayActivMemory;

    private String memory = "";

    private String formsCalcs;

    private boolean isResult = false;

    interface Callback{
        public void function();
    }

    public class ButtonEventClick{
        private Button btn;
        private Callback func;
        public Button set(int id, Callback fn){
            func = (Callback) fn;
            btn = (Button) findViewById(id);
            btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.blink_anim);
                    btn.startAnimation(anim);
                    func.function();
                }
            });
            return btn;
        }
    }

    public void alert(String s){
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setMessage(s);
        dlg.setNeutralButton("OK", null);
        dlg.show();
    }

    private  boolean isSimbol(String v){
        if(searchString(v,"[\\*\\/\\+\\-\\%]") > 0){
            return true;
        }
        return false;
    }

    private void addOnDisplay(String v){
        if((v == "0" || (isSimbol(v) && searchString(v,"\\-") <= 0)) && formsCalcs.length() <= 0){
        }else{
            if(isResult){clearDisplay();}
            if(formsCalcs == "0"){formsCalcs = "";}
            if(formsCalcs.length() > 0){
                String s = String.valueOf(formsCalcs.charAt(formsCalcs.length()-1));
                if(isSimbol(s) && searchString(s,"\\%") <= 0 && isSimbol(v)){
                    backForm();
                }else if(searchString(s,"[0-9\\.]") <= 0 && searchString(v,"\\.") > 0){
                    v = "0.";
                }
            }else if(searchString(v,"\\.") > 0){
                v = "0.";
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
        Pattern p;
        Matcher m;

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

    private void getResult(){
        String result = formsCalcs;
        Pattern p;
        Matcher m;

        result = GetFormResult(result, "\\%");
        result = GetFormResult(result, "\\*");
        result = GetFormResult(result, "\\/");
        result = GetFormResult(result, "\\+");
        result = GetFormResult(result, "\\-");

        result = result.replace("+", "");

        while (Pattern.compile("([\\*\\/\\+\\%])").matcher(result).find()){
            result = "err:syntax";
        }

        if(
            (searchString(result,"\\-") > 1) ||
            (searchString(result,"\\.") > 1)
        ){
            result = "err:syntax";
        }

        displayForm.setText(formsCalcs);
        displayResult.setText(result);
        displayResult.setTextColor(getResources().getColor(R.color.textColor_02));
        isResult = true;
    }

    private void addMemory(String simbol){
        if(formsCalcs.length() > 0) {
            getResult();
            if (searchString(displayResult.getText().toString(), "err//:syntax") <= 0) {
                if (memory.length() > 0) {
                    formsCalcs = memory + simbol + displayResult.getText().toString();
                    getResult();
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
            addOnDisplay(memory);
        }
    }

    private void clearMemory(){
        memory = "";
        clearDisplay();
        displayActivMemory.setVisibility(View.INVISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayActivMemory = (TextView) findViewById(R.id.isMemory);

        scrollDisplayForm = (HorizontalScrollView) findViewById(R.id.horizontalScrollDisplayFormCalc);

        displayForm = (TextView) findViewById(R.id.displayFormCalc);
        //displayResult.setText("");

        displayForm.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                scrollDisplayForm.fullScroll(scrollDisplayResult.FOCUS_RIGHT);
            }
        });

        scrollDisplayResult = (HorizontalScrollView) findViewById(R.id.horizontalScrollDisplayResultCalc);

        displayResult = (TextView) findViewById(R.id.displayResultCalc);
        displayResult.setText("0");

        displayResult.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                scrollDisplayResult.fullScroll(scrollDisplayResult.FOCUS_RIGHT);
            }
        });

        displayResult.setTextColor(getResources().getColor(R.color.textColor_01));

        formsCalcs = "";

        btn_mc = new ButtonEventClick().set(R.id.btn_mc, new Callback(){
            @Override
            public void function(){
                clearMemory();
            }
        });

        btn_mm = new ButtonEventClick().set(R.id.btn_mm, new Callback(){
            @Override
            public void function(){
                addMemory("-");
            }
        });

        btn_mp = new ButtonEventClick().set(R.id.btn_mp, new Callback(){
            @Override
            public void function(){
                addMemory("+");
            }
        });

        btn_mr = new ButtonEventClick().set(R.id.btn_mr, new Callback(){
            @Override
            public void function(){
                showMemory();
            }
        });

        btn_c = new ButtonEventClick().set(R.id.btn_c, new Callback(){
            @Override
            public void function(){
                clearDisplay();
            }
        });

        btn_perc = new ButtonEventClick().set(R.id.btn_perc, new Callback(){
            @Override
            public void function(){
                addOnDisplay((String)"%");
            }
        });

        btn_div = new ButtonEventClick().set(R.id.btn_div, new Callback(){
            @Override
            public void function(){
                addOnDisplay((String)"/");
            }
        });

        btn_mult = new ButtonEventClick().set(R.id.btn_mult, new Callback(){
            @Override
            public void function(){
                addOnDisplay((String)"*");
            }
        });

        btn_min = new ButtonEventClick().set(R.id.btn_min, new Callback(){
            @Override
            public void function(){
                addOnDisplay((String)"-");
            }
        });

        btn_add = new ButtonEventClick().set(R.id.btn_add, new Callback(){
            @Override
            public void function(){
                addOnDisplay((String)"+");
            }
        });

        btn_back = new ButtonEventClick().set(R.id.btn_back, new Callback(){
            @Override
            public void function(){
                backForm();
            }
        });

        btn_result = new ButtonEventClick().set(R.id.btn_result, new Callback(){
            @Override
            public void function(){
                getResult();
            }
        });

        btn_np = new ButtonEventClick().set(R.id.btn_np, new Callback(){
            @Override
            public void function(){
                addOnDisplay((String)".");
            }
        });

        btn_n0 = new ButtonEventClick().set(R.id.btn_n0, new Callback(){
            @Override
            public void function(){
                addOnDisplay((String)"0");
            }
        });

        btn_n1 = new ButtonEventClick().set(R.id.btn_n1, new Callback(){
            @Override
            public void function(){
                addOnDisplay((String)"1");
            }
        });

        btn_n2 = new ButtonEventClick().set(R.id.btn_n2, new Callback(){
            @Override
            public void function(){
                addOnDisplay((String)"2");
            }
        });

        btn_n3 = new ButtonEventClick().set(R.id.btn_n3, new Callback(){
            @Override
            public void function(){
                addOnDisplay((String)"3");
            }
        });

        btn_n4 = new ButtonEventClick().set(R.id.btn_n4, new Callback(){
            @Override
            public void function(){
                addOnDisplay((String)"4");
            }
        });

        btn_n5 = new ButtonEventClick().set(R.id.btn_n5, new Callback(){
            @Override
            public void function(){
                addOnDisplay((String)"5");
            }
        });

        btn_n6 = new ButtonEventClick().set(R.id.btn_n6, new Callback(){
            @Override
            public void function(){
                addOnDisplay((String)"6");
            }
        });

        btn_n7 = new ButtonEventClick().set(R.id.btn_n7, new Callback(){
            @Override
            public void function(){
                addOnDisplay((String)"7");
            }
        });

        btn_n8 = new ButtonEventClick().set(R.id.btn_n8, new Callback(){
            @Override
            public void function(){
                addOnDisplay((String)"8");
            }
        });

        btn_n9 = new ButtonEventClick().set(R.id.btn_n9, new Callback(){
            @Override
            public void function(){
                addOnDisplay((String)"9");
            }
        });
    }
}
