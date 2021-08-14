package com.example.logophile.Activity.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.logophile.Class.MerriamWebsterDictionaryRequest;
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

    LinearLayout definitionLayout;
    TextView iKnow, iDont, finish, word, yourOwnDefinition, oxfordDefinition, merriamWebsterDefinition;
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
        finish = view.findViewById(R.id.finish_button);
        word = view.findViewById(R.id.practice_word);
        definitionLayout = view.findViewById(R.id.definition_layout);
        yourOwnDefinition = view.findViewById(R.id.own_definition);
        oxfordDefinition = view.findViewById(R.id.oxford_definition);
        merriamWebsterDefinition = view.findViewById(R.id.merriam_webster_definition);

        showFinishButton(false);

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

        iKnow.setOnClickListener(v -> {
            // record the previous word to increase confidence level
            Word updatedWord = wordsList.get(keys.indexOf(currentKey));
            updatedWord.increaseConfidenceLevel();
            wordDbRef.child(currentKey).setValue(updatedWord);
            displayNextWord();
        });

        iDont.setOnClickListener(v -> {
                showFinishButton(true);

                // Decrease confidence level then show the definition
                Word updatedWord = wordsList.get(keys.indexOf(currentKey));
                updatedWord.decreaseConfidenceLevel();
                wordDbRef.child(currentKey).setValue(updatedWord);

                // Request API data to show the definition
                yourOwnDefinition.setText(updatedWord.getYourOwnDefinition());
                new OxfordDictionaryRequest(output -> oxfordDefinition.setText(output)).execute(oxfordDictionaryEntries(wordsList.get(keys.indexOf(currentKey)).getWord()));
                new MerriamWebsterDictionaryRequest(output -> merriamWebsterDefinition.setText(output)).execute(merriamRequestEntries(wordsList.get(keys.indexOf(currentKey)).getWord()));
            });

        finish.setOnClickListener(v -> {
            showFinishButton(false);
            displayNextWord();
        });

        return view;
    }

    private String oxfordDictionaryEntries(String lookupWord) {
        final String language = "en-gb";
        final String fields = "definitions";
        final String strictMatch = "false";
        final String word_id = lookupWord.toLowerCase();
        final String restUrl = "https://od-api.oxforddictionaries.com:443/api/v2/entries/" + language + "/" + word_id + "?" + "fields=" + fields + "&strictMatch=" + strictMatch;
        return restUrl;
    }

    private String merriamRequestEntries(String lookupWord) {
        final String key = "26ac3f1b-1789-4906-b982-abe931a6309b";
        final String restUrl = "https://www.dictionaryapi.com/api/v3/references/collegiate/json/" + lookupWord.toLowerCase() + "?key=" + key;
        return restUrl;
    }

    private void displayNextWord() {
        // display next word
        int randomWordIndex;
        if (wordsList.size() == 0) {
        } else {
            if (wordsList.size() == 1) {
                randomWordIndex = 0;
            } else {
                randomWordIndex = new Random().nextInt(wordsList.size()-1);
            }
            currentKey = keys.get(randomWordIndex);
            String nextWord = wordsList.get(randomWordIndex).getWord().substring(0, 1).toUpperCase() + wordsList.get(randomWordIndex).getWord().substring(1);
            word.setText(nextWord);

            // Clear every definition
            oxfordDefinition.setText(null);
            yourOwnDefinition.setText(null);
            merriamWebsterDefinition.setText(null);

        }
    }

    private void showFinishButton(boolean showDefinition) {
        if (showDefinition) {
            finish.setVisibility(View.VISIBLE);
            definitionLayout.setVisibility(View.VISIBLE);
            iKnow.setVisibility(View.GONE);
            iDont.setVisibility(View.GONE);
        } else {
            finish.setVisibility(View.GONE);
            definitionLayout.setVisibility(View.INVISIBLE);
            iKnow.setVisibility(View.VISIBLE);
            iDont.setVisibility(View.VISIBLE);
        }
    }

}