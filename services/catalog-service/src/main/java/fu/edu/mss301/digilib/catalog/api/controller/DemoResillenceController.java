package fu.edu.mss301.digilib.catalog.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog/demo")
public class DemoResillenceController {
    // API này cố tình delay 5 giây để test Timeout & Retry
    @GetMapping("/slow")
    public String slowEndpoint() throws InterruptedException {
        System.out.println("Nhận được request vào lúc: " + System.currentTimeMillis());
        Thread.sleep(5000);
        return "Success từ Catalog Service (nhưng rất chậm)";
    }
}
