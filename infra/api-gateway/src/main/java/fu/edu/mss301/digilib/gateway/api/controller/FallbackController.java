package fu.edu.mss301.digilib.gateway.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/catalog")
    public String catalogFallback() {
        return "Catalog Service hiện đang gặp sự cố. Trả về dữ liệu Fallback mặc định.";
    }
}
