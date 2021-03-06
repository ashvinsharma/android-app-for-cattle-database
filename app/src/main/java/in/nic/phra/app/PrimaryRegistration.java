package in.nic.phra.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.nic.phra.app.forms.FormPrimaryRegistration;

import static android.R.id.text1;
import static android.R.id.text2;
import static android.R.layout.simple_list_item_2;
import static android.content.Context.MODE_PRIVATE;
import static in.nic.phra.app.data.WebServiceDetails.GET_PRIMARY_REGISTRATION_LIST;
import static in.nic.phra.app.data.WebServiceDetails.WS_URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PrimaryRegistration.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PrimaryRegistration extends Fragment {
    private static final String TAG = "PrimaryRegistration";
    private ArrayList<JSONObject> list;
    private List<Map<String, String>> arrayListView = new ArrayList<>();
    private ListView listView;
    private SimpleAdapter adapter;

    public PrimaryRegistration() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inputFragmentView = inflater.inflate(R.layout.fragment_primary_registration, container, false);
        // Inflate the layout for this fragment

        listView = (ListView) inputFragmentView.findViewById(R.id.list);

        adapter = new SimpleAdapter(getActivity(), arrayListView,
                simple_list_item_2,
                new String[] {"OwnerName", "EarTagNo"},
                new int[]{text1, text2});

        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("userSession", MODE_PRIVATE);
        new LoadPrimaryRegistrationList().execute(sharedPreferences.getString("username", null));

        TextView addNewPrimaryRegistration = (TextView) inputFragmentView.findViewById(R.id.textViewNewPrimaryRegistration);
        addNewPrimaryRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Loading Primary Registration Form... ");
                Intent intent = new Intent(getContext(), FormPrimaryRegistration.class);
                startActivity(intent);
            }
        });

        return inputFragmentView;
    }

    private class LoadPrimaryRegistrationList extends AsyncTask<String, Void, ArrayList<JSONObject>> {

        @Override
        protected void onPreExecute() {
            Map<String, String> map = new HashMap<>();
            map.put("OwnerName", "Loading List...");
            arrayListView.add(map);
            listView.setAdapter(adapter);
        }


        @Override
        protected ArrayList<JSONObject> doInBackground(String... params) {
            String param = "?username=" + params[0];
            Log.d(TAG, param);

            try {
                URL url = new URL(WS_URL + GET_PRIMARY_REGISTRATION_LIST + param);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                int responseCode = connection.getResponseCode();
                Log.d(TAG, connection.getRequestMethod() + " response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    Log.d(TAG, "Response:\n" + response.toString());

                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("rows");

                    list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        list.add(jsonArray.getJSONObject(i));
                    }

                    Log.i(TAG, list.get(0).getString("EarTagNo"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<JSONObject> list) {
            //clearing the previous list
            arrayListView.clear();

            for (int i = 0; i < list.size(); i++) {
                try {
                    Map<String, String> map = new HashMap<>();
                    map.put("OwnerName", list.get(i).getString("OwnerName"));
                    map.put("EarTagNo", list.get(i).getString("EarTagNo"));
                    arrayListView.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            listView.setAdapter(adapter);
        }
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
    interface OnFragmentInteractionListener {
    }
}
