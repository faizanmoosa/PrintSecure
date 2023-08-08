package com.faizan.printsecure;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class QrScanner extends AppCompatActivity {

    private TextView textView;
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);
        textView = findViewById(R.id.testView);
        text = getIntent().getStringExtra("downloads");
        textView.setText(text);
    }
}