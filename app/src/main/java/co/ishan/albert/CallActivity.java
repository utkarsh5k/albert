package co.ishan.albert;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class CallActivity extends AppCompatActivity {

    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        //Log.d("number1","working");
        Intent intent = getIntent();
        number = intent.getStringExtra(OptionActivity.EXTRA_MESSAGE);
        //Log.d("number",number);
        callNumber(number);
    }

    public void callNumber(String number){
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
        phoneIntent.setData(Uri.parse("tel:" + number));
        try {
            startActivity(phoneIntent);
            Toast.makeText(this,"Calling",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

