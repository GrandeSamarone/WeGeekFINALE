package com.marlostrinidad.wegeek.nerdzone.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by fulanoeciclano on 21/07/2018.
 */

public class DatePickFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Calendar calendar = Calendar.getInstance();
       int ano= calendar.get(Calendar.YEAR);
       int mes = calendar.get(Calendar.MONTH);
       int dia = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),(DatePickerDialog.OnDateSetListener)getActivity(),ano,mes,dia);
    }
}
