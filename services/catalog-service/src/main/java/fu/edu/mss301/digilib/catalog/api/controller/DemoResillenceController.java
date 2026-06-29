package fu.edu.mss301.digilib.catalog.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog/demo")
public class DemoResillenceController {
    private int retryCount = 0;

    // API này cố tình delay 5 giây để test Timeout & Retry
    @GetMapping("/slow")
    public String slowEndpoint() throws InterruptedException {
        retryCount++;
        System.out.println("⏳ [Catalog] Đã nhận được request lần thứ: " + retryCount + " vào lúc: " + java.time.LocalTime.now());
        
        // Reset lại biến đếm sau 4 lần (1 gốc + 3 retry) để lần demo sau chạy lại từ đầu
        if (retryCount >= 4) {
            retryCount = 0;
        }

        Thread.sleep(5000);
        return "Success từ Catalog Service (nhưng rất chậm)";
    }
}
