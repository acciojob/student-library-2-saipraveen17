package com.driver.services;

import com.driver.models.*;
import com.driver.repositories.BookRepository;
import com.driver.repositories.CardRepository;
import com.driver.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    BookRepository bookRepository5;

    @Autowired
    CardRepository cardRepository5;

    @Autowired
    TransactionRepository transactionRepository5;

    @Value("${books.max_allowed}")
    public int max_allowed_books;

    @Value("${books.max_allowed_days}")
    public int getMax_allowed_days;

    @Value("${books.fine.per_day}")
    public int fine_per_day;

    public String issueBook(int cardId, int bookId) throws Exception {
        //check whether bookId and cardId already exist
        //conditions required for successful transaction of issue book:
        //1. book is present and available
        // If it fails: throw new Exception("Book is either unavailable or not present");
        //2. card is present and activated
        // If it fails: throw new Exception("Card is invalid");
        //3. number of books issued against the card is strictly less than max_allowed_books
        // If it fails: throw new Exception("Book limit has reached for this card");
        //If the transaction is successful, save the transaction to the list of transactions and return the id

        //Note that the error message should match exactly in all cases
        Book book = bookRepository5.findById(bookId).get();
        if(book==null || !book.isAvailable()) {
            throw new Exception("Book is either unavailable or not present");
        }
        Card card = cardRepository5.findById(cardId).get();
        if(card==null || card.getCardStatus().equals(CardStatus.DEACTIVATED)) {
            throw new Exception("Card is invalid");
        }
        if(card.getBooks().size()>=max_allowed_books) {
            throw new Exception("Book limit has reached for this card");
        }
        book.setAvailable(false);
        Transaction transaction = new Transaction();
        transaction.setBook(book);
        transaction.setCard(card);
        transaction.setIssueOperation(true);
        transaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        List<Book> bookList = card.getBooks();
        bookList.add(book);
        card.setBooks(bookList);
        List<Transaction> transactionList = book.getTransactions();
        transactionList.add(transaction);
        book.setTransactions(transactionList);
        return transaction.getTransactionId(); //return transactionId instead
    }

    public Transaction returnBook(int cardId, int bookId) throws Exception{

        List<Transaction> transactions = transactionRepository5.find(cardId, bookId, TransactionStatus.SUCCESSFUL, true);
        Transaction transaction = transactions.get(transactions.size() - 1);

        //for the given transaction calculate the fine amount considering the book has been returned exactly when this function is called
        //make the book available for other users
        //make a new transaction for return book which contains the fine amount as well

        Book book = bookRepository5.findById(bookId).get();
        Card card = cardRepository5.findById(cardId).get();
        book.setAvailable(true);
        Transaction returnBookTransaction  = new Transaction();
        transaction.setBook(book);
        transaction.setCard(card);
        transaction.setIssueOperation(false);
        transaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        List<Book> bookList = card.getBooks();
        bookList.remove(book);
        card.setBooks(bookList);
        int days = returnBookTransaction.getTransactionDate().compareTo(transaction.getTransactionDate());
        int fineAmount = 0;
        if(days>getMax_allowed_days) {
            fineAmount = fine_per_day*(days-getMax_allowed_days);
        }
        returnBookTransaction.setFineAmount(fineAmount);
        List<Transaction> transactionList = book.getTransactions();
        transactionList.add(transaction);
        book.setTransactions(transactionList);
        return returnBookTransaction; //return the transaction after updating all details
    }
}
