package com.example.expoandroid3;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private StorageReference myStorage;
    private Intent intent;
    private ImageView image;
    public static final int GALERY_CODE = 1;
    public static final int CAMERA_CODE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fabNewImage);
        image = findViewById(R.id.imageView);
        myStorage = FirebaseStorage.getInstance().getReference();

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
                        startActivityForResult(intent,GALERY_CODE);
                    }
                });
                builder.setPositiveButton("Camara", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivityForResult(intent,CAMERA_CODE);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case GALERY_CODE:
                if (resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    Picasso.get().load(uri).into(image);
                    StorageReference filePath = myStorage.child("fotos_galery").child(uri.getLastPathSegment());
                    filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(MainActivity.this, "Imagen subida correctamente", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;
            case CAMERA_CODE:
                if (resultCode == RESULT_OK){
                    Uri uriPhoto = (Uri)data.getExtras().get("data");
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
}
