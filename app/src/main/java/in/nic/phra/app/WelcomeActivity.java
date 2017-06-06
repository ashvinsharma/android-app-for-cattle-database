package in.nic.phra.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;


public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG="WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView textView = (TextView) findViewById(R.id.TextViewWelcome);
    }
}
