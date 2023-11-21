package com.faizan.printsecure;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DocumentViewer extends AppCompatActivity {

    private ImageView pdfImageView;
    private int currentPage = 0;
    private int totalPageCount;
    private Uri pdfUri;
    private Button nextPageButton;
    private Button prevPageButton;
    private File tempFile;
    private String pdfFilePath;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_document_viewer);

        toolbar = findViewById(R.id.myToolBar);
        pdfImageView = findViewById(R.id.pdfImageView);
        prevPageButton = findViewById(R.id.prevPageButton);
        nextPageButton = findViewById(R.id.nextPageButton);

        setSupportActionBar(toolbar);

        String pdfUrl = getIntent().getStringExtra("currentFile");

        downloadAndDisplayPDF(pdfUrl);
        prevPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage > 0) {
                    currentPage--;
                    loadPDF();
                }
                if(currentPage == 0) {
                    prevPageButton.setVisibility(View.INVISIBLE);
                }
                if(currentPage != totalPageCount) {
                    nextPageButton.setVisibility(View.VISIBLE);
                }
            }
        });
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage < totalPageCount - 1) {
                    currentPage++;
                    loadPDF();
                }
                if(currentPage != 0) {
                    prevPageButton.setVisibility(View.VISIBLE);
                }
                if(currentPage == totalPageCount - 1) {
                    nextPageButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.printButton) {
            pdfFilePath = tempFile.getPath();
            Intent intent = new Intent(getApplicationContext(), CustomPrint.class);
            startActivity(intent);
            item.setVisible(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void printDocument() {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = new PrintDocumentAdapter() {
            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                InputStream input = null;
                OutputStream output = null;

                try {
                    input = new FileInputStream(pdfFilePath);
                    output = new FileOutputStream(destination.getFileDescriptor());

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }

                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                }
                catch (Exception e) {
                    callback.onWriteFailed(e.getMessage());
                }
                finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                        if (output != null) {
                            output.close();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }

                PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder("document.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN);

                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            }
        };
        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 300, 300))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build();

        printManager.print("Document", printAdapter, printAttributes);
    }

    private void downloadAndDisplayPDF(String pdfUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(pdfUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    InputStream inputStream = connection.getInputStream();
                    tempFile = File.createTempFile("temp", ".pdf", getCacheDir());

                    FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                    fileOutputStream.close();

                    pdfUri = Uri.fromFile(tempFile);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadPDF();
                        }
                    });

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void loadPDF() {
        try {
            ParcelFileDescriptor fileDescriptor = getContentResolver().openFileDescriptor(pdfUri, "r");
            PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
            totalPageCount = pdfRenderer.getPageCount();

            if(currentPage == 0) {
                prevPageButton.setVisibility(View.INVISIBLE);
            }
            if(currentPage == totalPageCount - 1) {
                nextPageButton.setVisibility(View.INVISIBLE);
            }

            PdfRenderer.Page page = pdfRenderer.openPage(currentPage);
            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            pdfImageView.setImageBitmap(bitmap);

            page.close();
            pdfRenderer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}