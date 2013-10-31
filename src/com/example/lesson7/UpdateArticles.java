package com.example.lesson7;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: satori
 * Date: 10/31/13
 * Time: 9:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateArticles extends IntentService {
    public ArrayList<Feed> feeds;
    public ArrayList<Article> articles;
    public static ArrayList<ArrayList<String> > descriptions;
    //private  String[] urls;
    private RSSDBAdapter adapter;



    public int index = 0;
    public UpdateArticles() {
        super("UpdateArticles");
        adapter = new RSSDBAdapter(this);
        feeds = new ArrayList<>();
        articles = new ArrayList<>();
        index = 0;



    }
    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            getUrls();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {

                boolean item = false;
                boolean title = false;
                boolean description = false;
                String stitle;
                String sdescription;
                StringBuffer buffer;
                public void startElement(String uri, String localName,
                                         String qName, Attributes attributes)
                        throws SAXException {
                    buffer = new StringBuffer();
                    //System.out.println("Start Element :" + qName);
                    System.out.println("StartTag: " + qName);
                    if (qName.equals("item")) {
                        item = true;
                    }

                    if (qName.equals("title")) {
                        title = true;
                    }

                    if (qName.equals("description")) {
                        description = true;
                    }


                }

                public void endElement(String uri, String localName,
                                       String qName)
                        throws SAXException {
                    String all = buffer.toString();
                    if (item && description && qName.equals("description")) {
                        //descriptions.get(index).add(all);
//                        item = false;
                        //  description = false;
                        sdescription = all;
                    }
                    System.out.println("EndTag: " + qName);
                    //System.out.println("End Element :" + qName);
                    if (qName.equals("item")) {
                        item = false;
                        articles.add(new Article(index, stitle, sdescription));
                        assert(stitle != null && sdescription != null);
                    }

                    if (qName.equals("title")) {
                        title = false;
                    }

                    if (qName.equals("description")) {
                        description = false;
                    }

                }

                public void characters(char ch[], int start, int length)
                        throws SAXException {

                    //System.out.println(new String(ch, start, length));


                    if (item &&  title) {
                        System.out.println("First Name : "
                                + new String(ch, start, length));
                        //bfname = false;
                        //articles.get(index).add(new String(ch, start, length));
                        stitle = new String(ch, start, length);
                    }
                    else {


                        if(buffer != null) buffer.append(new String(ch, start, length));
                    }

                    //System.out.println("Last Name : "
//                            + new String(ch, start, length));
                    //blname = false;
                    //d

                }
            };
            for (Feed feed: feeds) {
                index = feed.id;
                //String coding = index == 5 ? "CP1251" : "UTF-8";
                System.out.println("Dowloading URL:" + feed.title);
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(feed.link);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                String charset = EntityUtils.getContentCharSet(httpEntity);
                if (charset == null) {
                    charset = "UTF-8";
                }

                System.out.println("Downloading charset: " + charset.toUpperCase());
                InputStream is = httpEntity.getContent();
                Reader reader = new InputStreamReader(is, charset.toUpperCase());
                InputSource isource = new InputSource(reader);
                isource.setEncoding(charset);
                saxParser.parse(isource, handler);
                is.close();
                boolean testupdate = adapter.updateArticles(articles);

            }
            Intent intent1 =new Intent("END LOADING");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);

            //FeedActivity.arrayAdapter.notifyDataSetChanged();

            //scheduleNextUpdate();
        } catch (Exception e) {
            //Log.d("Error", "Exception while loading xml");
            System.out.println("Error while parsing");
            e.printStackTrace();
        }  finally {
            adapter.close();
        }


    }
    private void getUrls() {
        feeds.clear();
        if (adapter != null) {
            adapter.open();
            Cursor cursor = adapter.fetchAllChannels();
            if (cursor.moveToFirst()) {
                int _id = cursor.getInt(0);
                String title = cursor.getString(1);
                String url = cursor.getString(2);
                feeds.add(new Feed(_id, title, url));
                while (cursor.moveToNext()) {
                    _id = cursor.getInt(0);
                    title = cursor.getString(1);
                    url = cursor.getString(2);
                    feeds.add(new Feed(_id, title, url));
                }
            }

        }

    }

}
