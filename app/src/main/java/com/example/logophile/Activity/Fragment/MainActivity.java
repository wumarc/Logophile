package com.example.logophile.Activity.Fragment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.example.logophile.Activity.AddWordDialog;
import com.example.logophile.Activity.Fragment.DashboardFragment;
import com.example.logophile.Activity.Fragment.ListFragment;
import com.example.logophile.Activity.Fragment.PracticeFragment;
import com.example.logophile.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    FirebaseDatabase databaseRoot = FirebaseDatabase.getInstance();
    DatabaseReference wordDbRef = databaseRoot.getReference("word");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PracticeFragment()).commit(); //start off the app to the practice fragment
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_practice:
                    selectedFragment = new PracticeFragment();
                    break;
                case R.id.navigation_list:
                    selectedFragment = new ListFragment();
                    break;
                case R.id.navigation_dashboard:
                    selectedFragment = new DashboardFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            // show a new dialog and add the word to firebase
            AddWordDialog showDialog = new AddWordDialog();
            showDialog.show(getSupportFragmentManager(), "AddNewWord");
        }
        return true;
    }
}