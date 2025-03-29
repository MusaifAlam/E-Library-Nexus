package service;

import dbutil.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BookManager {
    Scanner sc = new Scanner(System.in);
    Connection con = DBConnection.getConnection();

    public void addBook() {
    	System.out.println("Enter Book ID :");
        int bookId = sc.nextInt();
        sc.nextLine(); // Consume newline

        System.out.println("Enter Book Title:");
        String title = sc.nextLine();

        System.out.println("Enter Author:");
        String author = sc.nextLine();

        System.out.println("Enter Publisher:");
        String publisher = sc.nextLine();

        System.out.println("Enter Available Copies:");
        int copies = sc.nextInt();

        String query = "INSERT INTO Books (Book_id, Title, Author, Publisher, Available_copies) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, bookId);
            ps.setString(2, title);
            ps.setString(3, author);
            ps.setString(4, publisher);
            ps.setInt(5, copies);
            ps.executeUpdate();
            System.out.println("Book added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
        }
    }

    public void viewBooks() {
        String query = "SELECT * FROM Books";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            System.out.printf("%-10s %-30s %-20s %-20s %-10s\n", "Book ID", "Title", "Author", "Publisher", "Copies");
            System.out.println("--------------------------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-10d %-30s %-20s %-20s %-10d\n",
                        rs.getInt("Book_id"),
                        rs.getString("Title"),
                        rs.getString("Author"),
                        rs.getString("Publisher"),
                        rs.getInt("Available_copies"));
            }
        } catch (SQLException e) {
            System.err.println("Error viewing books: " + e.getMessage());
        }
    }
    
    public void deleteBook() {
        System.out.println("Enter Book ID to delete:");
        int bookId = sc.nextInt();

        String checkQuery = "SELECT * FROM BorrowedBooks WHERE Book_id = ?";
        String deleteQuery = "DELETE FROM Books WHERE Book_id = ?";

        try {
            // Check if the book is borrowed
            PreparedStatement psCheck = con.prepareStatement(checkQuery);
            psCheck.setInt(1, bookId);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                System.out.println("This book is currently borrowed. It cannot be deleted.");
            } else {
                // Proceed with deletion
                PreparedStatement psDelete = con.prepareStatement(deleteQuery);
                psDelete.setInt(1, bookId);

                int rows = psDelete.executeUpdate();
                if (rows > 0) {
                    System.out.println("Book deleted successfully.");
                } else {
                    System.out.println("Book not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
        }
    }
    
    
    public void updateBook() {
        System.out.println("Enter Book ID to update:");
        int bookId = sc.nextInt();
        sc.nextLine(); // Consume newline

        System.out.println("Enter new Title (leave blank to keep current):");
        String title = sc.nextLine();

        System.out.println("Enter new Author (leave blank to keep current):");
        String author = sc.nextLine();

        System.out.println("Enter new Publisher (leave blank to keep current):");
        String publisher = sc.nextLine();

        System.out.println("Enter new Available Copies (enter -1 to keep current):");
        int copies = sc.nextInt();

        String selectQuery = "SELECT * FROM Books WHERE Book_id = ?";
        String updateQuery = "UPDATE Books SET Title = ?, Author = ?, Publisher = ?, Available_copies = ? WHERE Book_id = ?";

        try {
            PreparedStatement psSelect = con.prepareStatement(selectQuery);
            psSelect.setInt(1, bookId);
            ResultSet rs = psSelect.executeQuery();

            if (rs.next()) {
                // Use current values if the user leaves fields blank or enters -1
                if (title.isEmpty()) title = rs.getString("Title");
                if (author.isEmpty()) author = rs.getString("Author");
                if (publisher.isEmpty()) publisher = rs.getString("Publisher");
                if (copies == -1) copies = rs.getInt("Available_copies");

                PreparedStatement psUpdate = con.prepareStatement(updateQuery);
                psUpdate.setString(1, title);
                psUpdate.setString(2, author);
                psUpdate.setString(3, publisher);
                psUpdate.setInt(4, copies);
                psUpdate.setInt(5, bookId);

                int rows = psUpdate.executeUpdate();
                if (rows > 0) {
                    System.out.println("Book updated successfully.");
                } else {
                    System.out.println("Book not found.");
                }
            } else {
                System.out.println("Book ID not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
        }
    }
    
    


}

