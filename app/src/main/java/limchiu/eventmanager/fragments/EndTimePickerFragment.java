package limchiu.eventmanager.fragments;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import limchiu.eventmanager.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EndTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView timeEnd = (TextView) getActivity().findViewById(R.id.add_event_time_end);
        if (hourOfDay < 12) {
            timeEnd.setText(String.format("%d : %02d AM", hourOfDay, minute));
        } else if (hourOfDay == 12) {
            timeEnd.setText(String.format("%d : %02d NN", hourOfDay, minute));
        } else {
            hourOfDay -= 12;
            timeEnd.setText(String.format("%d : %02d PM", hourOfDay, minute));
        }
        ((Button)getActivity().findViewById(R.id.btn_time_end)).setText("Edit");
    }
}
