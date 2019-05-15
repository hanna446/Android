package com.example.expoandroid3;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private StorageReference myStorage;
    private DatabaseReference myDataBase;
    private Intent intent;
    private ImageView image;
    private ProgressBar progressBar;
    public static final int GALERY_CODE = 1;
    public static final int CAMERA_CODE = 2;

    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fabNewImage);
        image = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        myStorage = FirebaseStorage.getInstance().getReference("uploads");
        myDataBase = FirebaseDatabase.getInstance().getReference("uploads");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Eliga el orginen de la imagen");
                builder.setNegativeButton("Galeria", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, GALERY_CODE);
                    }
                });
                builder.setPositiveButton("Camara", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivityForResult(intent, CAMERA_CODE);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GALERY_CODE:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    Picasso.get().load(imageUri).into(image);
                    if (imageUri != null) {
                        StorageReference filePath = myStorage.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
                        filePath.putFile(imageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setProgress(0);
                                            }
                                        }, 5000);
                                        Toast.makeText(MainActivity.this, "Imagen subida correctamente", Toast.LENGTH_LONG).show();
                                        //Image image = new Image(random(),taskSnapshot.get)
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressBar.setProgress((int) progress);
                            }
                        });

                    } else
                        Toast.makeText(this, "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show();

                }
                break;
            case CAMERA_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uriPhoto = (Uri) data.getExtras().get("data");
                    image.setImageURI(uriPhoto);
                    Picasso.get().load(uriPhoto).into(image);
                    /*Uri uri = data.getData();*/
                    StorageReference filePath = myStorage.child("fotos_camera").child(uriPhoto.getLastPathSegment());
                    filePath.putFile(uriPhoto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(MainActivity.this, "Foto subida correctamente", Toast.LENGTH_LONG).show();

                        }
                    });
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(resolver.getType(uri));
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(25);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
