
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

enum Grade { A, B, C, D, F, INCOMPLETE }

class Course {
    final String code;
    final String title;
    Course(String code, String title) { this.code = code; this.title = title; }
    @Override public String toString() { return code + " — " + title; }
}

class Student {
    private final int id;
    private String name;
    private String email;
    final java.util.List<Enrollment> enrollments = new ArrayList<>();
    Student(int id, String name, String email) {
        this.id = id; this.name = name; this.email = email;
    }
    int getId() { return id; }
    String getName() { return name; }
    String getEmail() { return email; }
    void setName(String n) { this.name = n; }
    void setEmail(String e) { this.email = e; }
    double gpa() {
        int count = 0; double pts = 0;
        for (Enrollment en : enrollments) {
            if (en.grade == null || en.grade == Grade.INCOMPLETE) continue;
            count++;
            switch (en.grade) {
                case A -> pts += 4;
                case B -> pts += 3;
                case C -> pts += 2;
                case D -> pts += 1;
                case F -> pts += 0;
            }
        }
        return count == 0 ? 0.0 : pts / count;
    }
    @Override public String toString() { return id + ": " + name; }
}

class Enrollment {
    final Student student;
    final Course course;
    Grade grade = Grade.INCOMPLETE;
    Enrollment(Student s, Course c) { this.student = s; this.course = c; }
}

class EnrollmentTableModel extends AbstractTableModel {
    private final String[] cols = {"Course Code", "Course Title", "Grade"};
    private java.util.List<Enrollment> data = List.of();
    void setData(java.util.List<Enrollment> rows) { this.data = rows; fireTableDataChanged(); }
    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int col) { return cols[col]; }
    @Override public Object getValueAt(int r, int c) {
        Enrollment e = data.get(r);
        return switch (c) {
            case 0 -> e.course.code;
            case 1 -> e.course.title;
            case 2 -> e.grade;
            default -> null;
        };
    }
    @Override public boolean isCellEditable(int r, int c) { return c == 2; }
    @Override public void setValueAt(Object aValue, int r, int c) {
        if (c == 2 && aValue instanceof Grade g) {
            data.get(r).grade = g;
            fireTableRowsUpdated(r, r);
        }
    }
    @Override public Class<?> getColumnClass(int c) {
        return c == 2 ? Grade.class : String.class;
    }
}

class StudentDialog extends JDialog {
    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private boolean confirmed = false;

    StudentDialog(Window owner, String title, Student existing) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(10,10,10,10));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4,4,4,4);
        gc.anchor = GridBagConstraints.LINE_END;

        gc.gridx=0; gc.gridy=0; form.add(new JLabel("Name:"), gc);
        gc.gridy=1; form.add(new JLabel("Email:"), gc);

        gc.anchor = GridBagConstraints.LINE_START;
        gc.gridx=1; gc.gridy=0; form.add(nameField, gc);
        gc.gridy=1; form.add(emailField, gc);

        if (existing != null) {
            nameField.setText(existing.getName());
            emailField.setText(existing.getEmail());
        }

        JButton ok = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(cancel); btns.add(ok);

        ok.addActionListener(e -> {
            if (validateInput()) { confirmed = true; dispose(); }
        });
        cancel.addActionListener(e -> dispose());

        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(btns, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }
    private boolean validateInput() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        var EMAIL_RE = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        if (!EMAIL_RE.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    boolean isConfirmed() { return confirmed; }
    String getNameValue() { return nameField.getText().trim(); }
    String getEmailValue() { return emailField.getText().trim(); }
}

public class StudentManagementSwingApp extends JFrame {
    private final DefaultListModel<Student> studentModel = new DefaultListModel<>();
    private final JList<Student> studentList = new JList<>(studentModel);

    private final java.util.List<Course> courses = new ArrayList<>();
    private final JComboBox<Course> courseCombo = new JComboBox<>();
    private final EnrollmentTableModel enrollmentTableModel = new EnrollmentTableModel();
    private final JTable enrollmentTable = new JTable(enrollmentTableModel);

    private int nextStudentId = 1001;

    public StudentManagementSwingApp() {
        super("Student Management System — Swing");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 640);
        setLocationRelativeTo(null);

