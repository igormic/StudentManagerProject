import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StudentManagerTest {

    private static final String TEST_FILE = "test_students_list.csv";

    @BeforeEach
void setup() {
    StudentManager.getStudents().clear();
    StudentManager.getStudents().add(new StudentManager.Student(1, "Maria", "Krawczyk"));
    StudentManager.getStudents().add(new StudentManager.Student(2, "Krzysztof", "Krawczyk"));
    StudentManager.getStudents().add(new StudentManager.Student(3, "Jan", "Krawczyk"));
    StudentManager.getStudents().add(new StudentManager.Student(4, "Alojzy", "Kołodziejski"));
}


    @AfterEach
    void cleanup() {
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testStudentBaseExport() {
        StudentManager.studentBaseExport(TEST_FILE);
        File file = new File(TEST_FILE);
        assertTrue(file.exists(), "File should be created during export");
    }

    @Test
    void testStudentBaseImport() {
        StudentManager.studentBaseExport(TEST_FILE);
        List<StudentManager.Student> importedStudents = StudentManager.studentBaseImport(TEST_FILE);
        assertEquals(4, importedStudents.size(), "Imported student count should match the exported count");
        assertEquals("Maria", importedStudents.get(0).name, "First student's name should match");
    }

    @Test
    void testStudentAdd() {
        List<StudentManager.Student> testStudents = new ArrayList<>(StudentManager.students);
        StudentManager.studentAdd(testStudents, "Anna", "Kowalska");
        assertEquals(5, testStudents.size(), "Student list size should increase after adding a new student");
        assertEquals("Anna", testStudents.get(4).name, "Newly added student's name should match");
    }

    @Test
    void testMarkAttendance() {
        List<StudentManager.Student> testStudents = new ArrayList<>(StudentManager.students);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        StudentManager.markAttendance(testStudents, 1, "Present", date);
        assertEquals(1, testStudents.get(0).attendance.size(), "Attendance size should increase after marking attendance");
        assertEquals("Present", testStudents.get(0).attendance.get(0).get("status"), "Attendance status should match");
    }

    @Test
    void testDisplayStudents() {
        List<StudentManager.Student> testStudents = new ArrayList<>(StudentManager.students);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        StudentManager.markAttendance(testStudents, 1, "Present", date);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        StudentManager.displayStudents(testStudents);

        System.setOut(originalOut);
        String output = outputStream.toString();
        assertTrue(output.contains("Maria Krawczyk"), "Output should contain student name");
        assertTrue(output.contains("Present"), "Output should contain attendance status");
    }

    @Test
    void testUpdateAttendance() {
        List<StudentManager.Student> testStudents = new ArrayList<>(StudentManager.students);
        String date = "2023-10-10";
        StudentManager.markAttendance(testStudents, 2, "Absent", date);

        StudentManager.updateAttendance(testStudents, 2, "Present", date);
        assertEquals("Present", testStudents.get(1).attendance.get(0).get("status"), "Attendance status should update to 'Present'");
    }

    @Test
    void testStudentBaseImportWithEmptyFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEST_FILE))) {
            writer.write("");
        } catch (IOException e) {
            fail("Setup failed while creating an empty file");
        }

        List<StudentManager.Student> importedStudents = StudentManager.studentBaseImport(TEST_FILE);
        assertTrue(importedStudents.isEmpty(), "Imported student list should be empty for an empty file");
    }

    @Test
    void testMarkAttendanceForNonExistingStudent() {
        List<StudentManager.Student> testStudents = new ArrayList<>(StudentManager.students);
        String date = "2023-10-10";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        StudentManager.markAttendance(testStudents, 999, "Present", date);

        System.setOut(originalOut);
        String output = outputStream.toString();
        assertTrue(output.contains("Student with ID 999 not found"), "Output should indicate that the student was not found");
    }

    @Test
    void testUpdateAttendanceForNonExistingDate() {
        List<StudentManager.Student> testStudents = new ArrayList<>(StudentManager.students);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        StudentManager.updateAttendance(testStudents, 1, "Present", "2023-10-10");

        System.setOut(originalOut);
        String output = outputStream.toString();
        assertTrue(output.contains("No attendance record for 1 on 2023-10-10"), "Output should indicate no attendance record found");
    }
}