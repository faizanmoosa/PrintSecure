package com.faizan.printsecure;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class VendorCustomer extends AppCompatActivity {
    private Button vendor, customer;
    private StorageReference storageReference;
    private String downloadURLs;
    private Handler myHandler;
    private ProgressBar loadingProgressBar;
    private ImageButton imageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_customer);
        vendor = findViewById(R.id.vendor);
        customer = findViewById(R.id.customer);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        imageButton = findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();

        Intent vendorIntent = new Intent(getApplicationContext(), CustomerScreen.class);
        vendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vendorIntent = new Intent(getApplicationContext(), QrScanner.class);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        downloadURLs = "";
        if (resultCode == RESULT_OK) {
            customer.setVisibility(View.INVISIBLE);
            loadingProgressBar.setVisibility(View.VISIBLE);
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
                            storageReference.child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadURLs += uri + "\n";
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            });
                            if(finalI == 0) {
                                Toast.makeText(VendorCustomer.this, "Please wait!", Toast.LENGTH_LONG).show();
                            }
                            else if(finalI == totalItems - 1) {
                                myHandler = new Handler();
                                myHandler.postDelayed(new Runnable() {
                                    public void run() {
                                        loadingProgressBar.setVisibility(View.INVISIBLE);
                                        customer.setVisibility(View.VISIBLE);
                                        Intent intent = new Intent(getApplicationContext(), CustomerScreen.class);
                                        intent.putExtra("downloads", downloadURLs);
                                        startActivity(intent);
                                    }
                                }, 2000);
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
                Toast.makeText(VendorCustomer.this, "Please wait!", Toast.LENGTH_SHORT).show();
                fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadURLs += uri + "\n";
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                            }
                        });
                        myHandler = new Handler();
                        myHandler.postDelayed(new Runnable() {
                            public void run() {
                                loadingProgressBar.setVisibility(View.INVISIBLE);
                                customer.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(getApplicationContext(), CustomerScreen.class);
                                intent.putExtra("downloads", downloadURLs);
                                startActivity(intent);
                            }
                        }, 2000);
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