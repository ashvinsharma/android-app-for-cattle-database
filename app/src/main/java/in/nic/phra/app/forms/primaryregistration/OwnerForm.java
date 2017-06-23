package in.nic.phra.app.forms.primaryregistration;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Gravity;
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

import java.util.ArrayList;
import java.util.List;

import in.nic.phra.app.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OwnerForm.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OwnerForm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OwnerForm extends Fragment implements AdapterView.OnItemSelectedListener{
    private static final String TAG = "OwnerForm";

    private OnFragmentInteractionListener mListener;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inputFragmentView = inflater.inflate(R.layout.fragment_owner_form, container, false);

        RadioGroup area = (RadioGroup) inputFragmentView.findViewById(R.id.areaRadioGroup);
        area.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radioButtonUrban) {
                    Log.i(TAG, "Area: Urban selected");
                    Toast.makeText(getContext(), "Urban", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.radioButtonRural) {
                    Log.i(TAG, "Area: Rural selected");
                    Toast.makeText(getContext(), "Rural", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Spinner villageTown = (Spinner) inputFragmentView.findViewById(R.id.spinnerVillageTown);
        villageTown.setOnItemSelectedListener(this);
        List<String> villageTownList = new ArrayList<>();
        villageTownList.add("Select Village/Town");
        villageTownList.add("anything yada yada ");
        ArrayAdapter<String> villageTownListDataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, villageTownList);
        villageTownListDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        villageTown.setAdapter(villageTownListDataAdapter);

        EditText editTextOwnerName = (EditText) inputFragmentView.findViewById(R.id.editTextOwnerName);
        String ownerName = editTextOwnerName.toString();

        EditText editTextFatherHusbandName = (EditText) inputFragmentView.findViewById(R.id.editTextFatherHusbandName);
        String fatherHusbandName = editTextFatherHusbandName.toString();

        Spinner category = (Spinner) inputFragmentView.findViewById(R.id.spinnerCategory);
        category.setOnItemSelectedListener(this);
        List<String> categoryList = new ArrayList<>();
        categoryList.add("General");
        categoryList.add("SC");
        categoryList.add("OBC");
        categoryList.add("Other");
        ArrayAdapter<String> categoryListDataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categoryList);
        categoryListDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryListDataAdapter);

        EditText editTextAddress = (EditText) inputFragmentView.findViewById(R.id.Address);
        String Address = editTextAddress.toString();

        EditText editTextMobileNumber = (EditText) inputFragmentView.findViewById(R.id.editTextMobileNumber);
        String mobileNumber = editTextMobileNumber.toString();

        EditText editTextTelephoneNumber = (EditText) inputFragmentView.findViewById(R.id.editTextTelephoneNumber);
        String telephoneNumber = editTextTelephoneNumber.toString();

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

        switch(parent.getId()){
            case R.id.spinnerVillageTown:
                Toast.makeText(parent.getContext(),"Selected Village/Town: " + item, Toast.LENGTH_SHORT).show();
                break;

            case R.id.spinnerCategory:
                Toast toast = Toast.makeText(parent.getContext(),"Selected Category: " + item, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 350);
                toast.show();
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
