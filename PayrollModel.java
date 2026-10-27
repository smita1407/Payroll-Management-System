import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * MODEL: Handles SQLite database connections, schema creation, 
 * CRUD operations, salary calculation algorithms, and payslip formatting.
 */
public class PayrollModel {
    private final String dbUrl = "jdbc:sqlite:payroll_system.db";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    public void initDatabase() throws SQLException {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            // 1. Employee Table
            String createEmployeeTable = "CREATE TABLE IF NOT EXISTS employees ("
                    + "emp_id INTEGER PRIMARY KEY, "
                    + "name TEXT NOT NULL, "
                    + "department TEXT NOT NULL, "
                    + "designation TEXT NOT NULL, "
                    + "basic_salary REAL NOT NULL, "
                    + "email TEXT, "
                    + "phone TEXT"
                    + ");";
            stmt.execute(createEmployeeTable);

            // 2. Payroll Table
            String createPayrollTable = "CREATE TABLE IF NOT EXISTS payroll ("
                    + "payroll_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "emp_id INTEGER NOT NULL, "
                    + "emp_name TEXT NOT NULL, "
                    + "department TEXT NOT NULL, "
                    + "month_year TEXT NOT NULL, "
                    + "working_days INTEGER NOT NULL, "
                    + "present_days INTEGER NOT NULL, "
                    + "basic_salary REAL NOT NULL, "
                    + "bonus REAL NOT NULL, "
                    + "attendance_deduction REAL NOT NULL, "
                    + "gross_salary REAL NOT NULL, "
                    + "tax_deduction REAL NOT NULL, "
                    + "net_salary REAL NOT NULL, "
                    + "generated_date TEXT NOT NULL, "
                    + "FOREIGN KEY(emp_id) REFERENCES employees(emp_id) ON DELETE CASCADE"
                    + ");";
            stmt.execute(createPayrollTable);
        }

