package exam.spring.mymuseum.ui;

import android.os.Parcel;
import android.os.Parcelable;

class Room extends CustomItem implements Parcelable
{
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
    }

    protected Room(Parcel in)
    {
        id=in.readString();
        name=in.readString();
        description=in.readString();
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);

    }
}
