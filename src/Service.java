import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Service {

    private  Map<Socket, Integer> storage = new HashMap<>();
    private int number;

    public  void handle(Socket socket) {
        setNumber(getNumber() + 1);
        System.out.printf("Connected new user: %s%n,", socket);
        storage.put(socket, getNumber());
        try (socket;
             var reader = getReader(socket);
             PrintWriter writer = getWriter(socket)){
            storage.put(socket, getNumber());
            while (true){
                var message = reader.nextLine().strip();
                if(isEmptyMsg(message) || isQuiteMsg(message)){
                    System.out.printf("%s left the chat", getNumber());
                }
                for (Socket connection: storage.keySet()) {
                    if(connection != storage){
                        sendResponse(storage.get(socket) + ")" + message, getWriter(connection)); }

                }
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
