package com.afap.discuz.chh.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afap.discuz.chh.R;
import com.afap.discuz.chh.greendao.CategoryListAtom;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


public class CategoryListAdapter extends BaseAdapter {

    private List<CategoryListAtom> mData;
    private LayoutInflater mInflater;

    public CategoryListAdapter(List<CategoryListAtom> list) {
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
        CategoryListAtom atom = mData.get(position);
        ViewHolder mHolder;
        if (convertView == null) {

            mHolder = new ViewHolder();

            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.getContext());
            }
            convertView = mInflater.inflate(R.layout.atom_list_category, null);
            mHolder.atom = convertView.findViewById(R.id.atom);
            mHolder.atom_img = (SimpleDraweeView) convertView.findViewById(R.id.atom_img);
            mHolder.atom_name = (TextView) convertView.findViewById(R.id.atom_name);
            mHolder.atom_desc = (TextView) convertView.findViewById(R.id.atom_desc);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.atom_name.setText(atom.getTitle());
        mHolder.atom_desc.setText(atom.getContent());
        mHolder.atom_img.setImageURI(atom.getThumb_url());
        return convertView;
    }

    class ViewHolder {
        View atom;
        SimpleDraweeView atom_img;
        TextView atom_name;
        TextView atom_desc;
    }
}
