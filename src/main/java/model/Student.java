package model;

public class Student {

    private int studentId;
    private String name;
    private String email;
    private String photoPath;
    private String backgroundColor;

    public Student(
            int studentId,
            String name,
            String email,
            String photoPath,
            String backgroundColor
    ) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.photoPath = photoPath;
        this.backgroundColor = backgroundColor;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}