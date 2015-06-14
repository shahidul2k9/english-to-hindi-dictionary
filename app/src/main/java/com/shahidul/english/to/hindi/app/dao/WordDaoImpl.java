package com.shahidul.english.to.hindi.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.shahidul.english.to.hindi.app.config.Configuration;
import com.shahidul.english.to.hindi.app.model.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shahidul
 * @since 5/30/2015
 */
public class WordDaoImpl implements WordDao {
    private SQLiteDatabase sqLiteDatabase;
    public WordDaoImpl(SQLiteDatabase sqLiteDatabase){
        this.sqLiteDatabase = sqLiteDatabase;
    }
    @Override
    public Word getWordById(int id) {
        Cursor cursor = sqLiteDatabase.query(Database.TABLE_NAME,new String[]{Database.COLUMN_ID, Database.COLUMN_ENGLISH, Database.COLUMN_HINDI, Database.COLUMN_FAVORITE},
                Database.COLUMN_ID + " = ?",new String[]{String.valueOf(id)},null,null,null,null);
        int idCoulumnIndex = cursor.getColumnIndex(Database.COLUMN_ID);
        int englishColumnIndex = cursor.getColumnIndex(Database.COLUMN_ENGLISH);
        int hindiColumnInex = cursor.getColumnIndex(Database.COLUMN_HINDI);
        int favoriteColumnIndex = cursor.getColumnIndex(Database.COLUMN_FAVORITE);
        Word word = null;
        if (cursor.moveToNext()){
            word = new Word(cursor.getInt(idCoulumnIndex),cursor.getString(englishColumnIndex), cursor.getString(hindiColumnInex), cursor.getInt(favoriteColumnIndex)==1);
        }
        return word;
    }

    @Override
    public Word getWordByEnglishWordName(String englishWordName) {
        Cursor cursor = sqLiteDatabase.query(Database.TABLE_NAME,new String[]{Database.COLUMN_ID, Database.COLUMN_ENGLISH, Database.COLUMN_HINDI, Database.COLUMN_FAVORITE},
                Database.COLUMN_ENGLISH + " = ?", new String[]{englishWordName},null,null,null);
        int idColumnIndex = cursor.getColumnIndex(Database.COLUMN_ID);
        int englishColumnIndex = cursor.getColumnIndex(Database.COLUMN_ENGLISH);
        int hindiColumnInex = cursor.getColumnIndex(Database.COLUMN_HINDI);
        int favoriteColumnIndex = cursor.getColumnIndex(Database.COLUMN_FAVORITE);
        Word word = null;
        if (cursor.moveToNext()){
            word = new Word(cursor.getInt(idColumnIndex), cursor.getString(englishColumnIndex), cursor.getString(hindiColumnInex), cursor.getInt(favoriteColumnIndex)==1);
        }
        return word;
    }

    @Override
    public List<Word> getWordListByPrefixMatching(String prefix) {
        Cursor cursor = sqLiteDatabase.query(Database.TABLE_NAME,new String[]{Database.COLUMN_ID, Database.COLUMN_ENGLISH, Database.COLUMN_HINDI, Database.COLUMN_FAVORITE},
        Database.COLUMN_ENGLISH + " LIKE ?",new String[]{prefix + "%"},null,null,Database.COLUMN_ENGLISH);
        List<Word> wordList = new ArrayList<Word>();
        int idColumnIndex = cursor.getColumnIndex(Database.COLUMN_ID);
        int englishColumnIndex = cursor.getColumnIndex(Database.COLUMN_ENGLISH);
        int hindiColumnInex = cursor.getColumnIndex(Database.COLUMN_HINDI);
        int favoriteColumnIndex = cursor.getColumnIndex(Database.COLUMN_FAVORITE);
        while (cursor.moveToNext()){
            Word word = new Word(cursor.getInt(idColumnIndex),cursor.getString(englishColumnIndex), cursor.getString(hindiColumnInex), cursor.getInt(favoriteColumnIndex)==1);
            wordList.add(word);
        }
        return wordList;
    }

    @Override
    public List<Word> getFavoriteWordList() {
        Cursor cursor = sqLiteDatabase.query(Database.TABLE_NAME,new String[]{Database.COLUMN_ID, Database.COLUMN_ENGLISH, Database.COLUMN_HINDI},
                Database.COLUMN_FAVORITE + " = ?",new String[]{String.valueOf(1)},null,null,Database.COLUMN_ENGLISH, String.valueOf(Configuration.MAX_DISPLAYABLE_FAVORITE_WORDS));
        List<Word> favoriteWordList = new ArrayList<Word>();
        int idColumnIndex = cursor.getColumnIndex(Database.COLUMN_ID);
        int englishColumnIndex = cursor.getColumnIndex(Database.COLUMN_ENGLISH);
        int hindiColumnInex = cursor.getColumnIndex(Database.COLUMN_HINDI);
        while (cursor.moveToNext()){
            Word word = new Word(cursor.getInt(idColumnIndex),cursor.getString(englishColumnIndex), cursor.getString(hindiColumnInex),true);
            favoriteWordList.add(word);
        }
        return favoriteWordList;
    }

    @Override
    public List<String> getEngilshWordByPrefixMatching(String prefix) {
        Cursor cursor = sqLiteDatabase.query(Database.TABLE_NAME,new String[]{Database.COLUMN_ENGLISH},
                Database.COLUMN_ENGLISH + " LIKE ?",new String[]{prefix + "%"},null,null,Database.COLUMN_ENGLISH);
        List<String> englishWordList = new ArrayList<String>();
        int englishColumnIndex = cursor.getColumnIndex(Database.COLUMN_ENGLISH);
        while (cursor.moveToNext()){
            englishWordList.add(cursor.getString(englishColumnIndex));
        }
        return englishWordList;
    }

    @Override
    public void makeFavorite(int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.COLUMN_FAVORITE,1);
        sqLiteDatabase.update(Database.TABLE_NAME,contentValues,Database.COLUMN_ID + " = ?" ,new String[]{String.valueOf(id)});
    }

    @Override
    public void removeFromFavorite(int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.COLUMN_FAVORITE,0);
        sqLiteDatabase.update(Database.TABLE_NAME,contentValues,Database.COLUMN_ID + " = ?" ,new String[]{String.valueOf(id)});
    }

    @Override
    public void insertWord(Word word) {
        int favorite;
        if (word.isFavorite()){
            favorite = 1;
        }
        else {
            favorite = 0;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.COLUMN_ENGLISH, word.getEnglish());
        contentValues.put(Database.COLUMN_HINDI, word.getHindi());
        contentValues.put(Database.COLUMN_FAVORITE, favorite);
        //TODO nullabl column hack
        sqLiteDatabase.insert(Database.TABLE_NAME,null,contentValues);
    }

    @Override
    public void updateWord(Word word) {
        int favorite;
        if (word.isFavorite()){
            favorite = 1;
        }
        else {
            favorite = 0;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.COLUMN_ENGLISH, word.getEnglish());
        contentValues.put(Database.COLUMN_HINDI, word.getHindi());
        contentValues.put(Database.COLUMN_FAVORITE, favorite);
        sqLiteDatabase.update(Database.TABLE_NAME,contentValues,Database.COLUMN_ID + " = ? ", new String[]{String.valueOf(word.getId())});
    }

    @Override
    public void deleteWord(int id) {
        sqLiteDatabase.delete(Database.TABLE_NAME,Database.COLUMN_ID + " = ? ", new String[]{String.valueOf(id)});
    }
}
