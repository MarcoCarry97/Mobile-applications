package exam.spring.bookcross.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ListView;

import java.sql.Date;
import java.util.ArrayList;

import exam.spring.bookcross.R;

public class StoryActivity extends AppCompatActivity {

    private ListView story;
    private DatabaseManager manager;
    private Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        manager=new DatabaseManager(StoryActivity.this);
        story=findViewById(R.id.story);
        ArrayList<Search> searches=new ArrayList<Search>();
        tools=new Tools(StoryActivity.this);
        Cursor cursor=manager.get();
        if(cursor.moveToFirst()) do {
            int index=cursor.getColumnIndex(DatabaseContract.ID);
            long id=cursor.getLong(index);
            index=cursor.getColumnIndex(DatabaseContract.BOOK);
            String book=cursor.getString(index);
            index=cursor.getColumnIndex(DatabaseContract.DATE);
            Date date=new Date(cursor.getLong(index));
            index=cursor.getColumnIndex(DatabaseContract.IMAGE);
            Bitmap photo=tools.convert(cursor.getString(index));
            Search search=new Search(book,photo,date);
            search.setId(id);
            searches.add(search);
        }while(cursor.moveToNext());
        SearchAdapter adapter=new SearchAdapter(StoryActivity.this,R.layout.search_layout,searches);
        story.setAdapter(adapter);
    }
}
