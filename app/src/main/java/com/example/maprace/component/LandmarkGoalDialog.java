package com.example.maprace.component;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.example.maprace.R;

public class LandmarkGoalDialog extends AppCompatDialogFragment {
    private NumberPicker goalPicker;

    public interface LandmarkGoalDialogListener {
        void onLandmarkGoalDialogPositiveClick(DialogFragment dialog, int goal);
    }

    LandmarkGoalDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (LandmarkGoalDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement LandmarkGoalDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.landmark_goal_dialog, null);
        goalPicker = view.findViewById(R.id.goalPicker);

        builder.setTitle("Map Race");
        builder.setMessage("Select the number of landmarks you aim to visit:");
        goalPicker.setMinValue(1);
        goalPicker.setMaxValue(10);

        builder.setView(view);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onLandmarkGoalDialogPositiveClick(LandmarkGoalDialog.this, goalPicker.getValue());
            }
        });

        return builder.create();
    }
}
