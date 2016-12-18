package com.deerslab.mathbomb;

/**
 * Created by keeper on 10.02.2016.
 */
public class CategoryChooserItem {

    //public int icon;
    public String title;
    public int progress;
    public boolean accessible;

    public CategoryChooserItem() {
        super();
    }

    public CategoryChooserItem(String title, int progress, boolean accessible) {
        super();
        //this.icon = icon;
        this.title = title;
        this.progress = progress;
        this.accessible = accessible;
    }
}
