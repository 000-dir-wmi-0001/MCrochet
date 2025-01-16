package com.example.thecrochetfactory;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.thecrochetfactory.fragment.AdminAddProductFragment;
import com.example.thecrochetfactory.fragment.AdminHomeFragment;
import com.example.thecrochetfactory.fragment.AdminUserReportFragment;
import com.example.thecrochetfactory.fragment.AllFeedbackFragment;
import com.example.thecrochetfactory.fragment.AllOrderFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            EdgeToEdge.enable(this);  // Optional: For edge-to-edge display support
        }
        setContentView(R.layout.activity_admin_home);

        BottomNavigationView btv = findViewById(R.id.bottombtn);
        btv.setOnItemSelectedListener(navListener);

        // Default fragment on activity launch
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragle, new AdminHomeFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private final BottomNavigationView.OnItemSelectedListener navListener = new BottomNavigationView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            // Replacing switch with if-else statements
            if (item.getItemId() == R.id.home) {
                selectedFragment = new AdminHomeFragment();
            } else if (item.getItemId() == R.id.addP) {
                selectedFragment = new AdminAddProductFragment();
            } else if (item.getItemId() == R.id.order) {
                 selectedFragment = new AllOrderFragment();  // Uncomment when fragment is ready
            } else if (item.getItemId() == R.id.log) {
                FirebaseAuth.getInstance().signOut(); // Log out user
                startActivity(new Intent(getApplicationContext(), UserLogin.class));
                finish(); // Close current activity
                overridePendingTransition(0, 0); // Optional: Avoid transition animation
                return true;
            } else if (item.getItemId() == R.id.enquiry) {
                selectedFragment = new AllFeedbackFragment();
            } else {
                selectedFragment = new AdminUserReportFragment();  // Fallback fragment if none of the above match
            }

            // Check if the fragment is not null before committing the transaction
            if (selectedFragment != null) {
                // Check if the fragment is not already the same as the one currently shown
                if (getSupportFragmentManager().findFragmentByTag(selectedFragment.getClass().getName()) == null) {
                    // Replace the fragment if it's not already shown
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragle, selectedFragment, selectedFragment.getClass().getName())
                            .commitAllowingStateLoss(); // Gracefully handle state loss
                }
            } else {
                // Handle the case where selectedFragment is null (though this should not happen with the above checks)
                Toast.makeText(AdminHomeActivity.this, "Error: Fragment not selected", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
    };
}

