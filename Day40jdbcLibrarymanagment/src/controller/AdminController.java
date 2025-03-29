package controller;

import service.BookManager;
import service.BorrowManager;
import service.MemberManager;

import java.util.Scanner;

public class AdminController {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BookManager bookManager = new BookManager();
        MemberManager memberManager = new MemberManager();
        BorrowManager borrowManager = new BorrowManager();

        boolean flag = true;
        while (flag) {
        	System.out.println("1: Add Book");
        	System.out.println("2: View Books");
        	System.out.println("3: Update Book");
        	System.out.println("4: Delete Book");
        	System.out.println("5: Add Member");
        	System.out.println("6: View Members");
        	System.out.println("7: Update Member");
        	System.out.println("8: Delete Member");
        	System.out.println("9: Borrow Book");
        	System.out.println("10: Return Book");
        	System.out.println("11: View Borrowed Books");
        	System.out.println("12: Exit");


            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline
            switch (choice) {
                case 1:
                    bookManager.addBook();
                    break;
                case 2:
                    bookManager.viewBooks();
                    break;
                case 3:
                    bookManager.updateBook();
                    break;
                case 4:
                    bookManager.deleteBook();
                    break;
                case 5:
                    memberManager.addMember();
                    break;
                case 6:
                    memberManager.viewMembers();
                    break;
                case 7:
                    memberManager.updateMember();
                    break;
                case 8:
                    memberManager.deleteMember();
                    break;
                case 9:
                    borrowManager.borrowBook();
                    break;
                case 10:
                    borrowManager.returnBook();  
                    break;
                case 11:
                    borrowManager.viewBorrowedBooks();  
                    break;
                case 12:
                    flag = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
            
        }
    }
}
