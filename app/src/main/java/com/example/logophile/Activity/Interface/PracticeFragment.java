package com.example.logophile.Activity.Interface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Random;

public class PracticeFragment extends Fragment {

    LinearLayout definitionLayout;
    TextView iKnow, iDont, finish, word, yourOwnDefinition, wordType, oxfordDefinition, merriamWebsterDefinition;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference wordDbRef = FirebaseDatabase.getInstance().getReference(uid);
    ArrayList<String> keys = new ArrayList<String>();
    ArrayList<Word> wordsList = new ArrayList<Word>();
    ArrayList<Integer> fourty = new ArrayList<>();
    ArrayList<Integer> twenty = new ArrayList<>();
    ArrayList<Integer> fifteen = new ArrayList<>();
    ArrayList<Integer> ten = new ArrayList<>();
    ArrayList<Integer> five = new ArrayList<>();
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
//        wordType = view.findViewById(R.id.word_type);
        definitionLayout = view.findViewById(R.id.definition_layout);
        yourOwnDefinition = view.findViewById(R.id.own_definition);
        oxfordDefinition = view.findViewById(R.id.oxford_definition);
        merriamWebsterDefinition = view.findViewById(R.id.merriam_webster_definition);
        oxfordDefinition.setMovementMethod(new ScrollingMovementMethod());
        merriamWebsterDefinition.setMovementMethod(new ScrollingMovementMethod());
        yourOwnDefinition.setMovementMethod(new ScrollingMovementMethod());
        showFinishButton(false);

        wordDbRef.child("word").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Word word = itemSnapshot.getValue(Word.class);
                    wordsList.add(word);
                    keys.add(itemSnapshot.getKey());
                    sortWord(word.getKnowledgeLevel(), wordsList.indexOf(word));
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
            Word updatedWord = wordsList.get(keys.indexOf(currentKey));
            updatedWord.increaseConfidenceLevel();
            wordDbRef.child("word").child(currentKey).setValue(updatedWord);
            displayNextWord();
        });

        iDont.setOnClickListener(v -> {
            showFinishButton(true);
            // Decrease confidence level then show the definition
            Word updatedWord = wordsList.get(keys.indexOf(currentKey));
            updatedWord.decreaseConfidenceLevel();
            wordDbRef.child("word").child(currentKey).setValue(updatedWord);

            // Request API data to show the definition TODO get the word type
            new OxfordDictionaryRequest(output -> oxfordDefinition.setText(output)).execute(oxfordDictionaryEntries(wordsList.get(keys.indexOf(currentKey)).getWord()));
            new MerriamWebsterDictionaryRequest(output -> merriamWebsterDefinition.setText(output)).execute(merriamRequestEntries(wordsList.get(keys.indexOf(currentKey)).getWord()));
            yourOwnDefinition.setText(updatedWord.getYourOwnDefinition());
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
            } else { //determine which subarray to pick the word from
                randomWordIndex = new Random().nextInt(100);
                ArrayList selected;
                if (randomWordIndex >= 0 && randomWordIndex <= 39) { //40%
                    selected = fourty;
                } else if (randomWordIndex >= 40 && randomWordIndex <= 64) { //25%
                    selected = twenty;
                } else if (randomWordIndex >= 65 && randomWordIndex <= 85) { //20%
                    selected = fifteen;
                } else if (randomWordIndex >= 86 && randomWordIndex <= 95) { //10%
                    selected = ten;
                } else { //5%
                    selected = five;
                }
                //pick a word from the subarray which will return an index from wordsList
                if (selected.size() - 1 < 0) { //TODO fix temporary solution
                    randomWordIndex = new Random().nextInt(fourty.size()-1);
                } else {
                    randomWordIndex = new Random().nextInt(selected.size()-1);
                }

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

    private void sortWord(int score, int wordIndex) {
        if (0 <= score && score <= 4) {
            fourty.add(wordIndex);
        } else if (5 <= score && score <= 8) {
            twenty.add(wordIndex);
        } else if (9 <= score && score <= 12) {
            fifteen.add(wordIndex);
        } else if (13 <= score && score <= 17) {
            ten.add(wordIndex);
        } else {
            five.add(wordIndex);
        }
    }

}