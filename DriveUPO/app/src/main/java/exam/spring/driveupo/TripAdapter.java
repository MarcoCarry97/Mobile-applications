package exam.spring.driveupo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class TripAdapter extends ArrayAdapter<Trip>
{
    private Context context;
    private int resource;
    private ArrayList<Trip> trips;
    private Tools tools;

    public TripAdapter(@NonNull Context context, int resource,ArrayList<Trip> trips) {
        super(context, resource);
        this.context=context;
        this.resource=resource;
        this.trips=trips;
        tools=new Tools(context);
    }

    @Override
    public int getCount() {
        return trips.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView=View.inflate(context,resource,null);
        try {
            TextView from=convertView.findViewById(R.id.from);
            String startString=trips.get(position).getStart();
            startString=startString.replace(startString.charAt(0),startString.toUpperCase().charAt(0));
            from.setText(startString);
            TextView to=convertView.findViewById(R.id.to);
            String endString=trips.get(position).getEnd();
            endString=endString.replace(endString.charAt(0),endString.toUpperCase().charAt(0));
            to.setText(endString);
            TextView seats=convertView.findViewById(R.id.seats);
            seats.setText(trips.get(position).getOccuped()+"/"+trips.get(position).getSeats());
            seats.setTextColor(context.getColor(android.R.color.holo_green_light));
            if(trips.get(position).getOccuped()>=((trips.get(position).getSeats()/2)+1))
                seats.setTextColor(context.getColor(R.color.yellow));
            else if(trips.get(position).getOccuped()>=((trips.get(position).getSeats()/4)+1))
                seats.setTextColor(context.getColor(R.color.colorPrimary));
        }
        catch (Exception e)
        {
            Log.e("Adapter error:",e.getMessage());
        }
        return  convertView;
    }
}
