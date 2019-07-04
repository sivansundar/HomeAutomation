package com.homeautomation;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        UID = mUser.getUid();

        if (name.isEmpty()) {

            getUserDetails();

        }

        getWeatherDetails();
    }

    private void getWeatherDetails() {
        databaseReference.child("ESP-A405/weather/indoor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                WeatherModel weatherModel = dataSnapshot.getValue(WeatherModel.class);

                float temp = weatherModel.getTemperature();
                int humidity = weatherModel.getHumidity();

                temp_text.setText(""+ temp + "°C");
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
