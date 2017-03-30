package com.afap.discuz.chh.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afap.discuz.chh.R;
import com.afap.discuz.chh.model.Category;

import java.util.List;


/**
 * 虎凤蝶行动
 */
public class CategoryAdapter extends RecyclerView.Adapter {

    private OnClickListener onClickListener;


    private Context mContext;
    private List<Category> mData;

    private static final int TYPE_LABEL = 1; // 分类label
    private static final int TYPE_NORMAL = 2; // 分类


    public CategoryAdapter(Context context, List<Category> categorys, OnClickListener listener) {
        mContext = context;
        mData = categorys;
        onClickListener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_LABEL:
                //banner
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.atom_category_label, null);
                return new CategoryLabelViewHolder(view);
            case TYPE_NORMAL:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.atom_category_column, null);
                return new CategoryColumnViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof CategoryLabelViewHolder) {
            ((CategoryLabelViewHolder) holder).tv_label.setText(mData.get(position).getName());
        } else if (holder instanceof CategoryColumnViewHolder) {
            TextView tv_column = ((CategoryColumnViewHolder) holder).tv_column;
            String title = mData.get(position).getName();
            if (!TextUtils.isEmpty(mData.get(position).getNum())) {
                title = title + "(" + mData.get(position).getNum() + ")";
            }
            tv_column.setText(title);
            tv_column.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.click(position);
                }
            });
            if (mData.get(position).isSelected()) {
                tv_column.setBackgroundResource(R.drawable.bg_category_selected);
                tv_column.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                tv_column.setBackgroundResource(R.drawable.bg_category_normal);
                tv_column.setTextColor(ContextCompat.getColor(mContext, R.color.black_1));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).isLabel() ? TYPE_LABEL : TYPE_NORMAL;
    }

    public interface OnClickListener {
        void click(int position);
    }


    class CategoryLabelViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_label;

        public CategoryLabelViewHolder(View itemView) {
            super(itemView);
            tv_label = (TextView) itemView.findViewById(R.id.tv_label);
        }
    }

    class CategoryColumnViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_column;

        public CategoryColumnViewHolder(View itemView) {
            super(itemView);
            tv_column = (TextView) itemView.findViewById(R.id.tv_column);
        }
    }
}
