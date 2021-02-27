package edu.escuelaing.arep.app.myspring;

import edu.escuelaing.arep.app.annotation.RequestMapping;
import edu.escuelaing.arep.app.httpserver.HttpServer;

/**
 * Hello world!
 *
 */
public class App {

    @RequestMapping("/prueba")
    public static void index() {}

    @RequestMapping("/cats.png")
    public static void cats(){}

    @RequestMapping("/dogs.jpg")
    public static void dogs(){}

    @RequestMapping("/js")
    public static void js(){}

    @RequestMapping("/css")
    public static void css(){}

    @RequestMapping("/prueba2")
    public static String index2() {
        return "Buen día, prueba2!";
    }

}
