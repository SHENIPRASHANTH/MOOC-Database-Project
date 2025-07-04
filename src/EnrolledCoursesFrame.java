package src;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;

public class EnrolledCoursesFrame extends JFrame {
    private int studentId;

    public EnrolledCoursesFrame(int studentId) {
        this.studentId = studentId;
        setTitle("My Enrolled Courses");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        showEnrolledCourses();
    }

    private void showEnrolledCourses() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = """
                SELECT e.course_id, c.title, c.description, e.enrollment_date
                FROM enrollments e
                JOIN courses c ON e.course_id = c.course_id
                WHERE e.student_id = ?
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                int courseId = rs.getInt("course_id");
                String title = rs.getString("title");
                String desc = rs.getString("description");
                Timestamp enrolledAt = rs.getTimestamp("enrollment_date");

                JPanel coursePanel = new JPanel(new BorderLayout(5, 5));
                JTextArea courseInfo = new JTextArea(title + "\n" + desc + "\n📅 Enrolled on: " + enrolledAt.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                courseInfo.setEditable(false);
                courseInfo.setBackground(new Color(240, 240, 240));

                JButton unenrollBtn = new JButton("❌ Unenroll");
                unenrollBtn.addActionListener(e -> unenroll(courseId));

                coursePanel.add(courseInfo, BorderLayout.CENTER);
                coursePanel.add(unenrollBtn, BorderLayout.EAST);

                panel.add(coursePanel);
            }

            if (!hasData) {
                panel.add(new JLabel("ℹ️ No courses enrolled yet."));
            }

            JScrollPane scrollPane = new JScrollPane(panel);
            add(scrollPane);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error loading enrolled courses: " + e.getMessage());
        }
    }

    private void unenroll(int courseId) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to unenroll?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection()) {
            String deleteQuery = "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?";
            PreparedStatement ps = conn.prepareStatement(deleteQuery);
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Successfully unenrolled.");
                dispose(); // Close and reopen the frame to refresh list
                new EnrolledCoursesFrame(studentId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ Not enrolled in this course.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error unenrolling: " + e.getMessage());
        }
    }
}
