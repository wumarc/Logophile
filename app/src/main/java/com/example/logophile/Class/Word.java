package com.example.logophile.Class;

public class Word implements Comparable {

    private String word;
    private String yourOwnDefinition;
    private int knowledgeLevel = 0;

    public Word(String word, String yourOwnDefinition, int confidenceLevel) {
        this.word = word;
        this.yourOwnDefinition = yourOwnDefinition;
        this.knowledgeLevel = confidenceLevel;
    }

    public Word() {}

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getYourOwnDefinition() {
        return yourOwnDefinition;
    }

    public void setYourOwnDefinition(String yourOwnDefinition) {
        this.yourOwnDefinition = yourOwnDefinition;
    }

    public int getKnowledgeLevel() {
        return knowledgeLevel;
    }

    public void setKnowledgeLevel(int knowledgeLevel) {
        this.knowledgeLevel = knowledgeLevel;
    }

    public void increaseConfidenceLevel() {
        if (this.knowledgeLevel < 20) {
            this.knowledgeLevel++;
        }
    }

    public void decreaseConfidenceLevel() {
        if (this.knowledgeLevel > 0) {
            this.knowledgeLevel--;
        }
    }

    @Override
    public int compareTo(Object o) {
        return word.compareTo(((Word)o).getWord());
    }

}
