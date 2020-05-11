package KompiuteriuTinklai_2;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POP3Client {

    private SSLSocket socket;
    private SSLSocketFactory sslSocketFactory;

    private BufferedReader reader;
    private BufferedWriter writer;

    public void connect(String host, int port) throws IOException {
        sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) sslSocketFactory.createSocket(host,port) ;

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String connectionStatus = reader.readLine();

        System.out.println("Connected to the host: " + connectionStatus);
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public void disconnect() throws IOException {
        if (!isConnected()) throw new IllegalStateException("You are already disconnected!");
        socket.close();
        socket = null;
        reader = null;
        writer = null;

        System.out.println("Disconnected from the host");
    }

    public String logIn(String username, String password) throws IOException {
        sendPOP3Command("USER " + username);
        String loginMessage = sendPOP3Command("PASS " + password);
        return loginMessage;
    }

    public void deleteMessage(int id) throws IOException {
        sendPOP3Command("DELE " + id);
    }

    public void logOut() throws IOException {
        sendPOP3Command("QUIT");
    }

    public int getNumberOfNewMessages() throws IOException {
        String response = sendPOP3Command("STAT");
        String[] values = response.split(" ");
        return Integer.parseInt(values[1]);
    }

    protected Message getMessage(int i) throws IOException {
        String pop3Response = sendPOP3Command("RETR " + i);
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        String headerName = null;

        if (pop3Response.startsWith("+OK message follows")) {
            while ((pop3Response = readPOP3ResponseLine()).length() != 0) {
                int colonPosition = pop3Response.indexOf(":");
                if (colonPosition != -1) headerName = pop3Response.substring(0, colonPosition);
                String headerValue;
                if (pop3Response.length() > colonPosition && colonPosition >= 0) {
                    headerValue = pop3Response.substring(colonPosition + 2);
                } else {
                    headerValue = "";
                }
                List<String> headerValues = headers.get(headerName);
                if (headerValues == null) {
                    headerValues = new ArrayList<String>();
                    headers.put(headerName, headerValues);
                }
                headerValues.add(headerValue);
            }

            StringBuilder bodyBuilder = new StringBuilder();
            while (!(pop3Response = readPOP3ResponseLine()).equals(".")) {
                bodyBuilder.append(pop3Response + "\n");
            }
            return new Message(i, headers, bodyBuilder.toString());
        }
        else {
            throw new IOException();
        }
    }

    public List<Message> getMessages() throws IOException {
        int numOfMessages = getNumberOfNewMessages();
        List<Message> messageList = new ArrayList<Message>();
        for (int i = 1; i <= numOfMessages; i++) {
            messageList.add(getMessage(i));
        }
        return messageList;
    }

    protected String sendPOP3Command(String command) throws IOException {
        if (command.startsWith("PASS")) System.out.println("Client --> Server: PASS ***");
        else System.out.println("Client --> Server: " + command);

        writer.write(command + "\n");
        writer.flush();
        
        String pop3Response = readPOP3ResponseLine();
        return pop3Response;
    }

    protected String readPOP3ResponseLine() throws IOException{
        String response = reader.readLine();
        System.out.println("Server --> Client: " + response);
        return response;
    }
}
