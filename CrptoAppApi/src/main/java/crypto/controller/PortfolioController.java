package crypto.controller;

import crypto.dto.LoginParam;
import crypto.dto.WithdrawDepositParam;
import crypto.entity.Portfolio;
import crypto.entity.User;
import crypto.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PortfolioController extends ControllerBase{

    @Autowired
    CryptoService service;

    @GetMapping("/{userId}/getportfolio")
    public ResponseEntity<Portfolio> getPortfolio(@PathVariable int userId) {
        Portfolio portfolio = service.getPortfolio(userId);

        if(portfolio == null) {
            return new ResponseEntity("Portfolio not found.", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(portfolio);
    }

    @PutMapping("/{userId}/createportfolio")
    public Portfolio createPortfolio(@PathVariable int userId) {
        return null;
    }

    @PutMapping("/{userId}/deposit")
    public ResponseEntity<Portfolio> depositNonInvestedBalance(@PathVariable int userId, @RequestBody WithdrawDepositParam deposit) {
        Portfolio portfolio = service.inputNonInvestedBalance(userId, deposit.getValue());

        if(portfolio == null) {
            return new ResponseEntity("Portfolio not found.", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(portfolio);
    }
    @PutMapping("/{userId}/withdraw")
    public ResponseEntity<Portfolio> withdrawNonInvestedBalance(@PathVariable int userId, @RequestBody WithdrawDepositParam withdraw) {
        Portfolio portfolio = service.withdrawFromNonInvBal(userId, withdraw.getValue());

        if(portfolio == null) {
            return new ResponseEntity("Portfolio not found.", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(portfolio);
    }
}
