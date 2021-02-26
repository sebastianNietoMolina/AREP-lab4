package edu.escuelaing.arep.app.myspring;

import edu.escuelaing.arep.app.annotation.RequestMapping;
import edu.escuelaing.arep.app.httpserver.HttpServer;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;

public class AnotherSpring {

    Map<String, Method> route = new HashMap<String,Method>();
    HttpServer httpServer;

    public static void main(String[] args) throws ClassNotFoundException {
        AnotherSpring anotherSpring = new AnotherSpring();
        anotherSpring.loadComponent(args);
        anotherSpring.startServer();
    }

    public AnotherSpring() {}

    public void loadComponent(String[] components) throws ClassNotFoundException {
        for(String compName: components){
            loadComponents(compName);
        }
    }

    private void loadComponents(String compName) throws ClassNotFoundException {
        Class component = Class.forName(compName);
        Method[] compMethods = component.getDeclaredMethods();
        for(Method m: compMethods){
            if(m.isAnnotationPresent(RequestMapping.class)){
                route.put(m.getAnnotation(RequestMapping.class).value(),m);
            }
        }
    }

    /**
     * Inicializa el servidor https.
     */
    public void startServer()   {
        System.out.println("Inicie");
        httpServer = new HttpServer();
        httpServer.registerProessor("/App", this);
        try {
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifica que la ruta a buscar si exista en nuestro arreglo de rutas.
     * @param path de la ruta que estamos buscando
     * @param req valor lambda
     * @param resp valor lambda
     * @return html diciendo si el valor encontrado es correcto o no, se mostrara en pantalla.
     */
    public String handle(String path, HttpRequest req, HttpResponse resp){

        if(route.containsKey(path)){
            try {
               return httpOk() + route.get(path).invoke(null,null).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return httpNotOk() + "Error";
    }

    private String httpNotOk() {
        return "HTTP/1.1 404 not Found\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta charset=\"UTF-8\">\n"
                + "<title>Error</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "<h1>There is an error</h1>\n"
                + "</body>\n"
                + "</html>\n";
    }

    private String httpOk() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n";
    }

}
