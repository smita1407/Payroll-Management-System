import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * CONTROLLER: Coordinates between the PayrollView and PayrollModel.
 * Registers event listeners, validates inputs, handles exceptions,
 * and maintains the flow of the application.
 */
public class PayrollController {
    private final PayrollModel model;
    private final PayrollView view;

    public PayrollController(PayrollModel model, PayrollView view) {
        this.model = model;
        this.view = view;
        initController();
    }

    private void initController() {
        // 1. Initialize SQLite Database Schema & Sample Data
        try {
            model.initDatabase();
            refreshAllData();
        } catch (SQLException e) {
            view.showMessage("Database Initialization Failed: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }

        // ==========================================
        // TAB 1: EMPLOYEE MANAGEMENT EVENT LISTENERS
        // ==========================================

        // Add Employee
        view.btnAddEmp.addActionListener(e -> handleAddEmployee());

        // Update Employee
        view.btnUpdateEmp.addActionListener(e -> handleUpdateEmployee());

        // Delete Employee
        view.btnDeleteEmp.addActionListener(e -> handleDeleteEmployee());

        // Clear Employee Fields
        view.btnClearEmp.addActionListener(e -> view.clearEmployeeInputs());

        // Search Employee
        view.btnSearchEmp.addActionListener(e -> handleSearchEmployee());
        view.btnRefreshEmp.addActionListener(e -> loadEmployees());

        // Table Row Selection -> Auto-populate form
        view.empTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = view.empTable.getSelectedRow();
                if (row != -1) {
                    view.txtEmpId.setText(view.empTableModel.getValueAt(row, 0).toString());
                    view.txtEmpId.setEditable(false); // ID should not be changed during update
                    view.txtEmpName.setText(view.empTableModel.getValueAt(row, 1).toString());
                    view.cmbDepartment.setSelectedItem(view.empTableModel.getValueAt(row, 2).toString());
                    view.txtDesignation.setText(view.empTableModel.getValueAt(row, 3).toString());
                    view.txtBasicSalary.setText(view.empTableModel.getValueAt(row, 4).toString());
                    view.txtEmail.setText(view.empTableModel.getValueAt(row, 5).toString());
                    view.txtPhone.setText(view.empTableModel.getValueAt(row, 6).toString());
                }
            }
        });

        // ==========================================
        // TAB 2: ATTENDANCE & PAYROLL EVENT LISTENERS
        // ==========================================

        // When Employee Selection changes in Payroll Tab
        view.cmbCalcEmployee.addActionListener(e -> updateCalcPreview());

        // Calculate Preview Button
        view.btnCalculatePreview.addActionListener(e -> updateCalcPreview());

        // Save & Process Payroll
        view.btnSavePayroll.addActionListener(e -> handleSavePayroll());

        // Reset Calc Form
        view.btnClearCalc.addActionListener(e -> {
            view.txtWorkingDays.setText("30");
            view.txtPresentDays.setText("30");
            view.txtBonus.setText("0.00");
            updateCalcPreview();
        });

        // ==========================================
        // TAB 3: PAY SLIP GENERATOR EVENT LISTENERS
        // ==========================================

        // Generate / View Payslip
        view.btnGenerateSlip.addActionListener(e -> handleDisplayPayslip());

        // Export Payslip as TXT
        view.btnExportSlip.addActionListener(e -> handleExportPayslip());

