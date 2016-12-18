package com.deerslab.mathbomb;

/**
 * Created by keeper on 10.02.2016.
 */
public class Database {


    public static CategoriesEnum currentCategory;
    public static int currentLevel;

    private static Database ourInstance = new Database();

    public static Database getInstance() {
        return ourInstance;
    }

    private Database() {

    }

}
