package com.example.logophile.Activity.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.logophile.Class.OxfordDictionaryRequest;
import com.example.logophile.Class.Word;
import com.example.logophile.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Random;

public class PracticeFragment extends Fragment {

    TextView iKnow, iDont, word, display_definition;
    DatabaseReference wordDbRef = FirebaseDatabase.getInstance().getReference("word");
    ArrayList<String> keys = new ArrayList<String>();
    ArrayList<Word> wordsList = new ArrayList<Word>();
    String currentKey;
    boolean first = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_practice, container, false);

        iDont = view.findViewById(R.id.idont_button);
        iKnow = view.findViewById(R.id.iknow_button);
        word = view.findViewById(R.id.practice_word);
        display_definition = view.findViewById(R.id.word_definition);

        wordDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String keyValue = itemSnapshot.getKey();
                    Word word = itemSnapshot.getValue(Word.class);
                    wordsList.add(word);
                    keys.add(itemSnapshot.getKey());
                }
                if (first) {
                    displayNextWord();
                    first = false;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        iKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // record the previous word to increase confidence level
                Word updatedWord = wordsList.get(keys.indexOf(currentKey));
                updatedWord.increaseConfidenceLevel();
                wordDbRef.child(currentKey).setValue(updatedWord);
                displayNextWord();
            }
        });

        iDont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decrease confidence level then show the definition
                Word updatedWord = wordsList.get(keys.indexOf(currentKey));
                updatedWord.decreaseConfidenceLevel();
                wordDbRef.child(currentKey).setValue(updatedWord);
                displayNextWord();

                // Request API data to show the definition
                new OxfordDictionaryRequest(new OxfordDictionaryRequest.AsyncResponse() {
                    @Override
                    public void processFinished(String output) {
                        display_definition.setText(output);
                    }
                }).execute(dictionaryEntries(wordsList.get(keys.indexOf(currentKey)).getWord()));

            }
        });

        return view;

    }

    private String dictionaryEntries(String lookupWord) {
        final String language = "en-gb";
        final String word = lookupWord;
        final String fields = "definitions";
        final String strictMatch = "false";
        final String word_id = word.toLowerCase();
        final String restUrl = "https://od-api.oxforddictionaries.com:443/api/v2/entries/" + language + "/" + word_id + "?" + "fields=" + fields + "&strictMatch=" + strictMatch;
        return restUrl;
    }

    private void displayNextWord() {
        // display next word
        int randomWordIndex = 0;
        if (wordsList.size() == 1) {
            randomWordIndex = 0;
        } else {
            randomWordIndex = new Random().nextInt(wordsList.size()-1);
        }
        currentKey = keys.get(randomWordIndex);
        word.setText(wordsList.get(randomWordIndex).getWord());
    }

}