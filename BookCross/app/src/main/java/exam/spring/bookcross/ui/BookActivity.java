package exam.spring.bookcross.ui;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import exam.spring.bookcross.R;

public class BookActivity extends AppCompatActivity {

    private TextView titleView, authorsView, catViews;
    private ImageView imageView;
    private Book book;
    private FirebaseApp app;
    private FirebaseFirestore store;
    private Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        app=FirebaseApp.initializeApp(BookActivity.this);
        store=FirebaseFirestore.getInstance();
        tools=new Tools(BookActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle=getIntent().getExtras();
        final Book book=bundle.getParcelable("book");
        titleView=findViewById(R.id.title);
        titleView.setText(book.getTitle());
        imageView=findViewById(R.id.photo);
        Log.d("BOOK",book.toString());
        imageView.setImageBitmap(book.getPhoto());
        FloatingActionButton fab = findViewById(R.id.fab);
        catViews=findViewById(R.id.categories);
        catViews.setText(unList(book.getCategories()));
        authorsView=findViewById(R.id.authors);
        authorsView.setText(unList(book.getAuthors()));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(BookActivity.this);
                builder.setTitle(R.string.take_message);
                builder.setCancelable(false);
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        store.collection("books").document(book.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                tools.toast(R.string.book_taken);
                                finish();
                            }
                        });
                    }
                });
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

            }
        });
    }

    private String unList(ArrayList<String> list)
    {
        String result="";
        for(int i=0;i<list.size();i++)
            result+="- "+list.get(i)+";\n";
        return result;
    }

}
