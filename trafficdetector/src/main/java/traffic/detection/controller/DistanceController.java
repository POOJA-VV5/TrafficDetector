package traffic.detection;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import traffic.detection.service.DistanceService;

@Controller
public class DistanceController {

    @Autowired
    private DistanceService distanceService;

    @PostMapping("/trafficSeverity")
    @ResponseBody
    public String getTrafficSeverity(@RequestParam String origin,
                                     @RequestParam String destination,
                                     @RequestParam String departure_time) {
        return distanceService.getTrafficSeverity(origin, destination, departure_time);
    }

    @GetMapping("/trafficSeverity")
    @ResponseBody
    public String getTrafficSeverityGet(@RequestParam String origin,
                                        @RequestParam String destination,
                                        @RequestParam String departure_time) {
        return distanceService.getTrafficSeverity(origin, destination, departure_time);
    }
}

