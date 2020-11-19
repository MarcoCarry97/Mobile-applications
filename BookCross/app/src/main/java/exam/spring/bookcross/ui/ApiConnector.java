package exam.spring.bookcross.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ApiConnector extends AsyncTask<String,Object, Book>
{
    private String action;
    private Activity activity;
    private Tools tools;
    private static final String URL="https://www.googleapis.com/books/v1/volumes?q=isbn:";

    public ApiConnector(String param,Activity activity)
    {
        this.activity=activity;
        action=URL+param;
        tools=new Tools(activity.getApplicationContext());
    }

    @Override
    protected Book doInBackground(String... strings)
    {
        Book book=null;
        try
        {

            Log.d("URL:",action);
            HttpURLConnection link;
            URL url = new URL(action);
            link= (HttpURLConnection) url.openConnection();
            link.setRequestMethod("GET");
            link.setReadTimeout(5000);
            link.setConnectTimeout(5000);
            int response=link.getResponseCode();
            if(response!=200) Log.e("REQUEST:","FAILED");
            else
            {
                Log.d("REQUEST:","SUCCESS");
                String result=getContent(link);
                JSONObject json=new JSONObject(result);
                book=createBook(json);
                link.disconnect();
            }
        }
        catch ( Exception e)
        {
            e.printStackTrace();
        }
        return book;
    }

    private Book createBook(JSONObject json) throws JSONException, ExecutionException, InterruptedException {
        JSONArray items=json.getJSONArray("items");
        JSONObject objects= items.getJSONObject(0).getJSONObject("volumeInfo");
        Log.i("item:",objects.toString(3));
        String url=objects.getJSONObject("imageLinks").getString("thumbnail");
        Bitmap photo=tools.download(url);
        Log.d("URL",url);
        Log.d("STAT","EXEC");
        int numPages=objects.getInt("pageCount");
        String title=objects.getString("title");
        JSONArray jsonAuthor=objects.getJSONArray("authors");
        ArrayList<String> authors=new ArrayList<String>();
        for(int i=0;i<jsonAuthor.length();i++)
            authors.add(jsonAuthor.getString(i));
        String date=objects.getString("publishedDate");
        JSONObject jsonIsbn=objects.getJSONArray("industryIdentifiers").getJSONObject(1);
        String isbn=jsonIsbn.getString("identifier");
        JSONArray jsonCat=objects.getJSONArray("categories");
        ArrayList<String> categories=new ArrayList<String>();
        for(int i=0;i<jsonCat.length();i++)
            categories.add(jsonCat.getString(i));
        Log.d("STAT","GET");
        //Bitmap photo=null;
        LocationTrack track=new LocationTrack(activity.getApplicationContext());
        LatLng position=null;
        /*if(!track.isServiceAvailable())
            track.checkPermission();*/
        double lat=track.getLatitude();
        double lng=track.getLongitude();
        position=new LatLng(lat,lng);
        return new Book(null,title,isbn,numPages,date,categories,authors,photo,position);
    }

    private String getContent(HttpURLConnection link) throws IOException {
        StringBuilder builder=new StringBuilder();
        BufferedReader buffer=new BufferedReader(new InputStreamReader(link.getInputStream()));
        String line=buffer.readLine();
        while(line!=null)
        {
            builder.append(line);
            line=buffer.readLine();
        }
        return builder.toString();
    }

    @Override
    protected void onPostExecute(Book book)
    {
        super.onPostExecute(book);
    }
}
