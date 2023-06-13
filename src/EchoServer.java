import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {
    private final int port;
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public EchoServer(int port) {
        this.port = port;
    }

    public static EchoServer bindToPort(int port){
        return new EchoServer(port);
    }

    public void run(){
        try(var server = new ServerSocket(port)){
            while (!server.isClosed()){
                Socket clientSocket = server.accept();
                pool.submit(() -> handle(clientSocket));
            }
        }catch (IOException e){
            System.out.printf("Вероятнее всего порт %s занят.%n", port);
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) {
        System.out.printf("Connected new user: %s%n,", socket);
        try (socket;
             var reader = getReader(socket);
             PrintWriter writer =getWriter(socket)){
            while (true){
                var message = reader.nextLine().strip();
                System.out.printf("Got: %s%n", message);
                if(isEmptyMsg(message) || isQuiteMsg(message)){
                    break;
                }
                sendResponse(message, writer);
            }
        }catch (NoSuchElementException e) {
            System.out.println("Client dropped connection");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private PrintWriter getWriter(Socket socket) throws IOException{
        OutputStream out = socket.getOutputStream();
        return new PrintWriter(out);
    }

    private Scanner getReader(Socket socket) throws IOException{
        InputStream input = socket.getInputStream();
        InputStreamReader reader = new InputStreamReader(input, "UTF-8");
        return new Scanner(reader);
    }

    private Boolean isQuiteMsg(String msg){
        return "bye".equalsIgnoreCase(msg);
    }

    private Boolean isEmptyMsg(String msg){
        return msg == null || msg.isBlank();
    }

    private void sendResponse(String response, Writer writer) throws IOException{
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();

    }
}
