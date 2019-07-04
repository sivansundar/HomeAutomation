package com.homeautomation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
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

import org.w3c.dom.Text;

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

                int status = applianceModel.getStatus();
                Log.d("Main Activity : ", "onBindViewHolder: Status : " + status);
                applianceViewHolder.setSwitch(status);

                ((DatabaseReference) query).child(applianceModel.getAppID()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ApplianceModel applianceModel = dataSnapshot.getValue(ApplianceModel.class);
                    int value = applianceModel.getStatus();

                    if (value==0) {
                        applianceViewHolder.setSwitch(0);
                    }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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

    public static class ApplianceViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ApplianceViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setTitle(String title) {
            TextView mTitleText = (TextView) mView.findViewById(R.id.appliance_title_text);
            mTitleText.setText(title);
        }

        public void setPinNo(String pinNo) {
            TextView mPinNo = mView.findViewById(R.id.appliance_gpio_text);
            mPinNo.setText("GPIO : " + pinNo);
        }

        public void setSwitch(int switchValue) {

            SwitchCompat switchCompat = mView.findViewById(R.id.switchStatus);
            if (switchValue==0) {
                switchCompat.setChecked(false);
            }

            if (switchValue==1) {
                switchCompat.setChecked(true);
            }
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
