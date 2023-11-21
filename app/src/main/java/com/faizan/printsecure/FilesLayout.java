package com.faizan.printsecure;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class FilesLayout extends AppCompatActivity {

    private String downloadURLs;
    private ListView listView;
    private ArrayList<String> filesList;
    private String files[];
    private Toolbar toolbar;
    private StorageReference storageReference;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_layout);
        listView = findViewById(R.id.listView);
        toolbar = findViewById(R.id.myToolBar);

        setSupportActionBar(toolbar);

        downloadURLs = getIntent().getStringExtra("documents");

        filesList = new ArrayList<String>();

        StringTokenizer stringTokenizer = new StringTokenizer(downloadURLs, "\n");
        while (stringTokenizer.hasMoreTokens()) {
            filesList.add(stringTokenizer.nextToken());
        }

        files = new String[filesList.size()];
        for(int i = 0; i < filesList.size(); i++) {
            files[i] = "Document " + (i + 1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, files);


        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), DocumentViewer.class);

                String currentFile = filesList.get(i);

                storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(currentFile);
                storageReference.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                intent.putExtra("currentFile", currentFile);
                                startActivity(intent);

                                handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        storageReference.delete();
                                    }
                                }, 4000);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(FilesLayout.this, "Sorry, can't access again!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}