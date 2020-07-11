package com.example.maprace;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.arch.core.internal.SafeIterableMap;
import androidx.fragment.app.DialogFragment;

public class LandmarkGoalDialog extends AppCompatDialogFragment {
    private TextView editTextGoalNum;

    public interface LandmarkGoalDialogListener {
        public void onLandmarkGoalDialogPositiveClick(DialogFragment dialog, String goalNum);
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.landmark_goal_dialog, null);
        builder.setView(view)
                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String goalNum = editTextGoalNum.getText().toString();
                            listener.onLandmarkGoalDialogPositiveClick(LandmarkGoalDialog.this, goalNum);
                        }
                    });

        editTextGoalNum = view.findViewById(R.id.goalNum);

        return builder.create();
    }
}
