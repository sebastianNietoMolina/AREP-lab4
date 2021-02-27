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
                    resp = isValid(inputLine, out);
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
     * Verifica
     * @param inputLine
     * @return
     */
    public String isValid(String inputLine, PrintWriter out
    ) throws IOException {
        String resp = null;
        String data;
        String[] typeData = inputLine.split(" ");
        data = typeData[1];
        if(data.equals("/")){
            readIndex(out);
        }
        else{
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
