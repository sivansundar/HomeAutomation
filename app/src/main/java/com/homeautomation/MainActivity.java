package com.homeautomation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.name_label)
    TextView name_label;
    @BindView(R.id.userdp_circleImageView)
    CircularImageView userpic_circular;
    @BindView(R.id.temp_text)
    TextView temp_text;
    @BindView(R.id.humidity_text)
    TextView humidity_text;
    @BindView(R.id.appliance_recyclerView)
    RecyclerView appliance_recyclerView;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    String UID = "";
    String name = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        name_label.setText("" + name);

        appliance_recyclerView = (RecyclerView) findViewById(R.id.appliance_recyclerView);
        appliance_recyclerView.setHasFixedSize(true);
        appliance_recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();
        UID = mUser.getUid();

        if (name.isEmpty()) {

            getUserDetails();

        }

        getWeatherDetails();

        getApplianceDetails();


    }

    private void getApplianceDetails() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("ESP-A405")
                .child("hall");

        FirebaseRecyclerOptions<ApplianceModel> options =
                new FirebaseRecyclerOptions.Builder<ApplianceModel>()
                .setQuery(query, ApplianceModel.class)
                .build();

        FirebaseRecyclerAdapter<ApplianceModel, ApplianceViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ApplianceModel, ApplianceViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ApplianceViewHolder applianceViewHolder, int i, @NonNull ApplianceModel applianceModel) {

                applianceViewHolder.setTitle(applianceModel.getTitle());
                applianceViewHolder.setPinNo(String.valueOf(applianceModel.getpinNo()));

                //applianceViewHolder.setSwitch(applianceModel.getAppID());

                int status = applianceModel.getStatus();

                if (status == 0) {
                    applianceViewHolder.switchstatus.setChecked(false);
                }

                if (status == 1) {
                    applianceViewHolder.switchstatus.setChecked(true);
                }

                String appID = applianceModel.getAppID();

//                applianceViewHolder.setSwitch(appID);

                applianceViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, ApplianceActivity.class);
                        intent.putExtra("appID", appID);
                        startActivity(intent);
                    }
                });


            }

            @NonNull
            @Override
            public ApplianceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.appliance_item, parent, false);

                SwitchCompat switchCompat = view.findViewById(R.id.switchStatus);


                return new ApplianceViewHolder(view);
            }
        };

        appliance_recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void updateSwitchStatus(int i, String appID) {
        databaseReference.child("ESP-A405/hall").child(appID).child("status").setValue(i).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("MainActivity", "Database value changed :  " + appID + ": " + i);

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MainActivity", "Database value updation error :  " + e);

                    }
                });
    }

    public class ApplianceViewHolder extends RecyclerView.ViewHolder {
        View mView;


        public SwitchCompat switchstatus;

        public ApplianceViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            switchstatus = (SwitchCompat) mView.findViewById(R.id.switchStatus);

        }


        public void setTitle(String title) {
            TextView mTitleText = (TextView) mView.findViewById(R.id.appliance_title_text);
            mTitleText.setText(title);
        }

        public void setPinNo(String pinNo) {
            TextView mPinNo = mView.findViewById(R.id.appliance_gpio_text);
            mPinNo.setText("GPIO : " + pinNo);
        }

        public void setSwitch(String appID) {

        }



    }

    private void getWeatherDetails() {
        databaseReference.child("ESP-A405/weather/indoor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                WeatherModel weatherModel = dataSnapshot.getValue(WeatherModel.class);

                float temp = weatherModel.getTemperature();
                int humidity = weatherModel.getHumidity();

                temp_text.setText("" + temp + "°C");
                humidity_text.setText("" + humidity + "%");
                Log.d("Mainactivity : ", "onDataChange: Humidity : " + humidity + "\n Temp : " + temp);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getUserDetails() {

        name = mUser.getDisplayName();
        name_label.setText(name);

        Picasso.get()
                .load(mUser.getPhotoUrl())
                .resize(50, 50)
                .centerCrop()
                .into(userpic_circular);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        finish();
    }
}
