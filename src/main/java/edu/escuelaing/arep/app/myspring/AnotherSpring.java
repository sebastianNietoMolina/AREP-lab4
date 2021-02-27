package edu.escuelaing.arep.app.myspring;

import edu.escuelaing.arep.app.annotation.RequestMapping;
import edu.escuelaing.arep.app.httpserver.HttpServer;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class AnotherSpring {

    Map<String, Method> route = new HashMap<String,Method>();
    HttpServer httpServer;

    /**
     * Metodo main, para que lea el POJO
     * @param args
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws ClassNotFoundException {
        AnotherSpring anotherSpring = new AnotherSpring();
        anotherSpring.loadComponent(args);
        anotherSpring.startServer();
    }

    /**
     * Constructor
     */
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
                if(path.equals("/prueba")){
                    return httpOk() + "prueba pasada";
                }else if (path.equals("/dogs.jpg")){
                    return doDogs();
                }else if(path.equals("/cats.png")){
                    return doCats();
                }else if(path.equals("/js")){
                    return js();
                }else if(path.equals("/css")){
                    return doCss();
                }else{
                    return httpOk() + route.get(path).invoke(null,null).toString();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IOException e) {
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

    private String js() throws IOException {
        String outputLine;
        File path = new File("src/main/resources/app.js");
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        outputLine = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/javascript  \r\n"
                + "\r\n";
        String inputLine;
        while ((inputLine=bufferedReader.readLine()) != null){
            outputLine += inputLine + "\n";
        }
        return outputLine;
    }

    private String doDogs(){
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta charset=\"UTF-8\">\n"
                + "<title>Dogs!!</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "<img src=\"https://www.happets.com/blog/wp-content/uploads/2019/08/ventajas-de-un-dispensador-de-comida-para-perros.jpg\" />\n"
                + "</body>\n"
                + "</html>\n";
    }

    private String doCats(){
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta charset=\"UTF-8\">\n"
                + "<title>Title of the document</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "<img src=\"https://www.pngkey.com/png/detail/909-9090660_adopt-cat-imagens-de-gatos-png.png\" />\n"
                + "</body>\n"
                + "</html>\n";
    }

    private String doCss() throws IOException {
        String outputLine;
        File path = new File("src/main/resources/app.css");
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        outputLine = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/css  \r\n"
                + "\r\n";
        String inputLine;
        while ((inputLine=bufferedReader.readLine()) != null){
            outputLine += inputLine + "\n";
        }
        return outputLine;
    }

}
