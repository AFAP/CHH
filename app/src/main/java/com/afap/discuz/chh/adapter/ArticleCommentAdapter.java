package com.afap.discuz.chh.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afap.discuz.chh.R;
import com.afap.discuz.chh.model.ArticleComment;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class ArticleCommentAdapter extends BaseAdapter {

    private int[] avatars = {
            R.drawable.ic_avatar_1, R.drawable.ic_avatar_2, R.drawable.ic_avatar_3, R.drawable.ic_avatar_4,
            R.drawable.ic_avatar_5, R.drawable.ic_avatar_6, R.drawable.ic_avatar_7, R.drawable.ic_avatar_8,
            R.drawable.ic_avatar_9, R.drawable.ic_avatar_10, R.drawable.ic_avatar_11, R.drawable.ic_avatar_12,
            R.drawable.ic_avatar_13, R.drawable.ic_avatar_14, R.drawable.ic_avatar_15, R.drawable.ic_avatar_16,
            R.drawable.ic_avatar_17};


    private List<ArticleComment> mData;
    private LayoutInflater mInflater;

    public ArticleCommentAdapter(List<ArticleComment> list) {
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
        ArticleComment atom = mData.get(position);
        ViewHolder mHolder = null;
        if (convertView == null) {

            mHolder = new ViewHolder();

            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.getContext());
            }
            convertView = mInflater.inflate(R.layout.atom_list_article_comment, null);
            mHolder.atom = convertView.findViewById(R.id.atom);
            mHolder.atom_img = (SimpleDraweeView) convertView.findViewById(R.id.atom_img);
            mHolder.atom_name = (TextView) convertView.findViewById(R.id.atom_name);
            mHolder.atom_time = (TextView) convertView.findViewById(R.id.atom_time);
            mHolder.atom_content = (TextView) convertView.findViewById(R.id.atom_content);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.atom_name.setText(atom.getName());
        mHolder.atom_time.setText(atom.getTime());
        mHolder.atom_content.setText(Html.fromHtml(atom.getContentHtml()));

        mHolder.atom_img.getHierarchy().setPlaceholderImage(avatars[position % avatars.length]);


        return convertView;
    }

    class ViewHolder {
        View atom;
        SimpleDraweeView atom_img;
        TextView atom_name;
        TextView atom_time;
        TextView atom_content;
    }
}
