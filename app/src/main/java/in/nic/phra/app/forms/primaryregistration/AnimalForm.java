package in.nic.phra.app.forms.primaryregistration;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.nic.phra.app.R;

import static android.content.Context.MODE_PRIVATE;
import static in.nic.phra.app.data.Strings.BUFFALO;
import static in.nic.phra.app.data.Strings.COW;
import static in.nic.phra.app.data.Strings.FEMALE;
import static in.nic.phra.app.data.Strings.MALE;
import static in.nic.phra.app.data.WebServiceDetails.COMPLETE_SPINNER_BREED;
import static in.nic.phra.app.data.WebServiceDetails.SEND_PRIMARY_REGISTRATION_FORM_DATA;
import static in.nic.phra.app.data.WebServiceDetails.WS_URL;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AnimalForm.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AnimalForm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnimalForm extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "AnimalForm";
    private SharedPreferences sharedPreferences = null;
    private OnFragmentInteractionListener mListener;
    Bundle animalBundle, ownerBundle;

    private String species;
    private String docDate, docMonth, docYear;
    private String frdDate, frdMonth, frdYear;
    private String sex;

    private Spinner breed;
    private Spinner lactationNumber;
    private EditText editTextEarTagNumber;
    private EditText editTextAnimalName;
    private EditText editTextAnimalAge;
    private EditText sireDetailEditText;
    private EditText damDetailEditText;
    private EditText calfEarTagEditText;
    private TextView dateOfCalving;
    private TextView firstRecordingDate;

    private List<String> breedList = new ArrayList<>();
    private ArrayAdapter<String> breedListAdapter;
    @SuppressLint("UseSparseArrays")
    private Map<String, Integer> breedHashMap = new HashMap<>();
    private View datePickerId;

    public AnimalForm() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AnimalForm.
     */
    public static AnimalForm newInstance() {
        AnimalForm fragment = new AnimalForm();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("userSession", MODE_PRIVATE);

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_animal_form, container, false);

        ((RadioGroup) fragmentView.findViewById(R.id.radio_group_species)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radioButtonCow) {
                    species = COW;
                    Log.i(TAG, "Species: Cow selected");
                    Toast.makeText(getContext(), "Cow", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.radioButtonBuffalo) {
                    species = BUFFALO;
                    Log.i(TAG, "Species: Buffalo selected");
                    Toast.makeText(getContext(), "Buffalo", Toast.LENGTH_SHORT).show();
                }
                String[] params = new String[]{String.valueOf(sharedPreferences.getInt("State_ID", 0)), species};
                new FillSpinner().execute(params);
            }
        });

        editTextEarTagNumber = (EditText) fragmentView.findViewById(R.id.editTextEarTag);
        editTextAnimalName = (EditText) fragmentView.findViewById(R.id.editTextAnimalName);
        editTextAnimalAge = (EditText) fragmentView.findViewById(R.id.editTextAnimalAge);
        sireDetailEditText = (EditText) fragmentView.findViewById(R.id.editTextSire);
        damDetailEditText = (EditText) fragmentView.findViewById(R.id.editTextDam);
        calfEarTagEditText = (EditText) fragmentView.findViewById(R.id.editTextEarTagofCalf);
        Button buttonBack = (Button) fragmentView.findViewById(R.id.backButton);

        dateOfCalving = (TextView) fragmentView.findViewById(R.id.DateOfCalving);

        breed = (Spinner) fragmentView.findViewById(R.id.spinner_breed);
        breed.setOnItemSelectedListener(this);
        breedList.add("Select a Breed");
        breedListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, breedList);
        breedListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        breed.setAdapter(breedListAdapter);

        dateOfCalving.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                datePickerId = view;
                showDatePicker();
            }
        });

        lactationNumber = (Spinner) fragmentView.findViewById(R.id.spinner_number_of_lactation);
        lactationNumber.setOnItemSelectedListener(this);
        ArrayList<Integer> lactationNumberList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            lactationNumberList.add(i + 1);
        }
        ArrayAdapter<Integer> lactationAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lactationNumberList);
        lactationNumber.setAdapter(lactationAdapter);


        firstRecordingDate = (TextView) fragmentView.findViewById(R.id.textViewFirstRecordingDate);
        firstRecordingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerId = view;
                showDatePicker();
            }
        });

        ((RadioGroup) fragmentView.findViewById(R.id.radio_group_sex)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radioButtonMale) {
                    sex = MALE;
                    Log.i(TAG, "Sex: Male selected");
                    Toast.makeText(getContext(), "Male", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.radioButtonFemale) {
                    sex = FEMALE;
                    Log.i(TAG, "Sex: Female selected");
                    Toast.makeText(getContext(), "Female", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPager mViewPager = (ViewPager) getActivity().findViewById(R.id.container);
                mViewPager.setCurrentItem(0);
            }
        });

        Button buttonSubmit = (Button) fragmentView.findViewById(R.id.submit_button);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
                new sendFormData().execute();
            }
        });
        return fragmentView;
    }

    private class FillSpinner extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String stateID = params[0];
            String species = params[1];
            String param = "?stateID=" + stateID + "&species=" + species;
            Log.d(TAG, "Query: " + param);

            try {
                URL url = new URL(WS_URL + COMPLETE_SPINNER_BREED + param);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                int responseCode = connection.getResponseCode();
                Log.i(TAG, connection.getRequestMethod() + " Response is: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    Log.d(TAG, "HTTP Response is: " + response.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject tuple = jsonArray.getJSONObject(i);
                            breedHashMap.put(tuple.getString("Breed_Name"), tuple.getInt("Breed_ID"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String string) {
            breedList.clear();

            for (Map.Entry breedMap : breedHashMap.entrySet()) {
                breedList.add(breedMap.getKey().toString());
            }
            breed.setAdapter(breedListAdapter);
        }
    }

    private class sendFormData extends AsyncTask<Void, Void, Boolean> {
        private final ProgressDialog progressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            lockScreenOrientation();

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Sending Form Data...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //TODO: handle whitespaces in string parameters {whitespace = "%20"}
            String param = "?area=" + ownerBundle.getString("area") +
                    "&villageTownID=" + ownerBundle.getString("villageTown") +
                    "&ownerName=" + ownerBundle.getString("ownerName") +
                    "&fatherHusbandName=" + ownerBundle.getString("fatherHusbandName") +
                    "&category=" + ownerBundle.getString("category") +
                    "&address=" + ownerBundle.getString("address") +
                    "&mobileNumber=" + ownerBundle.getString("mobileNumber") +
                    "&telephoneNumber=" + ownerBundle.getString("telephoneNumber") +
                    "&species=" + animalBundle.getString("species") +
                    "&earTagNumber=" + animalBundle.getString("earTagNumber") +
                    "&animalName=" + animalBundle.getString("animalName") +
                    "&animalAge=" + animalBundle.getString("animalAge") +
                    "&breed=" + animalBundle.getString("breed") +
                    "&dateOfCalving=" + animalBundle.getString("doc") +
                    "&lactationNumber=" + animalBundle.getString("numberOfLactation") +
                    "&sireDetails=" + animalBundle.getString("sireDetails") +
                    "&damDetails=" + animalBundle.getString("damDetails") +
                    "&firstRecordingDate=" + animalBundle.getString("frd") +
                    "&calfEarTag=" + animalBundle.getString("earTagCalf") +
                    "&sex=" + animalBundle.getString("sex") +
                    "&stateID=" + String.valueOf(sharedPreferences.getInt("State_ID", 0)) +
                    "&districtID=" + String.valueOf(sharedPreferences.getInt("District_ID", 0)) +
                    "&blockID=" + String.valueOf(sharedPreferences.getInt("Block_ID", 0)) +
                    "&centreID=" + String.valueOf(sharedPreferences.getInt("Centre_ID", 0)) +
                    "&username=" + sharedPreferences.getString("username", null);
            Log.d(TAG, param);

            try {
                URL url = new URL(WS_URL + SEND_PRIMARY_REGISTRATION_FORM_DATA + param);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                int responseCode = httpURLConnection.getResponseCode();
                Log.i(TAG, httpURLConnection.getRequestMethod() + " Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    if (response.toString().equals("\"Success\"")) {
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            if (flag) {
                Toast.makeText(getActivity(), "Form Submitted Successfully!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Some Error Occurred! Please Try Again.", Toast.LENGTH_LONG).show();
            }

            unlockScreenOrientation();
            progressDialog.dismiss();
        }
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();

        // Set Up Current Date Into dialog
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);

        //Set Call back to capture selected date
        date.setCallBack(onDate);
        date.show(getActivity().getSupportFragmentManager(), "Date Picker");
    }

    private void getData() {
        String[] params = new String[]{
                editTextEarTagNumber.getText().toString().trim(),
                editTextAnimalName.getText().toString().trim(),
                editTextAnimalAge.getText().toString().trim(),
                sireDetailEditText.getText().toString().trim(),
                damDetailEditText.getText().toString().trim(),
                calfEarTagEditText.getText().toString().trim()
        };

        //removing spaces with "%20" to send through GET
        for (int i = 0; i < params.length; i++)
            if (params[i].contains(" ")) {
                params[i] = params[i].replace(" ", "%20");
            }

        String breedID;
        if (breedHashMap.get(breed.getSelectedItem().toString()) == null) {
            breedID = null;
        } else {
            breedID = String.valueOf(breedHashMap.get(breed.getSelectedItem().toString()));
        }

        animalBundle = new Bundle();
        animalBundle.putString("species", species);
        animalBundle.putString("earTagNumber", params[0]);
        animalBundle.putString("animalName", params[1]);
        animalBundle.putString("animalAge", params[2]);
        animalBundle.putString("breed", breedID);
        animalBundle.putString("doc", docDate + docMonth + docYear);
        animalBundle.putString("numberOfLactation", lactationNumber.getSelectedItem().toString());
        animalBundle.putString("sireDetails", params[3]);
        animalBundle.putString("damDetails", params[4]);
        animalBundle.putString("frd", frdDate + frdMonth + frdYear);
        animalBundle.putString("earTagCalf", params[5]);
        animalBundle.putString("sex", sex);

        Log.i(TAG, "test");
    }

    DatePickerDialog.OnDateSetListener onDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            if (datePickerId.getId() == R.id.DateOfCalving) {
                if (dayOfMonth < 9)
                    docDate = "0";
                docDate += String.valueOf(dayOfMonth);

                if (monthOfYear < 9)
                    docMonth = "0";
                docMonth += String.valueOf(monthOfYear + 1); //month starts from 0

                docYear = String.valueOf(year);

                dateOfCalving.setText("Date of Calving: " +
                        String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear) + "/" + String.valueOf(year));
            } else if (datePickerId.getId() == R.id.textViewFirstRecordingDate) {
                if (dayOfMonth < 9)
                    frdDate = "0";
                frdDate += String.valueOf(dayOfMonth);

                if (monthOfYear < 9)
                    frdMonth = "0";
                frdMonth += String.valueOf(monthOfYear + 1); //month starts from 0

                frdYear = String.valueOf(year);

                firstRecordingDate.setText("First Recoding Date: " +
                        String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear) + "/" + String.valueOf(year));
            }
            datePickerId = null;
        }
    };

    public void sendBundle(Bundle bundle) {
        ownerBundle = bundle;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Manual method to lock the screen orientation; used in AsyncTask
     * prevents crash on changing orientation
     */
    private void lockScreenOrientation() {
        int currentConfig = getResources().getConfiguration().orientation;
        if (currentConfig == Configuration.ORIENTATION_LANDSCAPE)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Manual method to lock the screen orientation; used in AsyncTask
     * prevents crash on changing orientation
     */
    private void unlockScreenOrientation() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}
