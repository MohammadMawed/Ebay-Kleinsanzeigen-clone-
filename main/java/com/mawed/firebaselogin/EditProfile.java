package com.mawed.firebaselogin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class EditProfile extends AppCompatActivity {

    TextView username;
    TextView emailTextView;
    ImageView profilePic;
    String userID;
    Uri ImageUri;
    RecyclerView recyclerViewSavedItem;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch aSwitch;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference, databaseReference1;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    DocumentReference documentReference;
    FirebaseRecyclerOptions<modelClassDocs> options;
    FirebaseRecyclerAdapter<modelClassDocs, ImageAdapter> adapter;
    private static final int PICK_IMAGE_REQUEST = 1;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        username = findViewById(R.id.textView);
        emailTextView = findViewById(R.id.emailTextView);
        profilePic = findViewById(R.id.profilePic);
        recyclerViewSavedItem = findViewById(R.id.favorite_recyclerView);
        aSwitch = findViewById(R.id.switch1);


        SpacingItemInRecyclerView spacingItemInRecyclerView = new SpacingItemInRecyclerView(30);
        recyclerViewSavedItem.addItemDecoration(spacingItemInRecyclerView);
        recyclerViewSavedItem.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerViewSavedItem.setHasFixedSize(true);
        recyclerViewSavedItem.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference1 = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userID = firebaseAuth.getCurrentUser().getUid();

        documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                username.setText(" Hello, " + value.getString("username"));
            }
        });

        assert user != null;
        String email = user.getEmail();
        emailTextView.setText(email);

        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("users/" + firebaseAuth.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilePic);
            }
        });

        aSwitch.setTextOff("My Offers");
        aSwitch.setTextOn("My Favorite");
        loadFavoriteItems();
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    loadOwnItems();
                }else {
                    loadFavoriteItems();
                }
            }
        });

    }

    public void SignOutMethod(View view) {
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(EditProfile.this, MainActivity.class));
    }

    public void DeleteAccount(View view) {
        ShowAlertDialog();
    }

    public void ChangePassword(View view) {
        startActivity(new Intent(EditProfile.this, UpdateEmailOrPassword.class).putExtra("password", 1));
    }

    public void ChangeMail(View view) {
        startActivity(new Intent(EditProfile.this, UpdateEmailOrPassword.class).putExtra("email", 2));
    }


    private void ShowAlertDialog() {

        AlertDialog alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("Deleting Account")
                .setMessage("Are you sure ? you are about to delete your account")
                .setIcon(R.drawable.trash)
                .setNegativeButton("Cancel", (dialog, which) -> finish())
                .setPositiveButton("Delete", (dialog, which) -> {
                    //Deleting user's account

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    user.delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    Toast.makeText(EditProfile.this, "Account deleted successfully",
                                            Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(EditProfile.this, MainActivity.class));
                                }
                            });
                })
                .create();
        alertDialogBuilder.show();
    }

    public void ChangeProfilePic(View view) {
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
            //profilePic.setImageURI(ImageUri);
            //Uploading the image to firebase
            uploadImageToFirebase(ImageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageReference.child("users/" + firebaseAuth.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditProfile.this, "Image updated Successfully", Toast.LENGTH_LONG).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profilePic);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Image updated failed caused by " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void loadFavoriteItems() {
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("save").child(userID);
        options = new FirebaseRecyclerOptions.Builder<modelClassDocs>().setQuery(databaseReference1, modelClassDocs.class).build();
        adapter = new FirebaseRecyclerAdapter<modelClassDocs, ImageAdapter>(options) {
            @NonNull
            @Override
            public ImageAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view, parent, false);
                return new ImageAdapter(view);
            }

            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull ImageAdapter holder, int position, @NonNull modelClassDocs model) {
                String timeAgo = calculateTimeAge(model.getTime());
                holder.title.setText(model.getTitle());
                holder.time.setText(timeAgo);
                holder.priceView.setText(model.getPrice() + "€");
                holder.location.setText(model.getCity());
                String UserID = model.getUserID();
                String ImageID = model.getImageID();
                StorageReference fileRef11 = FirebaseStorage.getInstance().getReference().child("offers/" + ImageID + ".jpg");
                fileRef11.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).centerCrop().into(holder.imageView);
                    }
                });
                holder.itemViewView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //The image ID is the same like post ID
                        startActivity(new Intent(EditProfile.this, ViewSingleItem.class).putExtra("postID", ImageID)
                                .putExtra("providerUserID", UserID).putExtra("data", getRef(position).getKey()));
                    }
                });
            }
        };
        recyclerViewSavedItem.setAdapter(adapter);
        adapter.startListening();

    }

    private void loadOwnItems() {
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("ownItems").child(userID);
        options = new FirebaseRecyclerOptions.Builder<modelClassDocs>().setQuery(databaseReference1, modelClassDocs.class).build();
        adapter = new FirebaseRecyclerAdapter<modelClassDocs, ImageAdapter>(options) {
            @NonNull
            @Override
            public ImageAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view, parent, false);
                return new ImageAdapter(view);
            }

            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull ImageAdapter holder, int position, @NonNull modelClassDocs model) {
                String timeAgo = calculateTimeAge(model.getTime());
                holder.title.setText(model.getTitle());
                holder.time.setText(timeAgo);
                holder.priceView.setText(model.getPrice() + "€");
                holder.location.setText(model.getCity());
                String UserID = model.getUserID();
                String ImageID = model.getImageID();
                StorageReference fileRef11 = FirebaseStorage.getInstance().getReference().child("offers/" + ImageID + ".jpg");
                fileRef11.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).centerCrop().into(holder.imageView);
                    }
                });
                holder.itemViewView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //The image ID is the same like post ID
                        startActivity(new Intent(EditProfile.this, ViewSingleItem.class).putExtra("postID", ImageID)
                                .putExtra("providerUserID", UserID).putExtra("data", getRef(position).getKey()));
                    }
                });
            }
        };
        recyclerViewSavedItem.setAdapter(adapter);
        adapter.startListening();

    }

    private String calculateTimeAge(String time) {
        String convTime = null;

        String prefix = "";
        String suffix = "ago";

        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
            Date pasTime = dateFormat.parse(time);
            Date nowTime = new Date();
            long dateDiff = nowTime.getTime() - pasTime.getTime();
            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day = TimeUnit.MILLISECONDS.toDays(dateDiff);
            if (second < 60) {
                convTime = second + " Seconds " + suffix;
            } else if (minute < 60) {
                convTime = minute + " Minutes " + suffix;
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
                convTime = day + " Days " + suffix;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }

        return convTime;
    }
}

//01.01.2021
//head add friend
//else do not