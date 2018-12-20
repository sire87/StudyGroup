package at.risingr.studygroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private boolean eMailVerified;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (eMailVerified) {
                        fragment = new HomeFragment();
                    }
                    break;
                case R.id.navigation_search:
                    if (eMailVerified) {
                        fragment = new SearchFragment();
                    }
                    break;
                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    break;
            }

            return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set bottom navigation listener
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // check if user email is verified
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        eMailVerified = user.isEmailVerified();

        // load default content fragment
        if (eMailVerified) {
            loadFragment(new HomeFragment());
        } else {
            loadFragment(new ProfileFragment());
            navigation.setSelectedItemId(R.id.navigation_profile);
        }

        // set on clock listener for floating action button to create new study groups
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eMailVerified) {
                    Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Please verify your Email first.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean loadFragment(Fragment fragment) {

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }

        return false;
    }

}


