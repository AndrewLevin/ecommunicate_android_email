package ch.ecommunicate.email;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.iid.FirebaseInstanceId;

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


    public class LoginActivityAsyncTask1 extends AsyncTask<String, Void, String> {

        private static final String TAG = "LoginActivityAsyncTask1";



        @Override
        protected void onPostExecute(String string) {

            custom_token = string;

            Log.d(TAG,custom_token);

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

                                            Intent mIntent = new Intent(LoginActivity.this,EmailsActivity.class);

                                            mIntent.putExtra("id_token", id_token);
                                            mIntent.putExtra("sent", false);

                                            if (progress_dialog != null) {
                                                progress_dialog.dismiss();
                                            }

                                            startActivity(mIntent);

                                        } else {
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

            return response;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        this.finish();
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

            user.getToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {

                            if (task.isSuccessful()) {

                                if (progress_dialog != null) {
                                    progress_dialog.dismiss();
                                }

                                id_token = task.getResult().getToken();

                                Intent in = new Intent(LoginActivity.this, EmailsActivity.class);

                                in.putExtra("id_token", id_token);
                                in.putExtra("sent", false);

                                startActivity(in);

                            } else {
                                Log.d(TAG, "task is not successful");


                            }
                        }
                    });

        } else {

            setContentView(R.layout.activity_login);

            Button btnRegister = (Button) findViewById(R.id.btnLogin);
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    EditText editUsername = (EditText) findViewById(R.id.usernameText);

                    EditText editPassword = (EditText) findViewById(R.id.passwordText);

                    String usernameString = editUsername.getText().toString();

                    String passwordString = editPassword.getText().toString();

                    new LoginActivityAsyncTask1().execute(usernameString, passwordString);
                }

            });
        }

    }
}
