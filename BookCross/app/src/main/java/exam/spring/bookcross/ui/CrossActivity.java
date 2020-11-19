package exam.spring.bookcross.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.Transliterator;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import exam.spring.bookcross.R;

public class CrossActivity extends AppCompatActivity {

    private Activity activity=this;
    private EditText edit;
    private ListView books;
    private ArrayList<Book> bookList;
    private FirebaseApp app;
    private FirebaseFirestore store;
    private Tools tools;
    private DatabaseManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tools=new Tools(CrossActivity.this);
        manager=new DatabaseManager(CrossActivity.this);
        bookList=new ArrayList<Book>();
        app=FirebaseApp.initializeApp(CrossActivity.this);
        store=FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        books=findViewById(R.id.books);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addBook();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        getBooks();
        books.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(CrossActivity.this,BookActivity.class);
                intent.putExtra("book",bookList.get(position));
                String s= bookList.get(position).toString();
                Log.d("INTENT",s);
                manager.add(bookList.get(position).getTitle(),bookList.get(position).getPhoto(),new Date(System.currentTimeMillis()));
                startActivity(intent);
            }
        });
    }

    private void getBooks()
    {
        final CollectionReference ref=store.collection("books");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(DocumentSnapshot doc:task.getResult())
                    {
                        if(doc.exists())
                        {
                            String title=doc.getString("title");
                            String isbn=doc.getString("isbn");
                            int pages=doc.getDouble("pages").intValue();
                            String date=doc.getString("year");
                            Bitmap photo=tools.convert(doc.getString("photo"));
                            double lat=doc.getDouble("latitude");
                            double lng=doc.getDouble("longitude");
                            LatLng position=new LatLng(lat,lng);
                            ArrayList<String> authors=(ArrayList<String>)doc.get("authors");
                            ArrayList<String> categories=(ArrayList<String>)doc.get("categories");
                            Book book=new Book(doc.getId(),title,isbn,pages,date,authors,categories,photo,position);
                            bookList.add(book);
                            Log.d("BOOK",book.toString());
                        }
                    }
                    Log.d("BOOKS",bookList.toString());
                    BookAdapter adapter=new BookAdapter(CrossActivity.this,R.layout.book_layout,bookList);
                    books.setAdapter(adapter);

                }
            }
        });
        Log.d("FIRE:","OK");
        //Task<QuerySnapshot> task=store.collection("books").get();
    }

    private ArrayList<String> getCategories(DocumentSnapshot doc) {
        DocumentReference ref=doc.getDocumentReference("authors");
        Log.d("REF","OK");
        return null;
    }

    private ArrayList<String> getAuthors(DocumentSnapshot doc) {
        return  null;
    }

    private void addBook()
    {
        final AddDialog addDialog=new AddDialog(CrossActivity.this,R.layout.scan_dialog);
        addDialog.setTitle(R.string.scan_title);
        edit=addDialog.getView().findViewById(R.id.isbn);
        addDialog.setImageButtonListener(R.id.scan, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(activity).initiateScan();
            }
        });
        addDialog.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String isbn=addDialog.getText().trim();
                    if(isbn.equals("")) throw new IllegalArgumentException(getString(R.string.isbn_error));
                    else if(isbn.length()!=13) throw new IllegalArgumentException(getString(R.string.isbn_length));
                    else
                    {
                        Log.d("STAT:","CREATE");
                        ApiConnector link=new ApiConnector(isbn,CrossActivity.this);
                        Log.d("STAT:","EXE");
                        link.execute();
                        try {
                            Book book=link.get();
                            if(book==null) throw new JSONException(getString(R.string.book_not_exist));
                            bookList.add(book);
                            readapt();
                            insertFire(book);
                        } catch (Exception e) {
                            Log.e("BOOK","FAIL");
                            tools.toast(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
                catch (Exception e)
                {
                    tools.toast(e.getMessage());
                }
            }
        });
        addDialog.show();
    }

    private void insertFire(Book book)
    {
        HashMap<String,Object> bookMap=new HashMap<String,Object>();
        bookMap.put("title",book.getTitle());
        bookMap.put("year",book.getDate());
        bookMap.put("isbn",book.getIsbn());
        bookMap.put("pages",book.getPages());
        bookMap.put("photo",tools.convert(book.getPhoto()));
        bookMap.put("latitude",book.getPosition().latitude);
        bookMap.put("longitude",book.getPosition().longitude);
        bookMap.put("authors",book.getAuthors());
        bookMap.put("categories",book.getCategories());
        store.collection("books").add(bookMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful())
                            tools.toast(R.string.book_added);
                    }
                });
    }

    private void readapt()
    {
        BookAdapter adapter=new BookAdapter(CrossActivity.this,R.layout.book_layout,bookList);
        books.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String isbn=result.getContents();
                Toast.makeText(this, "Scanned: " + isbn, Toast.LENGTH_LONG).show();
                edit.setText(isbn);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
