package com.example.maprace;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class editProfileDialog extends BottomSheetDialogFragment {
    private BottomSheetListener listener;
    public editProfileDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_bottom_sheet, container, false);
        Button ok = v.findViewById(R.id.ok);
        EditText etName = v.findViewById(R.id.editName);
        ok.setOnClickListener(v1 -> {
            String name = etName.getText().toString();
            if (TextUtils.isEmpty(name)){
                Toast.makeText(getContext(), "Enter Name", Toast.LENGTH_LONG).show();
            } else{
                listener.onFinishEdit(name);
                dismiss();
            }
        });
        return v;
    }

    public interface BottomSheetListener {
        void onFinishEdit(String text);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}
