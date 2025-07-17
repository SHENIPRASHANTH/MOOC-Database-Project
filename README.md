# MOOC-Database-Project

A Java Swing and MySQL-based **MOOC (Massive Open Online Course)** system that enables **students and admins** to perform complete **CRUD operations**. This desktop application is designed to manage course enrollment, student records, and admin tasks through an intuitive graphical interface.

## 💡 Features

### 👨‍🎓 Student Module
- Register as a new student
- Login using credentials
- View available courses
- Enroll in courses
- Update personal profile
- View enrolled courses

### 👩‍💼 Admin Module
- Login as admin
- Add new courses
- View all students
- Update or delete student records
- Manage course database

### 🔄 CRUD Functionality
- **Create**: Add new students, admins, or courses
- **Read**: Display student data, enrolled courses, and course catalog
- **Update**: Modify existing records
- **Delete**: Remove students or courses

## 🛠️ Technologies Used

Tech                Description                     
----------------------------------------------
**Java Swing**  GUI for desktop interaction 
**MySQL**       Backend relational database   
**JDBC**        Java Database Connectivity    

## 🗃️ Database Schema

### Tables
- `students (id, name, email, password, ...)`
- `courses (id, title, description, ...)`
- `enrollments (student_id, course_id)`
- `admins (id, name, password)`

📌 The SQL schema is available in the `/database/` folder (if included).

## 🚀 Getting Started

### Prerequisites
- Java (JDK 8 or above)
- MySQL server
- MySQL Connector/J (JDBC Driver)

### Steps
1. **Clone this repository**
   ```bash
   git clone https://github.com/SHENIPRASHANTH/MOOC-Database-Project.git
   cd MOOC-Database-Project
