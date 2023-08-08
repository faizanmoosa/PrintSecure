package com.faizan.printsecure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class FilesLayout extends AppCompatActivity {

    private String downloadURLs;
    private TextView textView;
    private ListView listView;
    private ArrayList<String> filesList;
    private String files[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_layout);
        listView = findViewById(R.id.listView);

        downloadURLs = getIntent().getStringExtra("documents");

        StringTokenizer stringTokenizer = new StringTokenizer(downloadURLs, "\n");
        while (stringTokenizer.hasMoreTokens()) {
            filesList.add(stringTokenizer.nextToken());
        }

        files = new String[filesList.size()];
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, files);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), DocumentViewer.class);
                String currentFile = listView.getItemAtPosition(i).toString();
                intent.putExtra("documents", downloadURLs);
                intent.putExtra("currentSong", currentFile);
                intent.putExtra("position", i);
                startActivity(intent);
            }
        });
    }
}