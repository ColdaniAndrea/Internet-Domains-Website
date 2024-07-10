package it.unimib.sd2024;

import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import java.io.*;

public class Main {

    //files paths
    public static String usersPath = "data/user.json";
    public static String domainsPath = "data/domain.json";
    public static String ordersPath = "data/order.json";

    // listen socket
    public static final int PORT = 3030;

    // start the server
    public static void startServer() throws IOException {
        var server = new ServerSocket(PORT);

        System.out.println("Database listening at localhost:" + PORT);

        try {
            while (true)
                new Handler(server.accept()).start();
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            server.close();
        }
    }

    // client connection handler
    private static class Handler extends Thread {
        private Socket client;

        public Handler(Socket client) {
            this.client = client;
        }

        public void run() {
            try {
                var out = new PrintWriter(client.getOutputStream(), true);
                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.startsWith("IS_AVAILABLE")) {
                        if (availableDomain(inputLine.substring(13, inputLine.length() - 1)))
                            out.println("AVAILABLE");
                        else {
                            int ownerId = returnOwner(inputLine.substring(13, inputLine.length() - 1));
                            if (ownerId == -1)
                                out.println("ERROR");
                            else {
                                String infoUser[] = ownerInfo(ownerId);
                                if (infoUser == null)
                                    out.println("ERROR");
                                else {
                                    JSONObject obj = new JSONObject();
                                    obj.put("name", infoUser[0]);
                                    obj.put("surname", infoUser[1]);
                                    obj.put("email", infoUser[2]);
                                    out.println(obj);
                                }
                            }
                        }
                    } else if (inputLine.startsWith("REGISTER")) {

                        String[] infoDominio = parser(inputLine.substring(9, inputLine.length() - 1), 8);

                        // check if user id exists
                        if (!existingUser(Integer.parseInt(infoDominio[2])))
                            out.println("NO_USER");
                        else {
                            // today date
                            LocalDate today = LocalDate.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String todayString = today.format(formatter);

                            // expiring date
                            today = today.plusYears(Integer.parseInt(infoDominio[1]));
                            String expiringString = today.format(formatter);

                            Domain dominio = new Domain(infoDominio[0], todayString,
                                    expiringString, Integer.parseInt(infoDominio[2]),
                                    Integer.parseInt(infoDominio[3]));

                            boolean register = registerOrder(dominio, "Registrazione",
                                    Integer.parseInt(infoDominio[3]),
                                    Integer.parseInt(infoDominio[2]), infoDominio[4], infoDominio[5],
                                    Integer.parseInt(infoDominio[6]), infoDominio[7]);
                            if (!register)
                                out.println("UNSUCCESS");

                            boolean success = addToFile(dominio, domainsPath);

                            if (success)
                                out.println("SUCCESS");
                            else
                                out.println("UNSUCCESS");
                        }

                    } else if (inputLine.startsWith("RETURN_DOMAINS")) {
                        String[] infoDomains = parser(inputLine.substring(15, inputLine.length() - 1), 1);

                        JSONArray orders = returnObject(domainsPath, Integer.parseInt(infoDomains[0]));
                        if (orders == null)
                            out.println("NOT_FOUND");
                        else
                            out.println(orders);
                    } else if (inputLine.startsWith("RETURN_ORDERS")) {
                        String[] infoOrders = parser(inputLine.substring(14, inputLine.length() - 1), 1);

                        JSONArray orders = returnObject(ordersPath, Integer.parseInt(infoOrders[0]));
                        if (orders == null)
                            out.println("NOT_FOUND");
                        else
                            out.println(orders);
                    } else if (inputLine.startsWith("RENEW")) {
                        String[] renewInfo = parser(inputLine.substring(6, inputLine.length() - 1), 7);

                        if (availableDomain(renewInfo[0]))
                            out.println("NOT_EXISTS");
                        else {
                            int success = renew(renewInfo[0], Integer.parseInt(renewInfo[1]),
                                    Integer.parseInt(renewInfo[2]), renewInfo[3], renewInfo[4],
                                    Integer.parseInt(renewInfo[5]), renewInfo[6]);

                            if (success == 1)
                                out.println("SUCCESS");
                            else if (success == 0)
                                out.println("UNSUCCESS");
                            else
                                out.println("CANNOT_RENEW");
                        }
                    } else if (inputLine.startsWith("NEW_USER")) {
                        String[] infoUser = parser(inputLine.substring(9, inputLine.length() - 1), 4);
                        User user = new User(infoUser[0], infoUser[1], infoUser[2], Integer.parseInt(infoUser[3]));
                        boolean success;

                        if (!existingUser(Integer.parseInt(infoUser[3]))) {
                            success = addToFile(user, usersPath);
                            if (success)
                                out.println("CREATED");
                            else
                                out.println("NOT_CREATED");
                        } else
                            out.println("ALREADY_EXISTS");
                    } else if (inputLine.startsWith("LAST_ID")) {
                        int temp = lastId();
                        if (temp > -1)
                            out.println(temp);
                        else
                            out.println("ERROR");
                    } else if (inputLine.startsWith("IS_USER")) {
                        if (existingUser(Integer.parseInt(inputLine.substring(8, inputLine.length() - 1)))){
                            out.println("EXISTS");
                        }
                        else {
                            out.println("NOT_EXISTS");
                        }
                    }
                }

