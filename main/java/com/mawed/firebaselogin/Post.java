package com.mawed.firebaselogin;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.grpc.Context;


public class Post extends AppCompatActivity {

    ImageView imageView;
    Button upLoadButton;
    EditText descText, priceEditText, cityEditText, titleEditText;
    Spinner spinner;
    Uri ImageUri;
    String userID;
    DatabaseReference DataRef, owmItemRef;
    StorageReference StorageRef;
    StorageTask uploadTask;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    private static final int PICK_IMAGE_REQUEST = 1;
    boolean isImageAdd = false;
    String[] categoryArray = {"Cars", "Smartphones", "TV's", "Computers", "Laptops", "Tablets", "Bikes", "Properties"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        imageView = findViewById(R.id.imageView6);
        upLoadButton = findViewById(R.id.uploadButton);
        descText = findViewById(R.id.descEditText);
        priceEditText = findViewById(R.id.editTextPrice);
        cityEditText = findViewById(R.id.editTextCity);
        titleEditText = findViewById(R.id.editTextTitle);
        spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);

        String description = getIntent().getStringExtra("description");
        String category = getIntent().getStringExtra("category");
        String price = getIntent().getStringExtra("price");
        String city = getIntent().getStringExtra("city");
        String imageID = getIntent().getStringExtra("imageID");
        String title = getIntent().getStringExtra("title");

        owmItemRef = FirebaseDatabase.getInstance().getReference().child("ownItems");
        DataRef = FirebaseDatabase.getInstance().getReference().child("offers");
        StorageRef = FirebaseStorage.getInstance().getReference().child("offers");

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        userID = firebaseAuth.getCurrentUser().getUid();

        descText.setImeOptions(EditorInfo.TYPE_TEXT_VARIATION_LONG_MESSAGE);

        titleEditText.setText(title);
        descText.setText(description);
        priceEditText.setText(price);
        cityEditText.setText(city);

        StorageReference fileRef11 = FirebaseStorage.getInstance().getReference().child("offers/" + imageID + ".jpg");
        fileRef11.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(Post.this).load(uri).into(imageView);
            }
        });

        upLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = titleEditText.getText().toString().trim();
                final String description = descText.getText().toString().trim();
                final String price = priceEditText.getText().toString();
                final String city = cityEditText.getText().toString();
                if (description == null) {
                    Toast.makeText(Post.this, "Add description to you offer!", Toast.LENGTH_LONG).show();
                }
                if (price == null) {
                    Toast.makeText(Post.this, "Add price to your offer!", Toast.LENGTH_LONG).show();
                }
                if (city == null){
                    Toast.makeText(Post.this, "Add your city!", Toast.LENGTH_LONG).show();
                }
                if (uploadTask != null && uploadTask.isInProgress() ){
                    Toast.makeText(Post.this, "Upload in progress", Toast.LENGTH_LONG).show();
                } else if(isImageAdd && description != null && price  != null && city != null) {
                    upLoadImage(title, description, price, city);
                }
            }
        });
    }
    public void chooseImage(View view) {
        //Open the gallery
        Intent oenGalleryIntent = new Intent();
        oenGalleryIntent.setType("image/*");
        oenGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(oenGalleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            assert data != null;
            ImageUri = data.getData();
            //Uploading the image to firebase
            imageView.setImageURI(ImageUri);
            isImageAdd = true;
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void upLoadImage(final String title,final String description, final String price, final String city){
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
        long timeInMillis = c.getTimeInMillis();
        String time = df.format(c.getTime());
        String imageID = Long.toString(timeInMillis);
        String category = spinner.getSelectedItem().toString();
        uploadTask = StorageRef.child( imageID + "."+ getFileExtension(ImageUri)).putFile(ImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageRef.child(imageID + "."+ getFileExtension(ImageUri))
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("title", title);
                        hashMap.put("description", description);
                        hashMap.put("ImageUri", ImageUri.toString());
                        hashMap.put("imageID", imageID);
                        hashMap.put("category", category);
                        hashMap.put("price", price);
                        hashMap.put("city", city);
                        hashMap.put("Time", time);
                        hashMap.put("userID", userID);
                        owmItemRef.child(userID).child(imageID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                        DataRef.child(imageID).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Toast.makeText(Post.this, "Image uploaded successfully", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Post.this, MainUI.class));
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }
}