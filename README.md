# 🎓 StudentManagementSwingApp

## 🧩 Overview
This project is a **Java Swing-based graphical application** that manages students, their course enrollments, and assigned grades.  
It provides a complete **GUI interface** for adding and updating students, enrolling them in courses, assigning grades, and displaying GPA dynamically.  
Developed for a **University of the People** programming assignment, the system demonstrates practical mastery of **event-driven programming**, **Swing components**, and **object-oriented design**.

---

## ⚙️ Features
- **Add / Update Students** via dialog form with input validation (name & email format)  
- **Enroll / Drop Courses** with duplicate enrollment checks  
- **Assign Grades** directly through a JTable with combo-box editing  
- **Automatic GPA Calculation** after every grade update  
- **Persistent UI updates** (repaints and refreshes dynamically)  
- Built-in **menu bar** with File/Help options  
- **Preloaded demo students and courses** for easy testing  

---

## 🧠 Concepts Used

| Concept | Description |
|----------|-------------|
| **Java Swing GUI** | Built with JFrame, JPanel, JTable, JList, JComboBox, and dialog components |
| **MVC Pattern** | `EnrollmentTableModel` acts as the Model; GUI serves as the View & Controller |
| **Object-Oriented Design** | Classes like `Student`, `Course`, and `Enrollment` encapsulate logic and relationships |
| **Event-Driven Programming** | Button clicks, table edits, and list selections trigger real-time updates |
| **Inner Classes & Enums** | `Grade` enum defines grade categories; nested classes used for modularity |
| **Data Validation** | Regex-based email validation and user-friendly feedback dialogs |
| **Collections (ArrayList, DefaultListModel)** | Used for dynamic storage and updates of student and course data |
| **Polymorphism & Encapsulation** | Student GPA computed polymorphically through encapsulated grade logic |
| **Custom TableModel** | `EnrollmentTableModel` extends `AbstractTableModel` to support editable grade cells |

---

## ▶️ How to Run

1️⃣ Open the project in VS Code or your preferred IDE.  
2️⃣ Compile and run:
```bash
javac StudentManagementSwingApp.java
```
3️⃣ Run the program:
```bash
java StudentManagementSwingApp
```
## 🏫 Educational Context
This application was developed for a Computer Science course at the University of the People.
It demonstrates:

Event-driven logic using Swing listeners

Real-time updates through custom TableModels

Practical application of OOP design and GUI programming
It serves as a foundational project for learning desktop application development in Java.
