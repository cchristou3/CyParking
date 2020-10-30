package io.github.cchristou3.CyParking.view.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import io.github.cchristou3.CyParking.R;

/**
 * purpose: Going to get replaced with another Navigation option
 *
 * @author Charalambos Christou
 * @version 1.0 29/10/20
 */
public class SlideshowFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        return root;
        // TODO: to be replaced
    }
}