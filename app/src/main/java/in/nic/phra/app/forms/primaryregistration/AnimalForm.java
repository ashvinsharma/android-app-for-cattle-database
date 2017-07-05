package in.nic.phra.app.forms.primaryregistration;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import static in.nic.phra.app.data.WebServiceDetails.COMPLETE_SPINNER_BREED;
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
    private OnFragmentInteractionListener mListener;

    private String species;
    private String earTagNumber;
    private String animalName;
    private String animalAge;
    private Spinner breed;
    private Spinner lactationNumber;

    private TextView dateOfCalving;
    private List<String> breedList = new ArrayList<>();
    ArrayAdapter<String> breedListAdapter;

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


        final SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("userSession", MODE_PRIVATE);
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_animal_form, container, false);

        RadioGroup radioGroupSpecies = (RadioGroup) fragmentView.findViewById(R.id.radio_group_species);
        radioGroupSpecies.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

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

        EditText editTextEarTagNumber = (EditText) fragmentView.findViewById(R.id.editTextEarTag);
        earTagNumber = editTextEarTagNumber.toString();


        EditText editTextAnimalName = (EditText) fragmentView.findViewById(R.id.editTextAnimalName);
        animalName = editTextAnimalName.toString();

        EditText editTextAnimalAge = (EditText) fragmentView.findViewById(R.id.editTextAnimalAge);
        animalAge = editTextAnimalAge.toString();

        breed = (Spinner) fragmentView.findViewById(R.id.spinner_breed);
        breed.setOnItemSelectedListener(this);
        breedList.add("Select a Breed");
        breedListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, breedList);
        breedListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        breed.setAdapter(breedListAdapter);

        dateOfCalving = (TextView) fragmentView.findViewById(R.id.DateOfCalving);
        dateOfCalving.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
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

        Button buttonBack = (Button) fragmentView.findViewById(R.id.backButton);
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
                //TODO: Create webservices and invoke AsyncTask to send the details over GET Method
            }
        });
        return fragmentView;
    }

    private class FillSpinner extends AsyncTask<String, Void, String> {
        @SuppressLint("UseSparseArrays")
        Map<Integer, String> breedHashMap = new HashMap<>();

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
                Log.i(TAG, "POST Response is: " + responseCode);

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
                            breedHashMap.put(tuple.getInt("Breed_ID"), tuple.getString("Breed_Name"));
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
                breedList.add(breedMap.getValue().toString());
            }
            breed.setAdapter(breedListAdapter);
        }
    }

    private void getData() {
        Bundle bundle = new Bundle();
        bundle.getString("species", species);
        bundle.getString("earTagNumber", earTagNumber);
        bundle.getString("animalName", animalName);
        bundle.getString("animalAge", animalAge);
        bundle.getString("breed", breed.getSelectedItem().toString());
        //dateofcalving
//        bundle.getString();
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);

        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(onDate);
        date.show(getActivity().getSupportFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener onDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            Toast.makeText(
                    getActivity().getApplicationContext(),
                    String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear) + "/" + String.valueOf(year),
                    Toast.LENGTH_LONG).show();
            dateOfCalving.setText("Date of Calving: " +
                    String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear) + "/" + String.valueOf(year));
        }
    };

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
}
