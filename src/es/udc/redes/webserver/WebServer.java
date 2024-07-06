package es.udc.redes.webserver;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class WebServer {
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Format: java es.udc.redes.webserver.WebServer <port>");
            System.exit(-1);
        }
        Socket socketAux = null;
        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));

            while (true) {
                // Wait for connections
                socketAux = serverSocket.accept();
                // Create a ServerThread object, with the new connection as parameter
                ServerThread serverThread = new ServerThread(socketAux);
                // Initiate thread using the start() method
                serverThread.start();
            }
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally{
            //Close the socket
            assert socketAux != null;
            try {
                socketAux.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
}