        // Seed initial data if tables are empty
        seedDataIfEmpty();
    }

    private void seedDataIfEmpty() {
        try {
            if (getAllEmployees().isEmpty()) {
                addEmployee(new Employee(101, "Rahul Sharma", "Engineering", "Senior Software Engineer", 85000.0, "rahul.sharma@example.com", "9876543210"));
                addEmployee(new Employee(102, "Priya Patel", "Human Resources", "HR Manager", 65000.0, "priya.patel@example.com", "9876543211"));
                addEmployee(new Employee(103, "Amit Verma", "Finance", "Accountant", 50000.0, "amit.verma@example.com", "9876543212"));
                addEmployee(new Employee(104, "Sneha Kulkarni", "Marketing", "Marketing Lead", 72000.0, "sneha.k@example.com", "9876543213"));
                addEmployee(new Employee(105, "Vikram Singh", "Operations", "Operations Executive", 38000.0, "vikram.s@example.com", "9876543214"));

                // Seed sample payroll records
                calculateAndSavePayroll(101, "October 2026", 30, 29, 5000.0);
                calculateAndSavePayroll(102, "October 2026", 30, 30, 3000.0);
                calculateAndSavePayroll(103, "October 2026", 30, 27, 1500.0);
            }
        } catch (Exception e) {
            System.err.println("Note: Seeding initial data skipped: " + e.getMessage());
        }
    }

    // ==========================================
    // EMPLOYEE CRUD OPERATIONS
    // ==========================================

    public void addEmployee(Employee emp) throws SQLException {
        String sql = "INSERT INTO employees(emp_id, name, department, designation, basic_salary, email, phone) "
                   + "VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, emp.getEmpId());
            pstmt.setString(2, emp.getName());
            pstmt.setString(3, emp.getDepartment());
            pstmt.setString(4, emp.getDesignation());
            pstmt.setDouble(5, emp.getBasicSalary());
            pstmt.setString(6, emp.getEmail());
            pstmt.setString(7, emp.getPhone());
            pstmt.executeUpdate();
        }
    }

    public void updateEmployee(Employee emp) throws SQLException {
        String sql = "UPDATE employees SET name=?, department=?, designation=?, basic_salary=?, email=?, phone=? "
                   + "WHERE emp_id=?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emp.getName());
            pstmt.setString(2, emp.getDepartment());
            pstmt.setString(3, emp.getDesignation());
            pstmt.setDouble(4, emp.getBasicSalary());
            pstmt.setString(5, emp.getEmail());
            pstmt.setString(6, emp.getPhone());
            pstmt.setInt(7, emp.getEmpId());
            pstmt.executeUpdate();
        }
    }

    public void deleteEmployee(int empId) throws SQLException {
        String sql = "DELETE FROM employees WHERE emp_id=?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, empId);
            pstmt.executeUpdate();
        }
    }

    public Employee getEmployeeById(int empId) throws SQLException {
        String sql = "SELECT * FROM employees WHERE emp_id=?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, empId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                            rs.getInt("emp_id"),
                            rs.getString("name"),
                            rs.getString("department"),
                            rs.getString("designation"),
                            rs.getDouble("basic_salary"),
                            rs.getString("email"),
                            rs.getString("phone")
                    );
                }
            }
        }
        return null;
    }

    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY emp_id ASC";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Employee(
                        rs.getInt("emp_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("designation"),
                        rs.getDouble("basic_salary"),
                        rs.getString("email"),
                        rs.getString("phone")
                ));
            }
        }
        return list;
    }

    public List<Employee> searchEmployees(String query) throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE emp_id LIKE ? OR name LIKE ? OR department LIKE ? OR designation LIKE ? ORDER BY emp_id ASC";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String wildcard = "%" + query + "%";
            pstmt.setString(1, wildcard);
            pstmt.setString(2, wildcard);
            pstmt.setString(3, wildcard);
            pstmt.setString(4, wildcard);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Employee(
                            rs.getInt("emp_id"),
                            rs.getString("name"),
                            rs.getString("department"),
                            rs.getString("designation"),
                            rs.getDouble("basic_salary"),
                            rs.getString("email"),
                            rs.getString("phone")
                    ));
                }
            }
        }
        return list;
    }

    // ==========================================
    // PAYROLL CALCULATION & BUSINESS LOGIC
    // ==========================================

    /**
     * Calculates tax deduction based on monthly salary brackets:
     * - Up to ₹30,000 : 0%
     * - ₹30,001 to ₹60,000 : 5% on amount over 30,000
     * - ₹60,001 to ₹100,000 : ₹1,500 + 10% on amount over 60,000
     * - Above ₹100,000 : ₹5,500 + 20% on amount over 100,000
     */
    public double calculateTaxDeduction(double grossSalary) {
        if (grossSalary <= 30000.0) {
            return 0.0;
        } else if (grossSalary <= 60000.0) {
            return (grossSalary - 30000.0) * 0.05;
        } else if (grossSalary <= 100000.0) {
            return 1500.0 + (grossSalary - 60000.0) * 0.10;
        } else {
            return 5500.0 + (grossSalary - 100000.0) * 0.20;
        }
    }

    public PayrollRecord calculateAndSavePayroll(int empId, String monthYear, int workingDays, int presentDays, double bonus) throws SQLException {
        Employee emp = getEmployeeById(empId);
        if (emp == null) {
            throw new IllegalArgumentException("Employee ID " + empId + " not found!");
        }

        if (presentDays > workingDays) {
            throw new IllegalArgumentException("Present days (" + presentDays + ") cannot exceed working days (" + workingDays + ")!");
        }

        double basicSalary = emp.getBasicSalary();
        double dailyRate = basicSalary / (double) workingDays;
        int absentDays = workingDays - presentDays;
        double attendanceDeduction = absentDays * dailyRate;

        // Earned salary before bonus
        double earnedSalary = basicSalary - attendanceDeduction;
        double grossSalary = earnedSalary + bonus;

        // Tax deduction calculated on gross earnings
        double taxDeduction = calculateTaxDeduction(grossSalary);
        double netSalary = grossSalary - taxDeduction;

        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

        String sql = "INSERT INTO payroll(emp_id, emp_name, department, month_year, working_days, present_days, "
                   + "basic_salary, bonus, attendance_deduction, gross_salary, tax_deduction, net_salary, generated_date) "
                   + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int generatedId = 0;
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, emp.getEmpId());
            pstmt.setString(2, emp.getName());
            pstmt.setString(3, emp.getDepartment());
            pstmt.setString(4, monthYear);
            pstmt.setInt(5, workingDays);
            pstmt.setInt(6, presentDays);
            pstmt.setDouble(7, basicSalary);
            pstmt.setDouble(8, bonus);
            pstmt.setDouble(9, attendanceDeduction);
            pstmt.setDouble(10, grossSalary);
            pstmt.setDouble(11, taxDeduction);
            pstmt.setDouble(12, netSalary);
            pstmt.setString(13, currentDate);
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    generatedId = keys.getInt(1);
                }
            }
        }

        return new PayrollRecord(generatedId, emp.getEmpId(), emp.getName(), emp.getDepartment(), monthYear,
                workingDays, presentDays, basicSalary, bonus, attendanceDeduction, grossSalary, taxDeduction,
                netSalary, currentDate);
    }

    public List<PayrollRecord> getAllPayrollRecords() throws SQLException {
        List<PayrollRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM payroll ORDER BY payroll_id DESC";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapPayrollRecord(rs));
            }
        }
        return list;
    }

    public List<PayrollRecord> searchPayrollRecords(String query) throws SQLException {
        List<PayrollRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM payroll WHERE emp_id LIKE ? OR emp_name LIKE ? OR department LIKE ? OR month_year LIKE ? ORDER BY payroll_id DESC";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String wildcard = "%" + query + "%";
            pstmt.setString(1, wildcard);
            pstmt.setString(2, wildcard);
            pstmt.setString(3, wildcard);
            pstmt.setString(4, wildcard);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPayrollRecord(rs));
                }
            }
        }
        return list;
    }

    public PayrollRecord getPayrollRecordById(int payrollId) throws SQLException {
        String sql = "SELECT * FROM payroll WHERE payroll_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payrollId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapPayrollRecord(rs);
                }
            }
        }
        return null;
    }

    public void deletePayrollRecord(int payrollId) throws SQLException {
        String sql = "DELETE FROM payroll WHERE payroll_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payrollId);
            pstmt.executeUpdate();
        }
    }

    private PayrollRecord mapPayrollRecord(ResultSet rs) throws SQLException {
        return new PayrollRecord(
                rs.getInt("payroll_id"),
                rs.getInt("emp_id"),
                rs.getString("emp_name"),
                rs.getString("department"),
                rs.getString("month_year"),
                rs.getInt("working_days"),
                rs.getInt("present_days"),
                rs.getDouble("basic_salary"),
                rs.getDouble("bonus"),
                rs.getDouble("attendance_deduction"),
                rs.getDouble("gross_salary"),
                rs.getDouble("tax_deduction"),
                rs.getDouble("net_salary"),
                rs.getString("generated_date")
        );
    }

    // ==========================================
    // PAY SLIP FORMATTER
    // ==========================================

    public String generatePaySlipText(PayrollRecord r) {
        StringBuilder sb = new StringBuilder();
        sb.append("=================================================================================\n");
        sb.append("                          ACME ENTERPRISES PRIVATE LIMITED                       \n");
        sb.append("                     Technology Park, Phase 1, Bangalore - 560100                 \n");
        sb.append("                             SALARY PAY SLIP - ").append(r.getMonthYear().toUpperCase()).append("\n");
        sb.append("=================================================================================\n");
        sb.append(String.format(" Slip ID       : PAY-%05d                    Generated Date : %s\n", r.getPayrollId(), r.getGeneratedDate()));
        sb.append(String.format(" Employee ID   : %-15d               Department     : %s\n", r.getEmpId(), r.getDepartment()));
        sb.append(String.format(" Employee Name : %-15s               Period         : %s\n", r.getEmpName(), r.getMonthYear()));
        sb.append("---------------------------------------------------------------------------------\n");
        sb.append(" ATTENDANCE SUMMARY:\n");
        sb.append(String.format("   Total Working Days : %-5d   Days Present : %-5d   Days Absent : %-5d\n",
                r.getWorkingDays(), r.getPresentDays(), r.getAbsentDays()));
        sb.append("---------------------------------------------------------------------------------\n");
        sb.append(String.format(" %-40s | %-36s\n", "EARNINGS / CREDITS", "DEDUCTIONS"));
        sb.append("---------------------------------------------------------------------------------\n");
        sb.append(String.format(" Basic Fixed Salary   : INR %12.2f | Attendance Leave Loss : INR %12.2f\n", r.getBasicSalary(), r.getAttendanceDeduction()));
        sb.append(String.format(" Performance Bonus    : INR %12.2f | Income Tax (TDS)      : INR %12.2f\n", r.getBonus(), r.getTaxDeduction()));
        sb.append("---------------------------------------------------------------------------------\n");
        sb.append(String.format(" Gross Earned Salary  : INR %12.2f | Total Deductions      : INR %12.2f\n",
                r.getGrossSalary(), (r.getAttendanceDeduction() + r.getTaxDeduction())));
        sb.append("=================================================================================\n");
        sb.append(String.format(" NET PAYABLE SALARY   : INR %12.2f\n", r.getNetSalary()));
        sb.append("=================================================================================\n");
        sb.append(" Note: This is a computer-generated salary slip and does not require signature.\n");
        sb.append(" System: Java Swing MVC & SQLite Relational Database Engine.\n");
        sb.append("=================================================================================\n");
        return sb.toString();
    }
}

