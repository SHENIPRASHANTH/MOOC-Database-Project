package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminPanel extends JFrame {

    private JTextField titleField, descField;
    private DefaultListModel<String> courseListModel;
    private JList<String> courseList;
    private java.util.Map<String, Integer> courseIdMap = new java.util.HashMap<>();

    public AdminPanel() {
        setTitle("🎓 Admin Panel - Manage Courses");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadCourses();
    }

    private void initComponents() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        inputPanel.add(new JLabel("Course Title:"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Description:"));
        descField = new JTextField();
        inputPanel.add(descField);

        JButton addButton = new JButton("➕ Add Course");
        addButton.addActionListener(e -> addCourse());
        inputPanel.add(addButton);

        JButton deleteButton = new JButton("🗑️ Delete Selected Course");
        deleteButton.addActionListener(e -> deleteSelectedCourse());
        inputPanel.add(deleteButton);

        courseListModel = new DefaultListModel<>();
        courseList = new JList<>(courseListModel);
        JScrollPane scrollPane = new JScrollPane(courseList);

        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadCourses() {
        courseListModel.clear();
        courseIdMap.clear();

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT course_id, title FROM courses";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("course_id");
                String title = rs.getString("title");
                courseListModel.addElement(title);
                courseIdMap.put(title, id);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error loading courses: " + e.getMessage());
        }
    }

    private void addCourse() {
        String title = titleField.getText().trim();
        String desc = descField.getText().trim();

        if (title.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Enter both title and description");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String insert = "INSERT INTO courses (title, description) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(insert);
            ps.setString(1, title);
            ps.setString(2, desc);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Course added successfully");
            titleField.setText("");
            descField.setText("");
            loadCourses();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error adding course: " + e.getMessage());
        }
    }

    private void deleteSelectedCourse() {
        String selected = courseList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select a course to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete \"" + selected + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int courseId = courseIdMap.get(selected);

        try (Connection conn = DBConnection.getConnection()) {
            // Delete from enrollments first to avoid FK error
            PreparedStatement delEnrollments = conn.prepareStatement("DELETE FROM enrollments WHERE course_id = ?");
            delEnrollments.setInt(1, courseId);
            delEnrollments.executeUpdate();

            // Delete from courses
            PreparedStatement delCourse = conn.prepareStatement("DELETE FROM courses WHERE course_id = ?");
            delCourse.setInt(1, courseId);
            delCourse.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Course deleted");
            loadCourses();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error deleting course: " + e.getMessage());
        }
    }
}
