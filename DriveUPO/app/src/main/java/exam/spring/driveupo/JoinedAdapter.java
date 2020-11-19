package exam.spring.driveupo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

class JoinedAdapter extends ArrayAdapter<Trip>
{
    private Context context;
    private int resource;
    private ArrayList<Trip> trips;

    public JoinedAdapter(Context context, int resource, ArrayList<Trip> trips)
    {
        super(context,resource);
        this.context=context;
        this.resource=resource;
        this.trips=trips;
    }

    @Override
    public int getCount() {
        return trips.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView=View.inflate(context,resource,null);
        Trip trip=trips.get(position);
        TextView start=convertView.findViewById(R.id.from);
        start.setText(trip.getStart());
        TextView end=convertView.findViewById(R.id.to);
        end.setText(trip.getEnd());
        TextView seats=convertView.findViewById(R.id.seats);
        seats.setText(trip.getOccuped()+"/"+trip.getSeats());
        TextView date=convertView.findViewById(R.id.date);
        date.setText(trip.getDate().toString());
        String driverString=trip.getDriver().split("@")[0];
        TextView driver=convertView.findViewById(R.id.driver);
        driver.setText(driverString);
        return convertView;
    }
}
