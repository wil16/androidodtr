package com.example.wil.odtr;


import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private Camera.PictureCallback pictureCallback;
    private int cameraId = 0;
    private Intent intent;
    private ImageView ImageViewHolder;
    Bitmap bitmap;
    boolean check = true;

    private EditText passCode;
    private ImageButton btnOK;
    public static final String USERID = "user_id";
    private static final String URL = "http://192.168.1.38/onlinedtr/android/insertdata.php";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Display Current Time
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
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
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

        // Display current date
        Calendar calendar = Calendar.getInstance();
        TextView textViewDate = findViewById(R.id.date);
        SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy \nEEEE");
        String currenDate = sd.format(calendar.getTime());
        textViewDate.setText(currenDate);

        //Passcode textbox and keypad
        passCode = findViewById(R.id.passCode);
        Keypad keypad = findViewById(R.id.keypad);
        passCode.setRawInputType(InputType.TYPE_CLASS_TEXT);
        passCode.setTextIsSelectable(true);
        InputConnection ic = passCode.onCreateInputConnection(new EditorInfo());
        keypad.setInputConnection(ic);

        // Button OK
        btnOK = findViewById(R.id.keyok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //camera.takePicture(null, null, pictureCallback);
                //ImageUploadToServerFunction();
                //intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(intent, 7);
                //ImageUploadToServerFunction();
                sendData();
                passCode.getText().clear();
            }
        });

    }

    //Send data to server
    private void sendData() {
        final String user_id = passCode.getText().toString();
        if(user_id.matches("")){
            Toast.makeText(this, "Please enter your keycode.", Toast.LENGTH_LONG).show();
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put(USERID, user_id);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }

}