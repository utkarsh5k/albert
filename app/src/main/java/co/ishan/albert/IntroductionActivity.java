package co.ishan.albert;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.speech.tts.TextToSpeech;

import java.util.Locale;
import android.os.Handler;


public class IntroductionActivity extends AppCompatActivity {

    private Button btn;
    String intro = "Hi There! Meet your new Virtual Assistant Albert. I am a complete speech based intelligent system where you just have to ask me and I will take care of everything . Tap anywhere on the screen to continue.";
    TextToSpeech t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        btn = (Button) findViewById(R.id.button);
        t = new TextToSpeech(this,new TextToSpeech.OnInitListener() {
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t.setLanguage(Locale.ENGLISH);
                }
            }
        });

        new Handler().postDelayed(new Runnable(){
            public void run(){
                t.speak(intro, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        },1000);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(IntroductionActivity.this, OptionActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onPause(){
        if(t != null){
            t.stop();
            t.shutdown();
        }
        super.onPause();
    }
}
