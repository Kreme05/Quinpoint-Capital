package com.quinpoint;

import com.quinpoint.model.Advisor;
import com.quinpoint.model.Client;
import com.quinpoint.model.Message;
import com.quinpoint.service.CsvLoaderService;
import com.quinpoint.service.PortfolioService;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Quinpoint Capital - JavaFX Demo App
 */
public class AppFx extends Application {

    private CsvLoaderService csvLoader;
    private PortfolioService portfolioService;
    private NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.UK);

    @Override
    public void start(Stage stage) {
        try {
            csvLoader = new CsvLoaderService("src/main/resources/data");
            csvLoader.loadAll();
            portfolioService = new PortfolioService(csvLoader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        showLogin(stage); // start with login page
    }

    // ------------------- LOGIN PAGE -------------------
    private void showLogin(Stage stage) {
        VBox form = new VBox(15);
        form.setPadding(new Insets(40));
        form.setAlignment(Pos.CENTER);

        Label logo = new Label("Quinpoint Capital");
        logo.setStyle("-fx-font-size:22px; -fx-font-weight:bold;");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");

        // file upload
        Button uploadBtn = new Button("Upload ID");
        Label fileLabel = new Label("No file selected");
        final File[] uploadedFile = new File[1];
        uploadBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                uploadedFile[0] = file;
                fileLabel.setText("ID Uploaded: " + file.getName());
            }
        });

        // signature canvas
        Label sigLabel = new Label("Draw Signature Below:");
        Canvas signatureCanvas = new Canvas(200, 100);
        GraphicsContext gc = signatureCanvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, signatureCanvas.getWidth(), signatureCanvas.getHeight());

        signatureCanvas.setOnMousePressed(e -> {
            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
            gc.stroke();
        });
        signatureCanvas.setOnMouseDragged(e -> {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });

        Button clearSigBtn = new Button("Clear Signature");
        clearSigBtn.setOnAction(e -> {
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, signatureCanvas.getWidth(), signatureCanvas.getHeight());
        });

        StackPane signatureBox = new StackPane(signatureCanvas);
        signatureBox.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        signatureBox.setPadding(new Insets(2));

        HBox sigRow = new HBox(10, signatureBox, clearSigBtn);
        sigRow.setAlignment(Pos.CENTER);

        // login button
        Button loginBtn = new Button("Login");
        Label msg = new Label();

        loginBtn.setOnAction(e -> {
            if (nameField.getText().isBlank() || emailField.getText().isBlank()) {
                msg.setText("Please enter your details.");
                return;
            }
            if (uploadedFile[0] == null) {
                msg.setText("Please upload your ID.");
                return;
            }
            try {
                String fullName = nameField.getText().trim();
                String email = emailField.getText().trim();

                Client client = csvLoader.findClientByNameEmail(fullName, email);
                Advisor advisor = csvLoader.findAdvisorByNameEmail(fullName, email);

                if (client != null) {
                    AuthContext.getInstance().login(client.getId(), client.getName(), client.getEmail());
                    showMainApp(stage);
                } else if (advisor != null) {
                    AuthContext.getInstance().login(advisor.getId(), advisor.getName(), advisor.getEmail());
                    showMainApp(stage);
                } else {
                    msg.setText("Details not found in database.");
                }
            } catch (Exception ex) {
                msg.setText("Error: " + ex.getMessage());
            }
        });

        form.getChildren().addAll(
                logo, nameField, emailField,
                uploadBtn, fileLabel,
                sigLabel, sigRow,
                loginBtn, msg
        );

        Scene loginScene = new Scene(form, 500, 550);
        stage.setScene(loginScene);
        stage.setTitle("Login - Quinpoint Capital");
        stage.show();
    }

    // ------------------- MAIN APP WITH SIDEBAR -------------------
    private void showMainApp(Stage stage) {
        if (!AuthContext.getInstance().isAuthenticated()) {
            showLogin(stage);
            return;
        }

        BorderPane root = new BorderPane();
        StackPane contentWrapper = new StackPane();

        VBox sidebar = createSidebar(stage, contentWrapper);
        root.setLeft(sidebar);
        root.setCenter(contentWrapper);

        // default page after login
        contentWrapper.getChildren().setAll(createPortfolioPage());

        Scene mainScene = new Scene(root, 1100, 650);
        stage.setScene(mainScene);
        stage.setTitle("Quinpoint Capital");
        stage.show();
    }

    // sidebar with buttons
    private VBox createSidebar(Stage stage, StackPane contentWrapper) {
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: #d0e6fa;");

        AuthContext auth = AuthContext.getInstance();

        Label logo = new Label("Quinpoint");
        logo.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");
        Label userLabel = new Label(auth.getUserName() + "\n" + auth.getUserEmail());

        Button portfolioBtn = styledButton("Portfolio");
        portfolioBtn.setOnAction(e -> contentWrapper.getChildren().setAll(createPortfolioPage()));
        Button performanceBtn = styledButton("Performance");
        performanceBtn.setOnAction(e -> contentWrapper.getChildren().setAll(createPerformancePage()));
        Button transactionsBtn = styledButton("Transactions");
        transactionsBtn.setOnAction(e -> contentWrapper.getChildren().setAll(createTransactionsPage()));
        Button settingsBtn = styledButton("Settings");
        settingsBtn.setOnAction(e -> contentWrapper.getChildren().setAll(createSettingsPage()));
        Button messagingBtn = styledButton("Messaging");
        messagingBtn.setOnAction(e -> contentWrapper.getChildren().setAll(createMessagingPage()));
        Button logoutBtn = styledButton("Logout");
        logoutBtn.setOnAction(e -> {
            AuthContext.getInstance().logout();
            showLogin(stage);
        });

        sidebar.getChildren().addAll(logo, userLabel,
                portfolioBtn, performanceBtn, transactionsBtn,
                settingsBtn, messagingBtn, logoutBtn);

        return sidebar;
    }

    // simple styled button for sidebar
    private Button styledButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color: #e6f0fa;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: #c0d8f5;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: #e6f0fa;"));
        return b;
    }

    // ------------------- PORTFOLIO PAGE -------------------
    private VBox createPortfolioPage() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(20));

        Label header = new Label("Investment Portfolio");
        header.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        // total portfolio value card
        VBox valueCard = new VBox(10);
        valueCard.setPadding(new Insets(15));
        valueCard.setStyle("-fx-background-color:white; -fx-background-radius:10; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8,0,0,2);");

        BigDecimal total = portfolioService.getPortfolioValue(AuthContext.getInstance().getUserId());
        Label totalValue = new Label(currency.format(total));
        totalValue.setStyle("-fx-font-size:28px; -fx-font-weight:bold;");
        Label change = new Label("+£950.00   +2.2%");
        change.setStyle("-fx-text-fill: green;");
        valueCard.getChildren().addAll(new Label("Total Value"), totalValue, change);

        // pie chart card
        VBox pieCard = new VBox(10);
        pieCard.setPadding(new Insets(15));
        pieCard.setStyle("-fx-background-color:white; -fx-background-radius:10; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8,0,0,2);");
        PieChart pie = new PieChart();
        pie.getData().add(new PieChart.Data("Stocks", 60));
        pie.getData().add(new PieChart.Data("Bonds", 25));
        pie.getData().add(new PieChart.Data("Real Estate", 10));
        pie.getData().add(new PieChart.Data("Cash", 5));
        pieCard.getChildren().addAll(new Label("Holdings Breakdown"), pie);

        // daily returns + add funds
        VBox returnsCard = new VBox(10);
        returnsCard.setPadding(new Insets(15));
        returnsCard.setStyle("-fx-background-color:white; -fx-background-radius:10; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8,0,0,2);");
        Label returnsHeader = new Label("Daily Returns");
        Button addFunds = new Button("Add Funds");
        addFunds.setOnAction(e -> showToast((Stage) page.getScene().getWindow(), "Feature coming soon!"));
        VBox dailyList = new VBox(5);
        dailyList.getChildren().addAll(
                makeReturnRow("24 Apr", 1500),
                makeReturnRow("16 Apr", 2200),
                makeReturnRow("05 Apr", -11200)
        );
        returnsCard.getChildren().addAll(returnsHeader, addFunds, dailyList);

        // line chart
        VBox chartCard = new VBox(10);
        chartCard.setPadding(new Insets(15));
        chartCard.setStyle("-fx-background-color:white; -fx-background-radius:10; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8,0,0,2);");
        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();
        LineChart<Number, Number> chart = new LineChart<>(x, y);
        chart.setTitle("Portfolio Value Over Time");
        chartCard.getChildren().addAll(new Label("Portfolio Value"), chart);

        grid.add(valueCard, 0, 0);
        grid.add(pieCard, 1, 0);
        grid.add(returnsCard, 0, 1);
        grid.add(chartCard, 1, 1);

        page.getChildren().addAll(header, grid);
        return page;
    }

    // helper for daily return row
    private HBox makeReturnRow(String date, int amount) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label d = new Label(date);
        Label amt = new Label((amount >= 0 ? "+" : "") + "£" + Math.abs(amount));
        amt.setStyle("-fx-font-weight:bold;" + (amount >= 0 ? "-fx-text-fill: green;" : "-fx-text-fill: red;"));
        row.getChildren().addAll(d, amt);
        return row;
    }

    // ------------------- PERFORMANCE PAGE -------------------
    private VBox createPerformancePage() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(20));
        page.getChildren().addAll(
                new Label("Performance Analytics"),
                new Label("• 1 Week: +1.8%"),
                new Label("• 1 Month: +4.2%"),
                new Label("• 1 Year: +18.9%"),
                new Label("Risk Analysis: Medium, Sharpe Ratio 1.42")
        );
        return page;
    }

    // ------------------- TRANSACTIONS PAGE -------------------
    private VBox createTransactionsPage() {
        VBox page = new VBox(15);
        page.setPadding(new Insets(20));
        page.getChildren().addAll(
                new Label("Transactions"),
                new Label("Total Credits: £15,000"),
                new Label("Total Debits: £9,800"),
                new Label("GDPR: Records stored securely")
        );
        return page;
    }

    // ------------------- SETTINGS PAGE -------------------
    private VBox createSettingsPage() {
        VBox page = new VBox(15);
        page.setPadding(new Insets(20));
        TextField name = new TextField(AuthContext.getInstance().getUserName());
        TextField email = new TextField(AuthContext.getInstance().getUserEmail());
        Button save = new Button("Save Changes");
        save.setOnAction(e -> showToast((Stage) page.getScene().getWindow(), "Settings saved!"));
        page.getChildren().addAll(new Label("Settings"), new Label("Name:"), name, new Label("Email:"), email, save);
        return page;
    }

    // ------------------- MESSAGING PAGE -------------------
    private VBox createMessagingPage() {
        VBox page = new VBox(10);
        page.setPadding(new Insets(20));

        Label title = new Label("Messages");
        ListView<String> messages = new ListView<>();

        // load messages from csv
        csvLoader.loadMessagesForUser(AuthContext.getInstance().getUserId())
                .forEach(m -> messages.getItems().add(m.getSenderLabel() + ": " + m.getText()));

        TextField input = new TextField();
        input.setPromptText("Type a message...");
        Button emoji = new Button("😊");
        Button send = new Button("Send");

        send.setOnAction(e -> {
            if (!input.getText().isBlank()) {
                String text = input.getText();
                String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                Message m = new Message(AuthContext.getInstance().getUserId(), ts, "You", text);
                try {
                    csvLoader.saveMessage(m);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                messages.getItems().add("You (" + ts + "): " + text);
                input.clear();
            }
        });

        emoji.setOnAction(e -> input.appendText("😊"));

        HBox row = new HBox(5, input, emoji, send);
        row.setAlignment(Pos.CENTER_LEFT);

        page.getChildren().addAll(title, messages, row, new Label("All messages are encrypted and GDPR compliant"));
        return page;
    }

    // ------------------- TOAST NOTIFICATIONS -------------------
    private void showToast(Stage stage, String message) {
        Label toast = new Label(message);
        toast.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 5;");
        StackPane root = (StackPane) stage.getScene().getRoot();
        root.getChildren().add(toast);
        StackPane.setAlignment(toast, Pos.TOP_CENTER);

        new Thread(() -> {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> root.getChildren().remove(toast));
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
