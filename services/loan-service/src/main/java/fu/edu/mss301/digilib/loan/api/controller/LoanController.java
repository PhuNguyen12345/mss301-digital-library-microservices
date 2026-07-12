package fu.edu.mss301.digilib.loan.api.controller;

import fu.edu.mss301.digilib.loan.api.dto.BorrowLoanRequest;
import fu.edu.mss301.digilib.loan.api.dto.LoanResponse;
import fu.edu.mss301.digilib.loan.api.dto.ReturnLoanRequest;
import fu.edu.mss301.digilib.loan.application.command.BorrowBookCommand;
import fu.edu.mss301.digilib.loan.application.usecase.BorrowBookUseCase;
import fu.edu.mss301.digilib.loan.application.usecase.ManageLoanUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/loan")
@RequiredArgsConstructor
public class LoanController {

    private final BorrowBookUseCase borrowBookUseCase;
    private final ManageLoanUseCase manageLoanUseCase;

    @PostMapping
    public ResponseEntity<LoanResponse> borrow(@Valid @RequestBody BorrowLoanRequest request) {
        LoanResponse response = LoanResponse.from(borrowBookUseCase.handle(new BorrowBookCommand(
                request.memberId(), request.bookId(), request.bookType(), request.idempotencyKey())));
        return ResponseEntity.created(URI.create("/api/loan/" + response.loanId())).body(response);
    }

    @PostMapping("/{loanId}/return")
    public LoanResponse returnBook(
            @PathVariable Long loanId,
            @Valid @RequestBody ReturnLoanRequest request) {
        return LoanResponse.from(manageLoanUseCase.returnBook(loanId, request.idempotencyKey()));
    }

    @PostMapping("/{loanId}/renew")
    public LoanResponse renew(
            @PathVariable Long loanId,
            @RequestHeader(name = "X-Actor-Id", defaultValue = "SYSTEM") String actorId) {
        return LoanResponse.from(manageLoanUseCase.renew(loanId, actorId));
    }

    @GetMapping("/{loanId}")
    public LoanResponse findById(@PathVariable Long loanId) {
        return LoanResponse.from(manageLoanUseCase.findById(loanId));
    }

    @GetMapping
    public Page<LoanResponse> findAll(Pageable pageable) {
        return manageLoanUseCase.findAll(pageable).map(LoanResponse::from);
    }

    @GetMapping("/member/{memberId}")
    public List<LoanResponse> findByMember(@PathVariable String memberId) {
        return manageLoanUseCase.findByMember(memberId).stream().map(LoanResponse::from).toList();
    }
}