        seedCourses();
        buildUI();
        seedDemoStudents();
    }

    private void seedCourses() {
        courses.addAll(List.of(
            new Course("CS101", "Intro to CS"),
            new Course("CS201", "Data Structures"),
            new Course("CS301", "Algorithms"),
            new Course("MATH201", "Discrete Math")
        ));
        DefaultComboBoxModel<Course> m = new DefaultComboBoxModel<>();
        for (Course c : courses) m.addElement(c);
        courseCombo.setModel(m);
    }

    private void seedDemoStudents() {
        Student s1 = new Student(nextStudentId++, "Alice Johnson", "alice@example.com");
        Student s2 = new Student(nextStudentId++, "Brian Park", "brian@example.com");
        studentModel.addElement(s1);
        studentModel.addElement(s2);
    }

    private void buildUI() {
        JScrollPane left = new JScrollPane(studentList);
        left.setPreferredSize(new Dimension(240, 200));
        left.setBorder(BorderFactory.createTitledBorder("Students"));

        enrollmentTable.setFillsViewportHeight(true);
        enrollmentTable.setRowHeight(24);
        JComboBox<Grade> gradeEditor = new JComboBox<>(Grade.values());
        enrollmentTable.setDefaultEditor(Grade.class, new DefaultCellEditor(gradeEditor));
        JScrollPane center = new JScrollPane(enrollmentTable);
        center.setBorder(BorderFactory.createTitledBorder("Enrollments / Grades"));

        JButton addBtn = new JButton("Add Student");
        JButton updBtn = new JButton("Update Student");
        JButton enrollBtn = new JButton("Enroll in Course");
        JButton dropBtn = new JButton("Drop Enrollment");
        JLabel gpaLabel = new JLabel("GPA: 0.00");

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(10,10,10,10));
        right.add(new JLabel("Course:"));
        right.add(courseCombo);
        right.add(Box.createVerticalStrut(8));
        right.add(enrollBtn);
        right.add(Box.createVerticalStrut(12));
        right.add(dropBtn);
        right.add(Box.createVerticalStrut(24));
        right.add(addBtn);
        right.add(Box.createVerticalStrut(8));
        right.add(updBtn);
        right.add(Box.createVerticalStrut(24));
        right.add(gpaLabel);
        right.add(Box.createVerticalGlue());

        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> dispose());
        file.add(exit);
        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Student Management System (Swing)\nCS 1102 Assignment",
            "About", JOptionPane.INFORMATION_MESSAGE));
        help.add(about);
        bar.add(file); bar.add(help);
        setJMenuBar(bar);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, center);
        split.setResizeWeight(0.25);
        getContentPane().add(split, BorderLayout.CENTER);
        getContentPane().add(right, BorderLayout.EAST);

        studentList.addListSelectionListener(e -> {
            Student s = studentList.getSelectedValue();
            if (!e.getValueIsAdjusting()) {
                enrollmentTableModel.setData(s == null ? List.of() : s.enrollments);
                gpaLabel.setText("GPA: " + (s == null ? "0.00" : String.format(java.util.Locale.US, "%.2f", s.gpa())));
            }
        });

        addBtn.addActionListener(e -> onAddStudent());
        updBtn.addActionListener(e -> onUpdateStudent());
        enrollBtn.addActionListener(e -> onEnroll());
        dropBtn.addActionListener(e -> onDrop());

        enrollmentTable.addPropertyChangeListener(evt -> {
            if ("tableCellEditor".equals(evt.getPropertyName()) && !enrollmentTable.isEditing()) {
                Student s = studentList.getSelectedValue();
                if (s != null) {
                    ((AbstractTableModel) enrollmentTable.getModel()).fireTableDataChanged();
                    gpaLabel.setText("GPA: " + String.format(java.util.Locale.US, "%.2f", s.gpa()));
                }
            }
        });
    }

    private void onAddStudent() {
        StudentDialog dlg = new StudentDialog(this, "Add Student", null);
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return;
        Student s = new Student(nextStudentId++, dlg.getNameValue(), dlg.getEmailValue());
        studentModel.addElement(s);
        studentList.setSelectedValue(s, true);
    }

    private void onUpdateStudent() {
        Student s = studentList.getSelectedValue();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Select a student first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StudentDialog dlg = new StudentDialog(this, "Update Student", s);
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return;
        s.setName(dlg.getNameValue());
        s.setEmail(dlg.getEmailValue());
        studentList.repaint();
    }

    private void onEnroll() {
        Student s = studentList.getSelectedValue();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Select a student to enroll.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Course c = (Course) courseCombo.getSelectedItem();
        if (c == null) return;
        boolean already = s.enrollments.stream().anyMatch(en -> en.course.code.equals(c.code));
        if (already) {
            JOptionPane.showMessageDialog(this, "Student is already enrolled in " + c.code, "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        s.enrollments.add(new Enrollment(s, c));
        enrollmentTableModel.fireTableDataChanged();
    }

    private void onDrop() {
        int row = enrollmentTable.getSelectedRow();
        Student s = studentList.getSelectedValue();
        if (s == null || row < 0) return;
        s.enrollments.remove(row);
        enrollmentTableModel.fireTableDataChanged();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagementSwingApp().setVisible(true));
    }
}
