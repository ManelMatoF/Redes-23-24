package es.udc.redes.webserver;

import java.net.*;
import java.io.*;

import java.nio.file.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Date;


public class ServerThread extends Thread {

    private Socket socket;

    public ServerThread(Socket s) {
        // Store the socket s
        this.socket = s;
    }

    public void run() {
        try {
            
            // This code processes HTTP requests and generates
            // Set the input channel
            BufferedReader sInput = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            // Receive the message from the client
            StringBuilder requestBuilder = new StringBuilder();
            String referenceDate = null;
            String inputLine;
            while ((inputLine = sInput.readLine()) != null  && !inputLine.isEmpty()) {
                String aux = inputLine.toLowerCase();
                // Buscar la línea que comience con "if-modified-since:"
                if (aux.startsWith("if-modified-since:")) {
                    // Dividir la línea en dos partes usando ":"
                    String[] partes = inputLine.split(": ");
                    if (partes.length > 1) {
                        // Obtener la fecha de referencia y eliminar espacios en blanco
                        referenceDate = partes[1].trim();
                    }
                }
                requestBuilder.append(inputLine).append("\r\n");
            }

            String fullRequest = requestBuilder.toString();
            String[] requestLines = fullRequest.split("\r\n");
            String[] requestParts = requestLines[0].split(" ");
            String type = requestParts[0];
            String path = requestParts[1];
            String directorioBase = "p1-files";
            // Set the output channel
            OutputStream sOutput = socket.getOutputStream();

            if (type.equals("GET") || type.equals("HEAD")) {

                Path ruta = Paths.get(directorioBase, path);

                File fich = new File(ruta.toAbsolutePath().toString());
                boolean modified = false;

                if (fich.exists()) {
                    DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
                    if (referenceDate != null) {

                        Instant lastModified = Instant.ofEpochMilli(fich.lastModified());
                        ZonedDateTime lastModifiedZonedDateTime = lastModified.atZone(ZoneId.of("GMT"));
                        String lastModifiedRFC1123 = formatter.format(lastModifiedZonedDateTime);
                        if (referenceDate.compareTo(lastModifiedRFC1123) < 0){
                            modified = true;

                        }else {
                            sOutput.write("HTTP/1.0 304 Not Modified\r\n".getBytes());
                            sOutput.write(("Date: " + formatter.format(ZonedDateTime.now(ZoneId.of("GMT"))) + "\r\n").getBytes());
                            sOutput.write("Server: localhost\r\n".getBytes());
                            sOutput.write("\r\n".getBytes()); // Línea vacía entre cabeceras y contenido
                        }
                    }

                    if (referenceDate == null || modified){
                        byte[] contentFile = Files.readAllBytes(ruta);

                        sOutput.write("HTTP/1.0 200 OK\r\n".getBytes());
                        sOutput.write(("Date: " + formatter.format(ZonedDateTime.now(ZoneId.of("GMT"))) + "\r\n").getBytes());
                        sOutput.write("Server: localhost\r\n".getBytes());
                        sOutput.write(("Content-Length: " + contentFile.length + "\r\n").getBytes());
                        sOutput.write(("Content-Type: " + getFileType(ruta) + "\r\n").getBytes());
                        sOutput.write(("Last-Modified: " + formatter.format((new Date(fich.lastModified()).toInstant()).atZone(ZoneId.of("GMT"))) + "\r\n").getBytes());
                        sOutput.write("\r\n".getBytes()); // Línea vacía entre cabeceras y contenido
                        if (type.equals("GET")) {
                            sOutput.write(contentFile);
                        }
                    }

                } else {
                    // The resource not exist
                     Path ruta404 = Paths.get(directorioBase, "error404.html");
                     byte[] contentFile = Files.readAllBytes(ruta404);
                    sOutput.write("HTTP/1.0 404 Not Found\r\n".getBytes());
                    sOutput.write(("Date: " + new Date() + "\r\n").getBytes());
                    sOutput.write(("Content-Length: " + contentFile.length + "\r\n").getBytes());
                    sOutput.write(("Content-Type: " + getFileType(ruta404) + "\r\n").getBytes());
                    sOutput.write("Server: localhost\r\n".getBytes());
                    sOutput.write("\r\n".getBytes()); // Línea vacía entre cabeceras y contenido
                    if (type.equals("GET")) {
                        sOutput.write(contentFile);
                    }
                }

            } else {
                // The type is not GET or HEAD
                Path ruta400 = Paths.get(directorioBase, "error400.html");
                byte[] contentFile = Files.readAllBytes(ruta400);
                sOutput.write("HTTP/1.0 400 Bad Request\r\n".getBytes());
                sOutput.write(("Date: " + new Date() + "\r\n").getBytes());
                sOutput.write(("Content-Length: " + contentFile.length + "\r\n").getBytes());
                sOutput.write(("Content-Type: " + getFileType(ruta400) + "\r\n").getBytes());
                sOutput.write("Server: localhost\r\n".getBytes());
                sOutput.write("\r\n".getBytes()); // Línea vacía entre cabeceras y contenido
                sOutput.write(contentFile);
            }
            // HTTP responses
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in time");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            // Close the client socket
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Closing socket error: " + e.getMessage());
            }
        }
    }
    private static String getFileType(Path path) {
            String fileName = path.getFileName().toString().toLowerCase();
            if (fileName.endsWith(".txt")) {
                return "text/plain";
            } else if (fileName.endsWith(".jpg")) {
                return "image/jpeg";
            } else if (fileName.endsWith(".png")) {
                return "image/png";
            } else if (fileName.endsWith(".gif")) {
                return "image/gif";
            } else if (fileName.endsWith(".html")) {
                return "text/html";
            } else {
                return "application/octet-stream";
            }
    }
}
