package co.ishan.albert;

import android.content.Intent;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.Locale;

public class OptionActivity extends AppCompatActivity {

    String options = "Press on the left side for speech assisstant or on the right for camera";
    TextToSpeech t;
    ImageButton microPhone,camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        t = new TextToSpeech(this,new TextToSpeech.OnInitListener() {
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t.setLanguage(Locale.ENGLISH);
                }
            }
        });

        new Handler().postDelayed(new Runnable(){
            public void run(){
                t.speak(options, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        },1000);

        microPhone = (ImageButton) findViewById(R.id.microphoneButton);
        camera = (ImageButton) findViewById(R.id.cameraButton);

        microPhone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(OptionActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(OptionActivity.this, AssisstantActivity.class);
                startActivity(intent);
            }
        });




    }
}
