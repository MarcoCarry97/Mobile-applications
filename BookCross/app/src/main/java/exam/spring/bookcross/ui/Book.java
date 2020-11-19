package exam.spring.bookcross.ui;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Book implements Parcelable
{
    private String id;
    private String title;
    private String isbn;
    private int pages;
    private String date;
    private ArrayList<String> categories;
    private ArrayList<String> authors;
    private Bitmap photo;
    private LatLng position;

    public String getId()
    {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getPages() {
        return pages;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public LatLng getPosition(){
        return position;
    }

    public Book(String id,String title,String isbn,int pages,String date,ArrayList<String> categories,ArrayList<String> authors,Bitmap photo,LatLng position)
    {
        this.id=id;
        this.title=title;
        this.isbn=isbn;
        //this.publisher=publisher;
        this.date=date;
        this.pages=pages;
        this.categories=categories;
        this.authors=authors;
        this.photo=photo;
        this.position=position;
    }

    protected Book(Parcel in)
    {
        id=in.readString();
        title=in.readString();
        isbn=in.readString();
        //publisher=in.readString();
        date=in.readString();
        pages=in.readInt();
        photo=in.readParcelable(Bitmap.class.getClassLoader());
        position=in.readParcelable(LatLng.class.getClassLoader());
        categories= (ArrayList<String>) in.readSerializable();
        authors=(ArrayList<String>) in.readSerializable();

    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(isbn);
        //dest.writeString(publisher);
        dest.writeString(date);
        dest.writeInt(pages);
        dest.writeParcelable(photo,flags);
        dest.writeParcelable(position,flags);
        dest.writeSerializable(categories);
        dest.writeSerializable(authors);


    }

    public String toString()
    {
        return String.format("%s %s %s %s %s %n",title,isbn,photo,date,pages);
    }

    public void setId(String id)
    {
        this.id=id;
    }
}
