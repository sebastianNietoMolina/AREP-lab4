package edu.escuelaing.arep.app.httpserver;

import edu.escuelaing.arep.app.myspring.AnotherSpring;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    private boolean isAlive = true;
    Map<String, AnotherSpring> routes = new HashMap();

    /**
     * Constructor.
     */
    public HttpServer() {}

    /**
     * Servidor de peticiones http.
     * @throws IOException
     */
    public void start() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(getPort());
        } catch (IOException e) {
            System.err.println("Could not listen on port: 36000.");
            System.exit(1);
        }
        while(isAlive()) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine = null;
            String outputLine = null;
            String resp = null;
            boolean fisrtLine = true;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                if (!in.ready()) {
                    break;
                }
                if(fisrtLine) {
                    resp = watchingDisc(inputLine, out, clientSocket);
                    fisrtLine = false;
                }
            }
            if(resp==null){
                notFound(out);
            }else{
                outputLine = resp;
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    /**
     * Retorna la página de inicio por defecto.
     * @param out
     * @throws IOException
     */
    public void readIndex(PrintWriter out) throws IOException {
        String outputLine;

        File path = new File("src/main/resources/index.html");
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        outputLine = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html  \r\n"
                + "\r\n";
        String inputLine;
        while ((inputLine=bufferedReader.readLine()) != null){
            outputLine += inputLine + "\n";
        }
        System.out.println(outputLine);
        out.println(outputLine);
        out.close();
    }

    /**
     * Revisa en el path enviado, si la información la tiene en disco.
     * @param inputLine
     * @param out
     * @return una imprensin en pantalla dependiendo del formato pedido.
     */
    public String watchingDisc(String inputLine, PrintWriter out, Socket os) throws IOException {

        boolean flag = true;
        String data = null;

        if (inputLine.contains("GET")){
            String[] typeData = inputLine.split(" ");
            data = typeData[1];
            if(data.equals("/hello.html")){
                doHello(out);
                flag = false;
            }else if(data.equals("/dogs.jpg") || data.equals("/dogs.JPG")){
                doDogs(out);
                flag = false;
            }else if(data.contains("/cats.png") || data.contains("/cats.PNG")){
                doCats(out);
                flag = false;
            }else if(data.contains("/show.css")){
                doCss(os);
                flag = false;
            }else if(data.contains("/show.js")){
                doJs(out);
                flag = false;
            }else if(data.equals("/")){
                readIndex(out);
            }
        }

        return isValid(data, flag);
    }

    /**
     * Verifica
     * @param data
     * @param flag
     * @return
     */
    public String isValid(String data, boolean flag){
        String resp = null;

        if(flag && data!=null){
            for (String key: routes.keySet() ){
                if(data.startsWith(key) ){
                    String newPath = data.substring(key.length());
                    resp = routes.get(key).handle(newPath, null, null);
                }
            }
        }
        return resp;
    }

    /**
     * Nos dice si el servidor esta prendido o apagado.
     * @return boolean.
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     *  Nos permite cambiar el estado del servidor, de apagado a prendido, o al contrario.
     * @param alive
     */
    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    /**
     * html estático que está contenido en disco.
     * @param out
     */
    public void doHello(PrintWriter out){
        String outputLine;
        outputLine = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta charset=\"UTF-8\">\n"
                + "<title>Hello!!!</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "<h1>Very good days!</h1>\n"
                + "</body>\n"
                + "</html>\n";
        out.println(outputLine);
        out.close();
    }

    /**
     * html estático que nos devuelve una imagen de perros
     * @param out
     */
    public void doDogs(PrintWriter out){
        String outputLine;
        outputLine = "HTTP/1.1 200 OK\r\n"
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
        out.println(outputLine);
        out.close();
    }

    /**
     * html estático que nos devuelve una imagen de gatos.
     * @param out
     */
    public void doCats(PrintWriter out){
        String outputLine;
        outputLine = "HTTP/1.1 200 OK\r\n"
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
        out.println(outputLine);
        out.close();
    }

    /**
     * Fragmento de código css.
     * @param socket del cliente para poder mostrar los datos en pantalla.
     * @throws IOException
     */
    public void doCss(Socket socket) throws IOException {
        PrintWriter response = new PrintWriter(socket.getOutputStream(), true);
        response.println("HTTP/1.1 200 OK");
        response.println("Content-Type: text/css" + "\r\n");
        response.println(".rentContainer {" + "\r\n");
        response.println("display: grid;"+ "\r\n");
        response.println("grid-template-columns: 50% 50%;"+ "\r\n");
        response.println("grid-auto-rows:200px;"+ "\r\n");
        response.println("margin: 3%;"+ "\r\n");
        response.println("background-color: rgba(56, 56, 56, 0.048) "+ "\r\n");
        response.println("}" + "\r\n");
        response.flush();
        response.close();
    }

    /**
     * Busca en el path de archivos .js, y muestra uno en pantalla.
     * @param out para poder imprimir en pantalla el archivo .js sin necesidad de usar el socket client
     * @throws IOException
     */
    public void doJs(PrintWriter out) throws IOException {
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
        System.out.println(outputLine);
        out.println(outputLine);
        out.close();
    }

    /**
     * Método que nos retorna error en caso de no encontrar una petición en disco.
     * @param out
     */
    public void notFound(PrintWriter out) {
        String outputLine;
        outputLine = "HTTP/1.1 404 not Found\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta charset=\"UTF-8\">\n"
                + "<title>Error</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "<h1>Does not exist</h1>\n"
                + "</body>\n"
                + "</html>\n";
        out.println(outputLine);
        out.close();
    }



    /**
     * Método que nos devuelve el puerto por el que correrá localmente nuestro servicio
     * @return el puerto del servicio.
     */
    private int getPort(){
        if(System.getenv("PORT") != null){
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 36000;
    }

    /**
     * Agrega las rutas al servidor.
     * @param path de la ruta que vamos a usar
     * @param anotherSpring la clase, para luego poder retornar si el valor existe o no.
     */
    public void registerProessor(String path, AnotherSpring anotherSpring) {
        routes.put(path, anotherSpring);
    }
}
