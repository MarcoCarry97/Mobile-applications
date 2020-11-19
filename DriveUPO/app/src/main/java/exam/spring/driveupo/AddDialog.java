package exam.spring.driveupo;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class AddDialog extends AlertDialog
{
    private Builder dialogBuilder;
    private View view;
    private EditText start,end,seats;
    private TextView date;
    private ImageButton calendar;
    private Tools tools;
    //private ImageButton scan;

    protected AddDialog(Context context, int layoutId)
    {
        super(context);
        LayoutInflater inflater=getLayoutInflater().from(context);
        view=inflater.inflate(layoutId,null);
        dialogBuilder=new Builder(context);
        dialogBuilder.setView(view);
        tools=new Tools(context);
        //dialogBuilder.setCancelable(false);
        start=view.findViewById(R.id.start);
        end=view.findViewById(R.id.end);
        calendar=findViewById(R.id.calendar);
        date=view.findViewById(R.id.date);
        seats=view.findViewById(R.id.seats);
        //shoot=view.findViewById(R.id.shoot);
        //scan=view.findViewById(R.id.scan);
    }

    public void setTitle(int titleId)
    {
        dialogBuilder.setTitle(titleId);
    }

    public void setPositiveButton(int textId, OnClickListener listener)
    {
        dialogBuilder.setPositiveButton(textId, listener);
    }

    public void setNegativeButton(int textId, OnClickListener listener)
    {
        dialogBuilder.setPositiveButton(textId, listener);
    }

    public void setNeutralButton(int textId, OnClickListener listener)
    {
        dialogBuilder.setPositiveButton(textId, listener);
    }

    public void setImageButtonListener(int buttonId,View.OnClickListener listener)
    {
        ImageButton button=view.findViewById(buttonId);
        button.setOnClickListener(listener);
    }

    public void setValues(Trip trip)
    {
        start.setText(trip.getStart());
        end.setText(trip.getEnd());
        seats.setText(String.valueOf(trip.getSeats()));
        date.setText(trip.getDate().toString());
    }

    public void show()
    {
        dialogBuilder.create().show();
    }

    public View getView()
    {
        return view;
    }

}
