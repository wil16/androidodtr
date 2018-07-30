package com.example.wil.odtr;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.inputmethod.InputConnection;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.view.View;


public class Keypad extends LinearLayout implements View.OnClickListener {

    private ImageButton key1, key2, key3, key4, key5, key6, key7, key8, key9, key0,keydelete, keyok;
    private SparseArray<String> keyValues = new SparseArray<>();
    private InputConnection inputConnection;

    public Keypad(Context context){
        this(context, null, 0);
    }

    public Keypad(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public Keypad(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.keypad, this, true);
        key1 = (ImageButton) findViewById(R.id.key1);
        key1.setOnClickListener(this);
        key2 = (ImageButton) findViewById(R.id.key2);
        key2.setOnClickListener(this);
        key3 = (ImageButton) findViewById(R.id.key3);
        key3.setOnClickListener(this);
        key4 = (ImageButton) findViewById(R.id.key4);
        key4.setOnClickListener(this);
        key5 = (ImageButton) findViewById(R.id.key5);
        key5.setOnClickListener(this);
        key6 = (ImageButton) findViewById(R.id.key6);
        key6.setOnClickListener(this);
        key7 = (ImageButton) findViewById(R.id.key7);
        key7.setOnClickListener(this);
        key8 = (ImageButton) findViewById(R.id.key8);
        key8.setOnClickListener(this);
        key9 = (ImageButton) findViewById(R.id.key9);
        key9.setOnClickListener(this);
        key0 = (ImageButton) findViewById(R.id.key0);
        key0.setOnClickListener(this);
        keydelete = (ImageButton) findViewById(R.id.keydelete);
        keydelete.setOnClickListener(this);
        keyok = (ImageButton) findViewById(R.id.keyok);
        keyok.setOnClickListener(this);

        keyValues.put(R.id.key1, "1");
        keyValues.put(R.id.key2, "2");
        keyValues.put(R.id.key3, "3");
        keyValues.put(R.id.key4, "4");
        keyValues.put(R.id.key5, "5");
        keyValues.put(R.id.key6, "6");
        keyValues.put(R.id.key7, "7");
        keyValues.put(R.id.key8, "8");
        keyValues.put(R.id.key9, "9");
        keyValues.put(R.id.key0, "0");
        keyValues.put(R.id.keyok, "\n");
    }

    @Override
    public void onClick(View view) {
        if(inputConnection == null)
            return;

        if(view.getId()==R.id.keydelete){
            CharSequence selectedText = inputConnection.getSelectedText(0);

            if(TextUtils.isEmpty(selectedText)){
                inputConnection.deleteSurroundingText(1,0);
            }else {
                inputConnection.commitText("",1);
            }
        }else {
            String value = keyValues.get(view.getId());
            inputConnection.commitText(value, 1);
        }
    }

    public void setInputConnection(InputConnection ic){
        inputConnection = ic;
    }
}
