package com.sms.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.sms.model.Student;

public class JsonStudentDAO implements StudentDAO {
    private static final String FILE_PATH = "students.json";
    private List<Student> students;
    private int nextId;

    public JsonStudentDAO() {
        this.students = new ArrayList<>();
        this.nextId = 1;
        loadFromFile();
    }

    @Override
    public void save(Student student) {
        student.setId(nextId++);
        students.add(student);
        saveToFile();
    }

    @Override
    public List<Student> findAll() {
        return new ArrayList<>(students);
    }

    @Override
    public Optional<Student> findById(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return Optional.of(student);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Student student) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId() == student.getId()) {
                students.set(i, student);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        boolean removed = students.removeIf(s -> s.getId() == id);
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    private void loadFromFile() {
        java.io.File file = new java.io.File(FILE_PATH);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            String json = content.toString().trim();
            if (json.isEmpty() || json.equals("[]")) {
                return;
            }
            parseJsonArray(json);
        } catch (IOException e) {
            System.out.println("Warning: Could not load students from file: " + e.getMessage());
        }
    }

    private void parseJsonArray(String json) {
        // Remove outer brackets: [{"id":1,...}, {"id":2,...}]
        String inner = json.substring(1, json.length() - 1).trim();
        if (inner.isEmpty()) {
            return;
        }

        // Split by "}, {" pattern
        String[] objects = splitJsonObjects(inner);

        int maxId = 0;
        for (String obj : objects) {
            Student student = parseJsonObject(obj.trim());
            if (student != null) {
                students.add(student);
                if (student.getId() > maxId) {
                    maxId = student.getId();
                }
            }
        }
        nextId = maxId + 1;
    }

    private String[] splitJsonObjects(String inner) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int start = 0;

        for (int i = 0; i < inner.length(); i++) {
            char c = inner.charAt(i);
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
            } else if (c == ',' && depth == 0) {
                objects.add(inner.substring(start, i));
                start = i + 1;
            }
        }
        objects.add(inner.substring(start));
        return objects.toArray(new String[0]);
    }

    private Student parseJsonObject(String json) {
        // Remove outer braces: {"id":1,"name":"John",...}
        String content = json.trim();
        if (content.startsWith("{")) {
            content = content.substring(1);
        }
        if (content.endsWith("}")) {
            content = content.substring(0, content.length() - 1);
        }

        int id = 0;
        String name = "";
        int age = 0;
        String course = "";

        // Parse fields by finding "key":"value" or "key":number patterns
        String[] fields = content.split(",");
        for (String field : fields) {
            String[] keyValue = field.split(":", 2);
            if (keyValue.length != 2) continue;

            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim();

            switch (key) {
                case "id" -> id = Integer.parseInt(value);
                case "name" -> name = unescapeJson(value);
                case "age" -> age = Integer.parseInt(value);
                case "course" -> course = unescapeJson(value);
            }
        }

        return new Student(id, name, age, course);
    }

    private String unescapeJson(String value) {
        // Remove surrounding quotes and unescape common JSON characters
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        value = value.replace("\\\"", "\"");
        value = value.replace("\\\\", "\\");
        return value;
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(toJson());
        } catch (IOException e) {
            System.out.println("Error: Could not save students to file: " + e.getMessage());
        }
    }

    private String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < students.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(studentToJson(students.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private String studentToJson(Student student) {
        return "{" +
                "\"id\":" + student.getId() + "," +
                "\"name\":\"" + escapeJson(student.getName()) + "\"," +
                "\"age\":" + student.getAge() + "," +
                "\"course\":\"" + escapeJson(student.getCourse()) + "\"" +
                "}";
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
