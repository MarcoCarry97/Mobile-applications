package exam.spring.bookcross.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import exam.spring.bookcross.R;

public class Tools
{
    private Context context;

    public Tools(Context context)
    {
        this.context=context;
    }

    public void toast(int messageId)
    {
        toast(context.getString(messageId));
    }

    public void toast(String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public Bitmap convert(String base64Str) throws IllegalArgumentException
    {
        if(base64Str!=null)
        {
            byte[] bytes=Base64.decode(base64Str,Base64.DEFAULT);
            Bitmap image=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            return image;
        }
        else return null;
    }

    public  String convert(Bitmap bitmap)
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

    public Bitmap download(String action)
    {
        boolean ok=false;
        try {
            URL url = new URL(action);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            Log.d("HTTP","CONNECT");
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            Log.d("HTTP","DECODE");
            ok=true;
            return bitmap;
        } catch (IOException e) {
            // Log exception
            Log.e("EXCEPTION",e.getMessage());
            return null;
        }
        finally {
            if(ok) Log.d("IMAGE","OK");
            else Log.e("IMAGE","ERROR");
        }
    }

    public String geocode(LatLng position)
    {
        try
        {
            Geocoder geocoder=new Geocoder(context);
            List<Address> addresses=geocoder.getFromLocation(position.latitude,position.longitude,1);
            String result=(String) (!addresses.isEmpty() ? addresses.get(0) : context.getString(R.string.not_exist));
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return context.getString(R.string.not_exist);
        }
    }
}