        // Print Payslip
        view.btnPrintSlip.addActionListener(e -> {
            try {
                if (view.txtSlipArea.getText().trim().isEmpty() || view.txtSlipArea.getText().contains("Please select")) {
                    view.showMessage("Please generate a payslip first!", "Print Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                boolean complete = view.txtSlipArea.print();
                if (complete) {
                    view.showMessage("Printing completed successfully.", "Print", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                view.showMessage("Printing failed: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ==========================================
        // TAB 4: PAYROLL HISTORY & REPORTS EVENT LISTENERS
        // ==========================================

        // Search Payroll Records
        view.btnSearchPayroll.addActionListener(e -> handleSearchPayroll());
        view.btnRefreshPayroll.addActionListener(e -> loadPayrollRecords());

        // Delete Payroll Record
        view.btnDeletePayroll.addActionListener(e -> handleDeletePayroll());

        // View Selected Slip directly in Tab 3
        view.btnViewSelectedSlip.addActionListener(e -> handleViewSelectedPayrollFromTable());
    }

    // ==========================================
    // LOGIC: EMPLOYEE OPERATIONS
    // ==========================================

    private void handleAddEmployee() {
        try {
            int empId = Integer.parseInt(view.txtEmpId.getText().trim());
            String name = view.txtEmpName.getText().trim();
            String dept = (String) view.cmbDepartment.getSelectedItem();
            String designation = view.txtDesignation.getText().trim();
            double basicSalary = Double.parseDouble(view.txtBasicSalary.getText().trim());
            String email = view.txtEmail.getText().trim();
            String phone = view.txtPhone.getText().trim();

            if (name.isEmpty() || designation.isEmpty()) {
                view.showMessage("Employee Name and Designation cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (basicSalary <= 0) {
                view.showMessage("Basic Salary must be greater than zero!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Employee emp = new Employee(empId, name, dept, designation, basicSalary, email, phone);
            model.addEmployee(emp);
            view.showMessage("Employee added successfully (ID: " + empId + ")!", "Success", JOptionPane.INFORMATION_MESSAGE);
            view.clearEmployeeInputs();
            refreshAllData();
        } catch (NumberFormatException ex) {
            view.showMessage("Invalid format! Please enter valid numeric values for Employee ID and Basic Salary.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            view.showMessage("Database Error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateEmployee() {
        try {
            int empId = Integer.parseInt(view.txtEmpId.getText().trim());
            String name = view.txtEmpName.getText().trim();
            String dept = (String) view.cmbDepartment.getSelectedItem();
            String designation = view.txtDesignation.getText().trim();
            double basicSalary = Double.parseDouble(view.txtBasicSalary.getText().trim());
            String email = view.txtEmail.getText().trim();
            String phone = view.txtPhone.getText().trim();

            if (name.isEmpty() || designation.isEmpty()) {
                view.showMessage("Employee Name and Designation cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Employee emp = new Employee(empId, name, dept, designation, basicSalary, email, phone);
            model.updateEmployee(emp);
            view.showMessage("Employee updated successfully (ID: " + empId + ")!", "Success", JOptionPane.INFORMATION_MESSAGE);
            view.clearEmployeeInputs();
            refreshAllData();
        } catch (NumberFormatException ex) {
            view.showMessage("Invalid input values. Please verify ID and Salary.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            view.showMessage("Error updating employee: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteEmployee() {
        try {
            if (view.txtEmpId.getText().trim().isEmpty()) {
                view.showMessage("Please select an employee to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int empId = Integer.parseInt(view.txtEmpId.getText().trim());
            int confirm = view.showConfirm("Are you sure you want to delete Employee #" + empId + "? This will also remove associated payrolls.", "Confirm Delete");
            if (confirm == JOptionPane.YES_OPTION) {
                model.deleteEmployee(empId);
                view.showMessage("Employee deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                view.clearEmployeeInputs();
                refreshAllData();
            }
        } catch (Exception ex) {
            view.showMessage("Error deleting employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearchEmployee() {
        String query = view.txtSearchEmp.getText().trim();
        if (query.isEmpty()) {
            loadEmployees();
            return;
        }
        try {
            List<Employee> list = model.searchEmployees(query);
            view.updateEmployeeTable(list);
        } catch (SQLException ex) {
            view.showMessage("Search failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // LOGIC: ATTENDANCE & PAYROLL CALCULATION
    // ==========================================

    private void updateCalcPreview() {
        Employee selectedEmp = (Employee) view.cmbCalcEmployee.getSelectedItem();
        if (selectedEmp == null) {
            resetPreviewLabels();
            return;
        }

        try {
            int workingDays = Integer.parseInt(view.txtWorkingDays.getText().trim());
            int presentDays = Integer.parseInt(view.txtPresentDays.getText().trim());
            double bonus = Double.parseDouble(view.txtBonus.getText().trim());

            if (workingDays <= 0) {
                view.lblPreviewGross.setText("Working days must be > 0");
                return;
            }
            if (presentDays > workingDays) {
                view.lblPreviewGross.setText("Present days cannot exceed working days!");
                return;
            }

            int absentDays = workingDays - presentDays;
            double basicSalary = selectedEmp.getBasicSalary();
            double dailyRate = basicSalary / (double) workingDays;
            double attendanceDeduction = absentDays * dailyRate;
            double earnedSalary = basicSalary - attendanceDeduction;
            double grossSalary = earnedSalary + bonus;
            double taxDeduction = model.calculateTaxDeduction(grossSalary);
            double netSalary = grossSalary - taxDeduction;

            view.lblPreviewEmpName.setText(selectedEmp.getName() + " (" + selectedEmp.getDepartment() + ")");
            view.lblPreviewBasic.setText(String.format("₹ %,.2f", basicSalary));
            view.lblPreviewAbsent.setText(absentDays + " days (Rate: ₹" + String.format("%.2f", dailyRate) + "/day)");
            view.lblPreviewLeaveDed.setText(String.format("- ₹ %,.2f", attendanceDeduction));
            view.lblPreviewBonus.setText(String.format("+ ₹ %,.2f", bonus));
            view.lblPreviewGross.setText(String.format("₹ %,.2f", grossSalary));
            view.lblPreviewTax.setText(String.format("- ₹ %,.2f", taxDeduction));
            view.lblPreviewNet.setText(String.format("₹ %,.2f", netSalary));

        } catch (NumberFormatException ignored) {
            // Wait for user to finish typing valid numbers
        }
    }

    private void resetPreviewLabels() {
        view.lblPreviewEmpName.setText("-");
        view.lblPreviewBasic.setText("₹ 0.00");
        view.lblPreviewAbsent.setText("0 days");
        view.lblPreviewLeaveDed.setText("₹ 0.00");
        view.lblPreviewBonus.setText("₹ 0.00");
        view.lblPreviewGross.setText("₹ 0.00");
        view.lblPreviewTax.setText("₹ 0.00");
        view.lblPreviewNet.setText("₹ 0.00");
    }

    private void handleSavePayroll() {
        Employee selectedEmp = (Employee) view.cmbCalcEmployee.getSelectedItem();
        if (selectedEmp == null) {
            view.showMessage("Please select an employee!", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int workingDays = Integer.parseInt(view.txtWorkingDays.getText().trim());
            int presentDays = Integer.parseInt(view.txtPresentDays.getText().trim());
            double bonus = Double.parseDouble(view.txtBonus.getText().trim());
            String monthYear = (String) view.cmbMonthYear.getSelectedItem();

            if (workingDays <= 0 || presentDays < 0 || bonus < 0) {
                view.showMessage("Please enter valid positive values for days and bonus.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (presentDays > workingDays) {
                view.showMessage("Present days (" + presentDays + ") cannot exceed working days (" + workingDays + ")!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            PayrollRecord saved = model.calculateAndSavePayroll(selectedEmp.getEmpId(), monthYear, workingDays, presentDays, bonus);
            loadPayrollRecords();

            int choice = JOptionPane.showConfirmDialog(view,
                    "Payroll processed and saved successfully for " + selectedEmp.getName() + "!\n"
                  + "Net Salary: INR " + String.format("%.2f", saved.getNetSalary()) + "\n\n"
                  + "Would you like to view the generated Pay Slip now?",
                    "Payroll Saved", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                view.tabbedPane.setSelectedIndex(2); // Switch to Payslip tab
                view.txtSlipArea.setText(model.generatePaySlipText(saved));
            }

        } catch (NumberFormatException ex) {
            view.showMessage("Please enter valid numeric values for working days, present days, and bonus.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            view.showMessage("Error processing payroll: " + ex.getMessage(), "Processing Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // LOGIC: PAY SLIP GENERATION & EXPORT
    // ==========================================

    private void handleDisplayPayslip() {
        PayrollRecord selected = (PayrollRecord) view.cmbSlipRecords.getSelectedItem();
        if (selected == null) {
            view.showMessage("No payroll record selected! Please calculate payroll first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String slip = model.generatePaySlipText(selected);
        view.txtSlipArea.setText(slip);
    }

    private void handleExportPayslip() {
        String slipContent = view.txtSlipArea.getText().trim();
        if (slipContent.isEmpty() || slipContent.contains("Please select")) {
            view.showMessage("Please generate a payslip first before exporting!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Payslip_" + System.currentTimeMillis() + ".txt"));
        int userSelection = fileChooser.showSaveDialog(view);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(slipContent);
                view.showMessage("Pay slip successfully saved to:\n" + fileToSave.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                view.showMessage("Failed to save file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==========================================
    // LOGIC: PAYROLL HISTORY & SEARCH
    // ==========================================

    private void handleSearchPayroll() {
        String query = view.txtSearchPayroll.getText().trim();
        if (query.isEmpty()) {
            loadPayrollRecords();
            return;
        }
        try {
            List<PayrollRecord> list = model.searchPayrollRecords(query);
            view.updatePayrollTable(list);
        } catch (SQLException ex) {
            view.showMessage("Search failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeletePayroll() {
        int selectedRow = view.payrollTable.getSelectedRow();
        if (selectedRow == -1) {
            view.showMessage("Please select a payroll record from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String slipIdStr = view.payrollTableModel.getValueAt(selectedRow, 0).toString().replace("PAY-", "");
        int payrollId = Integer.parseInt(slipIdStr);

        int confirm = view.showConfirm("Are you sure you want to delete Payroll Record PAY-" + payrollId + "?", "Confirm Delete");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                model.deletePayrollRecord(payrollId);
                view.showMessage("Payroll record deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadPayrollRecords();
            } catch (SQLException ex) {
                view.showMessage("Error deleting record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleViewSelectedPayrollFromTable() {
        int selectedRow = view.payrollTable.getSelectedRow();
        if (selectedRow == -1) {
            view.showMessage("Please select a record from the history table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String slipIdStr = view.payrollTableModel.getValueAt(selectedRow, 0).toString().replace("PAY-", "");
        int payrollId = Integer.parseInt(slipIdStr);

        try {
            PayrollRecord record = model.getPayrollRecordById(payrollId);
            if (record != null) {
                view.tabbedPane.setSelectedIndex(2); // Switch to Payslip Generator Tab
                view.txtSlipArea.setText(model.generatePaySlipText(record));
            }
        } catch (SQLException ex) {
            view.showMessage("Error loading payslip: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // DATA LOADING HELPERS
    // ==========================================

    private void refreshAllData() {
        loadEmployees();
        loadPayrollRecords();
    }

    private void loadEmployees() {
        try {
            List<Employee> employees = model.getAllEmployees();
            view.updateEmployeeTable(employees);
            view.updateEmployeeDropdown(employees);
            updateCalcPreview();
        } catch (SQLException e) {
            view.showMessage("Error loading employees: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPayrollRecords() {
        try {
            List<PayrollRecord> records = model.getAllPayrollRecords();
            view.updatePayrollTable(records);
            view.updatePayrollDropdown(records);
        } catch (SQLException e) {
            view.showMessage("Error loading payroll records: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

