package com.gnomikx.www.gnomikx;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class BodyMassIndexFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private EditText heightText;
    private EditText weightText;
    private TextView bmiValueText;
    private TextView bmiValue;
    private int heightUnitCheck=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.body_mass_index,container,false);

        heightText = view.findViewById(R.id.bmi_height);
        weightText = view.findViewById(R.id.bmi_weight);
        bmiValueText = view.findViewById(R.id.bmi_text);
        bmiValue = view.findViewById(R.id.bmi_value);
        Button bmiButton = view.findViewById(R.id.bmi_button);
        Spinner heightUnitSpinner = view.findViewById(R.id.height_unit_spinner);
        heightUnitSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> heightAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.height_unit_choices, android.R.layout.simple_spinner_item);
        heightAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        heightUnitSpinner.setAdapter(heightAdapter);

        bmiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bmiValue.setText("");
                String height = heightText.getText().toString().trim();
                String weight = weightText.getText().toString().trim();

                //Play around with this technique for error-checking.
                //this simply displays errors in a better way

                //start of error-checking
                boolean error = false; //index of errors
                double height_value = 0, weight_value = 0;
                if(height.isEmpty()) {
                    heightText.setError(getString(R.string.no_height_error));
                    error = true;
                } else {
                    height_value = Double.parseDouble(height);
                    if(height_value <= 0) {
                        heightText.setError(getString(R.string.height_loo_low_error));
                        error = true;
                    }
                }
                if(weight.isEmpty()) {
                    weightText.setError(getString(R.string.no_weight_error));
                    error = true;
                } else {
                    weight_value = Double.parseDouble(weight);
                    if(weight_value <= 0) {
                        weightText.setError(getString(R.string.weight_too_low_error));
                        error = true;
                    }
                }

                if(!error) {
                    //no errors, proceed with calculation
                    double bmi=0;
                    bmiValueText.setText(getString(R.string.your_bmi));
                    bmiValueText.setVisibility(View.VISIBLE);
                    switch (heightUnitCheck){
                        case 0: //metre
                            height_value *=0.0254;
                            bmi = weight_value/(height_value*height_value);
                            break;
                        case 1: //cm
                            height_value *=0.3048;
                            bmi = weight_value/(height_value*height_value);
                            break;
                        case 2: //inch
                            height_value /=100;
                            bmi = weight_value/(height_value*height_value);
                            break;
                        case 3: //feet
                            bmi = weight_value/(height_value*height_value);
                            break;
                    }
                    bmiValue.setText(String.valueOf(bmi));
                    bmiValue.setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        switch (pos){
            case 0:
                heightUnitCheck = 0;
                heightText.setHint(R.string.bmi_in_height_hint);
                break;
            case 1:
                heightUnitCheck = 1;
                heightText.setHint(R.string.bmi_f_height_hint);
                break;
            case 2:
                heightUnitCheck = 2;
                heightText.setHint(R.string.bmi_cm_height_hint);
                break;
            case 3:
                heightUnitCheck = 3;
                heightText.setHint(R.string.bmi_m_height_hint);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

