package com.example.wil.odtr;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private Camera.PictureCallback pictureCallback;
    private int cameraId = 0;

    private EditText passCode;
    private ImageButton btnOK;
    public static final String USERID = "user_id";
    private static final String URL = "http://192.168.1.38/onlinedtr/android/insertdata.php";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Camera
        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);
            }
        }
        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap cbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), null, true);
                String pathFilename = currentDateFormat();
                storePhotoToStorage(cbmp, pathFilename);
                MainActivity.this.camera.startPreview();
            }
        };

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
                                SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy (EEEE)\nHH:mm:ss");
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

        //Passcode textbox and keypad
        passCode = findViewById(R.id.passCode);
        Keypad keypad = findViewById(R.id.keypad);
        passCode.setRawInputType(InputType.TYPE_CLASS_TEXT);
        passCode.setTextIsSelectable(true);
        InputConnection ic = passCode.onCreateInputConnection(new EditorInfo());
        keypad.setInputConnection(ic);

        btnOK = findViewById(R.id.keyok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
                passCode.getText().clear();
            }
        });
    }

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

    private int findFrontFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i=0; i < numberOfCameras; i++){
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private void storePhotoToStorage(Bitmap cbmp, String pathFilename) {
        File outputFile = new File(Environment.getExternalStorageDirectory(),
                "/DCIM/"+"photo_"+pathFilename+".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            cbmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTime = dateFormat.format(new Date());
        return  currentTime;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try{
            camera = Camera.open();
        }catch (Exception e){

        }
        Camera.Parameters parameters;
        parameters = camera.getParameters();
        parameters.setPreviewFrameRate(20);
        parameters.setPreviewSize(300, 300);
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

}