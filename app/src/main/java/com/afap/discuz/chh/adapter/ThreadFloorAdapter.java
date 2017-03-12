package com.afap.discuz.chh.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afap.discuz.chh.Constant;
import com.afap.discuz.chh.R;
import com.afap.discuz.chh.model.ThreadFloor;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


public class ThreadFloorAdapter extends BaseAdapter {


    private List<ThreadFloor> mData;
    private LayoutInflater mInflater;

    public ThreadFloorAdapter(List<ThreadFloor> list) {
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
        ThreadFloor atom = mData.get(position);
        ViewHolder mHolder = null;
        if (convertView == null) {

            mHolder = new ThreadFloorAdapter.ViewHolder();

            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.getContext());
            }
            convertView = mInflater.inflate(R.layout.atom_list_floor, null);
            mHolder.atom = convertView.findViewById(R.id.atom);
            mHolder.atom_img = (SimpleDraweeView) convertView.findViewById(R.id.atom_img);
            mHolder.atom_name = (TextView) convertView.findViewById(R.id.atom_name);
            mHolder.atom_floor_num = (TextView) convertView.findViewById(R.id.atom_floor_num);
            mHolder.atom_time = (TextView) convertView.findViewById(R.id.atom_time);
            mHolder.atom_webview = (WebView) convertView.findViewById(R.id.atom_webview);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.atom_name.setText(atom.getName());
        mHolder.atom_time.setText(atom.getTime());
        mHolder.atom_floor_num.setText(atom.getFloorNum() + "F");
        mHolder.atom_img.setImageURI(atom.getAvatarUrl());

        mHolder.atom_webview.loadDataWithBaseURL(Constant.HOST_APP, atom.getContentHtml(), "text/html", "utf-8", null);

        return convertView;
    }

    class ViewHolder {
        View atom;
        SimpleDraweeView atom_img;
        TextView atom_name;
        TextView atom_floor_num;
        TextView atom_time;
        WebView atom_webview;
    }
}