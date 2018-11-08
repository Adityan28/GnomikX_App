package com.gnomikx.www.gnomikx;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gnomikx.www.gnomikx.ActivityWidgetHandler.MakeVisible;

import java.util.Random;

public class PreLoader extends Fragment {

    private String LOG_TAG = "preLoader";
    private static String[] quotes = new String[]{"Never accept limitations.","Health is the greatest possession.",
            "A healthy outside starts from the inside."};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pre_loader, container, false);

        TextView quoteTextview = rootView.findViewById(R.id.quote_textView);

        Random random = new Random();

        switch (random.nextInt(3)){
            case 0:
                quoteTextview.setText(quotes[0]);
                break;
            case 1:
                quoteTextview.setText(quotes[1]);
                break;
            case 2:
                quoteTextview.setText(quotes[2]);
        }

        Thread welcomeThread = new Thread(){
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(  3000);
                } catch (Exception e) {
                    Log.v(LOG_TAG ,"Error!");
                } finally {
                    MainActivity activity = (MainActivity) getActivity();
                    assert activity != null;
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(PreLoader.this);
                    fragmentTransaction.replace(R.id.fragment_container, new BlogFragment(), MainActivity.blogTag);
                    fragmentTransaction = fragmentTransaction.disallowAddToBackStack();
                    fragmentTransaction.commit();
                }
            }
        };

        welcomeThread.start();
        return rootView;
    }

    @Override
    public void onDetach() {
        MakeVisible makeVisible = (MakeVisible) getActivity();
        if (makeVisible != null) {
            makeVisible.makeVisible();
        }
        super.onDetach();
    }
}
