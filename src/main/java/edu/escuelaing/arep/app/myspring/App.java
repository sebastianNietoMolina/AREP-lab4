package edu.escuelaing.arep.app.myspring;

import edu.escuelaing.arep.app.annotation.RequestMapping;
/**
 * Hello world!
 *
 */
public class App {

    @RequestMapping("/prueba")
    public static String index() {
        return "Buen día, prueba1!";
    }

    @RequestMapping("/prueba2")
    public static String index2() {
        return "Buen día, prueba2!";
    }

}
