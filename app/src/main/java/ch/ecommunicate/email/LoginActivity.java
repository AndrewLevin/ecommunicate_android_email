package ch.ecommunicate.email;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private String custom_token;
    private String id_token;

    private FirebaseAuth auth;

    Context context;

    ProgressDialog progress_dialog;


    public class LoginAndGetIDToken extends AsyncTask<String, Void, String> {

        private static final String TAG = "LoginActivityAsyncTask";

        String custom_token = "";


        @Override
        protected void onPreExecute(){

            //doing just progress_dialog.show(...) leads to null pointer exceptions when progress_dialog.dismiss is called later
            progress_dialog = ProgressDialog.show(context, "","Authenticating");

        }

        @Override
        protected void onPostExecute(String string) {

            if (string == "false") {

                if (progress_dialog != null) {
                    progress_dialog.dismiss();
                }

                TextView tv = (TextView) findViewById(R.id.loginerrors);
                tv.setText("Login unsuccessful");

                return;
            }

            auth.signInWithCustomToken(custom_token).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        FirebaseUser mUser = auth.getCurrentUser();

                        mUser.getToken(true)
                                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                    public void onComplete(@NonNull Task<GetTokenResult> task) {

                                        if (task.isSuccessful()) {

                                            id_token = task.getResult().getToken();

                                            new RegisterDevice().execute();

                                            Intent intent = new Intent(LoginActivity.this,EmailsActivity.class);

                                            intent.putExtra("sent", false);

                                            if (progress_dialog != null) {
                                                progress_dialog.dismiss();
                                            }

                                            startActivity(intent);

                                        } else {

                                            if (progress_dialog != null) {
                                                progress_dialog.dismiss();
                                            }

                                            TextView tv = (TextView) findViewById(R.id.loginerrors);
                                            tv.setText("Login unsuccessful");

                                            return;
                                        }
                                    }
                                });

                    } else {
                    }
                }
            });

        }

        @Override
        protected String doInBackground(String... username_and_password) {
            InputStream inputStream = null;
            HttpsURLConnection urlConnection = null;
            JSONObject json_object = null;
            String response = "";

            try {

                String username = username_and_password[0];

                String password = username_and_password[1];

                URL url = new URL("https://email.android.ecommunicate.ch:443/login/");

                urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Content-Type", "application/json");

                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.setDoInput(true);

                urlConnection.setDoOutput(true);

                OutputStream os = urlConnection.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));


                JSONObject username_and_password_json = new JSONObject();

                username_and_password_json.put("username",username);
                username_and_password_json.put("password",password);

                writer.write(username_and_password_json.toString());

                writer.flush();

                writer.close();

                os.close();

                urlConnection.setRequestMethod("POST");

                urlConnection.connect();

                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }

                    try {

                        json_object = new JSONObject(response);

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

                } else {



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
            }

            if (json_object != null){

                try {
                    if(json_object.getBoolean("success")){

                        custom_token = json_object.getString("custom_token");

                        return "true";
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

                    return "false";
                }

            }

            return "false";
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {

            this.finish();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Login");

        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        context = this;

        if (user != null) {

            //doing just progress_dialog.show(...) leads to null pointer exceptions when progress_dialog.dismiss is called later
            progress_dialog = ProgressDialog.show(context, "","Authenticating");

            user.getToken(false)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {

                            if (task.isSuccessful()) {

                                if (progress_dialog != null) {
                                    progress_dialog.dismiss();
                                }

                                id_token = task.getResult().getToken();

                                Intent in = new Intent(LoginActivity.this, EmailsActivity.class);

                                in.putExtra("sent", false);

                                startActivity(in);

                            } else {
                                Log.d(TAG, "task is not successful");


                            }
                        }
                    });

        } else {

            setContentView(R.layout.activity_login);

            TextView tv = (TextView) findViewById(R.id.register);
            tv.setText(Html.fromHtml("Register at <a href=\"https://ecommunicate.ch/register/\">ecommunicate.ch</a>"));
            tv.setLinksClickable(true);
            tv.setMovementMethod(LinkMovementMethod.getInstance());

            Button btnRegister = (Button) findViewById(R.id.btnLogin);
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    EditText editUsername = (EditText) findViewById(R.id.usernameText);

                    EditText editPassword = (EditText) findViewById(R.id.passwordText);

                    String usernameString = editUsername.getText().toString();

                    String passwordString = editPassword.getText().toString();

                    new LoginAndGetIDToken().execute(usernameString, passwordString);
                }

            });
        }

    }
}
