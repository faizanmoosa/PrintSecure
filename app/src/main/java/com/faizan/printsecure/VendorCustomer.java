package com.faizan.printsecure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class VendorCustomer extends AppCompatActivity {
    Button vendor, customer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_customer);
        vendor = findViewById(R.id.upload);
        customer = findViewById(R.id.customer);
        Intent vendorIntent = new Intent(getApplicationContext(), VendorScreen.class);
        Intent customerIntent = new Intent(getApplicationContext(), CustomerScreen.class);
        vendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(vendorIntent);
            }
        });
        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(customerIntent);
            }
        });
    }
}