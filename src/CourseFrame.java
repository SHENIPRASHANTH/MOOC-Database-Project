package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CourseFrame extends JFrame {
    private int studentId;

    public CourseFrame(int studentId) {
        this.studentId = studentId;
        setTitle("Available Courses");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 📚 "View My Courses" button at top
        JButton viewEnrolledBtn = new JButton("📚 View My Courses");
        viewEnrolledBtn.addActionListener(e -> new EnrolledCoursesFrame(studentId).setVisible(true));
        JPanel topPanel = new JPanel();
        topPanel.add(viewEnrolledBtn);
        add(topPanel, BorderLayout.NORTH);

        loadCourses();
    }

    private void loadCourses() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;

            String query = "SELECT * FROM courses";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));

            while (rs.next()) {
                int courseId = rs.getInt("course_id");
                String title = rs.getString("title");
                String desc = rs.getString("description");

                JButton enrollBtn = new JButton("Enroll in: " + title);
                enrollBtn.addActionListener(e -> enroll(courseId, title));
                panel.add(enrollBtn);
            }

            JScrollPane scrollPane = new JScrollPane(panel);
            add(scrollPane, BorderLayout.CENTER);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error loading courses: " + e.getMessage());
        }
    }

    private void enroll(int courseId, String courseName) {
        try (Connection conn = DBConnection.getConnection()) {
            String checkQuery = "SELECT * FROM enrollments WHERE student_id = ? AND course_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, studentId);
            checkStmt.setInt(2, courseId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "ℹ️ Already enrolled in " + courseName);
                return;
            }

            String insertQuery = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(insertQuery);
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Enrolled in " + courseName);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error enrolling: " + e.getMessage());
        }
    }
}
