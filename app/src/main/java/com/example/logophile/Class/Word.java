package com.example.logophile.Class;

public class Word {

    String word;
    String yourOwnDefinition;
    int confidenceLevel = 0; // scale of 1 to 20 which 20 is the word can be archived

    public Word(String word, String yourOwnDefinition, int confidenceLevel) {
        this.word = word;
        this.yourOwnDefinition = yourOwnDefinition;
        this.confidenceLevel = confidenceLevel;
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

    public int getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(int confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public void increaseConfidenceLevel() {
        if (this.confidenceLevel < 20) {
            this.confidenceLevel++;
        }
    }

    public void decreaseConfidenceLevel() {
        if (this.confidenceLevel > 0) {
            this.confidenceLevel--;
        }
    }

}
