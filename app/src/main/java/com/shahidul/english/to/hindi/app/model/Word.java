package com.shahidul.english.to.hindi.app.model;

/**
 * @author Shahidul
 * @since 5/27/2015
 */
public class Word {
    private int id;
    private String english;
    private String hindi;
    boolean favorite;

    public Word() {
    }

    public Word(int id, String english, String hindi, boolean favorite) {
        this.id = id;
        this.english = english;
        this.hindi = hindi;
        this.favorite = favorite;
    }

    public Word(String english, String hindi, boolean favorite) {
        this.english = english;
        this.hindi = hindi;
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getHindi() {
        return hindi;
    }

    public void setHindi(String hindi) {
        this.hindi = hindi;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
