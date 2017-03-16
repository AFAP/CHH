package com.afap.discuz.chh.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.afap.discuz.chh.R;
import com.afap.discuz.chh.activity.MainActivity;
import com.afap.discuz.chh.model.Category;
import com.afap.discuz.chh.model.Library;

import java.util.List;

public class LibraryAdapter extends BaseAdapter {

    private List<Library> mData;
    private LayoutInflater mInflater;


    public LibraryAdapter(List<Library> list) {
        mData = list;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Library library = mData.get(position);

        ViewHolder mHolder;
        if (convertView == null) {
            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.getContext());
            }

            mHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.atom_library, null);
            mHolder.atom_title = (AppCompatTextView) convertView.findViewById(R.id.atom_title);
            mHolder.atom_desc = (AppCompatTextView) convertView.findViewById(R.id.atom_desc);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.atom_title.setText(library.getTitle());

        return convertView;
    }

    class ViewHolder {
        AppCompatTextView atom_title;
        AppCompatTextView atom_desc;
    }
}