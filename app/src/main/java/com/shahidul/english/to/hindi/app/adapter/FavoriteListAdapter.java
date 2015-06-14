package com.shahidul.english.to.hindi.app.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.shahidul.english.to.hindi.app.R;
import com.shahidul.english.to.hindi.app.config.Configuration;
import com.shahidul.english.to.hindi.app.dao.DatabaseOpenHelper;
import com.shahidul.english.to.hindi.app.dao.WordDao;
import com.shahidul.english.to.hindi.app.dao.WordDaoImpl;
import com.shahidul.english.to.hindi.app.model.Word;

import java.util.List;

/**
 * @author Shahidul
 * @since 5/31/2015
 */
public class FavoriteListAdapter extends BaseAdapter {
    private Context context;
    private Typeface hindiTypeFace;
    private LayoutInflater layoutInflater;
    private List<Word> wordList;
    private TypedArray optionIcons;
    private String[] optionNames;
    private WordDao wordDao;

    public FavoriteListAdapter(Context context, List<Word> wordList){
        this.context = context;
        this.wordList = wordList;
        hindiTypeFace = Typeface.createFromAsset(context.getAssets(), Configuration.HINDI_TYPEFACE_PATH);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wordDao = new WordDaoImpl(new DatabaseOpenHelper(context).getReadWritableDatabase());
        optionNames = context.getResources().getStringArray(R.array.option_names);
        optionIcons = context.getResources().obtainTypedArray(R.array.option_icons);
    }

    @Override
    public int getCount() {
        return wordList.size();
    }

    @Override
    public Object getItem(int position) {
        return wordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view;
        FavoriteWordHolder holder;
        final Word word = wordList.get(position);
        if (convertView == null){
            view = layoutInflater.inflate(R.layout.favorite_list_item, null);
            holder = new FavoriteWordHolder();
            holder.wordContainer = (LinearLayout) view.findViewById(R.id.word_container);
            holder.english = (TextView) view.findViewById(R.id.english);
            holder.hindi = (TextView) view.findViewById(R.id.hindi);
            holder.remove = (ImageView) view.findViewById(R.id.remove);
            view.setTag(holder);
        }
        else{
            view = convertView;
            holder = (FavoriteWordHolder) view.getTag();
        }

        holder.english.setText(word.getEnglish());
        holder.hindi.setText(word.getHindi());
        holder.hindi.setTypeface(hindiTypeFace);
        holder.wordContainer.setLongClickable(true);
        holder.wordContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog optionDialog = new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.choose_one_option) + " " +  word.getEnglish())
                        .setAdapter(new OptionAdapter(context, optionNames, optionIcons), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        View updateView = layoutInflater.inflate(R.layout.update_word,null);
                                        final TextView englishView = (TextView) updateView.findViewById(R.id.english);
                                        final TextView banglaView = (TextView) updateView.findViewById(R.id.hindi);
                                        banglaView.setTypeface(hindiTypeFace);
                                        englishView.setText(word.getEnglish());
                                        banglaView.setText(word.getHindi());
                                        AlertDialog updateDialog = new AlertDialog.Builder(context)
                                                .setTitle(R.string.update_word)
                                                .setView(updateView)
                                                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        String englishText = englishView.getText().toString();
                                                        String hindiText = banglaView.getText().toString();
                                                        if (englishText.length() == 0 || hindiText.length() == 0) {
                                                            return;
                                                        }
                                                        word.setEnglish(englishText);
                                                        word.setHindi(hindiText);
                                                        wordDao.updateWord(word);
                                                        notifyDataSetChanged();
                                                    }
                                                })
                                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                                .create();
                                        updateDialog.show();
                                        break;
                                    case 1:
                                        wordDao.deleteWord(word.getId());
                                        wordList.remove(position);
                                        notifyDataSetChanged();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .create();
                optionDialog.show();
                return true;
            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordDao.removeFromFavorite(word.getId());
                wordList.remove(position);
                notifyDataSetChanged();
            }
        });
        return  view;
    }
    public void setWordList(List<Word> wordList){
        this.wordList = wordList;
        notifyDataSetChanged();
    }
    private static class FavoriteWordHolder {
        LinearLayout wordContainer;
        TextView english;
        TextView hindi;
        ImageView remove;
    }
}
