package com.example.erunning;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.fragment.app.DialogFragment;

public class SetMaxDialog extends DialogFragment {

    private static final int MAX_YEAR = 2099;
    private static final int MIN_YEAR = 1980;

    private DatePickerDialog.OnDateSetListener listener;

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    Button btnConfirm;
    Button btnCancel;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.number_picker_dialog, null);

        btnConfirm = dialog.findViewById(R.id.btn_confirm);

        final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.maxnumber);


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDateSet(null, 0, numberPicker.getValue(), 0);
                SetMaxDialog.this.getDialog().cancel();
            }
        });

        numberPicker.setMinValue(2);
        numberPicker.setMaxValue(8);
        numberPicker.setWrapSelectorWheel(false);//시작 ~ 끝 범위 고정
        builder.setView(dialog)
        ;

        return builder.create();
    }
}
