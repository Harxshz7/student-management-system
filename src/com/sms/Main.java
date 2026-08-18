package com.sms;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.sms.dao.JsonStudentDAO;
import com.sms.model.Student;
import com.sms.service.StudentService;
import com.sms.util.Utils;

public class Main {
    public static void main(String[] args) {
        JsonStudentDAO studentDAO = new JsonStudentDAO();
        StudentService studentService = new StudentService(studentDAO);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Loaded " + studentService.getTotalCount() + " student(s) from students.json");

        while (running) {
            System.out.println("\n=== Student Management System ===");
            System.out.println("1.  Add Student");
            System.out.println("2.  View All Students");
            System.out.println("3.  Search Students (by name/course)");
            System.out.println("4.  Update Student");
            System.out.println("5.  Delete Student");
            System.out.println("6.  Search by ID");
            System.out.println("7.  Sort by Name");
            System.out.println("8.  Sort by Age");
            System.out.println("9.  Show Stats");
            System.out.println("10. Exit");

            int choice = Utils.readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1 -> {
                    try {
                        Student student = new Student();
                        student.setName(Utils.readString(scanner, "Enter student name: "));
                        student.setAge(Utils.readInt(scanner, "Enter student age: "));
                        student.setCourse(Utils.readString(scanner, "Enter course: "));
                        studentService.addStudent(student);
                        System.out.println("Student added successfully.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case 2 -> {
                    List<Student> students = studentService.getAllStudents();
                    if (students.isEmpty()) {
                        System.out.println("No students found.");
                    } else {
                        for (Student student : students) {
                            System.out.println(student);
                        }
                    }
                }
                case 3 -> {
                    String keyword = Utils.readString(scanner, "Enter name or course to search: ");
                    List<Student> results = studentService.searchStudents(keyword);
                    if (results.isEmpty()) {
                        System.out.println("No matching students found.");
                    } else {
                        for (Student student : results) {
                            System.out.println(student);
                        }
                    }
                }
                case 4 -> {
                    int id = Utils.readInt(scanner, "Enter student ID to update: ");
                    Optional<Student> existing = studentService.findById(id);
                    if (existing.isEmpty()) {
                        System.out.println("Student not found.");
                    } else {
                        try {
                            Student updated = new Student();
                            updated.setName(Utils.readString(scanner, "Enter new name: "));
                            updated.setAge(Utils.readInt(scanner, "Enter new age: "));
                            updated.setCourse(Utils.readString(scanner, "Enter new course: "));
                            boolean success = studentService.updateStudent(id, updated);
                            System.out.println(success ? "Student updated successfully." : "Failed to update student.");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    }
                }
                case 5 -> {
                    int id = Utils.readInt(scanner, "Enter student ID to delete: ");
                    boolean success = studentService.deleteStudent(id);
                    System.out.println(success ? "Student deleted successfully." : "Student not found.");
                }
                case 6 -> {
                    int id = Utils.readInt(scanner, "Enter student ID to search: ");
                    Optional<Student> found = studentService.findById(id);
                    if (found.isEmpty()) {
                        System.out.println("Student not found.");
                    } else {
                        System.out.println(found.get());
                    }
                }
                case 7 -> {
                    List<Student> sorted = studentService.sortByName();
                    if (sorted.isEmpty()) {
                        System.out.println("No students found.");
                    } else {
                        System.out.println("Students sorted by name (A-Z):");
                        for (Student student : sorted) {
                            System.out.println(student);
                        }
                    }
                }
                case 8 -> {
                    List<Student> sorted = studentService.sortByAge();
                    if (sorted.isEmpty()) {
                        System.out.println("No students found.");
                    } else {
                        System.out.println("Students sorted by age (ascending):");
                        for (Student student : sorted) {
                            System.out.println(student);
                        }
                    }
                }
                case 9 -> {
                    int total = studentService.getTotalCount();
                    double avgAge = studentService.getAverageAge();
                    System.out.println("=== Student Statistics ===");
                    System.out.println("Total students: " + total);
                    System.out.printf("Average age: %.1f%n", avgAge);
                }
                case 10 -> {
                    running = false;
                    System.out.println("Exiting Student Management System.");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }
}
