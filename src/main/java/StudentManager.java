import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class StudentManager {

    static class Student {
        int id;
        String name;
        String surname;
        List<Map<String, String>> attendance;

        public Student(int id, String name, String surname) {
            this.id = id;
            this.name = name;
            this.surname = surname;
            this.attendance = new ArrayList<>();
        }
    }

    public static final List<Student> students = new ArrayList<>();

    public static List<Student> getStudents() {
        return students;
    }

    public static void main(String[] args) {
        students.add(new Student(1, "Maria", "Krawczyk"));
        students.add(new Student(2, "Krzysztof", "Krawczyk"));
        students.add(new Student(3, "Jan", "Krawczyk"));
        students.add(new Student(4, "Alojzy", "Kołodziejski"));

        studentBaseExport("students_list.csv");
        List<Student> importedStudents = studentBaseImport("students_list.csv");
        studentAdd(importedStudents, "Anna", "Kowalska");
        markAttendance(importedStudents, 1, "Present", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        markAttendance(importedStudents, 2, "Absent", "2023-10-10");
        markAttendance(importedStudents, 3, "Present", "2023-11-10");
        displayStudents(importedStudents);
        updateAttendance(importedStudents, 2, "Present", "2023-10-10");
        updateAttendance(importedStudents, 4, "Present", "2023-11-10");
        studentBaseExport("students_list.csv");
    }

    public static void studentAdd(List<Student> students, String name, String surname) {
        int newId = students.isEmpty() ? 1 : students.get(students.size() - 1).id + 1;
        students.add(new Student(newId, name, surname));
        System.out.println("Added student: " + newId + ", " + name + " " + surname);
    }

    public static void studentBaseExport(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("id,name,surname,attendance\n");
            for (Student student : students) {
                StringBuilder attendanceData = new StringBuilder();
                for (Map<String, String> entry : student.attendance) {
                    attendanceData.append(entry.get("date")).append(":").append(entry.get("status")).append(";");
                }
                writer.write(student.id + "," + student.name + "," + student.surname + "," + attendanceData + "\n");
            }
            System.out.println("Student list exported to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Student> studentBaseImport(String filename) {
        List<Student> importedStudents = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                int id = Integer.parseInt(data[0]);
                String name = data[1];
                String surname = data[2];
                List<Map<String, String>> attendance = new ArrayList<>();
                if (!data[3].isEmpty()) {
                    for (String entry : data[3].split(";")) {
                        String[] parts = entry.split(":");
                        Map<String, String> record = new HashMap<>();
                        record.put("date", parts[0]);
                        record.put("status", parts[1]);
                        attendance.add(record);
                    }
                }
                Student student = new Student(id, name, surname);
                student.attendance.addAll(attendance);
                importedStudents.add(student);
            }
            System.out.println("Student list imported from " + filename);
        } catch (IOException e) {
            System.out.println("File " + filename + " not found.");
        }
        return importedStudents;
    }

    public static void markAttendance(List<Student> students, int studentId, String attendance, String dateStr) {
        if (dateStr == null) {
            dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        for (Student student : students) {
            if (student.id == studentId) {
                Map<String, String> entry = new HashMap<>();
                entry.put("date", dateStr);
                entry.put("status", attendance);
                student.attendance.add(entry);
                System.out.println("Attendance marked: " + studentId + " -> " + attendance + " (" + dateStr + ")");
                return;
            }
        }
        System.out.println("Student with ID " + studentId + " not found.");
    }

    public static void displayStudents(List<Student> students) {
        System.out.println("\nStudent List:");
        for (Student student : students) {
            System.out.println(student.id + " - " + student.name + " " + student.surname);
            for (Map<String, String> entry : student.attendance) {
                System.out.println("  Date: " + entry.get("date") + " - Status: " + entry.get("status"));
            }
        }
        System.out.println();
    }

    public static void updateAttendance(List<Student> students, int studentId, String newStatus, String dateStr) {
        for (Student student : students) {
            if (student.id == studentId) {
                for (Map<String, String> entry : student.attendance) {
                    if (entry.get("date").equals(dateStr)) {
                        entry.put("status", newStatus);
                        System.out.println("Updated attendance: " + studentId + " -> " + newStatus + " (" + dateStr + ")");
                        return;
                    }
                }
                System.out.println("No attendance record for " + studentId + " on " + dateStr + ".");
                return;
            }
        }
        System.out.println("Student with ID " + studentId + " not found.");
    }
}