package test.qun.com.weishi.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import test.qun.com.weishi.R;

public class FoundGuide2Fragment extends Fragment {


    public FoundGuide2Fragment() {
    }

    public static FoundGuide2Fragment newInstance() {
        FoundGuide2Fragment fragment = new FoundGuide2Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_found_guide1, container, false);
    }
}
