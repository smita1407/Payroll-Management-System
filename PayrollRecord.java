public class PayrollRecord {
    private int payrollId;
    private int empId;
    private String empName;
    private String department;
    private String monthYear;
    private int workingDays;
    private int presentDays;
    private double basicSalary;
    private double bonus;
    private double attendanceDeduction;
    private double grossSalary;
    private double taxDeduction;
    private double netSalary;
    private String generatedDate;

    public PayrollRecord() {}

    public PayrollRecord(int payrollId, int empId, String empName, String department, String monthYear,
                         int workingDays, int presentDays, double basicSalary, double bonus,
                         double attendanceDeduction, double grossSalary, double taxDeduction,
                         double netSalary, String generatedDate) {
        this.payrollId = payrollId;
        this.empId = empId;
        this.empName = empName;
        this.department = department;
        this.monthYear = monthYear;
        this.workingDays = workingDays;
        this.presentDays = presentDays;
        this.basicSalary = basicSalary;
        this.bonus = bonus;
        this.attendanceDeduction = attendanceDeduction;
        this.grossSalary = grossSalary;
        this.taxDeduction = taxDeduction;
        this.netSalary = netSalary;
        this.generatedDate = generatedDate;
    }

    // Getters and Setters
    public int getPayrollId() { return payrollId; }
    public void setPayrollId(int payrollId) { this.payrollId = payrollId; }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getMonthYear() { return monthYear; }
    public void setMonthYear(String monthYear) { this.monthYear = monthYear; }

    public int getWorkingDays() { return workingDays; }
    public void setWorkingDays(int workingDays) { this.workingDays = workingDays; }

    public int getPresentDays() { return presentDays; }
    public void setPresentDays(int presentDays) { this.presentDays = presentDays; }

    public int getAbsentDays() { return workingDays - presentDays; }

    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }

    public double getBonus() { return bonus; }
    public void setBonus(double bonus) { this.bonus = bonus; }

    public double getAttendanceDeduction() { return attendanceDeduction; }
    public void setAttendanceDeduction(double attendanceDeduction) { this.attendanceDeduction = attendanceDeduction; }

    public double getGrossSalary() { return grossSalary; }
    public void setGrossSalary(double grossSalary) { this.grossSalary = grossSalary; }

    public double getTaxDeduction() { return taxDeduction; }
    public void setTaxDeduction(double taxDeduction) { this.taxDeduction = taxDeduction; }

    public double getNetSalary() { return netSalary; }
    public void setNetSalary(double netSalary) { this.netSalary = netSalary; }

    public String getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(String generatedDate) { this.generatedDate = generatedDate; }

    @Override
    public String toString() {
        return "Record #" + payrollId + " - Emp " + empId + " (" + empName + ") - " + monthYear;
    }
}

