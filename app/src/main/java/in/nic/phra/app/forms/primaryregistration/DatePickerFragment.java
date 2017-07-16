package in.nic.phra.app.forms.primaryregistration;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Ashvin Sharma <ashvinsharma97@gmail.com> on 05-07-2017.
 * shows date picker fragment
 */

public class DatePickerFragment extends DialogFragment {
    OnDateSetListener onDateSetListener;

    public DatePickerFragment() {
    }

    public void setCallBack(OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }

    private int year, month, day;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
    }

}
