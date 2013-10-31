package com.example.lesson7;

/**
 * Created with IntelliJ IDEA.
 * User: satori
 * Date: 10/31/13
 * Time: 9:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Article {
    public final int id;
    public final String title;
    public final String description;
    public Article(int _id, String _title, String _description) {
        id = _id;
        title = _title;
        description = _description;
    }
    @Override
    public String toString() {
        return title;
    }
}
