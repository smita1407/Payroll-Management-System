public class Employee {
    private int empId;
    private String name;
    private String department;
    private String designation;
    private double basicSalary;
    private String email;
    private String phone;

    public Employee() {}

    public Employee(int empId, String name, String department, String designation, double basicSalary, String email, String phone) {
        this.empId = empId;
        this.name = name;
        this.department = department;
        this.designation = designation;
        this.basicSalary = basicSalary;
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters
    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return empId + " - " + name + " (" + designation + ")";
    }
}