                in.close();
                out.close();
                client.close();

            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    // to return the owner of non available domain
    public static int returnOwner(String domain) {
        JSONArray domainsArray = readJson(domainsPath);

        if(domainsArray == null)
            return -1;
        else{
            // check domain
            for (int i = 0; i < domainsArray.size(); i++) {
                JSONObject domainObject = domainsArray.getJSONObject(i);
                if (domain.equals(domainObject.getString("dominio"))) {
                    return domainObject.getInt("idUtente");
                }
            }
            return -1;
        }
    }

    // return the string with info
    public static String[] ownerInfo(int id) {
        JSONArray domainsArray = readJson(usersPath);

        if(domainsArray == null)
            return null;
        else{
            // check user
            String[] infoUser = new String[3];
            for (int i = 0; i < domainsArray.size(); i++) {
                JSONObject domainObject = domainsArray.getJSONObject(i);
                if (id == domainObject.getInt("id")) {
                    infoUser[0] = domainObject.getString("name");
                    infoUser[1] = domainObject.getString("surname");
                    infoUser[2] = domainObject.getString("email");
                    return infoUser;
                }
            }
            return null;
        }
    }

    // return last used id associated to a user
    public static int lastId() {
        JSONArray userArray = readJson(usersPath);

        if(userArray == null)
            return -1;
        else{
            if (userArray.size() > 0) {
                // check last id
                JSONObject userObject = userArray.getJSONObject(userArray.size() - 1);
                return Integer.parseInt(userObject.getString("id"));
            } else
                return 0;
        }
    }

    // return true if user exists
    public static boolean existingUser(int user) {
        JSONArray userArray = readJson(usersPath);

        if(userArray == null)
            return false;
        else{
            // check user
            for (int i = 0; i < userArray.size(); i++) {
                JSONObject userObject = userArray.getJSONObject(i);
                if (user == Integer.parseInt(userObject.getString("id")))
                    return true;

            }
            return false;
        }
    }

    // to parse the input String (separator = $)
    public static String[] parser(String string, int nArgs) {
        String[] array = { "", "", "", "", "", "", "", "" };
        int x = 0;

        for (int i = 0; i < nArgs; i++) {
            while (x < string.length() && string.charAt(x) != '$') {
                array[i] += string.charAt(x);
                x++;
            }
            x++;
        }

        return array;
    }

    // to return user's orders/domains
    public static JSONArray returnObject(String fileName, int id) {
        JSONArray userArray1 = readJson(fileName);

        if(userArray1 == null)
            return null;
        else {
            JSONArray userArray2 = new JSONArray();

            // check user
            for (int i = 0; i < userArray1.size(); i++) {
                JSONObject userObject = userArray1.getJSONObject(i);
                if (id == Integer.parseInt(userObject.getString("idUtente")))
                    userArray2.add(userObject);
            }
            return userArray2;
        }
    }

