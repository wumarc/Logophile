package com.example.logophile.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.example.logophile.Class.Word;
import com.example.logophile.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddWordDialog extends AppCompatDialogFragment {

    private static FirebaseDatabase databaseRoot = FirebaseDatabase.getInstance();
//    private static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference wordDbRef = databaseRoot.getReference("word");

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater(); // Get the layout inflater
        View view = inflater.inflate(R.layout.activity_add_word_dialog, null);
        EditText word = view.findViewById(R.id.word);
        EditText description = view.findViewById(R.id.description);

        builder.setView(view)
                .setMessage("Add a new word")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O) // TODO ?
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get word data
                        Word newWord = new Word(word.getText().toString(), description.getText().toString(), 0);
                        wordDbRef.push().setValue(newWord);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

        return builder.create();
    }

}