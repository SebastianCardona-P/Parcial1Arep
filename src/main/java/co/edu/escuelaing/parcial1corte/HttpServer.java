/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.edu.escuelaing.parcial1corte;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sebastian
 */
public class HttpServer {

    private static final int PORT = 35000;
    private static List<Double> listStorage = new LinkedList<>();
    private static int countList = 0;

    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, URISyntaxException {
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

    private static void handleRequest(URI path, PrintWriter out) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (path.getPath().startsWith("/add")) {
            System.out.println("Voy para el add");
            handleAdd(path, out);
        } else if (path.getPath().startsWith("/list")) {
            handleList(path, out);
            System.out.println("Me fui para list");
        } else if (path.getPath().startsWith("/clear")) {
            handleClear(path, out);
            System.out.println("Me fui para clear");
        } else if (path.getPath().startsWith("/stats")) {
            handleStats(path, out);
            System.out.println("Me fui para stats");
        } else {
            System.out.println("No enconte petición");
            notSoported(out);
        }
    }

    private static void notSoported(PrintWriter out) {
        String response = "HTTP/1.1 404 Not Found\r\n"
                + "Content-Type: application/json\r\n"
                + "\r\n"
                + "{\"message\": \"This method is not soported yet\"}";
        out.write(response);
    }

    private static void handleAdd(URI path, PrintWriter out) {
        String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/json\r\n"
                + "\r\n";
        System.out.println("mi query es:" + path.getQuery());
        String stringReal = path.getQuery().split("=")[1].replace(" ", "");
        System.out.println("mi real es:" + stringReal);

        double real = Double.parseDouble(stringReal);

        listStorage.add(real);
        countList = listStorage.size();
        System.out.println("mi lista es" + listStorage.toString());

        response += "{\"status\": \"OK\", \"added\": \"" + real + "\"," + "\"count\": \"" + countList + "\"" + "}";

        out.write(response);
    }

    private static void handleList(URI path, PrintWriter out) {
        String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/json\r\n"
                + "\r\n";

        String listString = listStorage.toString();

        response += "{\"status\": \"OK\", \"values\": \"" + listString + "\"," + "\"count\": \"" + countList + "\"" + "}";

        out.write(response);
    }

    private static void handleClear(URI path, PrintWriter out) {
        String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/json\r\n"
                + "\r\n";

        listStorage.clear();
        countList = listStorage.size();

        response += "{\"status\": \"OK\", \"message\": \"list_cleared\"" + "}";

        out.write(response);
    }

    private static void handleStats(URI path, PrintWriter out) {
        String response;
        if (listStorage.isEmpty()) {
            response = "HTTP/1.1 409 OK\r\n"
                    + "Content-Type: application/json\r\n"
                    + "\r\n"
                    + "{\"status\": \"ERR\", \"error\": \"empty_list\"" + "}";
        } else {
            response = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: application/json\r\n"
                    + "\r\n";

            countList = listStorage.size();

            int totalSum = 0;
            for (Double num : listStorage) {
                totalSum += num;
            }
            
            System.out.println("total sum: " + totalSum);
            System.out.println("total lista: "+ countList);
            double mean = totalSum / (double) countList;
            
            System.out.println("mean es: "+ mean);

            double totalDiferencia = 0;
            for (Double num : listStorage) {
                totalDiferencia += Math.pow((double) num - mean, 2);
            }
            
            double desv = Math.sqrt((double) (1 / ( (double) countList - 1) * totalDiferencia));

            response += "{\"status\": \"OK\", \"mean\": \"" + mean + "\"," + "\"stddev\": \"" + desv + "\"," + "\"count\": \"" + countList + "\"" + "}";
        }

        out.write(response);
    }

}
