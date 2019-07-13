package com.homeautomation;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ApplianceActivity extends AppCompatActivity {

    public String appID;
    public FirebaseDatabase mDatabase;
    public DatabaseReference databaseReference;
    @BindView(R.id.appliance_text)
    TextView applianceText;
    @BindView(R.id.switchButton)
    SwitchCompat switchButton;
    @BindView(R.id.appliance_status)
    TextView applianceStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliance);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference();

        appID = getIntent().getStringExtra("appID");


        databaseReference.child("ESP-A405/hall").child(appID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ApplianceModel model = dataSnapshot.getValue(ApplianceModel.class);
                int status = model.getStatus();
                String appliance_name = model.getTitle();

                applianceText.setText(appliance_name);


                if (status == 0) {
                    applianceStatus.setText("Status : OFF");

                    switchButton.setChecked(false);
                }

                if (status == 1) {
                    applianceStatus.setText("Status : ON");

                    switchButton.setChecked(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    databaseReference.child("ESP-A405/hall").child(appID).child("status").setValue(1);
                }

                if (isChecked == false) {
                    databaseReference.child("ESP-A405/hall").child(appID).child("status").setValue(0);

                }
            }
        });

    }
}
