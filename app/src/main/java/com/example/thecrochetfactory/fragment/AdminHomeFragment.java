package com.example.thecrochetfactory.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.FragmentTransaction;
import com.example.thecrochetfactory.R;

public class AdminHomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    public static AdminHomeFragment newInstance(String param1, String param2) {
        AdminHomeFragment fragment = new AdminHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Set up the ImageButtons and their click listeners
        ImageButton imageButton1 = view.findViewById(R.id.imageButton1);
        ImageButton imageButton2 = view.findViewById(R.id.imageButton2);
        ImageButton imageButton3 = view.findViewById(R.id.imageButton3);
        ImageButton imageButton4 = view.findViewById(R.id.imageButton4);
        ImageButton imageButton5 = view.findViewById(R.id.imageButton5);

        // Set OnClickListeners for each ImageButton to navigate to respective fragments
        imageButton1.setOnClickListener(v -> {
            // Navigate to FeedbackFragment
            AllFeedbackFragment feedbackFragment = new AllFeedbackFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragle, feedbackFragment);
            transaction.addToBackStack(null);  // Add this transaction to back stack
            transaction.commit();
        });

        imageButton2.setOnClickListener(v -> {
            // Navigate to UserFragment
            AdminUserReportFragment userFragment = new AdminUserReportFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragle, userFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        imageButton3.setOnClickListener(v -> {
            // Navigate to OrderFragment
            AllOrderFragment orderFragment = new AllOrderFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragle, orderFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        imageButton4.setOnClickListener(v -> {
            // Navigate to TransactionFragment
            AdminAddProductFragment adminAddProductFragment = new AdminAddProductFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragle, adminAddProductFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        imageButton5.setOnClickListener(v -> {
            // Navigate to TransactionFragment
            AllProductFragment allProductFragment = new AllProductFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragle, allProductFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}
