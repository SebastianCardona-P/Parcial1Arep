/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package co.edu.escuelaing.parcial1corte;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 * @author Sebastian
 */
public class Facade {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://localhost:35000";
    private static final int PORT = 36000;
    private static String basePath = "src/main/java/resources/";

    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT + ".");
            System.exit(1);
        }

        boolean running = true;

        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            // IO stream
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;

            boolean isFirstLine = true;
            URI path = null;

            while ((inputLine = in.readLine()) != null) {

                if (isFirstLine) {

                    path = new URI(inputLine.split(" ")[1]);
                    System.out.println("Path: " + path.getPath());
                    isFirstLine = false;
                }
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }

            handleRequest(path, out);

            out.close();
            in.close();
            clientSocket.close();

        }

        serverSocket.close();
    }

    private static void handleRequest(URI path, PrintWriter out) throws IOException {
        if (path.getPath().startsWith("/cliente") || path.getPath().equalsIgnoreCase("/")) {
            getStaticFile(path, out);
        } else if (path.getPath().startsWith("/add") || path.getPath().startsWith("/list")
                || path.getPath().startsWith("/clear") || path.getPath().startsWith("/stats")) {
            try {
                connectionBack(path, out);
            } catch (MalformedURLException ex) {
                System.getLogger(Facade.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        } else {
            notFound(out);
        }
    }

    private static void connectionBack(URI path, PrintWriter out) throws MalformedURLException, IOException {
        String outputLine;
        URL obj;

        if(path.getPath().startsWith("/add")){
            obj = new URL(GET_URL + path.getPath() + "?" + path.getQuery().replace(" ", ""));
            String[] keyValue = path.getQuery().split("=");
            
            if(keyValue.length == 1){
                outputLine = "HTTP/1.1 400 OK\n\r"
                    + "contente-type: application/json\n\r"
                    + "\n\r";
            outputLine += "{\"status\": \"ERR\", \"error\": \"invalid_number\"}";
            out.write(outputLine);
            return;
            }
            String stringReal = keyValue[1].replace(" ", "");
        } else{
            obj = new URL(GET_URL + path.getPath());
        }
        
        System.out.println("URL ESSSSS para el back" + obj.toString());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            outputLine = "HTTP/1.1 200 OK\n\r"
                    + "contente-type: application/json\n\r"
                    + "\n\r";
            outputLine += response.toString();
        } else if (responseCode == 409 ) {
            outputLine = "HTTP/1.1 409 OK\n\r"
                    + "contente-type: application/json\n\r"
                    + "\n\r";
            outputLine += "{\"status\": \"ERR\", \"error\": \"empty_list\"" + "}";
        } else {
            System.out.println("GET request not worked");
            outputLine = "HTTP/1.1 502 OK\n\r"
                    + "contente-type: application/json\n\r"
                    + "\n\r";
            outputLine += "{\"status\": \"ERR\", \"error\": \"backend_unreachable\"}";
        }
        out.write(outputLine);
    }

    private static void getStaticFile(URI requestUri, PrintWriter out) {
        String outputLine = "HTTP/1.1 200 OK\n\r"
                + "contente-type: text/html\n\r"
                + "\n\r";

        // create the file path
        String file = basePath + "index.html";
        File realFile = new File(file);
        if (!realFile.exists() && !realFile.isFile()) {
            notFound(out);
            return;
        }

        // start reading the file
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String fileLine;
            while ((fileLine = reader.readLine()) != null) {
                outputLine += fileLine + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.write(outputLine);
    }

    private static void notFound(PrintWriter out) {
        String response = "HTTP/1.1 404 Not Found\r\n"
                + "Content-Type: text/plain\r\n"
                + "\r\n"
                + "404 Not Found";
        out.write(response);
    }
}
