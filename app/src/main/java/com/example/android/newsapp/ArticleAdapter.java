package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {
    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, 0, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //ViewHolder class for faster reloading items
        class ViewHolder {
            //items that i will use. Here are declared variables for it.
            private TextView authorText;
            private TextView sectionText;
            private TextView timeText;
        }
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list, parent, false);
            //setting up items in ViewHolder
            holder.authorText = convertView.findViewById(R.id.aTitle);
            holder.sectionText = convertView.findViewById(R.id.aSection);
            holder.timeText = convertView.findViewById(R.id.aTime);
            convertView.setTag(holder);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Article currentArticle = getItem(position);
        String artName = currentArticle.getaName();
        String artDate = currentArticle.getaDate();
        String artSection = currentArticle.getSection();
        String artAuthor = currentArticle.getmAuthor();
        String[] stringsTouse = {getContext().getResources().getString(R.string.published),
                getContext().getResources().getString(R.string.section),
                getContext().getResources().getString(R.string.written)};
        String[] separator = {getContext().getResources().getString(R.string.separator0),
                getContext().getResources().getString(R.string.separator1),
                getContext().getResources().getString(R.string.separator2)};
        holder.authorText.setText(artName);
        if (artDate.contains(separator[0])) {
            String[] parts = artDate.split(separator[0]);
            String dayTime = parts[0];
            String hourTime = parts[1];
            if (hourTime.contains(separator[1])) {
                String[] partsofHour = hourTime.split(separator[1]);
                String hourConverted = partsofHour[0];
                holder.timeText.setText(stringsTouse[0] + dayTime + " " + hourConverted);
            }
        } else {
            holder.timeText.setText(artDate);
        }
        if (artAuthor.contains(separator[2])) {
            String[] locationRemover = artAuthor.split(separator[2]);
            holder.sectionText.setText(stringsTouse[1] + artSection + stringsTouse[2] + locationRemover[0]);
        } else {
            holder.sectionText.setText(stringsTouse[1] + artSection + stringsTouse[2] + artAuthor);
        }
        return convertView;
    }
}
