
package com.mawed.firebaselogin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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

public class MainUI extends AppCompatActivity {

    TextView username;
    ImageView profilePic;
    EditText searchField;
    RecyclerView recyclerView;
    String userID;
    SwipeRefreshLayout swipeRefreshLayout;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    DocumentReference documentReference;
    DatabaseReference databaseReference;

    FirebaseRecyclerOptions<modelClassDocs> options;
    FirebaseRecyclerAdapter<modelClassDocs, ImageAdapter> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_u_i);

        username = findViewById(R.id.usernameTextView);
        profilePic = findViewById(R.id.profilePic);
        recyclerView = findViewById(R.id.inputSearch);
        searchField = findViewById(R.id.searchField);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);

        SpacingItemInRecyclerView spacingItemInRecyclerView = new SpacingItemInRecyclerView(30);
        recyclerView.addItemDecoration(spacingItemInRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setHasFixedSize(true);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("offers");
            documentReference = firebaseFirestore.collection("users").document(userID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    assert value != null;
                    username.setText(value.getString("username"));
                }
            });

            StorageReference fileRef = storageReference.child("users/" + firebaseAuth.getCurrentUser().getUid() + "/profile.jpg");
            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profilePic);
                }
            });

            loadData("");

            searchField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString() != null) {
                        if (!isOnline()) {
                            showNoInternetToast();
                        } else {
                            loadData(s.toString());
                        }
                    } else {
                        if (!isOnline()) {
                            showNoInternetToast();
                        } else {
                            loadData("");
                        }
                    }
                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (!isOnline()) {
                        showNoInternetToast();
                        //To refresh the page whenever the internet is back
                        loadData("");
                    }
                    loadData("");
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            if (!isOnline()) {
                showNoInternetToast();
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void loadData(String Data) {
        Query searchQuery = databaseReference.orderByChild("category").startAt(Data).endAt(Data + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<modelClassDocs>().setQuery(searchQuery, modelClassDocs.class).build();
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
                // loading the poster user's info
                /*StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("users/" + model.getUserID() + "/profile.jpg");
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).into(holder.profilePic);
                    }
                });*/
                /*documentReference1 = firebaseFirestore.collection("users").document(model.getUserID());
                documentReference1.addSnapshotListener(MainUI.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        holder.username.setText(value.getString("username"));
                    }
                });*/
                String time = model.getTime();
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
                        databaseReference.child("views").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    String viewNumberText = snapshot.child("views").getValue().toString();
                                    int views = Integer.parseInt(viewNumberText);
                                    views = views + 1;
                                    String viewNumberText1 = String.valueOf(views);
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put("views", viewNumberText1);
                                    databaseReference.child("views").removeValue();
                                    databaseReference.child(ImageID).child("views").setValue(hashMap);
                                    startActivity(new Intent(MainUI.this, ViewSingleItem.class).putExtra("postID", ImageID).putExtra("views", viewNumberText1)
                                            .putExtra("time", time).putExtra("providerUserID", UserID).putExtra("data", getRef(position).getKey()));
                                }else{
                                    int views = 0;
                                    views +=1;
                                    String viewNumberText = String.valueOf(views);
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put("views", viewNumberText);
                                    databaseReference.child(ImageID).child("views").setValue(hashMap);
                                    startActivity(new Intent(MainUI.this, ViewSingleItem.class).putExtra("postID", ImageID).putExtra("views", viewNumberText)
                                            .putExtra("time", time).putExtra("providerUserID", UserID).putExtra("data", getRef(position).getKey()));
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //The image ID is the same like post ID

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
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

    @Override
    protected void onStart() {
        super.onStart();
        if (isOnline()) {
            adapter.startListening();
        }
    }

    public void EditProfile(View view) {
        Intent editProfileIntent = new Intent(MainUI.this, EditProfile.class);
        startActivity(editProfileIntent);
    }

    public void showNoInternetToast() {
        //Creating the LayoutInflater instance
        LayoutInflater li = getLayoutInflater();
        //Getting the View object as defined in the customtoast.xml file
        View layout = li.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.layoutToast));

        //Creating the Toast object
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(layout);//setting the view of custom toast layout
        toast.show();
    }

    public void Post(View view) {
        startActivity(new Intent(MainUI.this, Post.class));
    }

}


