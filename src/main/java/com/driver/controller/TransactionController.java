package com.driver.controller;

import com.driver.models.Transaction;
import com.driver.repositories.TransactionRepository;
import com.driver.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/issueBook")
    public ResponseEntity<String> issueBook(@RequestParam("cardId") int cardId, @RequestParam("bookId") int bookId) throws Exception{

        String result = transactionService.issueBook(cardId, bookId);
        return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
    }

    @PostMapping("/returnBook")
    public ResponseEntity<Transaction> returnBook(@RequestParam("cardId") int cardId, @RequestParam("bookId") int bookId) throws Exception{

        Transaction transaction = transactionService.returnBook(cardId, bookId);
        return new ResponseEntity<>(transaction, HttpStatus.ACCEPTED);
    }
}
