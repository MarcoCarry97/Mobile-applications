package exam.spring.driveupo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tools
{
    public static final int LOGOUT = 0;
    private Context context;
    Geocoder coder;

    public Tools(Context context)
    {
        this.context=context;
        coder=new Geocoder(context);
    }

    public HashMap<String,Object> map(Trip trip)
    {
        HashMap<String,Object> tripMap=new HashMap<String, Object>();
        tripMap.put("start",trip.getStart());
        tripMap.put("end",trip.getEnd());
        tripMap.put("date",trip.getDate().getTime());
        tripMap.put("seats",trip.getSeats());
        tripMap.put("occuped",trip.getOccuped());
        tripMap.put("driver",trip.getDriver());
        return tripMap;
    }

    public void toast(int messageId)
    {
        toast(context.getString(messageId));
    }

    public void toast(String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public static Bitmap convert(String base64Str) throws IllegalArgumentException
    {
        if(base64Str!=null)
        {
            byte[] bytes=Base64.decode(base64Str,Base64.DEFAULT);
            Bitmap image=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            return image;
        }
        else return null;
    }

    public static String convert(Bitmap bitmap)
    {
       if(bitmap!=null)
       {
           ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
           bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
           byte[] byteArray = byteArrayOutputStream .toByteArray();
           String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
           return encoded;
       }
       else return null;
    }

    public void adapt(ListView list, ArrayList<Trip> trips)
    {
        TripAdapter adapter=new TripAdapter(context,R.layout.trip_layout,trips);
        list.setAdapter(adapter);
    }

    public void adaptJoined(ListView list,ArrayList<Trip> joined)
    {
        JoinedAdapter adapter=new JoinedAdapter(context,R.layout.joined_layout,joined);
        list.setAdapter(adapter);
    }

    public boolean isFirstDateGreater(String date1,String date2,String pattern) throws ParseException {
        long chosenMillis=new SimpleDateFormat(pattern).parse(date1).getTime();
        long curMillis=new SimpleDateFormat(pattern).parse(date2).getTime();
        return chosenMillis>=curMillis;
    }

    public long greaterDate(String date1,String date2,String pattern) throws ParseException {
        return new SimpleDateFormat(pattern).parse(isFirstDateGreater(date1,date2,pattern) ? date1 : date2).getTime();
    }

    public String geocode(LatLng position) throws IOException {
        List<Address> addresses=coder.getFromLocation(position.latitude,position.longitude,1);
        if(addresses.size()==0) throw new IOException();
        Address address=addresses.get(0);
        return address.getLocality();
        /*try
        {
            PosConverter converter=new PosConverter();
            converter.execute(position);
            return converter.get();
        }
        catch (Exception e)
        {
            return null;
        }*/
    }

    public LatLng geocode(String street) throws IOException {
        List<Address> addresses=coder.getFromLocationName(street,1);
        if(addresses.size()==0) throw new IOException();
        Address address=addresses.get(0);
        LatLng position=new LatLng(address.getLatitude(),address.getLongitude());
        return position;
        /*try
        {
            AddressConverter converter=new AddressConverter();
            converter.execute(street);
            return converter.get();
        }
        catch (Exception e)
        {
            return null;
        }*/
    }

    private class PosConverter extends AsyncTask<LatLng,LatLng,String>
    {

        @Override
        protected String doInBackground(LatLng... position)
        {
            Address address=null;
            try {
                List<Address> addresses=coder.getFromLocation(position[0].latitude,position[0].longitude,1);
                if(addresses.size()==0) throw new IOException();
                address=addresses.get(0);
                Log.d("Address",address.getLocality());
                return address.getLocality();
            }
            catch (Exception e)
            {
                return null;
            }
        }
    }

    private class AddressConverter extends AsyncTask<String,String,LatLng>
    {


        @Override
        protected LatLng doInBackground(String... street) {
            try {
                List<Address> addresses=coder.getFromLocationName(street[0],1);
                if(addresses.size()==0) throw new IOException();
                Address address=addresses.get(0);
                LatLng position=new LatLng(address.getLatitude(),address.getLongitude());
                return position;
            }
            catch (Exception e)
            {
                return null;
            }
        }
    }
}
