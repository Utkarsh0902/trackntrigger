package com.example.oop_project_2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.util.LinkedList;

public class GalleryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    Uri imageUri;
    ImageView image;
    Button saveButton;
    int position;
    LinkedList<ItemViewModel> ItemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        saveButton = findViewById(R.id.save_button);
        image = findViewById(R.id.imageView);

        getIncomingIntent();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GalleryActivity.this, TrackerActivity.class);
                startActivity(intent);

            }
        });

        image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                image.setImageBitmap(bitmap);
                setImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void getIncomingIntent(){
        if(getIntent().hasExtra("position")){
            position = getIntent().getIntExtra("position",100);

        }
    }

    private void setImage(Bitmap bm){
        TrackerActivity.ItemList.get(position).setImg(bm);

    }
}