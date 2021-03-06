package in.nic.phra.app.forms.primaryregistration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.nic.phra.app.R;

import static android.content.Context.MODE_PRIVATE;
import static in.nic.phra.app.data.Strings.NO_INTERNET_CONNECTION;
import static in.nic.phra.app.data.Strings.RURAL;
import static in.nic.phra.app.data.Strings.TOWN;
import static in.nic.phra.app.data.Strings.URBAN;
import static in.nic.phra.app.data.Strings.VILLAGE;
import static in.nic.phra.app.data.WebServiceDetails.COMPLETE_SPINNER_TOWN_VILLAGE;
import static in.nic.phra.app.data.WebServiceDetails.WS_URL;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OwnerForm.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OwnerForm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OwnerForm extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "OwnerForm";
    private OnFragmentInteractionListener mListener;

    private Spinner villageTown;
    private Spinner category;
    private EditText editTextAddress;
    private EditText editTextTelephoneNumber;
    private EditText editTextMobileNumber;
    private EditText editTextFatherHusbandName;
    private EditText editTextOwnerName;

    @SuppressLint("UseSparseArrays")
    Map<String, Integer> villageTownMap = new HashMap<>();  //stores name+ID of village/town

    private final List<String> villageTownList = new ArrayList<>();
    private ArrayAdapter<String> villageTownListDataAdapter;

    private String area;

    public OwnerForm() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OwnerForm.
     */
    public static OwnerForm newInstance() {
        OwnerForm fragment = new OwnerForm();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("userSession", MODE_PRIVATE);

        // Inflate the layout for this fragment
        View inputFragmentView = inflater.inflate(R.layout.fragment_owner_form, container, false);

        RadioGroup radioGroupArea = (RadioGroup) inputFragmentView.findViewById(R.id.areaRadioGroup);
        radioGroupArea.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radioButtonUrban) {
                    area = URBAN;
                    Log.i(TAG, "Area: Urban selected");
                } else if (checkedId == R.id.radioButtonRural) {
                    area = RURAL;
                    Log.i(TAG, "Area: Rural selected");
                }

                new FillSpinner().execute(area, String.valueOf(sharedPreferences.getInt("State_ID", 0)),
                        String.valueOf(sharedPreferences.getInt("District_ID", 0)), String.valueOf(sharedPreferences.getInt("Block_ID", 0)),
                        String.valueOf(sharedPreferences.getInt("Centre_ID", 0)));
            }
        });

        editTextAddress = (EditText) inputFragmentView.findViewById(R.id.Address);
        editTextTelephoneNumber = (EditText) inputFragmentView.findViewById(R.id.editTextTelephoneNumber);
        editTextMobileNumber = (EditText) inputFragmentView.findViewById(R.id.editTextMobileNumber);
        editTextFatherHusbandName = (EditText) inputFragmentView.findViewById(R.id.editTextFatherHusbandName);
        editTextOwnerName = (EditText) inputFragmentView.findViewById(R.id.editTextOwnerName);

        villageTown = (Spinner) inputFragmentView.findViewById(R.id.spinnerVillageTown);
        villageTown.setOnItemSelectedListener(this);
        villageTownList.add("Select one Village/Town");
        villageTownListDataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, villageTownList);
        villageTownListDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        villageTown.setAdapter(villageTownListDataAdapter);

        category = (Spinner) inputFragmentView.findViewById(R.id.spinnerCategory);
        category.setOnItemSelectedListener(this);
        List<String> categoryList = new ArrayList<>();
        categoryList.add("General");
        categoryList.add("SC");
        categoryList.add("OBC");
        categoryList.add("Other");
        ArrayAdapter<String> categoryListDataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryList);
        categoryListDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryListDataAdapter);

        Button nextButton = (Button) inputFragmentView.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPager mViewPager = (ViewPager) getActivity().findViewById(R.id.container);
                mViewPager.setCurrentItem(1);
            }
        });

        return inputFragmentView;
    }

    private class FillSpinner extends AsyncTask<String, Void, String> {
        int responseCode;

        @Override
        protected void onPreExecute() {
            villageTownList.clear();
            villageTownList.add("Please wait ...");
        }

        @Override
        protected String doInBackground(String... params) {
            String area = params[0];
            String state = params[1];
            String district = params[2];
            String block = params[3];
            String centre = params[4];
            String param = "?area=" + area
                    + "&stateID=" + state
                    + "&districtID=" + district
                    + "&blockID=" + block
                    + "&centreID=" + centre;
            Log.d(TAG, "Query: " + param);

            try {
                URL url = new URL(WS_URL + COMPLETE_SPINNER_TOWN_VILLAGE + param);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                responseCode = connection.getResponseCode();
                Log.i(TAG, "POST Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    Log.d(TAG, "HTTP Response: " + response.toString());
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("rows");

                    villageTownMap.clear();
                    ArrayList<JSONObject> jsonResults = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonResults.add(jsonArray.getJSONObject(i));
                        villageTownMap.put(jsonResults.get(i).getString("Village_Name"),
                                jsonResults.get(i).getInt("Village_ID"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            switch (area) {
                case VILLAGE:
                    return "Village";
                case TOWN:
                    return "Town";
                default:
                    return null;
            }
        }

        @Override
        protected void onPostExecute(String area) {
            //clearing the previous list
            villageTownList.clear();

            //populate the list of villages/town
            if (villageTownMap.size() != 0) {
                for (Map.Entry vt : villageTownMap.entrySet()) {
                    villageTownList.add(vt.getKey().toString());
                }
            } else {
                villageTownList.add("No " + area + " found");
            }
            villageTown.setAdapter(villageTownListDataAdapter);

            if (responseCode == 0) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public Bundle getData() {

        String[] params = new String[]{
                editTextOwnerName.getText().toString().trim(),
                editTextFatherHusbandName.getText().toString().trim(),
                editTextAddress.getText().toString().trim(),
                editTextMobileNumber.getText().toString().trim(),
                editTextTelephoneNumber.getText().toString().trim()
        };

        //removing spaces with "%20" to send through GET
        for (int i = 0; i < params.length; i++)
            if (params[i].contains(" ")) {
                params[i] = params[i].replace(" ", "%20");
            }

        //Prevent NPE in case of empty spinner
        String villageID;
        if (villageTownMap.get(villageTown.getSelectedItem().toString()) == null) {
            villageID = null;
        } else {
            villageID = String.valueOf(villageTownMap.get(villageTown.getSelectedItem().toString()));
        }

        Bundle bundle = new Bundle();
        bundle.putString("area", area);
        bundle.putString("villageTown", villageID);
        bundle.putString("ownerName", params[0]);
        bundle.putString("fatherHusbandName", params[1]);
        bundle.putString("category", category.getSelectedItem().toString());
        bundle.putString("address", params[2]);
        bundle.putString("mobileNumber", params[3]);
        bundle.putString("telephoneNumber", params[4]);

        return bundle;
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
        String item = parent.getItemAtPosition(position).toString();

        switch (parent.getId()) {
            case R.id.spinnerVillageTown:
                break;

            case R.id.spinnerCategory:
                break;
        }
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
    }
}
