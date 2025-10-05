package com.quinpoint.service;

import com.quinpoint.model.Advisor;
import com.quinpoint.model.Client;
import com.quinpoint.model.Holding;
import com.quinpoint.model.Message;
import com.quinpoint.model.PricePoint;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Simple CSV loader and message persistence.
 * Expects a data directory path in constructor.
 */
public class CsvLoaderService {

    private final Path dataDir;
    private final Map<String, Client> clients = new HashMap<>();
    private final Map<String, Advisor> advisors = new HashMap<>();
    private final Map<String, List<Holding>> holdings = new HashMap<>();
    private final Map<String, List<PricePoint>> prices = new HashMap<>();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Path messagesFile;

    public CsvLoaderService(String dataFolder) {
        this.dataDir = Path.of(dataFolder);
        this.messagesFile = dataDir.resolve("messages.csv");
    }

    public void loadAll() throws IOException {
        loadClients();
        loadAdvisors();
        loadHoldings();
        loadPrices();
        // messages file is optional; loadMessagesForUser reads it when needed
    }

    // clients.csv: Client ID,First Name,Last Name,Email,Risk Score
    private void loadClients() throws IOException {
        Path f = dataDir.resolve("clients.csv");
        if (!Files.exists(f)) return;
        try (BufferedReader br = Files.newBufferedReader(f)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue;
                String id = parts[0].trim();
                String first = parts[1].trim();
                String last = parts[2].trim();
                String email = parts[3].trim();
                int risk = Integer.parseInt(parts[4].trim());
                clients.put(id, new Client(id, first, last, email, risk));
            }
        }
    }

    // advisors.csv: Advisor ID,First Name,Last Name,Email,Desk
    private void loadAdvisors() throws IOException {
        Path f = dataDir.resolve("advisors.csv");
        if (!Files.exists(f)) return;
        try (BufferedReader br = Files.newBufferedReader(f)) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue;
                String id = parts[0].trim();
                String first = parts[1].trim();
                String last = parts[2].trim();
                String email = parts[3].trim();
                String desk = parts[4].trim();
                advisors.put(id, new Advisor(id, first, last, email, desk));
            }
        }
    }

    // portfolio_holdings/Pxxx.csv: Client ID,Instrument ID,Quantity
    private void loadHoldings() throws IOException {
        Path dir = dataDir.resolve("portfolio_holdings");
        if (!Files.exists(dir)) return;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.csv")) {
            for (Path f : stream) {
                String clientId = f.getFileName().toString().replace(".csv", "");
                List<Holding> list = new ArrayList<>();

                try (BufferedReader br = Files.newBufferedReader(f)) {
                    br.readLine(); // skip header
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length < 4) continue;

                        String instrumentId = parts[0].trim();
                        LocalDate datePurchased = LocalDate.parse(parts[1].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        BigDecimal initialValue = new BigDecimal(parts[2].replace("\"", "").replace(",", "").trim());
                        BigDecimal qty = new BigDecimal(parts[3].trim());

                        Holding h = new Holding(clientId, instrumentId, qty, datePurchased, initialValue);
                        list.add(h);
                    }
                }

                holdings.put(clientId, list);
            }
        }
    }


    // prices/xxx.csv: Instrument ID,Date(yyyy-MM-dd),Close
    private void loadPrices() throws IOException {
        Path dir = dataDir.resolve("prices");
        if (!Files.exists(dir)) return;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.csv")) {
            for (Path f : stream) {
                String instrumentId = f.getFileName().toString().replace(".csv", "");
                List<PricePoint> list = new ArrayList<>();

                try (BufferedReader br = Files.newBufferedReader(f)) {
                    br.readLine(); // skip header
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length < 5) continue;

                        LocalDate d = LocalDate.parse(parts[0].trim(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                        BigDecimal close = new BigDecimal(parts[4].trim());
                        list.add(new PricePoint(instrumentId, d, close));
                    }
                }

                list.sort(Comparator.comparing(PricePoint::getDate));
                prices.put(instrumentId, list);
            }
        }
    }


    // --- Finders used at login ---
    public Client findClientByNameEmail(String fullName, String email) {
        String normalized = fullName.trim().toLowerCase();
        for (Client c : clients.values()) {
            String fn = (c.getFirstName() + " " + c.getLastName()).toLowerCase();
            if (fn.equals(normalized) && c.getEmail().equalsIgnoreCase(email)) return c;
        }
        return null;
    }

    public Advisor findAdvisorByNameEmail(String fullName, String email) {
        String normalized = fullName.trim().toLowerCase();
        for (Advisor a : advisors.values()) {
            String fn = (a.getFirstName() + " " + a.getLastName()).toLowerCase();
            if (fn.equals(normalized) && a.getEmail().equalsIgnoreCase(email)) return a;
        }
        return null;
    }

    // messages CSV format: userId,timestamp,senderLabel,text
    public List<Message> loadMessagesForUser(String userId) {
        List<Message> out = new ArrayList<>();
        if (!Files.exists(messagesFile)) return out;
        try (BufferedReader br = Files.newBufferedReader(messagesFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", 4); // split into at most 4 parts
                if (p.length < 4) continue;
                String uid = p[0].trim();
                if (!uid.equals(userId)) continue;
                String ts = p[1].trim();
                String sender = p[2].trim();
                String text = p[3].trim();
                out.add(new Message(uid, ts, sender, text));
            }
        } catch (IOException e) {
            // ignore for prototype
        }
        return out;
    }

    public void saveMessage(Message m) throws IOException {
        // ensure dir exists
        Files.createDirectories(messagesFile.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(messagesFile, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND)) {
            // safe CSV: userId,timestamp,sender,text
            String line = String.join(",", m.getUserId(), m.getTimestamp(), m.getSenderLabel(), m.getText());
            bw.write(line);
            bw.newLine();
        }
    }

    // basic getters used elsewhere
    public Map<String, List<PricePoint>> getPrices() { return prices; }
    public Map<String, List<Holding>> getHoldings() { return holdings; }

}
