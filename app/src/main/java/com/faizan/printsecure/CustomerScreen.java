package com.faizan.printsecure;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class CustomerScreen extends AppCompatActivity {

    private String urlDocs;
    private ImageView imageView;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_screen);
        imageView = findViewById(R.id.qrcode);

        urlDocs = getIntent().getStringExtra("downloads");

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        int width = point.x;
        int height = point.y;
        int dimen;

        if(width < height) {
            dimen = height;
        }
        else {
            dimen = width;
        }
        dimen = dimen * 3 / 4;

        qrgEncoder = new QRGEncoder(urlDocs, null, QRGContents.Type.TEXT, dimen);
        bitmap = qrgEncoder.getBitmap();
        imageView.setImageBitmap(bitmap);
    }

}