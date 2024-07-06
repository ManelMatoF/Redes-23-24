package es.udc.redes.tutorial.udp.server;

import java.net.*;

/**
 * Implements a UDP echo server.
 */
public class UdpServer {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.udp.server.UdpServer <port_number>");
            System.exit(-1);
        }

        int portNumber = Integer.parseInt(argv[0]);

        DatagramSocket ServerSocket = null;
        try {
            // Create a server socket
            ServerSocket = new DatagramSocket(portNumber);
            // Set maximum timeout to 300 secs
            ServerSocket.setSoTimeout(300000);
            while (true) {
                // Prepare datagram for reception
                byte buf[] = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                // Receive the message
                ServerSocket.receive(packet);
                System.out.println("SERVER: Received "
                        + new String(packet.getData(), 0, packet.getLength())
                        + " from " + packet.getAddress().toString() + ":"
                        + packet.getPort());

                // Prepare datagram to send response
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                // Send response
                ServerSocket.send(packet);
                System.out.println("SERVER: Sending "
                        + new String(packet.getData()) + " to "
                        + packet.getAddress().toString() + ":"
                        + packet.getPort());
            }
          
        } catch (SocketTimeoutException e) {
            System.err.println("No requests received in 300 secs ");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close the socket
            assert ServerSocket != null;
            ServerSocket.close();
        }
    }
}
