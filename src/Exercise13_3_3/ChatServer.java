package Exercise13_3_3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatServer {
    ArrayList<Client> clients;
    ServerSocket serverSocket;

    ChatServer(){
        clients = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(1234);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    void sendAll(String message){
        for(Client client : clients){
            client.receive(message);
        }
    }

    public void run(){
        while (true) {
            System.out.println("Waiting...");
            try {
                // ждем клиента
                Socket socket = serverSocket.accept();
                System.out.println("Client connected!");
                clients.add(new Client(socket, this));
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new ChatServer().run();
    }
}

class Client implements Runnable {
    Socket socket;
    PrintStream out;
    Scanner in;
    ChatServer chatServer;

    public Client(Socket socket, ChatServer chatServer) {
        this.socket = socket;
        this.chatServer = chatServer;
        new Thread(this).start();
    }

    void receive (String message){
        out.println(message);
    }

    @Override
    public void run() {
        try {
            // получаем потоки ввода и вывода
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            // создаем удобные средства ввода и вывода
             in = new Scanner(is);
             out = new PrintStream(os);

            // читаем из сети и пишем в сеть
            out.println("What's your name?");
            String name = in.nextLine();
            chatServer.sendAll(name + " joined to chat");
            String input = in.nextLine();
            while (!input.equals("bye")) {
                chatServer.sendAll(name + ": " + input);
                input = in.nextLine();
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
