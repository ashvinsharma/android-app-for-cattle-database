package in.nic.phra.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.nic.phra.app.forms.FormPrimaryRegistration;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PrimaryRegistration.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PrimaryRegistration#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrimaryRegistration extends Fragment {
    private static final String TAG = "PrimaryRegistration";
    private OnFragmentInteractionListener mListener;
    private View view;

    public PrimaryRegistration() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PrimaryRegistration.
     */
    public static PrimaryRegistration newInstance() {
        PrimaryRegistration fragment = new PrimaryRegistration();
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
        View inputFragmentView = inflater.inflate(R.layout.fragment_primary_registration, container, false);
        // Inflate the layout for this fragment
        TextView addNewPrimaryRegistration = (TextView) inputFragmentView.findViewById(R.id.textViewNewPrimaryRegistration);
        addNewPrimaryRegistration.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Loading Primary Registration Form... ");
                Intent intent = new Intent(getContext(), FormPrimaryRegistration.class);
                startActivity(intent);
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
