package com.example.wil.odtr;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Display Current Date and Time
       Thread t = new Thread(){
        @Override
           public void run(){
            try{
                while(!isInterrupted()){
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView tdate = findViewById(R.id.dateTime);
                            long date = System.currentTimeMillis();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy (EEEE)\nkk:mm:ss");
                            String datestring = sdf.format(date);
                            tdate.setText(datestring);
                        }
                    });
                }

            }catch(InterruptedException e){

            }
        }
       };
       t.start();

        //Passcode and Keypad
        EditText editText = findViewById(R.id.passCode);
        Keypad keypad = findViewById(R.id.keypad);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText.setTextIsSelectable(true);

        InputConnection ic = editText.onCreateInputConnection(new EditorInfo());
        keypad.setInputConnection(ic);

    }
}