    // renew an existing domain
    public static synchronized int renew(String domain, int years, int prezzo, String numeroCarta, String scadenza,
            int cvv, String intestatario) {
        
        JSONArray domainArray = readJson(domainsPath);
        
        if(domainArray == null)
            return 0;
        else{
            // check domain
            for (int i = 0; i < domainArray.size(); i++) {
                JSONObject domainObject = domainArray.getJSONObject(i);
                if (domain.equals(domainObject.getString("dominio"))) {
                    boolean success;
                    // change expire date
                    LocalDate date = LocalDate.parse(domainObject.getString("dataScadenza"));
                    date = date.plusYears(years);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String newDate = date.format(formatter);

                    // check if renewed for more than 10 years
                    if (date.isAfter(LocalDate.now().plusYears(10)))
                        return 2;

                    // create new domain instance to put into database
                    Domain newDomain = new Domain(domain, domainObject.getString("dataRegistrazione"),
                            newDate, Integer.parseInt(domainObject.getString("idUtente")),
                            prezzo);

                    success = removeFromFile(domainArray, domainsPath, i);

                    if (success) {
                        success = addToFile(newDomain, domainsPath);
                        if (success)
                            success = registerOrder(newDomain, "Rinnovo", prezzo,
                                    Integer.parseInt(domainObject.getString("idUtente")), numeroCarta, scadenza, cvv,
                                    intestatario);

                        if (success)
                            return 1;
                        else
                            return 1;
                    } else
                        return 0;
                }
            }
            return 0;
        }
    }

    // checks if domain is available
    public static boolean availableDomain(String domain) {
        JSONArray domainsArray = readJson(domainsPath);
        if(domainsArray == null)
            return false;
        else{
            // check domain
            for (int i = 0; i < domainsArray.size(); i++) {
                JSONObject domainObject = domainsArray.getJSONObject(i);
                if (domain.equals(domainObject.getString("dominio"))) {
                    LocalDate expireDate = LocalDate.parse(domainObject.getString("dataScadenza"));
                    if (expireDate.isAfter(LocalDate.now()))
                        return false;
                }
            }
            return true;
        }
    }

    // to write new Json object in a given file: used in REGISTER and NEW_USER
    public static synchronized boolean addToFile(Object obj, String fileName) {
        // extract jsonString
        String jsonString;
        
        try {
            jsonString = new String(Files.readAllBytes(Paths.get(fileName)));

            // convert into jsonArray
            JSONArray jsonArray = (JSONArray) JSONSerializer.toJSON(jsonString);

            // create object and insert into array
            JSONObject oggetto = JSONObject.fromObject(obj);
            jsonArray.add(oggetto);

            return writeFile(fileName, jsonArray.toString(1));
        } catch (IOException e) {
            return false;
        }
    }

    // to remove from the JSONArray (old) renewed domain
    public static synchronized boolean removeFromFile(JSONArray domainArray, String fileName, int index) {
        // remove object from array
        domainArray.remove(index);

        return writeFile(fileName, domainArray.toString(1));
    }

    // to register the order in the database/order.json
    public static synchronized boolean registerOrder(Domain dominio, String oggetto, int prezzo, int idUtente,
            String numeroCarta,
            String scadenza, int cvv, String intestatario) {

        JSONObject jsonCreditCard = JSONObject.fromObject(new CreditCard(numeroCarta, scadenza, cvv, intestatario));

        Order order = new Order(dominio.getDominio(), dominio.getDataRegistrazione(), oggetto, dominio.getPrezzo(),
                idUtente, jsonCreditCard);

        // read the order file
        JSONArray jsonArray = readJson(ordersPath);

        if(jsonArray == null)
                return false;
        else{
            // create order JsonObject
            JSONObject jsonObject = JSONObject.fromObject(order);

            // add it to json file
            jsonArray.add(jsonObject);

            // write on file the array
            return writeFile(ordersPath, jsonArray.toString(1));
        }

    }

    // to write in a given file something
    public static synchronized boolean writeFile(String fileName, String toWrite) {
        try {
            FileWriter file = new FileWriter(fileName);
            file.write(toWrite);
            file.flush();
            file.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // read the json file given as a path and return the corrisponding JSONArray
    public static JSONArray readJson(String fileName){
        // read from json
        StringBuilder jsonContent = new StringBuilder();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
            String in;
            while ((in = br.readLine()) != null) {
                jsonContent.append(in);
            }
            br.close();

            // convert into JsonArray
            JSONArray array = JSONArray.fromObject(jsonContent.toString());
            return array;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * Metodo principale di avvio del database.
     *
     * @param args argomenti passati a riga di comando.
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        startServer();
    }
}
