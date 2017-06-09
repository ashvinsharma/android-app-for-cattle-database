package in.nic.phra.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

import static in.nic.phra.app.data.WebServiceDetails.authenticate;
import static in.nic.phra.app.data.WebServiceDetails.wsURL;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private Boolean authFlag = false;
    private UserBean userBean;
    private EditText editTextUsername;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        TextView tvForgetPassword = (TextView) findViewById(R.id.textViewForgetPassword);
        tvForgetPassword.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void sendLoginRequest(View view) {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        //Converting password to md5 hash and initiating login process
        String digestedKey = hashKey(password);
        this.loginMethod(username, digestedKey);


    }

    private void loginMethod(final String username, String password) {

        class LoginMethodAsyncTask extends AsyncTask<String, Void, String> {
            private final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            private int responseCode;

            @Override
            protected void onPreExecute() {
                //Stopping the change of orientation of the device to prevent killing of the AsyncTask Process
                // unlocking right when the process is completed
                lockScreenOrientation();
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Logging in...");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                String paramUsername = params[0];
                String paramPassword = params[1];
                String postParam = "LoginUserId=" + paramUsername + "&Pwd=" + paramPassword;
                Log.d(TAG, "POST Query: LoginUserId=" + paramUsername + "&Pwd=" + paramPassword);
                //TODO: check network availability
                try {
                    //Apache Libraries and namevaluepair has been deprecated since APK 21(?). Using HttpURLConnection instead.
                    URL url = new URL(wsURL + authenticate);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                    OutputStream os = connection.getOutputStream();
                    os.write(postParam.getBytes());
                    os.flush();
                    os.close();

                    // Fetching the response code for debugging purposes.
                    responseCode = connection.getResponseCode();
                    Log.d(TAG, "POST Response Code: " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        //Adding every responseCode in the inputLine.
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        Log.d(TAG, "HTTP Response: " + response.toString());
                        //TODO: Check login logic again
                        //Sets Authentication flag
                        if (!response.toString().contains("Authentication Failed!")) {
                            authFlag = true;
                            Log.i(TAG, "Authentication Completed: Logged in Successfully!");
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("rows");
                                JSONObject beanObject = jsonArray.getJSONObject(0);

                                userBean = new UserBean(username, beanObject.getString("User_FullName"), beanObject.getInt("UserType_Code"));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Log.e(TAG, "Error!!! Abort!!!");
                    }
                    connection.disconnect();
                } catch (MalformedURLException e) {
                    Log.e(TAG, "URLConnection Exception: " + e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, "IOException Exception: " + e);
                }

                return postParam;
            }

            @Override
            protected void onPostExecute(String result) {
                unlockScreenOrientation();
                progressDialog.dismiss();
                Log.i(TAG, "in onPostExecute");

                //Moving to new Activity if the login is successful otherwise shows a toast
                if (authFlag) {
                    Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                    intent.putExtra("bean", userBean);
                    startActivity(intent);
                } else {
                    Context context = getApplicationContext();
                    final CharSequence INVALID_USERNAME_PASSWORD = "Invalid Username/Password!";
                    final CharSequence NO_INTERNET_CONNECTION = "Please check if you have an active Internet Connection";
                    int duration = Toast.LENGTH_LONG;

                    if (responseCode != 0) {
                        Toast toast = Toast.makeText(context, INVALID_USERNAME_PASSWORD, duration);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(context, NO_INTERNET_CONNECTION, duration);
                        toast.show();
                    }

                }
            }
        }
        LoginMethodAsyncTask loginMethodAsyncTask = new LoginMethodAsyncTask();
        loginMethodAsyncTask.execute(username, password);
    }

    private String hashKey(String pass) {
        MessageDigest messageDigest;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(pass.getBytes());
            byte[] messageDigestMD5 = messageDigest.digest();
            for (byte bytes : messageDigestMD5) {
                stringBuilder.append(String.format("%02x", bytes & 0xff));
            }

            Log.d(TAG, "pass: " + pass);
            Log.d(TAG, "hash key: " + stringBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private void lockScreenOrientation() {
        int currentConfig = getResources().getConfiguration().orientation;
        if (currentConfig == Configuration.ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

}

