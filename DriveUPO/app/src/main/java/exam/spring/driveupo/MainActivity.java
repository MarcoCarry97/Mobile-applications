package exam.spring.driveupo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ListView trips;
    private ArrayList<Trip> tripList;
    private FirebaseApp app;
    private FirebaseFirestore store;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        app=FirebaseApp.initializeApp(MainActivity.this);
        auth=FirebaseAuth.getInstance();
        store=FirebaseFirestore.getInstance();
        trips=findViewById(R.id.list);
        trips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,TripActivity.class);
                intent.putExtra("trip",tripList.get(position));
                startActivity(intent);
            }
        });
        trips.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try
                {
                    if(!user.getEmail().equals(tripList.get(position).getDriver()))
                        throw new IllegalArgumentException(getString(R.string.error_choose));
                    choose(position);
                }
                catch (Exception e)
                {
                    tools.toast(e.getMessage());
                }
                return false;
            }
        });
        tools=new Tools(MainActivity.this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });
    }

    private void choose(final int position)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.choose);
        builder.setPositiveButton(R.string.modify, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                modify(position);
            }
        });
        builder.setNegativeButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                remove(position);
            }
        });
        builder.create().show();
    }

    private void remove(final int position)
    {
        store.collection("trips").document(tripList.get(position).getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                tools.toast(R.string.trip_removed);
                tripList.remove(position);
                tools.adapt(trips,tripList);
            }
        });
    }

    private void modify(final int position)
    {
            final AddDialog addDialog=new AddDialog(MainActivity.this,R.layout.trip_input);
            final Trip trip=tripList.get(position);
            setButtons(addDialog);
            addDialog.setPositiveButton(R.string.modify, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try
                    {
                        final HashMap<String,Object> map=getMap(addDialog);
                        map.put("occuped",trip.getOccuped());
                        store.collection("trips").document(trip.getId()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                tools.toast(R.string.trip_modified);
                                Trip trip=tripList.get(position);
                                trip.setStart((String)map.get("start"));
                                trip.setEnd((String)map.get("end"));
                                trip.setDate(new Date((int)map.get("date")));
                                trip.setSeats((int)map.get("seats"));
                                trip.setOccuped((int) map.get("occuped"));
                            }
                        });
                    }
                    catch (IOException e)
                    {
                        Log.e("Error",e.getMessage());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
            addDialog.setTitle(R.string.modify_trip);
            addDialog.show();
            addDialog.setValues(trip);
    }

    private void add()
    {
        final AddDialog addDialog=new AddDialog(MainActivity.this,R.layout.trip_input);
        addDialog.setTitle(R.string.add_trip);
        setButtons(addDialog);
        addDialog.show();
    }

    private void setButtons(final AddDialog addDialog)
    {
        addDialog.setImageButtonListener(R.id.calendar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicker(addDialog);
            }
        });
        addDialog.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addFire(addDialog);
            }
        });
    }

    private void addFire(final AddDialog addDialog)
    {
        try
        {
            final HashMap<String,Object> map=getMap(addDialog);
            store.collection("trips").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    tools.toast(R.string.trip_added);
                    Trip trip=new Trip(documentReference.getId(),map);
                    tripList.add(trip);
                    tools.adapt(trips,tripList);
                }
            });
        }
        catch (Exception e)
        {
            tools.toast(e.getMessage());
        }
    }

    private HashMap<String,Object> getMap(AddDialog addDialog) throws IOException, ParseException {
        final HashMap<String,Object> map=new HashMap<String, Object>();
        View view=addDialog.getView();
        EditText start=view.findViewById(R.id.start);
        EditText end=view.findViewById(R.id.end);
        EditText seats=view.findViewById(R.id.seats);
        TextView date=view.findViewById(R.id.date);
        if(!date.getText().toString().matches("^[0-9]{4}-((0[1-9]{1})|10|11|12)-(([0-2]{1}[0-9])|30|31)$"))
            throw new IllegalArgumentException(getString(R.string.date_error));
        if(start.getText().toString().equals("") || end.getText().toString().equals("") || !seats.getText().toString().matches("[1-9]{1}"))
            throw new IllegalArgumentException(getString(R.string.add_error));
        map.put("start",start.getText().toString());
        //map.put("start-lng",tools.geocode(start.getText().toString()).longitude);
        map.put("end",end.getText().toString());
        //map.put("end-lng",tools.geocode(end.getText().toString()).longitude);
        if(Integer.parseInt(seats.getText().toString())==0)
            throw new IllegalArgumentException(getString(R.string.seats_error));
        map.put("seats",Integer.parseInt(seats.getText().toString()));
        map.put("occuped",0);
        map.put("driver",user.getEmail());
        String dateString=date.getText().toString();
        Log.d("DATE LOLOLOLOLOLOLO",dateString);
        long millis=new SimpleDateFormat("yyyy-MM-dd").parse(dateString).getTime();
        map.put("date",millis);
        return  map;
    }

    private void showPicker(final AddDialog addDialog)
    {
        Calendar calendar=Calendar.getInstance();
        final int currentYear=calendar.get(Calendar.YEAR);
        final int currentMonth=calendar.get(Calendar.MONTH);
        final int currentDay=calendar.get(Calendar.DAY_OF_MONTH);
        addDialog.setTitle(R.string.add_trip);
        DatePickerDialog picker=new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                try
                {
                    TextView date=addDialog.getView().findViewById(R.id.date);
                    String chosen=dayOfMonth+"/"+(month+1)+"/"+year;
                    String current=currentDay+"/"+(currentMonth+1)+"/"+currentYear;
                    date.setText(new Date(tools.greaterDate(chosen,current,"dd/MM/yyyy")).toString());
                }
                catch (Exception e)
                {
                    Log.e("ERROR",e.getMessage()+" "+e.toString());
                }
            }
        },currentYear,currentMonth,currentDay);
        picker.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        user=auth.getCurrentUser();
        store.collection("trips").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                tripList=new ArrayList<Trip>();
                try
                {
                    for(final DocumentSnapshot doc:queryDocumentSnapshots)
                    {
                        long current=new SimpleDateFormat("yyyy-MM-dd").parse(new Date(System.currentTimeMillis()).toString()).getTime();
                        long millis=new SimpleDateFormat("yyyy-MM-dd").parse(new Date(doc.getLong("date")).toString()).getTime();
                        Log.d(new Date(current).toString(),new Date(current).toString());
                        Log.d("CURRENT",String.valueOf(current));
                        Log.d("DATE",String.valueOf(millis));
                        Log.d("CONTROL",String.valueOf(current>=doc.getLong("date")));
                        if(doc.getDouble("occuped").intValue()!=doc.getDouble("seats").intValue() && millis>=current)
                        {
                            final String start=doc.getString("start");
                            final String end=doc.getString("end");
                            final String id=doc.getId();
                            final String driver=doc.getString("driver");
                            final int seats=doc.getDouble("seats").intValue();
                            final int occuped=doc.getDouble("occuped").intValue();
                            final Date date=new Date(doc.getLong("date"));
                            Log.d("SEATS",String.valueOf(seats));
                            Log.d("OCCUPED",String.valueOf(occuped));
                            //if(doc.getLong("date")>=current)
                            store.collection("trips").document(doc.getId()).collection("bookings").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    ArrayList<String> users=new ArrayList<String>();
                                    for(DocumentSnapshot user:queryDocumentSnapshots) {
                                        String driverString=user.getString("user").split("@")[0];
                                        users.add(driverString);
                                    }
                                    final Trip trip=new Trip(doc.getId(),start,end,date,seats,driver,users);
                                    trip.setOccuped(occuped);
                                    tripList.add(trip);
                                    tools.adapt(trips,tripList);
                                }
                            });
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.e("Error",e.getMessage());
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.signout) {
            auth.signOut();
            Intent result=new Intent();
            //result.setData(Uri.parse(getString(android.R.string.yes)));
            setResult(RESULT_OK,result);
            finish();
            return true;
        }
        else if(id==R.id.joined)
        {
            Intent intent= new Intent(MainActivity.this,JoinedActivity.class);
            intent.putExtra("email",user.getEmail());
            startActivity(intent);
        }
        if (id == R.id.map) {
            Intent intent=new Intent(MainActivity.this,MapsActivity.class);
            intent.putExtra("trips",tripList);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
