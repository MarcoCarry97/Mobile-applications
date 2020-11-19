package exam.spring.bookcross.ui;

import android.graphics.Bitmap;

import java.util.Date;

public class Search
{
    private long id;
    private String book;
    private Bitmap photo;
    private Date date;

    public long getId() {
        return id;
    }

    public void setId(long id)
    {
        this.id=id;
    }

    public String getBook() {
        return book;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public Date getDate() {
        return date;
    }

    public Search(String book, Bitmap photo, Date date) {
        this.book = book;
        this.photo = photo;
        this.date = date;
    }
}


