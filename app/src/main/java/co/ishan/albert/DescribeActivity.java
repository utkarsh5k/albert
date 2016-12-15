package co.ishan.albert;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisInDomainResult;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;
import com.microsoft.projectoxford.vision.contract.Model;
import com.microsoft.projectoxford.vision.contract.ModelResult;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class DescribeActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_IMAGE = 0;
    private Uri mImageUri;
    private Bitmap mBitmap;
    private VisionServiceClient vclient;
    private GoogleApiClient gclient;
    private TextToSpeech engine;
    private EditText et;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_describe);

        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.getStringExtra("imageUri"));
        Log.d("Hello", uri.toString());
        try {
            InputStream image_stream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            mBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        iv = (ImageView)findViewById(R.id.selectedImage);
        iv.setImageBitmap(mBitmap);

        if(vclient == null){
            vclient = new VisionServiceRestClient("68376a5b1a1646cfaad72641025605b7");
        }

        engine = new TextToSpeech(this, new TextToSpeech.OnInitListener(){
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    engine.setLanguage(Locale.ENGLISH);
                }
            }
        });

        et = (EditText) findViewById(R.id.result);
        gclient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }

    public void doDescribe() {

        et.setText("Please Wait...");

        try {
            new doRequest().execute();
        } catch (Exception e) {
            et.setText("Exception occurred is:" + e.toString());
        }
    }

    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        AnalysisResult v = this.vclient.describe(inputStream, 1);

        String result = gson.toJson(v);
        Log.d("result", result);

        return result;
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Describe Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        gclient.connect();
        AppIndex.AppIndexApi.start(gclient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        AppIndex.AppIndexApi.end(gclient, getIndexApiAction());
        gclient.disconnect();
    }



    private class doRequest extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        public doRequest() {
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                return process();
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            // Display based on error existence

            et.setText("");
            if (e != null) {
                et.setText("Error: " + e.getMessage());
                this.e = null;
            } else {
                Gson gson = new Gson();
                AnalysisResult result = gson.fromJson(data, AnalysisResult.class);

                et.append("\n");

                for (Caption caption : result.description.captions) {
                    et.append("Caption: " + caption.text + "\n");
                    engine.speak(caption.text, TextToSpeech.QUEUE_FLUSH, null, null);
                }
                et.append("\n");

                for (String tag : result.description.tags) {
                    et.append("Tags: " + tag + "\n");
                }
                et.append("\n");

            }
        }
    }







}
