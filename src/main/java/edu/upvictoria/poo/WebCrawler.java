package edu.upvictoria.poo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebCrawler implements LinkHandler{

    private static final Set<String> links = new HashSet<>();
    private ExecutorService executorService;

    public WebCrawler(String startLink, int maxThreads){
        executorService = Executors.newFixedThreadPool(maxThreads);
        executorService.execute(new LinkFinder(startLink,this));
    }
    /**
     * Guarda el link en la cosa
     *
     * @param link
     * @throws Exception
     */
    @Override
    public void queueLink(String link) throws Exception {

    }

    /**
     * Retorna el número de links visitados
     *
     * @return
     */
    @Override
    public int size() {
        return links.size();
    }

    /**
     * Verifica si el link ya ha sido visitado
     *
     * @param link
     * @return
     */
    @Override
    public boolean visited(String link) {
        return links.contains(link);
    }

    /**
     * Marca el link como visitado
     *
     * @param link
     */
    @Override
    public void addVisited(String link) {
        links.add(link);
    }
}
