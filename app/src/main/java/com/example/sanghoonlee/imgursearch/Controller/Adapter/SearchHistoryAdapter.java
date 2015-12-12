package com.example.sanghoonlee.imgursearch.Controller.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sanghoonlee.imgursearch.R;

import java.util.List;


/**
 * Created by sanghoonlee on 2015-12-11.
 */
public class SearchHistoryAdapter extends ArrayAdapter<String>{

    private List<String> mHistory;
    private Context mContext;
    private ListView mHistoryListView;


    public SearchHistoryAdapter(Context context, List<String> history,ListView listview) {
        super(context, R.layout.item_list_history, history);
        mHistory=history;
        mContext = context;
        mHistoryListView =listview;
        if(mHistory.isEmpty()) {
            mHistoryListView.setVisibility(View.GONE);
        }
    }

    public void refreshHistory(List<String> filteredHistory) {
        mHistory = filteredHistory;
        notifyDataSetChanged();
        mHistoryListView.setVisibility(mHistory.isEmpty()? View.GONE:View.VISIBLE);
    }

    public String getItem(int position) {
        return mHistory.get(position);
    }

    @Override
    public int getCount() {
        return mHistory.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_list_history, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.search_string);
        textView.setText(mHistory.get(position));
        return rowView;
    }
}
