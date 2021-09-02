package com.example.logophile.Class;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logophile.R;

import java.util.ArrayList;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {

    ArrayList<Word> wordsList;
    private Context context;
    OnItemClickListener listener;

    public WordAdapter(ArrayList<Word> wordsList, Context context) {
        this.wordsList = wordsList;
        this.context = context;
    }

    public WordAdapter(ArrayList<Word> wordsList) {
        this.wordsList = wordsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.word_adapter, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull WordAdapter.ViewHolder holder, int position) {
        Word word = wordsList.get(position);
        holder.setIsRecyclable(false);
        holder.word.setText(word.getWord());
        holder.score.setText(String.valueOf(word.getKnowledgeLevel()));
    }

    public int getItemCount() {
        return wordsList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView word, score;

        public ViewHolder(View view, final OnItemClickListener listener) {
            super(view);
            word = view.findViewById(R.id.word_in_adapter);
            score = view.findViewById(R.id.score_level);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

}
