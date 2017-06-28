package in.nic.phra.app.forms.primaryregistration;

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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import in.nic.phra.app.R;
import in.nic.phra.app.data.Breed;

import static android.content.Context.MODE_PRIVATE;
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
    Spinner breed;
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
        String earTagNumber;
        String animalName;
        String animalAge;

        final SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("userSession", MODE_PRIVATE);
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_animal_form, container, false);

        RadioGroup radioGroupSpecies = (RadioGroup) fragmentView.findViewById(R.id.radio_group_species);
        radioGroupSpecies.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radioButtonCow) {
                    species = "1";
                    Log.i(TAG, "Species: Cow selected");
                    Toast.makeText(getContext(), "Cow", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.radioButtonBuffalo) {
                    species = "2";
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
                //TODO: Create webservices and invoke AsyncTask to send the details over GET Method
            }
        });
        return fragmentView;
    }

    private class FillSpinner extends AsyncTask<String, Void, String> {
        ArrayList<Breed> breedBeanList = new ArrayList<>();

        @Override
        protected String doInBackground(String... params) {
            String stateID = params[0];
            String species = params[1];
            String param =  "?stateID="+ stateID + "&species=" + species;
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
                    try{
                        JSONObject jsonObject = new JSONObject(response.toString());
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");

                        for(int i=0 ; i < jsonArray.length(); i++) {
                            JSONObject tuple = jsonArray.getJSONObject(i);

                            Breed breed = new Breed(tuple.getString("Breed_Name"),tuple.getInt("Breed_ID"));
                            breedBeanList.add(breed);
                        }
                        Log.i(TAG, "hi");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String string){
            breedList.clear();

            for (Breed breedBean : breedBeanList) {
                breedList.add(breedBean.getBreed());
            }
            breed.setAdapter(breedListAdapter);
        }
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
}
