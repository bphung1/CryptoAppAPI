package crypto.controller;


import crypto.entity.Investment;
import crypto.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class InvestmentsController extends ControllerBase {
    @Autowired
    private CryptoService service;
    @GetMapping("/getInvestments/{portfolioId}")
    public ResponseEntity<List<Investment>> getInvestments(@PathVariable int portfolioId){
        List<Investment> investmentList=service.getInvestmentsByPortfolioId(portfolioId);
        if(investmentList == null){
            return new ResponseEntity("user does not exist", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(investmentList);
    }

}
