package ch.ecommunicate.email;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class ReadOneActivity extends AppCompatActivity implements View.OnClickListener  {


    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    private static final String TAG = "ReadOneActivity";

    String email_body;
    String email_subject;
    String email_from;
    String email_cc;
    String email_date;
    String email_to;
    String email_attachment1;
    String email_attachment1_id;

    private Boolean sent;
    private String email_id;
    private String id_token;
    private String contact_username;
    private String contact_name;

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("new_email")
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    public class ReadOneActivityAsyncTask2 extends AsyncTask<String, Void, Integer> {

        private static final String TAG = "ReadOneActivity";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }



        @Override
        protected void onPostExecute(Integer result) {



        }

        @Override
        protected Integer doInBackground(String... strings) {
            InputStream inputStream = null;
            HttpsURLConnection urlConnection = null;
            Integer result = 0;

            String attachment_id = strings[0];
            String filename = strings[1];

            try {

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://email.android.ecommunicate.ch:443/downloadattachment/?sent=\""+sent+"\"&&email_id=\""+email_id+"\"&&attachment_id=\""+attachment_id+"\""));

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

                request.addRequestHeader("Authorization",id_token);

                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

                manager.enqueue(request);

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

    public class ReadOneActivityAsyncTask1 extends AsyncTask<String, Void, Integer> {

        private static final String TAG = "ROActivityAsyncTask1";

        @Override
        protected void onPostExecute(Integer result) {



            TextView from = (TextView)findViewById(R.id.from);
            from.setText(email_from);
            TextView to = (TextView)findViewById(R.id.to);
            to.setText(email_to);
            TextView body = (TextView)findViewById(R.id.body);
            body.setText(email_body);
            TextView date = (TextView)findViewById(R.id.date);

            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat format2 = new SimpleDateFormat("dd MMM yyyy HH:mm");
            try {
                date.setText(format2.format(format1.parse(email_date)));

            } catch (ParseException e) {

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

            }

            //date.setText(email_date);
            TextView subject = (TextView)findViewById(R.id.subject);
            subject.setText(email_subject);
            Button attachment1 = (Button)findViewById(R.id.attachment1);

            if (email_attachment1 != "") {

                attachment1.setText(email_attachment1);

                attachment1.setTag(email_attachment1_id);

                attachment1.setVisibility(View.VISIBLE);

                attachment1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        final Button button = (Button) v;

                        if (ContextCompat.checkSelfPermission(ReadOneActivity.this,

                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(ReadOneActivity.this,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                                // Show an explanation to the user *asynchronously* -- don't block
                                // this thread waiting for the user's response! After the user
                                // sees the explanation, try again to request the permission.

                            } else {

                                // No explanation needed, we can request the permission.

                                ActivityCompat.requestPermissions(ReadOneActivity.this,
                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);

                                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                // app-defined int constant. The callback method gets the
                                // result of the request.
                            }
                        } else {

                            FirebaseAuth auth = FirebaseAuth.getInstance();

                            FirebaseUser user = auth.getCurrentUser();

                            user.getToken(false)
                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {

                                            if (task.isSuccessful()) {

                                                id_token = task.getResult().getToken();

                                                new ReadOneActivityAsyncTask2().execute((String) button.getTag(), (String) button.getText());

                                            }
                                        }
                                    });


                        }



                    }
                });

            } else {

                attachment1.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        protected Integer doInBackground(String... strings) {
            InputStream inputStream = null;
            HttpsURLConnection urlConnection = null;
            Integer result = 0;

            try {

                URL url = new URL("https://email.android.ecommunicate.ch:443/readone/");
                urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Content-Type","application/json");

                urlConnection.setRequestProperty("Accept","application/json");

                urlConnection.setRequestMethod("POST");

                urlConnection.setDoInput(true);

                urlConnection.setDoOutput(true);

                OutputStream os = urlConnection.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                JSONObject json_object = new JSONObject();

                json_object.put("email_id",email_id);
                json_object.put("sent",sent);
                json_object.put("id_token",id_token);

                writer.write(json_object.toString());

                writer.flush();

                writer.close();

                os.close();

                urlConnection.connect();

                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    String response = convertInputStreamToString(inputStream);

                    process_response(response);

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

        private void process_response(String response) {

            JSONObject email_json = null;
            try {
                email_json = new JSONObject(response);
            } catch (JSONException e) {

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
            }

            try {
                email_body = email_json.getString("body");
                email_subject = email_json.getString("subject");
                email_from = email_json.getString("from");
                email_date = email_json.getString("date");
                email_cc = email_json.getString("cc");
                email_to = email_json.getString("to");

                if (email_json.getJSONArray("attachments").length() > 0) {

                    email_attachment1 = ((JSONObject) email_json.getJSONArray("attachments").get(0)).getString("filename");
                    email_attachment1_id = ((JSONObject) email_json.getJSONArray("attachments").get(0)).getString("id");

                }

                else {

                    email_attachment1 = "";

                }

            } catch (JSONException e) {

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
            }

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_readone);

        final Button button = (Button)findViewById(R.id.reply);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent in = new Intent(ReadOneActivity.this, ComposeActivity.class);

                in.putExtra("sent", sent);
                in.putExtra("email_id",email_id);
                in.putExtra("reply",true);
                in.putExtra("sent",sent);

                startActivity(in);

            }
        });

        Intent in = getIntent();
        email_id = in.getStringExtra("email_id");
        sent = in.getBooleanExtra("sent",false);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        user.getToken(false)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {

                        if (task.isSuccessful()) {

                            id_token = task.getResult().getToken();

                            new ReadOneActivityAsyncTask1().execute();

                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {

    }
}

