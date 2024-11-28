package traffic.detection.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DistanceService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiKey = "AIzaSyCAPBj6R29p4PUqLrIFIQqaDC9F6jo0vTo"; // Replace with your actual key
    private final String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json";

    public String getTrafficSeverity(String origin, String destination, String departure_time) {
        try {
            // Construct API URL
            String url = String.format("%s?origins=%s&destinations=%s&departure_time=%s&key=%s",
                    apiUrl, origin, destination, departure_time, apiKey);

            // Fetch API response
            String response = restTemplate.getForObject(url, String.class);
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.getString("status").equals("OK")) {
                JSONObject element = jsonResponse.getJSONArray("rows")
                        .getJSONObject(0)
                        .getJSONArray("elements")
                        .getJSONObject(0);

                // Get the duration values
                double durationValue = element.getJSONObject("duration").getDouble("value");
                double durationInTrafficValue = element.getJSONObject("duration_in_traffic").getDouble("value");
                double distanceValue = element.getJSONObject("distance").getDouble("value");

                // Calculate the traffic severity
                double diff = Math.abs((durationInTrafficValue - durationValue) / durationValue * 100);
                String trafficSeverity;
                if (diff < 10) {
                    trafficSeverity = "Light Traffic";
                } else if (diff <= 30) {
                    trafficSeverity = "Moderate Traffic";
                } else {
                    trafficSeverity = "Heavy Traffic";
                }

                // Create the result JSON object
                JSONObject result = new JSONObject();
                result.put("origin", jsonResponse.getJSONArray("origin_addresses").getString(0));
                result.put("destination", jsonResponse.getJSONArray("destination_addresses").getString(0));
                result.put("distance", distanceValue / 1000); // Convert to kilometers
                result.put("durationWithoutTraffic", formatDuration(durationInTrafficValue)); // Format duration without traffic
                result.put("durationWithTraffic", formatDuration(durationValue)); // Format duration with traffic
                result.put("traffic_severity", trafficSeverity);

                return result.toString();
            } else {
                return jsonResponse.toString();
            }
        } catch (Exception e) {
            return "Error fetching traffic data: " + e.getMessage();
        }
    }

    // Helper method to format duration in a readable format (e.g., hours and minutes)
    private String formatDuration(double durationInSeconds) {
        long hours = (long) (durationInSeconds / 3600);
        long minutes = (long) ((durationInSeconds % 3600) / 60);
        return String.format("%d hours %d minutes", hours, minutes);
    }
}



