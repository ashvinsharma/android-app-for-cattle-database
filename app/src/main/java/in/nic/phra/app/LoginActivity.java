package in.nic.phra.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import static in.nic.phra.app.data.Strings.INVALID_USERNAME_PASSWORD;
import static in.nic.phra.app.data.Strings.NO_INTERNET_CONNECTION;
import static in.nic.phra.app.data.WebServiceDetails.AUTHENTICATE;
import static in.nic.phra.app.data.WebServiceDetails.WS_URL;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String TAG = "LoginActivity";
    private Boolean authFlag = false;
    private EditText editTextUsername;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getApplicationContext().getSharedPreferences("userSession", MODE_PRIVATE);

        if (!(sharedPreferences.getString("username", "null").equals("null"))) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }

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
                /* Stopping the change of orientation of the device to prevent killing of the AsyncTask Process
                 * unlocking right when the process is completed
                 * Hiding Keyboard
                 */

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(editTextUsername.getWindowToken(), 0);

                lockScreenOrientation();

                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Logging in...");
                progressDialog.show();

            }

            @Override
            protected String doInBackground(String... params) {
                String paramUsername = params[0];
                String paramPassword = params[1];
                editor = sharedPreferences.edit();
                String postParam = "LoginUserId=" + paramUsername + "&Pwd=" + paramPassword;
                Log.d(TAG, "POST Query: " + postParam);
                try {
                    //Apache Libraries and namevaluepair has been deprecated since APK 21(?). Using HttpURLConnection instead.
                    URL url = new URL(WS_URL + AUTHENTICATE);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                    OutputStream os = connection.getOutputStream();
                    os.write(postParam.getBytes());
                    os.flush();
                    os.close();

                    // Fetching the response code
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
                        //Sets Authentication flag
                        if (!response.toString().contains("Authentication Failed!")) {
                            authFlag = true;
                            Log.i(TAG, "Authentication Completed: Logged in Successfully!");
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("rows");
                                JSONObject beanObject = jsonArray.getJSONObject(0);

                                editor.putString("username", username);
                                editor.putString("User_FullName", beanObject.getString("User_FullName"));
                                editor.putInt("State_ID", beanObject.getInt("State_ID"));
                                editor.putString("State_Name", beanObject.getString("State_Name"));
                                editor.putInt("District_ID", beanObject.getInt("District_ID"));
                                editor.putString("District_Name", beanObject.getString("District_Name"));
                                editor.putInt("Block_ID", beanObject.getInt("Block_ID"));
                                editor.putString("Block_Name", beanObject.getString("Block_Name"));
                                editor.putInt("Centre_ID", beanObject.getInt("Centre_ID"));
                                editor.putString("Centre_Name", beanObject.getString("Centre_Name"));
                                editor.putInt("UserType_Code", beanObject.getInt("UserType_Code"));
                                editor.apply();

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
                    startActivity(intent);
                } else {
                    Context context = getApplicationContext();
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

    @NonNull
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

    /**
     * Manual method to lock the screen orientation; used in AsyncTask
     * prevents crash on changing orientation
     */
    private void lockScreenOrientation() {
        int currentConfig = getResources().getConfiguration().orientation;
        if (currentConfig == Configuration.ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Manual method to lock the screen orientation; used in AsyncTask
     * prevents crash on changing orientation
     */
    private void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

}