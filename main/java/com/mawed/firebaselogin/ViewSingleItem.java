package com.mawed.firebaselogin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.internal.cache.DiskLruCache;


public class ViewSingleItem extends AppCompatActivity {

    ImageView imageView, profilePic;
    TextView descriptionView, priceView, categoryView, cityName, timeView, titleView, providerUsername, viewsNumberTextView;
    Button sendMessageButton;
    ImageButton saveButton;
    Snackbar snackbar;
    ConstraintLayout layout;
    String description, price, category, city, imageID, time, title, providerUserID, timeForSave, userIDProvider, viewNumberText;
    int views = 0;
    ImageButton editButton;

    DatabaseReference databaseReference, databaseReference1;
    FirebaseAuth firebaseAuth;
    StorageReference storage;
    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_item);

        imageView = findViewById(R.id.image_single_view_activity);
        profilePic = findViewById(R.id.profilePicSingleView);
        providerUsername = findViewById(R.id.providerUsername);
        descriptionView = findViewById(R.id.text_single_view_activity);
        priceView = findViewById(R.id.priceView);
        categoryView = findViewById(R.id.categoryView);
        viewsNumberTextView = findViewById(R.id.vieNumberView);
        cityName = findViewById(R.id.locationView);
        timeView = findViewById(R.id.timeTextView);
        titleView = findViewById(R.id.titleTextView);
        sendMessageButton = findViewById(R.id.sendButton);
        saveButton = findViewById(R.id.saveButton);
        layout = findViewById(R.id.layout);
        editButton = findViewById(R.id.editButton);

        storage = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("offers");
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("save");
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        String data = getIntent().getStringExtra("data");
        String postID = getIntent().getStringExtra("postID");
        providerUserID = getIntent().getStringExtra("providerUserID");
        timeForSave = getIntent().getStringExtra("time");
        String currentUserID = firebaseAuth.getCurrentUser().getUid();
        String viewNumberText =  getIntent().getStringExtra("views");

        databaseReference.child(data).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    description = snapshot.child("description").getValue().toString();
                    category = snapshot.child("category").getValue().toString();
                    price = snapshot.child("price").getValue().toString();
                    city = snapshot.child("city").getValue().toString();
                    imageID = snapshot.child("imageID").getValue().toString();
                    time = calculateTimeAge(snapshot.child("Time").getValue().toString());
                    title = snapshot.child("title").getValue().toString();
                    userIDProvider = snapshot.child("userID").getValue().toString();
                    StorageReference fileRef11 = FirebaseStorage.getInstance().getReference().child("offers/" + imageID + ".jpg");
                    fileRef11.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(ViewSingleItem.this).load(uri).into(imageView);
                        }
                    });
                    descriptionView.setText(description);
                    priceView.setText(price + "€");
                    categoryView.setText(category);
                    cityName.setText(city);
                    timeView.setText(time);
                    titleView.setText(title);
                    viewsNumberTextView.setText(viewNumberText);

                    editButton.setVisibility(View.INVISIBLE);

                    if (userIDProvider.equals(currentUserID)){
                        editButton.setVisibility(View.VISIBLE);
                    }
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent editIntent = new Intent( ViewSingleItem.this, Post.class).putExtra("description", description)
                                    .putExtra("category", category).putExtra("price", price).putExtra("city", city).putExtra("imageID", imageID)
                                    .putExtra("title", title);
                            startActivity(editIntent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (providerUserID != null){
            firebaseFirestore = FirebaseFirestore.getInstance();
            documentReference = firebaseFirestore.collection("users").document(providerUserID);
            documentReference.addSnapshotListener(ViewSingleItem.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    providerUsername.setText(value.getString("username"));
                }
            });
            StorageReference fileRef = storageReference.child("users/" + providerUserID + "/profile.jpg");
            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profilePic);
                }
            });
        }
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent chatIntent = new Intent(ViewSingleItem.this, chating.class);
                chatIntent.putExtra("UserID", userID);
                startActivity(chatIntent);*/
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveButton.getTag().equals("save")){
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("title", title);
                    hashMap.put("description", description);
                    hashMap.put("imageID", imageID);
                    hashMap.put("category", category);
                    hashMap.put("price", price);
                    hashMap.put("city", city);
                    hashMap.put("Time", timeForSave);
                    hashMap.put("userID", providerUserID);
                    databaseReference1.child(firebaseAuth.getCurrentUser().getUid()).child(imageID).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Toast.makeText(Post.this, "Image uploaded successfully", Toast.LENGTH_LONG).show();
                        }
                    });
                    saveButton.setImageResource(R.drawable.ic_baseline_bookmark_24);
                    saveButton.setTag("saved");
                }else{
                    databaseReference1.child(firebaseAuth.getCurrentUser().getUid()).child(data).removeValue();
                    saveButton.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                    snackbar = Snackbar.make(layout, "You removed this item from favorite list", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put("title", title);
                                    hashMap.put("description", description);
                                    hashMap.put("imageID", imageID);
                                    hashMap.put("category", category);
                                    hashMap.put("price", price);
                                    hashMap.put("city", city);
                                    hashMap.put("Time", timeForSave);
                                    hashMap.put("userID", providerUserID);
                                    databaseReference1.child(firebaseAuth.getCurrentUser().getUid()).child(imageID).setValue(hashMap);
                                    Snackbar snackbar1 = Snackbar.make(layout,"added Successful",Snackbar.LENGTH_LONG);
                                    snackbar1.show();
                                }
                            });
                    snackbar.show();
                }
            }
        });
        isSaved(data);
    }

    private String calculateTimeAge(String time) {
        String convTime = null;

        String prefix = "";
        String suffix = "Ago";

        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
            Date pasTime = dateFormat.parse(time);
            Date nowTime = new Date();
            long dateDiff = nowTime.getTime() - pasTime.getTime();
            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);
            if (second < 60) {
                convTime = second + " Seconds " + suffix;
            } else if (minute < 60) {
                convTime = minute + " Minutes "+suffix;
            } else if (hour < 24) {
                convTime = hour + " Hours " + suffix;
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = (day / 360) + " Years " + suffix;
                } else if (day > 30) {
                    convTime = (day / 30) + " Months " + suffix;
                } else {
                    convTime = (day / 7) + " Week " + suffix;
                }
            } else if (day < 7) {
                convTime = day+" Days "+suffix;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }

        return convTime;
    }

    private void isSaved(String itemID){
       DatabaseReference reference =  FirebaseDatabase.getInstance().getReference().child("save")
               .child(firebaseAuth.getCurrentUser().getUid());

       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.child(itemID).exists()){
                    saveButton.setImageResource(R.drawable.ic_baseline_bookmark_24);
                    saveButton.setTag("saved");
               }else {
                   saveButton.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                   saveButton.setTag("save");
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }
}