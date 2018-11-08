package com.gnomikx.www.gnomikx;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.gnomikx.www.gnomikx.Adapters.GeneticTestAdapter;
import com.gnomikx.www.gnomikx.Data.GeneticTest;

import java.util.ArrayList;

/**
 * Fragment class for Genetic Tests
 */

public class FragmentGeneticTests extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_genetic_tests, container, false);

        ListView testsListView = rootview.findViewById(R.id.genetic_test_listview);
        ArrayList<GeneticTest> geneticTests = new ArrayList<>();
        geneticTests.add(0,new GeneticTest(R.drawable.diabetes_image,getString(R.string.genetic_test_diabetes_register_button_text)));
        geneticTests.add(1,new GeneticTest(R.drawable.hypertension_image,getString(R.string.genetic_test_hypertension_register_button_text)));
        geneticTests.add(2,new GeneticTest(R.drawable.obesity_image,getString(R.string.genetic_test_obesity_register_button_text)));

        GeneticTestAdapter geneticTestAdapter = new GeneticTestAdapter(getContext(), geneticTests,
                (ProgressBar) rootview.findViewById(R.id.genetic_tests_progress_bar));
        testsListView.setAdapter(geneticTestAdapter);

        return rootview;
    }
}
