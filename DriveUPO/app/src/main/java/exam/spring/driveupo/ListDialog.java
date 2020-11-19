package exam.spring.driveupo;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class ListDialog extends AlertDialog
{
    private Context context;
    private ArrayList<String> users;
    private View view;
    private AlertDialog dialog;

    public ListDialog(Context context,ArrayList<String> users)
    {
        super(context);
        this.context=context;
        this.users=users;
        LayoutInflater inflater=LayoutInflater.from(context);
        view=inflater.inflate(R.layout.list_dialog,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setTitle(R.string.list_title);
        ListView list=view.findViewById(R.id.list);
        String array[]=new String[users.size()];
        for(int i=0;i<array.length;i++)
            array[i]=users.get(i);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(context,android.R.layout.simple_expandable_list_item_1,array);
        list.setAdapter(adapter);
        dialog=builder.create();
    }

    public void show()
    {
        dialog.show();
    }
}
