package exam.spring.bookcross.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import exam.spring.bookcross.R;

public class ImageActivity extends AppCompatActivity {

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.show_dialog);
        image=findViewById(R.id.image);
        Bundle bundle=getIntent().getExtras();
        image.setImageBitmap((Bitmap) bundle.get("image"));
    }
}
