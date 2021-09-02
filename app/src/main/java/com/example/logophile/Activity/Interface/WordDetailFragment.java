package com.example.logophile.Activity.Interface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.logophile.Class.MerriamWebsterDictionaryRequest;
import com.example.logophile.Class.OxfordDictionaryRequest;
import com.example.logophile.Class.Word;
import com.example.logophile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WordDetailFragment extends Fragment {

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference wordsDbRef = FirebaseDatabase.getInstance().getReference(uid);
    LinearLayout editLayout;
    TextView wordType, oxfordDefinition, merriamWebsterDefinition, word, value;
    EditText ownDefinition;
    Button editBtn, deleteBtn, saveBtn;
    SeekBar confidenceLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_word_detail, container, false);

        word = view.findViewById(R.id.practice_word);
        ownDefinition = view.findViewById(R.id.own_definition);
        oxfordDefinition = view.findViewById(R.id.oxford_definition);
        merriamWebsterDefinition = view.findViewById(R.id.merriam_webster_definition);
        confidenceLevel = view.findViewById(R.id.seek_bar);
        editBtn = view.findViewById(R.id.edit_button);
        deleteBtn = view.findViewById(R.id.delete_button);
        saveBtn = view.findViewById(R.id.save_button);
        editLayout = view.findViewById(R.id.edit_layout);
        value =  view.findViewById(R.id.value);
        oxfordDefinition.setMovementMethod(new ScrollingMovementMethod());
        merriamWebsterDefinition.setMovementMethod(new ScrollingMovementMethod());
        ownDefinition.setMovementMethod(new ScrollingMovementMethod());
        String key = getArguments().getString("wordKey");

        wordsDbRef.child("word").child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DataSnapshot> task) {
                 if (task.isSuccessful()) {
                     word.setText(task.getResult().getValue(Word.class).getWord());
                     value.setText(Integer.toString(task.getResult().getValue(Word.class).getKnowledgeLevel()));
                     confidenceLevel.setProgress(task.getResult().getValue(Word.class).getKnowledgeLevel());
                     ownDefinition.setText(task.getResult().getValue(Word.class).getYourOwnDefinition());
                     new OxfordDictionaryRequest(output -> oxfordDefinition.setText(output)).execute(oxfordDictionaryEntries(task.getResult().getValue(Word.class).getWord()));
                     new MerriamWebsterDictionaryRequest(output -> merriamWebsterDefinition.setText(output)).execute(merriamRequestEntries(task.getResult().getValue(Word.class).getWord()));
                 }
             }
        });

        displayEditButton(true);

        editBtn.setOnClickListener(v -> displayEditButton(false));

        deleteBtn.setOnClickListener(v -> {
            wordsDbRef.child("word").child(key).removeValue();
            getFragmentManager().popBackStack();
            Toast.makeText(getActivity(), "Word deleted", Toast.LENGTH_SHORT).show();
        });

        saveBtn.setOnClickListener(v -> {
            wordsDbRef.child("word").child(key).child("yourOwnDefinition").setValue(ownDefinition.getText().toString());
            wordsDbRef.child("word").child(key).child("confidenceLevel").setValue(confidenceLevel.getProgress());
            displayEditButton(true);
        });

        confidenceLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value.setText(Integer.toString(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return view;
    }

    private void displayEditButton(boolean edit) {
        if (edit) {
            editBtn.setVisibility(View.VISIBLE);
            editLayout.setVisibility(View.GONE);
            ownDefinition.setEnabled(false);
            confidenceLevel.setEnabled(false);
        } else {
            editBtn.setVisibility(View.GONE);
            editLayout.setVisibility(View.VISIBLE);
            ownDefinition.setEnabled(true);
            confidenceLevel.setEnabled(true);
        }

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

}
