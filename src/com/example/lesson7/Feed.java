package com.example.lesson7;

/**
 * Created with IntelliJ IDEA.
 * User: satori
 * Date: 10/31/13
 * Time: 9:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Feed {
    public final int id;
    public final String title;
    public final String link;
    public Feed(int _id, String _title, String _link) {
        id = _id;
        title =  _title;
        link = _link;
    }
}
