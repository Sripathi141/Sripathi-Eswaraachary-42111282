package credential;

import java.sql.*;
import java.util.Scanner;

class Home {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose an option:\n1. Login\n2. Sign Up\n3. Exit");
        int action = scanner.nextInt();
        scanner.nextLine();

        String url = "jdbc:mysql://localhost:3306/librarymanagementsystem";
        String dbUser = "root";
        String dbPassword = "Eswar@123";

        try {
            Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);

            if (action == 1) {
                System.out.println("Login as:\n1. Librarian\n2. Consumer");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                System.out.print("Enter Username: ");
                String username = scanner.nextLine();
                System.out.print("Enter Password: ");
                String password = scanner.nextLine();

                if (choice == 1) {
                    boolean loginSuccess = login(conn, "employee", username, password);
                    if (loginSuccess) {
                        System.out.println("Login successful!");
                        Librarian librarian = new Librarian();
                        librarian.menu(conn);
                    } else {
                        System.out.println("Invalid credentials!");
                    }
                } else if (choice == 2) {
                    boolean loginSuccess = login(conn, "consumer", username, password);
                    if (loginSuccess) {
                        System.out.println("Login successful!");
                        Consumer consumer = new Consumer();
                        consumer.menu(conn);
                    } else {
                        System.out.println("Invalid credentials!");
                    }
                } else {
                    System.out.println("Invalid choice!");
                }
            }

            else if (action == 2) {
                System.out.println("Sign up as:\n1. Librarian\n2. Consumer");
                int userType = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Enter Name: ");
                String name = scanner.nextLine();
                System.out.print("Enter Phone Number (10 digits): ");
                String phoneNo = scanner.nextLine();
                System.out.print("Enter Email: ");
                String email = scanner.nextLine();
                System.out.print("Enter Username: ");
                String username = scanner.nextLine();
                System.out.print("Enter Password (min 9 characters): ");
                String password = scanner.nextLine();

                // Input validation
                if (!isValidInput(name, phoneNo, email, username, password)) {
                    return;
                }

                String tableName = (userType == 1) ? "employee" : (userType == 2) ? "consumer" : null;
                if (tableName == null) {
                    System.out.println("Invalid choice!");
                    return;
                }

                String query = "INSERT INTO " + tableName + " (Name, PhoneNo, email, userName, password) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, name);
                    pstmt.setString(2, phoneNo);
                    pstmt.setString(3, email);
                    pstmt.setString(4, username);
                    int rowsAffected = pstmt.executeUpdate();
                    System.out.println(rowsAffected > 0 ? "Sign-up successful!" : "Sign-up failed!");
                } catch (SQLException e) {
                    handleSQLException(e);
                }
            }

            else if (action == 3) {
                System.out.println("Exiting...");
                return;
            }

            else {
                System.out.println("Invalid option!");
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        scanner.close();
    }

    public static boolean login(Connection conn, String tableName, String username, String password) {
        String query = "SELECT password FROM " + tableName + " WHERE userName = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isValidInput(String name, String phoneNo, String email, String username, String password) {
        if (password.length() < 9) {
            System.out.println("Password must be at least 9 characters!");
            return false;
        }

        if (!phoneNo.matches("\\d{10}")) {
            System.out.println("Phone number must be 10 digits!");
            return false;
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            System.out.println("Invalid email format!");
            return false;
        }

        if (username.isEmpty() || name.isEmpty()) {
            System.out.println("Username and Name cannot be empty!");
            return false;
        }

        return true;
    }

    public static void handleSQLException(SQLException e) {
        if (e.getSQLState().startsWith("23")) { // Duplicate entry error code
            System.out.println("Username or Email already exists. Please choose another.");
        } else {
            e.printStackTrace();
        }
    }
}
