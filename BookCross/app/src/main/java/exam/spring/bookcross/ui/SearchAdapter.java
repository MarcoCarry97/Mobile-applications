package exam.spring.bookcross.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import exam.spring.bookcross.R;

public class SearchAdapter extends ArrayAdapter<Search>
{
    private Context context;
    private int resource;
    private ArrayList<Search> searches;

    public SearchAdapter(@NonNull Context context, int resource,ArrayList<Search> searches)
    {
        super(context, resource);
        this.context=context;
        this.resource=resource;
        this.searches=searches;
    }

    @Override
    public int getCount() {
        return searches.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView=View.inflate(context,resource,null);
        Search search=searches.get(position);
        ImageView imageView= convertView.findViewById(R.id.photo);
        TextView title=convertView.findViewById(R.id.book);
        TextView date=convertView.findViewById(R.id.date);
        imageView.setImageBitmap(search.getPhoto());
        title.setText(search.getBook());
        date.setText(search.getDate().toString());
        return convertView;
    }
}
