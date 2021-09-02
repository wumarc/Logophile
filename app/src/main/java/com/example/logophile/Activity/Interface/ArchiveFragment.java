package com.example.logophile.Activity.Interface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ArchiveFragment extends Fragment {

//    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//    DatabaseReference wordsDbRef = FirebaseDatabase.getInstance().getReference(uid);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.example.logophile.R.layout.fragment_archive, container, false);
        
        return view;

    }


}
