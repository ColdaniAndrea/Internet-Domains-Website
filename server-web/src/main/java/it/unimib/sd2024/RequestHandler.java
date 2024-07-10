package it.unimib.sd2024;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestHandler {
    private AtomicInteger lastId = new AtomicInteger(0);
    
    private static final String HOST_NAME = "localhost";
    private static final int PORT = 3030;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int annualPrice;
    private int anniScadenza;

    public int getAnniScadenza() {
        return anniScadenza;
    }

    public void setAnniScadenza(int anniScadenza) {
        this.anniScadenza = anniScadenza;
    }

    public int getAnnualPrice() {
        return annualPrice;
    }

    public void setAnnualPrice(int annualPrice) {
        this.annualPrice = annualPrice;
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public synchronized String newRequest(String command) throws IOException {
        StringBuilder jsonResponse = new StringBuilder();
        BufferedReader in = null;
        PrintWriter out = null;
        Socket socket = null;

        try {
            socket = new Socket(HOST_NAME, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(command);
            String responseLine;
            while ((responseLine = in.readLine()) != null) {
                jsonResponse.append(responseLine);
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResources(socket, in, out);
        }
        return jsonResponse.toString();
        }

    private void closeResources(Socket socket, BufferedReader in, PrintWriter out) {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (in != null) in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (out != null) out.close();
    }

    public AtomicInteger getLastId() {
        return lastId;
    }

    public void setLastId(AtomicInteger lastId) {
        this.lastId = lastId;
    }

    public int createUniqueId() {
        return lastId.incrementAndGet();
    }

    public int nuovoPrezzo() {
        annualPrice = (int) (Math.random()*10 + 10); 
        return annualPrice*anniScadenza;
    }
}
