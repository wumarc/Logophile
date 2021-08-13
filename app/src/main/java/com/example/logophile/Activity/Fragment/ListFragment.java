package com.example.logophile.Activity.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.logophile.Class.Word;
import com.example.logophile.R;
import com.example.logophile.Class.WordAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ListFragment extends Fragment {

    RecyclerView wordsListRecycler;
    DatabaseReference wordDbRef = FirebaseDatabase.getInstance().getReference("word");
    WordAdapter wordAdapter;
    ArrayList<Word> wordsList = new ArrayList<Word>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        wordsListRecycler = view.findViewById(R.id.word_recycler_view);

        // Recycler View / Adapter set up
        wordAdapter = new WordAdapter(wordsList, getActivity());
        wordsListRecycler.setHasFixedSize(true);
        wordsListRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        wordsListRecycler.setAdapter(wordAdapter);

        wordDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wordsList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Word word = itemSnapshot.getValue(Word.class);
                    wordsList.add(word);
                }
                wordAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        return view;
    }


}