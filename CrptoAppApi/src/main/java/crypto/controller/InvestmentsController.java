package crypto.controller;


import crypto.dto.CoinMarkets;
import crypto.entity.Investment;
import crypto.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/getCryptos")
    public ResponseEntity<List<CoinMarkets>> getCrypto() {
        try {

            List<CoinMarkets> coinMarkets = service.rateForCrypto();

            return ResponseEntity.ok(coinMarkets);

        } catch (Exception ex) {
            return new ResponseEntity("Crypto symbol does not exist", HttpStatus.NOT_FOUND);
        }
    }

}
