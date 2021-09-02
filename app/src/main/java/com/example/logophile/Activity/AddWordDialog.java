package com.example.logophile.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.example.logophile.Class.Word;
import com.example.logophile.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddWordDialog extends AppCompatDialogFragment {

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    static FirebaseDatabase databaseRoot = FirebaseDatabase.getInstance();
    DatabaseReference userDbRef = databaseRoot.getReference(uid);
    EditText word, description;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater(); // Get the layout inflater
        View view = inflater.inflate(R.layout.activity_add_word_dialog, null);
        word = view.findViewById(R.id.word);
        description = view.findViewById(R.id.description);

        builder.setView(view)
                .setMessage("Add a new word")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O) //TODO ?
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Word to add
                        String wordToAdd = word.getText().toString().substring(0, 1).toUpperCase() + word.getText().toString().substring(1);
                        Word newWord = new Word(wordToAdd, description.getText().toString(), 0);

                        //If first time adding a word create a uid node
                        userDbRef.addListenerForSingleValueEvent(new ValueEventListener() { //TODO improve runtime by removing loop?
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot itemSnapshot : snapshot.child("word").getChildren()) {
                                    Word word = itemSnapshot.getValue(Word.class);
                                    if (word.getWord().toUpperCase().equals(wordToAdd.toUpperCase())) {
                                        String message = "the word " + "\"" + wordToAdd + "\"" + " already exists in your dictionary";
                                        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                }
                                userDbRef.child("word").push().setValue(newWord); //if there is no duplicate, add the word
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

        return builder.create();
    }

}