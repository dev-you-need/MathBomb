package com.deerslab.mathbomb;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by keeper on 11.02.2016.
 */
public class LevelChooserAdapter extends ArrayAdapter<LevelChooserItem> {

    Context context;
    int layoutResourceId;
    LevelChooserItem data[] = null;

    public LevelChooserAdapter(Context context, int layoutResourceId, LevelChooserItem[] data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ItemHolder holder = null;

        if (row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ItemHolder();

            holder.txtTitle = (TextView)row.findViewById(R.id.tvLevel);
            holder.lock = (ImageView)row.findViewById(R.id.ivLevelLock);

            row.setTag(holder);
        }
            else {
            holder = (ItemHolder)row.getTag();
        }

        LevelChooserItem levelItem = data[position];
        holder.txtTitle.setText(levelItem.title);
        holder.lock.setVisibility(levelItem.accessible ? View.INVISIBLE : View.VISIBLE);

        return row;
    }

    static class ItemHolder{
        TextView txtTitle;
        ImageView lock;
    }
}
