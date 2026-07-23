package fu.edu.mss301.digilib.notification.infrastructure.client;

import fu.edu.mss301.digilib.notification.infrastructure.client.dto.LoanDueDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "loan-service", configuration = LoanServiceFeignConfig.class)
public interface LoanServiceClient {

    @GetMapping("/api/v1/loans/internal/due-soon")
    List<LoanDueDto> getLoansDueInDays(@RequestParam("days") int days);

    @GetMapping("/api/v1/loans/internal/overdue")
    List<LoanDueDto> getOverdueLoans();
}
