package es.udc.redes.tutorial.tcp.server;

import java.net.*;
import java.io.*;

/**
 * MonoThread TCP echo server.
 */
public class MonoThreadTcpServer {

    public static void main(String argv[]) throws IOException {
        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.tcp.server.MonoThreadTcpServer <port>");
            System.exit(-1);
        }
        Socket socketAux = null;
        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(argv[0]));
            // Set a timeout of 300 secs
            serverSocket.setSoTimeout(300000);
            while (true) {
                // Wait for connections
                socketAux = serverSocket.accept();
                // Set the input channel
                BufferedReader sInput = new BufferedReader(new InputStreamReader(
                        socketAux.getInputStream()));
                // Set the output channel
                PrintWriter sOutput = new PrintWriter(socketAux.getOutputStream(), true);
                // Receive the client message
                String received = sInput.readLine();
                System.out.println("SERVER: Received " + received
                        + " from " + socketAux.getInetAddress().toString()
                        + ":" + socketAux.getPort());
                // Send response to the client
                System.out.println("SERVER: Sending " + received +
                        " to " + socketAux.getInetAddress().toString() +
                        ":" + socketAux.getPort());
                sOutput.println(received);
                // Close the streams
                sOutput.close();
                sInput.close();
            }
       } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs ");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
	        //Close the socket
            assert socketAux != null;
            socketAux.close();
        }
    }
}
