package exam.spring.bookcross.ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import exam.spring.bookcross.R;

public class BookAdapter extends ArrayAdapter<Book>
{
    private Context context;
    private int resource;
    private ArrayList<Book> books;

    public BookAdapter(@NonNull Context context, int resource,ArrayList<Book> books)
    {
        super(context, resource);
        this.context=context;
        this.resource=resource;
        this.books=books;
        Log.d("ADP BOOKS",this.books.toString());
    }

    @Override
    public int getCount() {
        Log.d("LEN",String.valueOf(books.size()));
        return books.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        convertView=View.inflate(context,R.layout.book_layout,null);
        ImageView image=convertView.findViewById(R.id.image);
        TextView title=convertView.findViewById(R.id.title);
        Book book=books.get(position);
        image.setImageBitmap(book.getPhoto());
        title.setText(book.getTitle());
        Log.d("TITLE",title.getText().toString());
        //Log.d("PHOTO",book.getPhoto());
        Log.d("TILE","COMPLETE");
        return convertView;
    }
}
