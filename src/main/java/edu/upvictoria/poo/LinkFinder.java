package edu.upvictoria.poo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class LinkFinder implements Runnable {

    private String url;
    private LinkHandler linkHandler;

    public LinkFinder(String link, LinkHandler handler) {
        url = link;
        linkHandler = handler;
    }

    @Override
    public void run() {
        crawler(url);
    }

    private void crawler(String url) {
        Document doc = movieRequest(url);

        if (doc != null) {
            for (Element link : doc.select("a[href]")) {
                String next_link = link.absUrl("href");
                if (!linkHandler.visited(next_link)) {
                    crawler(next_link);
                }
            }
        }
    }

    private Document connection(String url) throws IOException {
        Connection con = Jsoup.connect(url).userAgent("Mozilla");
        Document doc = con.get();

        if (con.response().statusCode() == 200) {
            return doc;
        }

        return null;
    }

    private Document movieRequest(String url) {
        if (!linkHandler.visited(url)) {
            try {
                Document doc = connection(url);

                Element title = doc.selectFirst("span.hero__primary-text[data-testid=hero__primary-text]");
                if (title != null) {
                    System.out.println(title.text());
                }

                Element body = doc.selectFirst("span[role=presentation][data-testid=plot-xl].sc-eb5317c9-2.bruFve");
                if (body != null) {
                    System.out.println(body.text());
                }

                Element review = doc.selectFirst("div[data-testid=hero-rating-bar__aggregate-rating__score].sc-bde20123-2.cdQqzc");
                if (review != null) {
                    System.out.println(review.text());
                }

                Elements actorLinks = doc.select("a[data-testid=title-cast-item__actor]");

                for (Element actor_link : actorLinks) {
                    String next_link = String.format("https://www.imdb.com%s", actor_link.attr("href"));
                    if (!linkHandler.visited(next_link)) {
                        String movieLink = actorRequest(next_link);
                        if (movieLink != null && !linkHandler.visited(movieLink)) {
                            movieRequest(movieLink);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String actorRequest(String url) throws IOException {
        Document doc = connection(url);
        if (doc != null) {
            Element name = doc.selectFirst("span.hero__primary-text[data-testid=hero__primary-text]");
            if (name != null) {
                System.out.println(name.text());
            }

            Elements movies = doc.select("ul.ipc-metadata-list.ipc-metadata-list--dividers-between.ipc-metadata-list--base[role=presentation] a[href]");
            if (movies != null && !movies.isEmpty()) {
                for (Element movie : movies) {
                    String movieLink = String.format("https://www.imdb.com%s", movie.attr("href"));
                    System.out.println(movieLink);
                    return movieLink; // Retorna el primer link de película encontrado
                }
            }
        }
        return null;
    }
}
