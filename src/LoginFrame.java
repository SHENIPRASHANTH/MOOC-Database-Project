package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JTextField nameField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("MOOC - Student Login");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));  // extra row for admin
        panel.add(new JLabel("Name:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        loginButton = new JButton("Login / Register");
        panel.add(new JLabel());  // Empty cell
        panel.add(loginButton);

        JButton adminBtn = new JButton("Login as Admin");
        adminBtn.addActionListener(e -> {
            dispose();  // close current window
            new AdminPanel().setVisible(true);
        });
        panel.add(new JLabel());  // empty label for spacing
        panel.add(adminBtn);

        add(panel);

        loginButton.addActionListener(e -> loginOrRegister());
    }

    private void loginOrRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Please enter both name and email.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;

            // Check if student already exists
            String query = "SELECT * FROM students WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int studentId = rs.getInt("student_id");
                JOptionPane.showMessageDialog(this, "✅ Welcome back, " + rs.getString("name") + "!");
                dispose(); // close login window
                new CourseFrame(studentId).setVisible(true);
            } else {
                // Register new student
                String insert = "INSERT INTO students (name, email) VALUES (?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
                insertStmt.setString(1, name);
                insertStmt.setString(2, email);
                insertStmt.executeUpdate();

                ResultSet keys = insertStmt.getGeneratedKeys();
                if (keys.next()) {
                    int studentId = keys.getInt(1);
                    JOptionPane.showMessageDialog(this, "📝 Registered and logged in as " + name);
                    dispose(); // close login window
                    new CourseFrame(studentId).setVisible(true);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
    