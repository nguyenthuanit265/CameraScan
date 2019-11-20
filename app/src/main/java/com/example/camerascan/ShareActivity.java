package com.example.camerascan;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.net.URLConnection;

public class ShareActivity extends Activity {
    Button buttonToShare;
    Button buttonToPickFile;
    TextView textViewToShowPath;

    final private int REQUEST_PICK_FILE = 555;

    Uri _fileUri;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void requestPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_layout);

        buttonToShare = findViewById(R.id.buttonToShare);
        buttonToPickFile = findViewById(R.id.buttonToPickFile);
        textViewToShowPath=findViewById(R.id.textViewToShowPath);

        requestPermissions(ShareActivity.this);

        if (savedInstanceState!=null)
        {
            textViewToShowPath.setText(savedInstanceState.getString("tmpUri"));
            _fileUri=Uri.parse(savedInstanceState.getString("tmpUri"));
        }

        buttonToShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewToShowPath.getText()=="")
                {
                    Toast.makeText(ShareActivity.this, "Please choose file first!",Toast.LENGTH_SHORT).show();
                }
                else {
                    File fileName = new File(_fileUri.getPath());
                    Intent intentToShareSheet = new Intent(Intent.ACTION_SEND);

                    intentToShareSheet.setType(URLConnection.guessContentTypeFromName(fileName.getName()));
                    intentToShareSheet.putExtra(Intent.EXTRA_STREAM,Uri.parse("file://"+fileName.getAbsolutePath()));
                    startActivity(Intent.createChooser(intentToShareSheet, "Share File"));
                }
            }
        });

        buttonToPickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent requestFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                requestFileIntent.setType("file/*");
                startActivityForResult(requestFileIntent, REQUEST_PICK_FILE);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (_fileUri!=null)
            outState.putString("tmpUri",_fileUri.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_FILE:
                    Uri fileUriToShare = data.getData();
                    _fileUri=fileUriToShare;
                    textViewToShowPath.setText(_fileUri.toString());
                    try {
                        ParcelFileDescriptor inputPFD = getContentResolver().openFileDescriptor(fileUriToShare, "r");
                        FileDescriptor fd = inputPFD.getFileDescriptor();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e("MainActivity", "File not found.");
                        return;
                    }
                    break;

            }
        } else if (resultCode == RESULT_CANCELED) {
//            switch (requestCode) {
//                case TAKE_PHOTO_CODE:
//                    // Canceled, so imgPath is set to blank
//                    imgPath = "";
//                    break;
        }
    }

}
