package co.ishan.albert;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OptionActivity extends AppCompatActivity {

    private String options = "Press on the left side for speech assisstant or on the right for camera";
    private TextToSpeech t;
    private ImageButton microPhone,camera;
    private String callString, command, firstWord;
    private String arr[];
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private AudioManager manager;
    public final static String EXTRA_MESSAGE = "intent message";
    public TextView tv;

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
        tv = (TextView) findViewById(R.id.tv);

        microPhone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(OptionActivity.this, AssisstantActivity.class);
                startActivity(intent);
            }
        });




    }

    private void promptSpeechInput() {
        Log.d("status10","working fine");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        Log.d("status11","working fine");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Something");
        Log.d("status12","working fine");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),"Sorry! Your device doesn't support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("status13","working fine");
        switch (requestCode){
            case REQ_CODE_SPEECH_INPUT: {
                Log.d("status14","working fine");
                if(resultCode == RESULT_OK && null != data){
                    Log.d("status15","working fine");
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    command = result.get(0).toString();
                    Log.d("status16",command);
                    tv.setText(command);
                    arr = command.split(" ",2);
                    firstWord = arr[0];
                    Log.d("First Word", firstWord);
                    switch(firstWord)
                    {
                        case "silent":
                            switchToSilent();
                            break;
                        case "general":
                            switchToRinger();
                            break;
                        case "call":
                            callString = arr[1];
                            Log.d("Second Word", callString);
                            callPhone(callString);
                            break;
                        case "open":
                            callString = arr[1];
                            Log.d("Second Word", callString);
                            openApp(callString);
                            break;
                        default:
                            Log.d("Message", "Sorry! I can't catch you!");
                    }

                }
            }
        }
    }

    public void callPhone(String callNumber){
        Intent callIntent = new Intent(this, CallActivity.class);
        callIntent.putExtra(EXTRA_MESSAGE, callNumber);
        startActivity(callIntent);

    }

    public void switchToSilent(){
        manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        manager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Toast.makeText(OptionActivity.this,"Now in Silent Mode", Toast.LENGTH_SHORT).show();
    }

    public void switchToRinger(){
        manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Toast.makeText(OptionActivity.this,"Now in Ringer Mode", Toast.LENGTH_SHORT).show();
    }

    public void openApp(String callString){
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> packageAppsList = this.getPackageManager().queryIntentActivities(intent,0);
        int i;
        int counter = 0;
        for(ResolveInfo res : packageAppsList){
            if(callString.equals(res.loadLabel(getPackageManager()).toString())){
                break;
            }
            ++counter;
            Log.d("App Name", res.loadLabel(getPackageManager()).toString());
        }

        String finalPackageName = "";
        for(i = 1; i < packageAppsList.size(); i ++ ){
            Object obj = packageAppsList.get(i);
            String temp = obj.toString().split(" ")[1];
            String temp1 = temp.split("/")[0];
            Log.d("Final App Name",temp1);
            if(i == counter)
                finalPackageName = temp1;
        }

        PackageManager p = getPackageManager();
        try{

            String packageName = finalPackageName;
            Intent launchIntent = p.getLaunchIntentForPackage(packageName);
            startActivity(launchIntent);
        }
        catch (Exception e1)
        {
        }

    }


}
