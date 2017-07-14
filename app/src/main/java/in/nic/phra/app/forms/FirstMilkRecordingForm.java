package in.nic.phra.app.forms;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.nic.phra.app.R;

public class FirstMilkRecordingForm extends AppCompatActivity {
    private JSONObject jsonObject;
    private String total;
    private String morning;
    private String evening;

    private TextView tvDate;
    private TextView tvDay;
    private EditText editTextMorning;
    private EditText editTextEvening;
    private TextView tvTotal, tvProduction;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_milk_recording_form);

        tvDate = (TextView) findViewById(R.id.textViewDateShow);
        tvDay = (TextView) findViewById(R.id.textViewDayShow);
        editTextMorning = (EditText) findViewById(R.id.editTextMorning);
        editTextEvening = (EditText) findViewById(R.id.editTextEvening);
        tvTotal = (TextView) findViewById(R.id.textViewTotal);
        tvProduction = (TextView) findViewById(R.id.textViewProduction);

        try {
            jsonObject = new JSONObject(getIntent().getStringExtra("JSONDetails"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("First Milk Recording");

        String doc = "";
        String frd = "";

        try {
            doc = jsonObject.getString("DateOfCalving");
            frd = jsonObject.getString("FirstRecDate");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Date dateCalving = new Date();
        Date firstDate = new Date();
        try {
            dateCalving = new SimpleDateFormat("dd-MM-yyyy").parse(doc);
            firstDate = new SimpleDateFormat("dd-MM-yyyy").parse(frd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int days = (int) (firstDate.getTime() - dateCalving.getTime()) / (1000 * 60 * 60 * 24);

        tvDate.setText(frd);
        tvDay.setText(String.valueOf(days));

        morning = editTextMorning.getText().toString();
        evening = editTextEvening.getText().toString();
        editTextMorning.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    if (!evening.equals("")) {
                        tvTotal.setText(String.valueOf(Integer.parseInt(s.toString()) + Integer.parseInt(evening)));
                    } else {
                        tvTotal.setText(s.toString());
                    }
                } else {
                    tvTotal.setText(evening);
                }
                tvProduction.setText(String.valueOf((Integer.parseInt(tvTotal.getText().toString()) * Integer.parseInt(tvDay.getText().toString()))));
                morning = s.toString();
            }
        });
        editTextEvening.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    if (!morning.equals("")) {
                        tvTotal.setText(String.valueOf(Integer.parseInt(s.toString()) + Integer.parseInt(morning)));
                    } else {
                        tvTotal.setText(s.toString());
                    }
                } else {
                    tvTotal.setText(morning);
                }
                tvProduction.setText(String.valueOf((Integer.parseInt(tvTotal.getText().toString()) * Integer.parseInt(tvDay.getText().toString()))));
                evening = s.toString();
            }
        });
    }
}