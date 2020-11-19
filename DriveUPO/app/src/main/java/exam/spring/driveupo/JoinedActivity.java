package exam.spring.driveupo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

public class JoinedActivity extends AppCompatActivity {

    private ListView joined;
    private ArrayList<Trip> trips;
    private FirebaseApp app;
    private FirebaseFirestore store;
    private String email;
    private Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined);
        tools=new Tools(JoinedActivity.this);
        app=FirebaseApp.initializeApp(JoinedActivity.this);
        store=FirebaseFirestore.getInstance();
        joined=findViewById(R.id.joined);
        Bundle bundle=getIntent().getExtras();
        email=bundle.getString("email");
        joined.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createDialog(position);
            }
        });
    }

    public void createDialog(final int position)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(JoinedActivity.this);
        builder.setTitle(R.string.cancel);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String,Object> map=tools.map(trips.get(position));
                map.put("occuped",(int) map.get("occuped")-1);
                store.collection("trips").document(trips.get(position).getId()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        store.collection("trips").document(trips.get(position).getId()).collection("bookings").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot doc:queryDocumentSnapshots)
                                {
                                    if(doc.getString("user").equals(email))
                                    {
                                        store.collection("trips").document(trips.get(position).getId())
                                                .collection("bookings").document(doc.getId()).delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        tools.toast(R.string.booking_deleted);
                                                        trips.remove(position);
                                                        tools.adaptJoined(joined,trips);
                                                    }
                                                });
                                        break;
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });
        builder.create().show();
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        store.collection("trips").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(final DocumentSnapshot doc:queryDocumentSnapshots)
                {
                    trips=new ArrayList<Trip>();
                    store.collection("trips").document(doc.getId()).collection("bookings").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            try {
                                for(DocumentSnapshot user:queryDocumentSnapshots)
                                {
                                    Log.d("USER",user.getId());
                                    Date date=new Date(doc.getLong("date"));
                                    Date current=new Date(System.currentTimeMillis());
                                    if(user.getString("user").equals(email) && tools.isFirstDateGreater(date.toString(),current.toString(),"yyyy-MM-dd"))
                                    {
                                        final String start=doc.getString("start"), end=doc.getString("end"), driver=doc.getString("driver");
                                        //final Date date=new Date(doc.getLong("date"));
                                        final int seats=doc.getDouble("seats").intValue(), occuped=doc.getDouble("occuped").intValue();
                                        Log.e(String.valueOf(seats),String.valueOf(occuped));
                                        final Trip trip=new Trip(doc.getId(),start,end,date,seats,driver);
                                        trip.setOccuped(occuped);
                                        trips.add(trip);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                store.collection("trips").document(doc.getId()).collection("bookings").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        for(DocumentSnapshot user:queryDocumentSnapshots)
                                                            trip.join(user.getString("user"));
                                                    }
                                                });
                                            }
                                        }).start();

                                    }
                                }
                                tools.adaptJoined(joined,trips);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}
