package com.shahidul.english.to.hindi.app.dao;


import com.shahidul.english.to.hindi.app.model.Word;

import java.util.List;

/**
 * @author Shahidul
 * @since 5/30/2015
 */
public interface WordDao {
    Word getWordById(int id);
    Word getWordByEnglishWordName(String englishName);
    List<Word> getWordListByPrefixMatching(String prefix);
    List<Word> getFavoriteWordList();
    List<String> getEngilshWordByPrefixMatching(String prefix);
    void makeFavorite(int id);
    void removeFromFavorite(int id);
    void insertWord(Word word);
    void updateWord(Word word);
    void deleteWord(int id);
}
