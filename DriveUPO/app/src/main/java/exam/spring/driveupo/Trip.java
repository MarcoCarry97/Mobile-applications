package exam.spring.driveupo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

public class Trip implements Parcelable
{
    private String id;
    private String start;
    private String end;
    private Date date;
    private int seats;
    private int occuped;
    private String driver;
    private ArrayList<String> joined;

    public Trip(String id, String start, String end, Date date, int seats,String driver)
    {
        this(id,start,end,date,seats,driver,0,new ArrayList<String>());
    }

    public Trip(String id,HashMap<String,Object> map)
    {
        this(id,(String)map.get("start"),(String) map.get("end"),new Date((long)map.get("date")),(int)map.get("seats"),(String)map.get("driver"));
        occuped=(int)map.get("occuped");
        joined=new ArrayList<String>();
    }

    public Trip(String id, String start, String end, Date date, int seats,String driver,ArrayList<String> joined) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.date = date;
        this.seats = seats;
        this.driver=driver;
        occuped=0;
        this.joined=joined;
    }

    public Trip(String id, String start, String end, Date date, int seats,String driver, int occuped,ArrayList<String> joined) {
        this(id,start,end,date,seats,driver,joined);
        this.occuped = occuped;
    }

    public boolean join(String user)
    {
        if(occuped<seats)
        {
            joined.add(user);
            occuped++;
            return true;
        }
        else return false;
    }

    public ArrayList<String> getUsers()
    {
        return joined;
    }

    public boolean delete(String user)
    {
        return joined.remove("user");
    }

    @NonNull
    @Override
    public String toString() {
        return id+" "+start.toString()+" "+end.toString()+" "+date.toString()+" "+seats+" "+occuped+" "+driver;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void addOccuped()
    {
        occuped++;
    }

    public void removeOccuped()
    {
        occuped--;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public int getOccuped() {
        return occuped;
    }

    public void setOccuped(int occuped) {
        this.occuped = occuped;
    }

    protected Trip(Parcel in) {
        id=in.readString();
        start = in.readString();
        end = in.readString();
        seats = in.readInt();
        occuped = in.readInt();
        date=new Date(in.readLong());
        driver=in.readString();
        joined=(ArrayList<String>)in.readSerializable();
    }



    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(start);
        dest.writeString(end);
        dest.writeInt(seats);
        dest.writeInt(occuped);
        dest.writeLong(date.getTime());
        dest.writeString(driver);
        dest.writeSerializable(joined);
    }
}
