package exam.spring.bookcross.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import exam.spring.bookcross.R;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton add;
    private Activity activity=this;
    private HashMap<Marker,Book> bookMarks;
    private FirebaseApp app;
    private FirebaseFirestore store;
    private Tools tools;
    private EditText edit;
    private DatabaseManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        manager=new DatabaseManager(MapsActivity.this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        bookMarks=new HashMap<Marker, Book>();
        tools=new Tools(MapsActivity.this);
        app=FirebaseApp.initializeApp(MapsActivity.this);
        store=FirebaseFirestore.getInstance();
        add=findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener()
       {
           public void onClick(View view) {
               try {
                   addBook();
               }
               catch (Exception e)
               {
                   e.printStackTrace();
               }
           }
       });

    }

    private void addBook()
    {
        final AddDialog addDialog=new AddDialog(MapsActivity.this,R.layout.scan_dialog);
        edit=addDialog.getView().findViewById(R.id.isbn);
        addDialog.setTitle(R.string.scan_title);
        addDialog.setImageButtonListener(R.id.scan, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(activity).initiateScan();
            }
        });
        addDialog.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               try {
                   String isbn=addDialog.getText().trim();
                   if(isbn.equals("")) throw new IllegalArgumentException(getString(R.string.isbn_error));
                   else if(isbn.length()!=13) throw new IllegalArgumentException(getString(R.string.isbn_length));
                   else
                   {
                       try {
                           Log.d("STAT:","CREATE");
                           ApiConnector link=new ApiConnector(isbn,MapsActivity.this);
                           Log.d("STAT:","EXE");
                           link.execute();
                           final Book book=link.get();
                           if(book==null) throw new JSONException(getString(R.string.book_not_exist));
                           HashMap<String,Object> bookMap=new HashMap<String, Object>();
                           bookMap.put("title",book.getTitle());
                           bookMap.put("year",book.getDate());
                           bookMap.put("isbn",book.getIsbn());
                           bookMap.put("pages",book.getPages());
                           bookMap.put("photo",tools.convert(book.getPhoto()));
                           bookMap.put("authors",book.getAuthors());
                           bookMap.put("categories",book.getCategories());
                           bookMap.put("latitude",book.getPosition().latitude);
                           bookMap.put("longitude",book.getPosition().longitude);

                           store.collection("books").add(bookMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                               @Override
                               public void onSuccess(DocumentReference documentReference) {
                                   book.setId(documentReference.getId());
                                   MarkerOptions marker=new MarkerOptions().position(book.getPosition()).title(book.getTitle()).snippet(tools.geocode(book.getPosition()));
                                   Marker mark=mMap.addMarker(marker);
                                   bookMarks.put(mark,book);
                                   manager.add(book.getTitle(),book.getPhoto(),new Date(System.currentTimeMillis()));
                                   tools.toast(R.string.book_added);
                               }
                           });
                       }
                       catch (Exception e)
                       {
                           tools.toast(e.getMessage());
                           Log.e("ERROR",e.getMessage());
                       }
                   }
               }
               catch (Exception e)
               {
                   tools.toast(e.getMessage());
                   e.printStackTrace();
               }
            }
        });
        addDialog.show();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        getBooks();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent=new Intent(MapsActivity.this,BookActivity.class);
                Book book=bookMarks.get(marker);
                intent.putExtra("book",book);
                manager.add(book.getTitle(),book.getPhoto(),new Date(System.currentTimeMillis()));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.list: goToList(); break;
            case R.id.logout: finish(); break;
            case R.id.searches: goToStory(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToStory()
    {
        Intent intent=new Intent(MapsActivity.this,StoryActivity.class);
        startActivity(intent);
    }

    private void goToList()
    {
        Intent intent=new Intent(MapsActivity.this, CrossActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                edit.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getBooks()
    {
        final CollectionReference ref=store.collection("books");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(DocumentSnapshot doc:task.getResult())
                    {
                        if(doc.exists())
                        {
                            String title=doc.getString("title");
                            String isbn=doc.getString("isbn");
                            int pages=doc.getDouble("pages").intValue();
                            String date=doc.getString("year");
                            Bitmap photo=tools.convert(doc.getString("photo"));
                            double lat=doc.getDouble("latitude");
                            double lng=doc.getDouble("longitude");
                            LatLng position=new LatLng(lat,lng);
                            ArrayList<String> authors=(ArrayList<String>)doc.get("authors");
                            ArrayList<String> categories=(ArrayList<String>)doc.get("categories");
                            Log.d(authors.toString(),categories.toString());
                            Book book=new Book(doc.getId(),title,isbn,pages,date,categories,authors,photo,position);
                            List<Address> loc=null;
                            try {
                                Geocoder geo= new Geocoder(MapsActivity.this);
                                LatLng pos=book.getPosition();
                                loc=geo.getFromLocation(pos.latitude,pos.longitude,1);
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            String geoName=  loc!=null ?  !loc.isEmpty() ? loc.get(0).getAddressLine(0) : getString(R.string.not_exist) : getString(R.string.not_exist);
                            MarkerOptions marker=new MarkerOptions().position(book.getPosition()).title(book.getTitle()).snippet(geoName);
                            Marker mark=mMap.addMarker(marker);
                            bookMarks.put(mark,book);
                            //bookList.add(title);
                            Log.d("BOOK",book.toString());
                        }
                    }
                    Log.d("BOOKS",bookMarks.toString());
                }
            }
        });
        Log.d("FIRE:","OK");
        //Task<QuerySnapshot> task=store.collection("books").get();
    }


}
