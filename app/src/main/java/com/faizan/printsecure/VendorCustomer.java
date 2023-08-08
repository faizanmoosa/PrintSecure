package com.faizan.printsecure;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class VendorCustomer extends AppCompatActivity {
    private Button vendor, customer;
    private StorageReference storageReference;
    private String downloadURLs = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_customer);
        vendor = findViewById(R.id.upload);
        customer = findViewById(R.id.customer);

        storageReference = FirebaseStorage.getInstance().getReference();

        Intent vendorIntent = new Intent(getApplicationContext(), VendorScreen.class);
        vendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(vendorIntent);
            }
        });
        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult.launch(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int totalItems = data.getClipData().getItemCount();
                for (int i = 0; i < totalItems; i++) {
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    File file = new File(fileUri.getPath());
                    String fileName = file.getName();

                    StorageReference fileToUpload = storageReference.child(fileName);
                    int finalI = i;
                    fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if(finalI == 0) {
                                Toast.makeText(VendorCustomer.this, "PLEASE WAIT", Toast.LENGTH_SHORT).show();
                            }
                            else if(finalI == totalItems - 1) {
                                // Need to add downloadURLs here
                                Intent intent = new Intent(getApplicationContext(), QrScanner.class);
                                intent.putExtra("downloads", downloadURLs);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
            else if (data.getData() != null){
                Uri fileUri = data.getData();
                File file = new File(fileUri.getPath());
                String fileName = file.getName();

                StorageReference fileToUpload = storageReference.child(fileName);
                Toast.makeText(VendorCustomer.this, "PLEASE WAIT", Toast.LENGTH_SHORT).show();
                fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Need to add downloadURL here
                        Intent intent = new Intent(getApplicationContext(), QrScanner.class);
                        intent.putExtra("downloads", downloadURLs);
                        startActivity(intent);
                    }
                });
            }
        }
    }
    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                }
            }
    );
}