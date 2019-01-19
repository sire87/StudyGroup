package at.risingr.studygroup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private boolean eMailVerified;
    public boolean isLeave;
    private Context mContext;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (eMailVerified) {
                        fragment = new HomeFragment();
                        ((Toolbar) findViewById(R.id.toolbar_main)).setTitle(R.string.title_home);
                        ((ImageView) findViewById(R.id.toolbar_menu)).setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.navigation_search:
                    if (eMailVerified) {
                        fragment = new SearchFragment();
                        ((Toolbar) findViewById(R.id.toolbar_main)).setTitle(R.string.title_search);
                        ((ImageView) findViewById(R.id.toolbar_menu)).setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    ((Toolbar) findViewById(R.id.toolbar_main)).setTitle(R.string.title_profile);
                    ((ImageView) findViewById(R.id.toolbar_menu)).setVisibility(View.GONE);
                    break;
            }

            return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        // set bottom navigation listener
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // check if user email is verified
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        eMailVerified = user.isEmailVerified();

        // set options on click listener
        ((ImageView) findViewById(R.id.toolbar_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_sort, popupMenu.getMenu());

                ViewParent viewParent = view.getParent();
                View parentView = (View) viewParent;

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.action_sort_group_event_date:
                                Toast.makeText(mContext, "TODO event date", Toast.LENGTH_SHORT).show();
                                return true;

                            case R.id.action_sort_group_creation_date:
                                Toast.makeText(mContext, "TODO creation date", Toast.LENGTH_SHORT).show();
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        // load default content fragment
        if (eMailVerified) {
            loadFragment(new HomeFragment());
            ((Toolbar) findViewById(R.id.toolbar_main)).setTitle(R.string.title_home);
            ((ImageView) findViewById(R.id.toolbar_menu)).setVisibility(View.VISIBLE);
        } else {
            loadFragment(new ProfileFragment());
            navigation.setSelectedItemId(R.id.navigation_profile);
            ((Toolbar) findViewById(R.id.toolbar_main)).setTitle(R.string.title_profile);
            ((ImageView) findViewById(R.id.toolbar_menu)).setVisibility(View.GONE);
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


