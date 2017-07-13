package in.nic.phra.app.welcomefragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.id.text1;
import static android.R.id.text2;
import static android.R.layout.simple_list_item_2;
import in.nic.phra.app.R;
import in.nic.phra.app.forms.FirstMilkRecordingForm;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PrimaryRegistrationRecordDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PrimaryRegistrationRecordDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressWarnings("ConstantConditions")
public class PrimaryRegistrationRecordDetails extends Fragment {
    private static final String TAG = "PrimaryRegRecordDetails";
    JSONObject jsonObject = null;

    public PrimaryRegistrationRecordDetails() {
        // Required empty public constructor
    }

    public static PrimaryRegistrationRecordDetails newInstance() {
        PrimaryRegistrationRecordDetails fragment = new PrimaryRegistrationRecordDetails();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        try {
            jsonObject = new JSONObject(bundle.getString("details"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"PointlessBooleanExpression", "UnusedAssignment"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_primary_registration_record_details, container, false);
        // Inflate the layout for this fragment

        ListView details = (ListView) fragmentView.findViewById(R.id.details);
        List<Map<String, String>> list = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), list,
                simple_list_item_2,
                new String[]{"key", "value"},
                new int[]{text1, text2});

        if (jsonObject != null) {
            try {
                for (int i = 0; i < jsonObject.names().length(); i++) {
                    //noinspection RedundantIfStatement
                    if (jsonObject.names().getString(i).equals("PReg_ID") ||
                            jsonObject.names().getString(i).equals("State_ID") ||
                            jsonObject.names().getString(i).equals("District_ID") ||
                            jsonObject.names().getString(i).equals("Block_ID") ||
                            jsonObject.names().getString(i).equals("Centre_ID") ||
                            jsonObject.names().getString(i).equals("Village_ID") ||
                            jsonObject.names().getString(i).equals("Species_ID") ||
                            jsonObject.names().getString(i).equals("Area") ||
                            jsonObject.names().getString(i).equals("Breed_ID") ||
                            jsonObject.names().getString(i).equals("Flag") ||
                            jsonObject.names().getString(i).equals("isEdit")) {
                        continue;
                    }

                    Map<String, String> map = new HashMap<>();

                    if (jsonObject.names().getString(i).equals("Town/Village")) {
                        if (jsonObject.getString("Area").equals("Rural")) {
                            map.put("key", "Village");
                        } else{
                            map.put("key", "Town");
                        }
                    } else {
                        map.put("key", jsonObject.names().getString(i));
                    }

                    map.put("value", jsonObject.getString(jsonObject.names().getString(i)));
                    list.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        details.setAdapter(adapter);

        TextView addFirstMilkRecordingTextView = (TextView) fragmentView.findViewById(R.id.textViewAddFirstMilkRecording);
        int numLactation = 1;
        try {
            //noinspection SpellCheckingInspection
            numLactation = Integer.parseInt(jsonObject.getString("NumberofLactation"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        //grays out the @string/add_first_milk_recording and makes it unClickable
        if (numLactation > 1) {
            addFirstMilkRecordingTextView.setEnabled(false);
            addFirstMilkRecordingTextView.setTextColor(Color.rgb(220, 220, 220));
        }
        addFirstMilkRecordingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Loading First Milk Recording Form...");
                Intent intent = new Intent(getActivity(), FirstMilkRecordingForm.class);
                startActivity(intent);
            }
        });

        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
