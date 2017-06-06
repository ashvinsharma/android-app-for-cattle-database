package in.nic.phra.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

import static in.nic.phra.app.data.WebServiceDetails.*;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public Boolean flag = false;
    public UserBean userBean;
    EditText editTextUsername, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
    }

    public void sendLoginRequest(View view) {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        //Converting password to md5 hash and initiating login process
        String digestedKey = hashKey(password);
        this.loginMethod(username, digestedKey);

        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }

    private void loginMethod(final String username, String password) {

        class LoginMethodAsyncTask extends AsyncTask<String, Void, String> {
            private final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);

            @Override
            protected void onPreExecute() {
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
                    int responseCode = connection.getResponseCode();
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
                            flag = true;
                            Log.i(TAG, "Authentication Completed: Logged in Successfully!");
                            //TODO:get user details here
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
                    System.out.println("URLConnection Exception: " + e);
                } catch (IOException e) {
                    System.out.println("IOStream Exception: " + e);
                }
                /*catch (Exception e) {
                    Log.e(TAG, e.toString());
                   // e.printStackTrace();
                }*/
                return postParam;
            }

            @Override
            protected void onPostExecute(String result) {
                //TODO:Move to welcome Activity
                progressDialog.dismiss();
                Log.i(TAG, "in onPostExecute");
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
}
