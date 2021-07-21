package com.example.oop_project_2;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class TrackerActivity extends AppCompatActivity {
    String itemCategory = "";
    RecyclerView mRecyclerView;
    private ItemListAdapter mAdapter;
    public static LinkedList<ItemViewModel> ItemList = new LinkedList<>();
    LinkedList<ItemViewModel> categorisedList = new LinkedList<>();
    static FirebaseAuth fAuth;
    static FirebaseFirestore fstore;
    static StorageReference mStorageRef;
    static DocumentReference documentReference,documentReference3;
    Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Get the calling intent
        Intent intent = getIntent();
        if(intent.hasExtra("itemCategory")) {
            itemCategory = getIntent().getStringExtra("itemCategory");
        }

        setContentView(R.layout.activity_tracker);
        //get the items belonging to a particular category only
        for (ItemViewModel item : ItemList) {
            if (item.getCategory().toLowerCase().equals(itemCategory.toLowerCase())) {
                categorisedList.add(item);
            }
        }
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        documentReference3 = fstore.collection("users").document(fAuth.getCurrentUser().getEmail() + "1234");
        documentReference3.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Map<String,Object> map1 = documentSnapshot.getData();

                    List<String> ItemTotalList = (List<String>) map1.get("Items");
                    try {
                        Set<String> ItemName = new HashSet<>();
                        for (String Item : ItemTotalList) {
                            ItemName.add(Item.split("@")[0]);


                        }

                        for (String Itemn : ItemName) {
                            ItemViewModel i = new ItemViewModel();
                            for (String Item : ItemTotalList) {
                                if (Item.split("@")[0].equals(Itemn)) {
                                    if ((Item.split("@")[1]).equals("Name")) {

                                        i.title = Item.split("@")[2];
                                    }

                                    if ((Item.split("@")[1]).equals("Notes")) {
                                        try {
                                            i.notes = Item.split("@")[2];
                                        } catch (Exception e) {
                                            i.notes = "";
                                        }
                                    }
                                    if ((Item.split("@")[1]).equals("Quantity")) {
                                        i.quantity = Integer.valueOf(Item.split("@")[2]);

                                    }
                                    if ((Item.split("@")[1]).equals("Category")) {
                                        i.category = Item.split("@")[2];
                                    }
                                }

                            }

                            ItemViewModel item = i;
                            ItemList.add(item);
                            int itemLastSize = ItemList.size();
                            item.setCategory(i.getCategory());
                            ItemList.add(item);

                            categorisedList.add(item);
                            // Notify the adapter, that the data has changed.
                            mRecyclerView.getAdapter().notifyItemInserted(itemLastSize);
                            // Scroll to the bottom.
                            mRecyclerView.smoothScrollToPosition(itemLastSize);

                        }
                    }
                    catch (Exception e){

                    }

            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadToFirebase();
                startActivity(new Intent(TrackerActivity.this,Dashboard.class));


            }
        });

        FloatingActionButton itemFAB= findViewById(R.id.itemFAB);
        itemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemLastSize = ItemList.size();
                ItemViewModel item = new ItemViewModel();
                item.setCategory(itemCategory);
                ItemList.add(item);

                categorisedList.add(item);
                // Notify the adapter, that the data has changed.
                mRecyclerView.getAdapter().notifyItemInserted(itemLastSize);
                // Scroll to the bottom.
                mRecyclerView.smoothScrollToPosition(itemLastSize);

            }
        });

        EditText editText = findViewById(R.id.searchItem);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }

            private void filter(String text) {
                LinkedList<ItemViewModel> filteredList = new LinkedList<>();
                for (ItemViewModel item : ItemList) {
                    if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
                mAdapter.filteredList(filteredList);
            }
        });



        // Get a handle to the RecyclerView.
        mRecyclerView = findViewById(R.id.item_recycler_view);

        // Create an adapter and supply the data to be displayed.
        mAdapter = new ItemListAdapter(this,categorisedList);
        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    public static void UploadItemToFirebase(){

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        DocumentReference documentReference;
        documentReference = fstore.collection("users").document(fAuth.getCurrentUser().getEmail() + "123");

    }

 public static void UploadToFirebase (){
     fAuth = FirebaseAuth.getInstance();
     fstore = FirebaseFirestore.getInstance();
     mStorageRef = FirebaseStorage.getInstance().getReference();
     DocumentReference documentReference1;
     List<String> listOfLists = new ArrayList<>();

//     for(ItemViewModel Items: ItemList){
//
//        Bitmap selectedImageUri = Items.getImg();
//        getImageUri();
//         StorageReference reference = mStorageRef.child("images/" + fAuth.getCurrentUser().getEmail() + "/Profile_image.jpg");
//         reference.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//             @Override
//             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                 // Set the image in ImageView
//
//             }
//         }).addOnFailureListener(new OnFailureListener() {
//             @Override
//             public void onFailure(@NonNull Exception e) {
//                 Toast.makeText(UserProfile.this,e.getMessage(),Toast.LENGTH_LONG);
//             }
//         });
//     }
     for(ItemViewModel Items: ItemList ){
//         ArrayList<String> ItemDetails = new ArrayList<>();
//         ItemDetails.add(Items.getTitle());
//         ItemDetails.add(Items.getNotes());
//         ItemDetails.add(Items.getCategory());
//         ItemDetails.add(Items.getQuantity() + "");
         listOfLists.add(Items.getTitle() + "@Name@" + Items.getTitle());
         listOfLists.add(Items.getTitle() + "@Notes@" + Items.getNotes());
         listOfLists.add(Items.getTitle() + "@Category@" + Items.getCategory());
         listOfLists.add(Items.getTitle() + "@Quantity@" + Items.getQuantity());

     }
       // for(String category:Dashboard.categoryList){

            //for(ItemViewModel Item:ItemList){
            //if(Item.getCategory().equals(category)){

                documentReference1 = fstore.collection("users").document(fAuth.getCurrentUser().getEmail() + "1234");

                //Log.d("lol", Item.getTitle());
               // documentReference1 = fstore.document("users/"+ fAuth.getCurrentUser().getEmail()  + category+"/" + Item.getTitle());

//                Map<String,Object> map = new HashMap<>();
//                map.put("Title",Item.getTitle());
//                map.put("Notes",Item.getNotes());
//                map.put("Quantity",Item.getQuantity());
//                map.put("Category", category);
                Map <String,Object> map2 = new HashMap<>();
//                LinkedList <ItemViewModel> list1 = new LinkedList<>();
                //map2.put(Item.getTitle(),map);
                map2.put("Categories",Dashboard.categoryList);
                map2.put("Items",listOfLists);

                //LinkedList <ItemViewModel> list1 = new LinkedList<>();
                documentReference1.set(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Log.d("TAG","OnSuccess : userprofile is created for " + username);


                    }
                });
            //}
        //}

        //}


 }
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}

