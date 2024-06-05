package edu.upvictoria.poo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.Time;

public class LinkFinder implements Runnable{

    private String url;
    private LinkHandler linkHandler;

    public LinkFinder(String link, LinkHandler hanlder){

        url = link;
        linkHandler = hanlder;

    }


    @Override
    public void run(){
        crawler(url);
    }

    private void crawler(String url){
        Document doc = request(url,this.linkHandler);

        if (doc != null){
            for (Element link: doc.select("a[href]")){
                String next_link = link.absUrl("href");
                if (linkHandler.visited(next_link) == false){
                    crawler(next_link);
                }
            }

        }
    }

    private static Document request(String url, LinkHandler handler){

        if (!handler.visited(url)) {
            try {
                Connection con = Jsoup.connect(url);
                Document doc = con.get();

                if (con.response().statusCode() == 200) {

                    System.out.println("Link: " + url);
                    System.out.println(doc.title());
                    handler.addVisited(url);

                    return doc;

                }

            } catch (IOException e) {

            }

        }
        return null;
    }

}
