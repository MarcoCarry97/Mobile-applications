package exam.spring.driveupo;

import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

public class TripActivity extends AppCompatActivity {

    private Trip trip;
    private TextView driver,start,end,date,seats;
    private FirebaseApp app;
    private FirebaseFirestore store;
    private FirebaseAuth auth;
    private Tools tools;
    private FirebaseUser user;
    private FloatingActionButton fab;
    private FloatingActionButton list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        tools=new Tools(TripActivity.this);
        app=FirebaseApp.initializeApp(TripActivity.this);
        store=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        start=findViewById(R.id.from);
        end=findViewById(R.id.to);
        date=findViewById(R.id.date);
        driver=findViewById(R.id.driver);
        seats=findViewById(R.id.seats);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle=getIntent().getExtras();
        trip=bundle.getParcelable("trip");
        start.setText(trip.getStart());
        end.setText(trip.getEnd());
        String driverString=trip.getDriver().split("@")[0];
        driver.setText(driverString);
        date.setText(trip.getDate().toString());
        seats.setText(trip.getOccuped()+"/"+trip.getSeats());
        fab = findViewById(R.id.fab);
        list=findViewById(R.id.joined);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!user.getEmail().equals(trip.getDriver()))
                {
                    final HashMap<String,Object> map=tools.map(trip);
                    map.put("occuped",(int) map.get("occuped")+1);
                    HashMap<String,Object> userMap=new HashMap<String, Object>();
                    userMap.put("user",user.getEmail());
                    store.collection("trips").document(trip.getId()).collection("bookings").add(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            store.collection("trips").document(trip.getId()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    tools.toast(R.string.joined);
                                }
                            });
                        }
                    });
                    finish();
                }
                else tools.toast(R.string.driver_booking);
            }
        });
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDialog dialog=new ListDialog(TripActivity.this,trip.getUsers());
                dialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        user=auth.getCurrentUser();
        store.collection("trips").document(trip.getId()).collection("bookings").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot doc:queryDocumentSnapshots)
                    if(doc.getString("user").equals(user.getEmail()))
                        fab.setVisibility(View.GONE);
                if(trip.getOccuped()==0)  list.setVisibility(View.GONE);
            }
        });
    }
}
