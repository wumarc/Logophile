package com.example.logophile.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.logophile.Activity.Interface.ArchiveFragment;
import com.example.logophile.Activity.Interface.ListFragment;
import com.example.logophile.Activity.Interface.PracticeFragment;
import com.example.logophile.Activity.Identification.LoginActivity;
import com.example.logophile.Activity.Interface.WordDetailFragment;
import com.example.logophile.Class.Word;
import com.example.logophile.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference wordsDbRef = FirebaseDatabase.getInstance().getReference();
    static PracticeFragment practiceFragment = new PracticeFragment();
    static ListFragment listFragment = new ListFragment();
    static ArchiveFragment archiveFragment = new ArchiveFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, practiceFragment)
                .add(R.id.fragment_container, listFragment)
                .add(R.id.fragment_container, archiveFragment)
                .hide(archiveFragment)
                .hide(listFragment)
                .commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragmentToHide = null;
            Fragment fragmentToHide2 = null;
            Fragment fragmentToShow = null;
            switch (item.getItemId()) {
                case R.id.navigation_practice:
                    fragmentToHide = listFragment;
                    fragmentToHide2 = archiveFragment;
                    fragmentToShow = practiceFragment;
                    break;
                case R.id.navigation_list:
                    fragmentToHide = practiceFragment;
                    fragmentToHide2 = archiveFragment;
                    fragmentToShow = listFragment;
                    break;
                case R.id.navigation_archive:
                    fragmentToHide = practiceFragment;
                    fragmentToHide2 = listFragment;
                    fragmentToShow = archiveFragment;
            }
            getSupportFragmentManager().beginTransaction()
                    .show(fragmentToShow)
                    .hide(fragmentToHide)
                    .hide(fragmentToHide2)
                    .commit();
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
            AddWordDialog showDialog = new AddWordDialog();
            showDialog.show(getSupportFragmentManager(), "AddNewWord");
        } else if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (item.getItemId() == R.id.delete) {
            AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        wordsDbRef.child(uid).removeValue();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Account deleted", Toast.LENGTH_SHORT).show();
                    }
                });
        }
        return true;
    }

}