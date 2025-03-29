package service;

import dbutil.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MemberManager {
    Scanner sc = new Scanner(System.in);
    Connection con = DBConnection.getConnection();

    public void addMember() {
    	System.out.println("Enter Member ID :");
        int memberId = sc.nextInt();
        sc.nextLine(); // Consume newline

        System.out.println("Enter Member Name:");
        String name = sc.nextLine();

        System.out.println("Enter Contact:");
        String contact = sc.nextLine();

        System.out.println("Enter Email:");
        String email = sc.nextLine();

        String query = "INSERT INTO Members (Member_id, Name, Contact, Email) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, memberId);
            ps.setString(2, name);
            ps.setString(3, contact);
            ps.setString(4, email);
            ps.executeUpdate();
            System.out.println("Member added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding member: " + e.getMessage());
        }
    }

    public void viewMembers() {
        String query = "SELECT * FROM Members";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            System.out.printf("%-10s %-20s %-15s %-30s\n", "Member ID", "Name", "Contact", "Email");
            System.out.println("--------------------------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-10d %-20s %-15s %-30s\n",
                        rs.getInt("Member_id"),
                        rs.getString("Name"),
                        rs.getString("Contact"),
                        rs.getString("Email"));
            }
        } catch (SQLException e) {
            System.err.println("Error viewing members: " + e.getMessage());
        }
    }
    
    public void deleteMember() {
        System.out.println("Enter Member ID to delete:");
        int memberId = sc.nextInt();

        String checkQuery = "SELECT * FROM BorrowedBooks WHERE Member_id = ? AND Return_date IS NULL";
        String deleteQuery = "DELETE FROM Members WHERE Member_id = ?";

        try {
            // Check if the member has unreturned books
            PreparedStatement psCheck = con.prepareStatement(checkQuery);
            psCheck.setInt(1, memberId);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                System.out.println("This member has unreturned books. Cannot delete until all books are returned.");
            } else {
                // Proceed with deletion
                PreparedStatement psDelete = con.prepareStatement(deleteQuery);
                psDelete.setInt(1, memberId);

                int rows = psDelete.executeUpdate();
                if (rows > 0) {
                    System.out.println("Member deleted successfully.");
                } else {
                    System.out.println("Member not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error deleting member: " + e.getMessage());
        }
    }
    
    public void updateMember() {
        System.out.println("Enter Member ID to update:");
        int memberId = sc.nextInt();
        sc.nextLine(); // Consume newline

        System.out.println("Enter new Name (leave blank to keep current):");
        String name = sc.nextLine();

        System.out.println("Enter new Contact (leave blank to keep current):");
        String contact = sc.nextLine();

        System.out.println("Enter new Email (leave blank to keep current):");
        String email = sc.nextLine();

        String selectQuery = "SELECT * FROM Members WHERE Member_id = ?";
        String updateQuery = "UPDATE Members SET Name = ?, Contact = ?, Email = ? WHERE Member_id = ?";

        try {
            PreparedStatement psSelect = con.prepareStatement(selectQuery);
            psSelect.setInt(1, memberId);
            ResultSet rs = psSelect.executeQuery();

            if (rs.next()) {
                // Use current values if the user leaves fields blank
                if (name.isEmpty()) name = rs.getString("Name");
                if (contact.isEmpty()) contact = rs.getString("Contact");
                if (email.isEmpty()) email = rs.getString("Email");

                PreparedStatement psUpdate = con.prepareStatement(updateQuery);
                psUpdate.setString(1, name);
                psUpdate.setString(2, contact);
                psUpdate.setString(3, email);
                psUpdate.setInt(4, memberId);

                int rows = psUpdate.executeUpdate();
                if (rows > 0) {
                    System.out.println("Member updated successfully.");
                } else {
                    System.out.println("Member not found.");
                }
            } else {
                System.out.println("Member ID not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
        }
    }


}

