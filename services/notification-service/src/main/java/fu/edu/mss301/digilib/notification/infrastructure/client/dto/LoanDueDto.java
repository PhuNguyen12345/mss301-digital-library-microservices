package fu.edu.mss301.digilib.notification.infrastructure.client.dto;

import java.time.LocalDate;

public record LoanDueDto(
        String loanId,
        String studentId,
        String studentEmail,
        String bookTitle,
        LocalDate dueDate) {
}
