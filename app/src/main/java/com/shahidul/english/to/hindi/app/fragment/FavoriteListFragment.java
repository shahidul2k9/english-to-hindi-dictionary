package com.shahidul.english.to.hindi.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import com.shahidul.english.to.hindi.app.adapter.FavoriteListAdapter;
import com.shahidul.english.to.hindi.app.dao.DatabaseOpenHelper;
import com.shahidul.english.to.hindi.app.dao.WordDao;
import com.shahidul.english.to.hindi.app.dao.WordDaoImpl;
import com.shahidul.english.to.hindi.app.model.Word;

import java.util.List;

/**
 * @author Shahidul
 * @since 5/31/2015
 */
public class FavoriteListFragment extends ListFragment {
    private WordDao wordDao;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        wordDao = new WordDaoImpl(new DatabaseOpenHelper(activity).getReadWritableDatabase());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<Word> favoriteWordList = wordDao.getFavoriteWordList();
        setListAdapter(new FavoriteListAdapter(getActivity(),favoriteWordList));
    }
}
