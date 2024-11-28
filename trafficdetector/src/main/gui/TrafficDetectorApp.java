import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TrafficDetectorApp {

    private static Image backgroundImage;

    public static void main(String[] args) {
        // Load the background image
        try {
            File imgFile = new File("C:/Users/Dell/Downloads/trafficdetector/trafficdetector/src/main/gui/background.jpg");
            backgroundImage = ImageIO.read(imgFile);
        } catch (IOException e) {
            System.out.println("Error loading image: " + e.getMessage());
        }

        // Create and show the GUI
        SwingUtilities.invokeLater(TrafficDetectorApp::createAndShowGUI);
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Traffic Detector");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);

        // Create a custom JPanel to paint the background
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        // Use GridBagLayout to align components in a grid
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add space between components

        // Create text fields and other components
        JTextField originField = new JTextField(20);
        JTextField destinationField = new JTextField(20);
        JButton detectTrafficButton = new JButton("Detect Traffic");
        JTextArea resultArea = new JTextArea(10, 30);  // Increased the height of the result box (10 rows)
        resultArea.setEditable(false);

        // Set font and text color for visibility on a dark background
        originField.setForeground(Color.BLACK);  // Set user input text color to black
        destinationField.setForeground(Color.BLACK);
        resultArea.setForeground(Color.BLACK);  // Set result text color to bright white
        detectTrafficButton.setForeground(Color.BLACK);
        detectTrafficButton.setBackground(Color.LIGHT_GRAY);

        // Set background color to white for text fields
        originField.setBackground(Color.WHITE);
        destinationField.setBackground(Color.WHITE);
        resultArea.setBackground(Color.WHITE);

        // Set font for text fields and button
        originField.setFont(new Font("Arial", Font.BOLD, 18));  // Larger font size
        destinationField.setFont(new Font("Arial", Font.BOLD, 18));  // Larger font size
        detectTrafficButton.setFont(new Font("Arial", Font.BOLD, 18));  // Larger font size
        resultArea.setFont(new Font("Arial", Font.BOLD, 18));  // Larger font size

        // Set the label text color to white and increase font size
        JLabel originLabel = new JLabel("Origin:");
        originLabel.setForeground(Color.WHITE);
        originLabel.setFont(new Font("Arial", Font.BOLD, 20));  // Larger font size
        JLabel destinationLabel = new JLabel("Destination:");
        destinationLabel.setForeground(Color.WHITE);
        destinationLabel.setFont(new Font("Arial", Font.BOLD, 20));  // Larger font size
        JLabel resultLabel = new JLabel("Result:");
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));  // Larger font size

        // Create boxes to hold the fields
        JPanel originPanel = new JPanel();
        originPanel.setBackground(Color.BLACK);
        originPanel.setLayout(new BorderLayout());
        originPanel.add(originLabel, BorderLayout.WEST);
        originPanel.add(originField, BorderLayout.CENTER);

        JPanel destinationPanel = new JPanel();
        destinationPanel.setBackground(Color.BLACK);
        destinationPanel.setLayout(new BorderLayout());
        destinationPanel.add(destinationLabel, BorderLayout.WEST);
        destinationPanel.add(destinationField, BorderLayout.CENTER);

        JPanel resultPanel = new JPanel();
        resultPanel.setBackground(Color.BLACK);
        resultPanel.setLayout(new BorderLayout());
        resultPanel.add(resultLabel, BorderLayout.WEST);
        resultPanel.add(resultArea, BorderLayout.CENTER);  // Added resultArea without JScrollPane

        // Add components to the panel with GridBagLayout constraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(originPanel, gbc); // Place origin panel in row 0, spanning columns 0 and 1

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(destinationPanel, gbc); // Place destination panel in row 1, spanning columns 0 and 1

        // Center the Detect Traffic button in the next row (it won't move)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Make the button span across both columns
        panel.add(detectTrafficButton, gbc); // Place Detect Traffic button in row 2, spanning columns 0 and 1

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(resultPanel, gbc); // Place result panel in row 3, spanning columns 0 and 1

        // Action listener for the Detect Traffic button
        detectTrafficButton.addActionListener(e -> {
            String origin = originField.getText();
            String destination = destinationField.getText();
            String result = fetchTrafficData(origin, destination);
            resultArea.setText(result);
        });

        // Set the panel as the content pane of the frame
        frame.setContentPane(panel);

        frame.setLocationRelativeTo(null);  // Center the window
        frame.setVisible(true);
    }

    private static String fetchTrafficData(String origin, String destination) {
        String apiUrl = "http://localhost:8080/trafficSeverity?origin=" + origin + "&destination=" + destination + "&departure_time=now";
        StringBuilder result = new StringBuilder();

        try {
            // Create a URL object from the API endpoint
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Read the response from the API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            // Log the full response for debugging
            System.out.println("API Response: " + result.toString());

            // Parse the JSON response
            JSONObject responseJson = new JSONObject(result.toString());

            // Check the actual data types for each field and fetch accordingly
            String distance = String.valueOf(responseJson.get("distance"));  // Can be an integer, double, or string
            String durationWithoutTraffic = responseJson.optString("durationWithoutTraffic", "N/A");  // Default to "N/A" if not found
            String durationWithTraffic = responseJson.optString("durationWithTraffic", "N/A"); // Default to "N/A" if not found
            String trafficSeverity = responseJson.optString("traffic_severity", "Moderate"); // Default if not found

            // Construct the result with proper formatting
            return String.format("Origin: %s\nDestination: %s\nDistance: %s\nDuration (Without Traffic): %s\nDuration (With Traffic): %s\nTraffic Severity: %s",
                    origin, destination, distance, durationWithoutTraffic, durationWithTraffic, trafficSeverity);

        } catch (IOException ex) {
            ex.printStackTrace();
            return "Error fetching data from the API.";
        }
    }
}


