package edu.upvictoria.poo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
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

        Document doc = movieRequest(url);

        if (doc != null){
            for (Element link: doc.select("a[href]")){
                String next_link = link.absUrl("href");
                if (!linkHandler.visited(next_link)){
                    crawler(next_link);
                }
            }

        }
    }

    private Document connection(String url) throws IOException{

        Connection con = Jsoup.connect(url).userAgent("Mozilla");
        Document doc = con.get();

        if (con.response().statusCode() == 200){

           return doc;

        }

        return null;

    }

    private Document movieRequest(String url){

        if (!linkHandler.visited(url)) {
            try {

                Document doc = connection(url);

                Element titel = doc.selectFirst("span.hero__primary-text[data-testid=hero__primary-text]");
                System.out.println(titel.text());
                Element body = doc.selectFirst("span[role=presentation][data-testid=plot-xl].sc-eb5317c9-2.bruFve");
                System.out.println(body.text());
                Element review = doc.selectFirst("div[data-testid=hero-rating-bar__aggregate-rating__score].sc-bde20123-2.cdQqzc");
                System.out.println(review.text());

                Elements actorLink = doc.select("a[data-testid=title-cast-item__actor]");

                for (Element actor_link: actorLink ){

                    String next_link = String.format("https://www.imdb.com%s",actor_link.attr("href"));

                    if (!linkHandler.visited(next_link)){

                        actorRequest(next_link);

                    }

                }


            } catch (IOException e) {

            }

        }
        return null;
    }

    private void actorRequest(String url) throws IOException{

        Document doc = connection(url);
        Element name = doc.selectFirst("span.hero__primary-text[data-testid=hero__primary-text]");
        System.out.println(name.text());
        Elements movies = doc.select("ul.ipc-metadata-list.ipc-metadata-list--dividers-between.ipc-metadata-list--base[role=presentation]").select("a[href]");

        System.out.println(movies.text());

        for (Element actor: movies){

            System.out.println(String.format("https://www.imdb.com%s",movies.attr("href")));

        }



    }

}
