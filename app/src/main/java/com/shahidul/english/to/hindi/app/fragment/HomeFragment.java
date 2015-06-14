package com.shahidul.english.to.hindi.app.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shahidul.english.to.hindi.app.R;
import com.shahidul.english.to.hindi.app.adapter.WordListAdapter;
import com.shahidul.english.to.hindi.app.dao.DatabaseOpenHelper;
import com.shahidul.english.to.hindi.app.dao.WordDao;
import com.shahidul.english.to.hindi.app.dao.WordDaoImpl;
import com.shahidul.english.to.hindi.app.model.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements View.OnClickListener, TextWatcher,
        AdapterView.OnItemClickListener, TextToSpeech.OnInitListener {

    private static final int REQ_CODE_SPEECH_INPUT = 101;
    private static final String TAG = HomeFragment.class.getSimpleName();
    @InjectView(R.id.auto_complete_word)
	AutoCompleteTextView autoCompleteEnglishWord;
    @InjectView(R.id.clear)
    ImageButton clear;
    @InjectView(R.id.search)
    ImageButton search;
    @InjectView(R.id.speaker)
    ImageButton speaker;
    @InjectView(R.id.input_voice)
    ImageButton inputVoice;
    @InjectView(R.id.no_word_found)
    TextView noWordFound;
    @InjectView(R.id.list)
    ListView wordListView;

    private WordDao wordDao;
    private WordListAdapter wordListAdapter;

    private TextToSpeech textToSpeech;
    public HomeFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,new Exception().getStackTrace()[0].getMethodName());
        wordDao = new WordDaoImpl(new DatabaseOpenHelper(getActivity()).getReadableDatabase());
        textToSpeech = new TextToSpeech(getActivity(),this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG,new Exception().getStackTrace()[0].getMethodName());
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.inject(this, rootView);

        autoCompleteEnglishWord.addTextChangedListener(this);
        autoCompleteEnglishWord.setOnItemClickListener(this);
        clear.setOnClickListener(this);
        search.setOnClickListener(this);
        speaker.setOnClickListener(this);
        inputVoice.setOnClickListener(this);

        wordListAdapter = new WordListAdapter(getActivity());
        wordListView.setAdapter(wordListAdapter);

        return rootView;
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        String englishWord = autoCompleteEnglishWord.getText().toString();
        List<Word> wordList;
        List<String> englishWordList;
        if (englishWord.length() != 0){
            clear.setVisibility(View.VISIBLE);
            wordList = wordDao.getWordListByPrefixMatching(englishWord);
            englishWordList = wordDao.getEngilshWordByPrefixMatching(englishWord.substring(0,1));
        }
        else {
            wordList = new ArrayList<Word>();
            englishWordList = new ArrayList<String>();
        }

        wordListAdapter.setWordList(wordList);
        autoCompleteEnglishWord.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.autocomplete_list_item,englishWordList));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clear:
                noWordFound.setVisibility(View.GONE);
                autoCompleteEnglishWord.setText("");
                autoCompleteEnglishWord.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.autocomplete_list_item,new ArrayList<String>()));
                wordListAdapter.setWordList(new ArrayList<Word>());
                break;
            case R.id.search:
                String englishWordPrefix = autoCompleteEnglishWord.getText().toString();
                List<Word> wordList;
                if (englishWordPrefix.length()==0){
                    wordList = new ArrayList<Word>();
                }
                else{
                    Word exactWord = wordDao.getWordByEnglishWordName(englishWordPrefix);
                    wordList = wordDao.getWordListByPrefixMatching(englishWordPrefix);
                    if (exactWord != null && wordList.size() > 1){
                        wordList.add(0, exactWord);
                    }
                }
                if (wordList.size() == 0){
                    noWordFound.setVisibility(View.VISIBLE);
                }
                else {
                    noWordFound.setVisibility(View.GONE);
                }
                wordListAdapter.setWordList(wordList);
                break;
            case R.id.speaker:
                String englishWord = autoCompleteEnglishWord.getText().toString();
                if (englishWord.length() == 0){
                    return;
                }
                textToSpeech.speak(englishWord,TextToSpeech.QUEUE_FLUSH,null);
                break;
            case R.id.input_voice:
                showTextInputView();
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length()== 0){
            clear.setVisibility(View.GONE);
            autoCompleteEnglishWord.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.autocomplete_list_item,new ArrayList<String>()));
        }
        else if (s.length() == 1){
            clear.setVisibility(View.VISIBLE);
            List<String> englishWordList = wordDao.getEngilshWordByPrefixMatching(s.toString());
            autoCompleteEnglishWord.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.autocomplete_list_item,englishWordList));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        searchSingleWord((String) autoCompleteEnglishWord.getAdapter().getItem(position));
    }

    void searchSingleWord(String englishWord){
        if (englishWord.length() == 0){
            return;
        }
        Word word = wordDao.getWordByEnglishWordName(englishWord);
        List<Word> wordList = new ArrayList<Word>();
        if (word != null){
            noWordFound.setVisibility(View.GONE);
            wordList.add(word);
        }
        else {
            noWordFound.setVisibility(View.VISIBLE);
        }
        wordListAdapter.setWordList(wordList);
        List<String> englishWordList = wordDao.getEngilshWordByPrefixMatching(englishWord.substring(0,1));
        autoCompleteEnglishWord.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.autocomplete_list_item,englishWordList));
    }
    void showTextInputView(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //TODO may need to change locale
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.say_english_word));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            showToast(getString(R.string.speech_not_supported));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQ_CODE_SPEECH_INPUT && data != null){
            List<String> resultList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (resultList != null && resultList.size() > 0){
                String englishWord = resultList.get(0);
                autoCompleteEnglishWord.setText(englishWord);
                searchSingleWord(englishWord);
            }
        }
    }

    void showToast(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInit(int status) {
        if ( status == TextToSpeech.SUCCESS){
            textToSpeech.setLanguage(Locale.US);
        }
    }


    @Override
    public void onDestroy() {
        Log.d(TAG,new Exception().getStackTrace()[0].getMethodName());
        textToSpeech.stop();
        textToSpeech.shutdown();
        ButterKnife.reset(this);
        super.onDestroy();
    }

}