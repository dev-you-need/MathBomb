package com.deerslab.mathbomb;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by keeper on 10.02.2016.
 */
public class CategoryChooserAdapter extends ArrayAdapter<CategoryChooserItem> {

    Context context;
    int layoutResourceId;
    CategoryChooserItem data[] = null;

    public CategoryChooserAdapter(Context context, int layoutResourceId, CategoryChooserItem[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ItemHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ItemHolder();
            //holder.imgIcon = (ImageView)row.findViewById(R.id.ivIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.tvTitle);
            holder.progressBar = (ProgressBar)row.findViewById(R.id.progressBar);
            holder.lock = (ImageView)row.findViewById(R.id.ivLock);

            row.setTag(holder);
        }
        else
        {
            holder = (ItemHolder)row.getTag();
        }

        CategoryChooserItem categoryItem = data[position];
        holder.txtTitle.setText(categoryItem.title);
        //holder.imgIcon.setImageResource(categoryItem.icon);
        holder.progressBar.setProgress(categoryItem.progress * 10);
        holder.lock.setVisibility(categoryItem.accessible ? View.INVISIBLE : View.VISIBLE);

        return row;
    }

    static class ItemHolder{
        ImageView imgIcon;
        TextView txtTitle;
        ProgressBar progressBar;
        ImageView lock;
    }
}
