package ch.ecommunicate.email;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ComposeActivity extends AppCompatActivity {

    private static final String TAG="ComposeActivity";

    private String id_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Intent in = getIntent();
        id_token = in.getStringExtra("id_token");

        final Button button = (Button)findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText subject_edittext = (EditText) findViewById(R.id.compose_subject);
                EditText body_edittext = (EditText) findViewById(R.id.compose_body);
                EditText to_edittext = (EditText) findViewById(R.id.compose_to);

                new ComposeActivityAsyncTask().execute(to_edittext.getText().toString(), subject_edittext.getText().toString(),body_edittext.getText().toString());

            }
        });


    }

    public class ComposeActivityAsyncTask extends AsyncTask<String, Void, Integer> {

        private static final String TAG = "CAAsyncTask";


        @Override
        protected void onPostExecute(Integer result) {

        }

        @Override
        protected Integer doInBackground(String... strings) {
            InputStream inputStream = null;
            HttpsURLConnection urlConnection = null;
            Integer result = 0;
            String to = strings[0];
            String subject = strings[1];
            String body = strings[2];

            try {

                URL url = new URL("https://email.android.ecommunicate.ch:443/sendemail/");
                urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Content-Type","application/json");

                urlConnection.setRequestProperty("Accept","application/json");

                urlConnection.setRequestMethod("POST");

                urlConnection.setDoInput(true);

                urlConnection.setDoOutput(true);

                OutputStream os = urlConnection.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                JSONObject json = new JSONObject();

                json.put("auth_token",id_token);
                json.put("to",to);
                json.put("subject",subject);
                json.put("body",body);

                writer.write(json.toString());

                writer.flush();

                writer.close();

                os.close();

                urlConnection.connect();

                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {

                    result = 1;

                } else {

                    result = 0;

                }
            }
            catch (Exception e) {

                if (e.getMessage() != null) {
                    Log.d(TAG, e.getMessage());
                }

                if (e.getLocalizedMessage() != null) {
                    Log.d(TAG, e.getLocalizedMessage());
                }

                if (e.getCause() != null) {
                    Log.d(TAG, e.getCause().toString());
                }

                e.printStackTrace();

                result = 0;
            }

            return result;
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null){
                result += line;
            }

            if(null!=inputStream){
                inputStream.close();
            }
            return result;
        }

    }


}
