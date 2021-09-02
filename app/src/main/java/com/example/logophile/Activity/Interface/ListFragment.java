package com.example.logophile.Activity.Interface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.logophile.Class.Word;
import com.example.logophile.R;
import com.example.logophile.Class.WordAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ListFragment extends Fragment {

    static WordDetailFragment wordDetailFragment = new WordDetailFragment();
    RecyclerView wordsListRecycler;
    String uid;
    DatabaseReference wordsDbRef;
    WordAdapter wordAdapter;
    ArrayList<Word> wordsList = new ArrayList<Word>();
    ArrayList<Word> originalList = new ArrayList<Word>();
    ArrayList<String> keysList = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        wordsDbRef = FirebaseDatabase.getInstance().getReference(uid);

        // Recycler View / Adapter set up
        wordAdapter = new WordAdapter(wordsList, getActivity());
        wordsListRecycler = view.findViewById(R.id.word_recycler_view);
        wordsListRecycler.setHasFixedSize(true);
        wordsListRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        wordsListRecycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        wordsListRecycler.setAdapter(wordAdapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(wordsListRecycler);

        wordsDbRef.child("word").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wordsList.clear();
                originalList.clear();
                keysList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Word word = itemSnapshot.getValue(Word.class);
                    wordsList.add(word);
                    keysList.add(itemSnapshot.getKey());
                }
                originalList = new ArrayList<Word>(wordsList);
                Collections.sort(wordsList); //TODO fix error here
                wordAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Listener
        wordAdapter.setOnItemClickListener(new WordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                //get position in sorted array and get the word that was clicked on
                Word WordFromWordsList = wordsList.get(position);
                int positionFinal = originalList.indexOf(WordFromWordsList);

                bundle.putString("wordKey", keysList.get(positionFinal));
                wordDetailFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.fragment_container, wordDetailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            wordsList.remove(viewHolder.getAdapterPosition());
            wordsDbRef.child("word").setValue(wordsList);
            wordAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Word removed", Toast.LENGTH_SHORT).show();
        }

    };

}