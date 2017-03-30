package com.afap.discuz.chh.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afap.discuz.chh.R;
import com.afap.discuz.chh.greendao.CategoryListAtom;
import com.afap.discuz.chh.greendao.ForumListAtom;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


public class ForumListAdapter extends BaseAdapter {

    private List<ForumListAtom> mData;
    private LayoutInflater mInflater;

    public ForumListAdapter(List<ForumListAtom> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ForumListAtom atom = mData.get(position);
        ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();

            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.getContext());
            }
            convertView = mInflater.inflate(R.layout.atom_list_forum, null);
            mHolder.atom = convertView.findViewById(R.id.atom);
            mHolder.atom_author_avatar = (SimpleDraweeView) convertView.findViewById(R.id.atom_author_avatar);
            mHolder.atom_author = (TextView) convertView.findViewById(R.id.atom_author);
            mHolder.atom_num_view = (TextView) convertView.findViewById(R.id.atom_num_view);
            mHolder.atom_num_comment = (TextView) convertView.findViewById(R.id.atom_num_comment);
            mHolder.atom_time = (TextView) convertView.findViewById(R.id.atom_time);
            mHolder.atom_title = (TextView) convertView.findViewById(R.id.atom_title);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.atom_author_avatar.setImageURI(atom.getAvatarUrl());
        mHolder.atom_author.setText(atom.getAuthor_name());
        mHolder.atom_num_view.setText(atom.getNum_view());
        mHolder.atom_num_comment.setText(atom.getNum_comment());
        mHolder.atom_time.setText(atom.getTime());
        mHolder.atom_title.setText(atom.getTitle());

        return convertView;
    }

    class ViewHolder {
        View atom;
        SimpleDraweeView atom_author_avatar;
        TextView atom_author;
        TextView atom_num_view;
        TextView atom_num_comment;
        TextView atom_time;
        TextView atom_title;
    }
}
