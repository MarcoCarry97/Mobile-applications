package exam.spring.driveupo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Trip> trips;
    private Tools tools;
    private HashMap<Marker,Trip> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        tools=new Tools(MapsActivity.this);
        Bundle bundle=getIntent().getExtras();
        trips=bundle.getParcelableArrayList("trips");
        markers=new HashMap<Marker, Trip>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(final Trip trip:trips)
                {
                    try
                    {
                        final LatLng start=tools.geocode(trip.getStart());
                        final LatLng end=tools.geocode(trip.getEnd());
                        final MarkerOptions startMarker=new MarkerOptions().position(start).title(trip.getStart()).snippet(trip.getDriver()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        final MarkerOptions endMarker=new MarkerOptions().position(end).title(trip.getEnd()).snippet(trip.getDriver()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                markers.put(mMap.addMarker(startMarker),trip);
                                markers.put(mMap.addMarker(endMarker),trip);
                            }
                        });
                    }
                    catch (Exception e) {
                        //Log.e("MAP ERROR",e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent=new Intent(MapsActivity.this,TripActivity.class);
                intent.putExtra("trip",markers.get(marker));
                startActivity(intent);
            }
        });
    }
}
