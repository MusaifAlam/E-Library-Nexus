package service;

import dbutil.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BorrowManager {
    Scanner sc = new Scanner(System.in);
    Connection con = DBConnection.getConnection();

    public void borrowBook() {
        System.out.println("Enter Member ID:");
        int memberId = sc.nextInt();

        System.out.println("Enter Book ID:");
        int bookId = sc.nextInt();

        String checkBookQuery = "SELECT Available_copies FROM Books WHERE Book_id = ?";
        String checkMemberQuery = "SELECT * FROM Members WHERE Member_id = ?";
        String updateBookQuery = "UPDATE Books SET Available_copies = Available_copies - 1 WHERE Book_id = ?";
        String insertBorrowQuery = "INSERT INTO BorrowedBooks (Book_id, Member_id, Borrow_date) VALUES (?, ?, CURDATE())";

        try {
            // Check if the book exists and has available copies
            PreparedStatement psCheckBook = con.prepareStatement(checkBookQuery);
            psCheckBook.setInt(1, bookId);
            ResultSet rsBook = psCheckBook.executeQuery();

            if (!rsBook.next()) {
                System.out.println("Book ID not found.");
                return;
            }

            int availableCopies = rsBook.getInt("Available_copies");
            if (availableCopies <= 0) {
                System.out.println("No copies available for borrowing.");
                return;
            }

            // Check if the member exists
            PreparedStatement psCheckMember = con.prepareStatement(checkMemberQuery);
            psCheckMember.setInt(1, memberId);
            ResultSet rsMember = psCheckMember.executeQuery();

            if (!rsMember.next()) {
                System.out.println("Member ID not found.");
                return;
            }

            // Update the book's available copies
            PreparedStatement psUpdateBook = con.prepareStatement(updateBookQuery);
            psUpdateBook.setInt(1, bookId);
            psUpdateBook.executeUpdate();

            // Insert into BorrowedBooks table
            PreparedStatement psInsertBorrow = con.prepareStatement(insertBorrowQuery);
            psInsertBorrow.setInt(1, bookId);
            psInsertBorrow.setInt(2, memberId);
            psInsertBorrow.executeUpdate();

            System.out.println("Book borrowed successfully.");
        } catch (SQLException e) {
            System.err.println("Error during borrowing process: " + e.getMessage());
        }
    }
    public void returnBook() {
        System.out.println("Enter Member ID:");
        int memberId = sc.nextInt();

        System.out.println("Enter Book ID:");
        int bookId = sc.nextInt();

        String checkBorrowQuery = "SELECT * FROM BorrowedBooks WHERE Book_id = ? AND Member_id = ? AND Return_date IS NULL";
        String updateBorrowQuery = "UPDATE BorrowedBooks SET Return_date = CURDATE() WHERE Book_id = ? AND Member_id = ?";
        String updateBookQuery = "UPDATE Books SET Available_copies = Available_copies + 1 WHERE Book_id = ?";

        try {
            // Check if the book is currently borrowed by the member
            PreparedStatement psCheckBorrow = con.prepareStatement(checkBorrowQuery);
            psCheckBorrow.setInt(1, bookId);
            psCheckBorrow.setInt(2, memberId);
            ResultSet rs = psCheckBorrow.executeQuery();

            if (rs.next()) {
                // Update the return date in BorrowedBooks table
                PreparedStatement psUpdateBorrow = con.prepareStatement(updateBorrowQuery);
                psUpdateBorrow.setInt(1, bookId);
                psUpdateBorrow.setInt(2, memberId);
                psUpdateBorrow.executeUpdate();

                // Increment the available copies in Books table
                PreparedStatement psUpdateBook = con.prepareStatement(updateBookQuery);
                psUpdateBook.setInt(1, bookId);
                psUpdateBook.executeUpdate();

                System.out.println("Book returned successfully.");
            } else {
                System.out.println("No active borrow record found for this Book ID and Member ID.");
            }
        } catch (SQLException e) {
            System.err.println("Error during return process: " + e.getMessage());
        }
    }
    
    public void viewBorrowedBooks() {
        String query = "SELECT bb.Transaction_id, b.Title, m.Name, m.Contact, bb.Borrow_date, bb.Return_date " +
                       "FROM BorrowedBooks bb " +
                       "JOIN Books b ON bb.Book_id = b.Book_id " +
                       "JOIN Members m ON bb.Member_id = m.Member_id";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.printf("%-15s %-30s %-20s %-15s %-15s %-15s\n", 
                              "Transaction ID", "Book Title", "Member Name", "Contact", "Borrow Date", "Return Date");
            System.out.println("--------------------------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-15d %-30s %-20s %-15s %-15s %-15s\n",
                                  rs.getInt("Transaction_id"),
                                  rs.getString("Title"),
                                  rs.getString("Name"),
                                  rs.getString("Contact"),
                                  rs.getDate("Borrow_date"),
                                  rs.getDate("Return_date") != null ? rs.getDate("Return_date") : "Not Returned");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching borrowed books: " + e.getMessage());
        }
    }


}

