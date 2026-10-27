import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * VIEW: Graphical User Interface implemented using Java Swing.
 * Exposes UI controls, tables, and dialogs to the Controller.
 */
public class PayrollView extends JFrame {

    // --- Tabbed Pane ---
    public JTabbedPane tabbedPane = new JTabbedPane();

    // ==========================================
    // TAB 1: EMPLOYEE MANAGEMENT COMPONENTS
    // ==========================================
    public JTextField txtEmpId = new JTextField();
    public JTextField txtEmpName = new JTextField();
    public JComboBox<String> cmbDepartment = new JComboBox<>(new String[]{
            "Engineering", "Human Resources", "Finance", "Marketing", "Operations", "Sales", "Customer Support"
    });
    public JTextField txtDesignation = new JTextField();
    public JTextField txtBasicSalary = new JTextField();
    public JTextField txtEmail = new JTextField();
    public JTextField txtPhone = new JTextField();

    public JButton btnAddEmp = new JButton("Add Employee");
    public JButton btnUpdateEmp = new JButton("Update Employee");
    public JButton btnDeleteEmp = new JButton("Delete Employee");
    public JButton btnClearEmp = new JButton("Clear Fields");

    public JTextField txtSearchEmp = new JTextField(15);
    public JButton btnSearchEmp = new JButton("Search");
    public JButton btnRefreshEmp = new JButton("Show All");

    public DefaultTableModel empTableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Department", "Designation", "Basic Salary (₹)", "Email", "Phone"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    public JTable empTable = new JTable(empTableModel);

    // ==========================================
    // TAB 2: ATTENDANCE & PAYROLL CALCULATION
    // ==========================================
    public JComboBox<Employee> cmbCalcEmployee = new JComboBox<>();
    public JComboBox<String> cmbMonthYear = new JComboBox<>(new String[]{
            "October 2026", "November 2026", "December 2026", "January 2027", "February 2027",
            "March 2027", "April 2027", "May 2027", "June 2027", "July 2027", "August 2027", "September 2027"
    });
    public JTextField txtWorkingDays = new JTextField("30");
    public JTextField txtPresentDays = new JTextField("30");
    public JTextField txtBonus = new JTextField("0.00");

    // Preview Labels
    public JLabel lblPreviewEmpName = new JLabel("-");
    public JLabel lblPreviewBasic = new JLabel("₹ 0.00");
    public JLabel lblPreviewAbsent = new JLabel("0 days");
    public JLabel lblPreviewLeaveDed = new JLabel("₹ 0.00");
    public JLabel lblPreviewBonus = new JLabel("₹ 0.00");
    public JLabel lblPreviewGross = new JLabel("₹ 0.00");
    public JLabel lblPreviewTax = new JLabel("₹ 0.00");
    public JLabel lblPreviewNet = new JLabel("₹ 0.00");

    public JButton btnCalculatePreview = new JButton("1. Calculate Preview");
    public JButton btnSavePayroll = new JButton("2. Save & Process Payroll");
    public JButton btnClearCalc = new JButton("Reset");

    // ==========================================
    // TAB 3: PAY SLIP GENERATOR
    // ==========================================
    public JComboBox<PayrollRecord> cmbSlipRecords = new JComboBox<>();
    public JButton btnGenerateSlip = new JButton("View Pay Slip");
    public JButton btnExportSlip = new JButton("Save / Export as .TXT");
    public JButton btnPrintSlip = new JButton("Print Pay Slip");
    public JTextArea txtSlipArea = new JTextArea();

    // ==========================================
    // TAB 4: PAYROLL HISTORY & REPORTS
    // ==========================================
    public JTextField txtSearchPayroll = new JTextField(15);
    public JButton btnSearchPayroll = new JButton("Search History");
    public JButton btnRefreshPayroll = new JButton("Refresh");
    public JButton btnDeletePayroll = new JButton("Delete Record");
    public JButton btnViewSelectedSlip = new JButton("Open in Payslip Viewer");

    public DefaultTableModel payrollTableModel = new DefaultTableModel(
            new String[]{"Slip ID", "Emp ID", "Name", "Department", "Period", "Present/Work", "Basic (₹)", "Bonus (₹)", "Leave Ded (₹)", "Gross (₹)", "Tax (₹)", "Net Salary (₹)", "Date"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    public JTable payrollTable = new JTable(payrollTableModel);

    public JLabel lblTotalPayrollCount = new JLabel("Total Records: 0");
    public JLabel lblTotalNetDisbursed = new JLabel("Total Disbursed: ₹ 0.00");
    public JLabel lblTotalTaxCollected = new JLabel("Total Tax (TDS): ₹ 0.00");

    public PayrollView() {
        setTitle("Employee Payroll System - MVC & SQLite [Group: A2 | Team: 7]");
        setSize(1050, 720);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Banner
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Build Tabs
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.addTab("👤 Employee Management", buildEmployeeTab());
        tabbedPane.addTab("⚡ Attendance & Payroll", buildPayrollCalcTab());
        tabbedPane.addTab("📄 Pay Slip Generator", buildPaySlipTab());
        tabbedPane.addTab("📊 Payroll History & Reports", buildPayrollHistoryTab());

        add(tabbedPane, BorderLayout.CENTER);

        // Style tables
        styleTable(empTable);
        styleTable(payrollTable);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(24, 43, 73));
        panel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("EMPLOYEE PAYROLL SYSTEM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Class Group: A2 | Team No: 7 | Java Swing GUI + SQLite Relational Database (MVC)");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(200, 220, 255));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subtitleLabel, BorderLayout.SOUTH);
        return panel;
    }

    // --- TAB 1: EMPLOYEE MANAGEMENT UI ---
    private JPanel buildEmployeeTab() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left Form Panel
        JPanel formContainer = new JPanel(new BorderLayout(5, 5));
        formContainer.setPreferredSize(new Dimension(340, 0));
        formContainer.setBorder(BorderFactory.createTitledBorder("Employee Details"));

        JPanel fieldsPanel = new JPanel(new GridLayout(7, 2, 8, 10));
        fieldsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        fieldsPanel.add(new JLabel("Employee ID:"));
        fieldsPanel.add(txtEmpId);
        fieldsPanel.add(new JLabel("Full Name:"));
        fieldsPanel.add(txtEmpName);
        fieldsPanel.add(new JLabel("Department:"));
        fieldsPanel.add(cmbDepartment);
        fieldsPanel.add(new JLabel("Designation:"));
        fieldsPanel.add(txtDesignation);
        fieldsPanel.add(new JLabel("Basic Salary (₹):"));
        fieldsPanel.add(txtBasicSalary);
        fieldsPanel.add(new JLabel("Email Address:"));
        fieldsPanel.add(txtEmail);
        fieldsPanel.add(new JLabel("Phone Number:"));
        fieldsPanel.add(txtPhone);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        styleButton(btnAddEmp, new Color(40, 140, 60));
        styleButton(btnUpdateEmp, new Color(30, 110, 190));
        styleButton(btnDeleteEmp, new Color(190, 40, 40));
        styleButton(btnClearEmp, new Color(100, 100, 100));

        buttonPanel.add(btnAddEmp);
        buttonPanel.add(btnUpdateEmp);
        buttonPanel.add(btnDeleteEmp);
        buttonPanel.add(btnClearEmp);

        formContainer.add(fieldsPanel, BorderLayout.CENTER);
        formContainer.add(buttonPanel, BorderLayout.SOUTH);

        // Right Table Panel
        JPanel tableContainer = new JPanel(new BorderLayout(5, 5));
        tableContainer.setBorder(BorderFactory.createTitledBorder("Registered Employees List (Click a row to edit)"));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.add(new JLabel("Filter / Search:"));
        searchBar.add(txtSearchEmp);
        searchBar.add(btnSearchEmp);
        searchBar.add(btnRefreshEmp);

        tableContainer.add(searchBar, BorderLayout.NORTH);
        tableContainer.add(new JScrollPane(empTable), BorderLayout.CENTER);

        mainPanel.add(formContainer, BorderLayout.WEST);
        mainPanel.add(tableContainer, BorderLayout.CENTER);
        return mainPanel;
    }

    // --- TAB 2: ATTENDANCE & PAYROLL CALCULATION UI ---
    private JPanel buildPayrollCalcTab() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Input Details Box
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 12));
        inputPanel.setBorder(BorderFactory.createTitledBorder("1. Monthly Attendance & Payroll Inputs"));

        inputPanel.add(new JLabel("Select Employee:"));
        inputPanel.add(cmbCalcEmployee);
        inputPanel.add(new JLabel("Salary Month / Year:"));
        inputPanel.add(cmbMonthYear);
        inputPanel.add(new JLabel("Total Working Days:"));
        inputPanel.add(txtWorkingDays);
        inputPanel.add(new JLabel("Days Present:"));
        inputPanel.add(txtPresentDays);
        inputPanel.add(new JLabel("Bonus / Incentives (₹):"));
        inputPanel.add(txtBonus);

        // Live Breakdown Preview Box
        JPanel previewPanel = new JPanel(new GridLayout(8, 2, 8, 8));
        previewPanel.setBorder(BorderFactory.createTitledBorder("2. Salary & Deduction Breakdown Preview"));

        lblPreviewNet.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPreviewNet.setForeground(new Color(20, 120, 40));

        previewPanel.add(new JLabel("Selected Employee:"));
        previewPanel.add(lblPreviewEmpName);
        previewPanel.add(new JLabel("Monthly Basic Pay:"));
        previewPanel.add(lblPreviewBasic);
        previewPanel.add(new JLabel("Unpaid Absent Days:"));
        previewPanel.add(lblPreviewAbsent);
        previewPanel.add(new JLabel("Attendance Deduction:"));
        previewPanel.add(lblPreviewLeaveDed);
        previewPanel.add(new JLabel("Performance Bonus:"));
        previewPanel.add(lblPreviewBonus);
        previewPanel.add(new JLabel("Gross Earned Salary:"));
        previewPanel.add(lblPreviewGross);
        previewPanel.add(new JLabel("Tax Deduction (TDS):"));
        previewPanel.add(lblPreviewTax);
        previewPanel.add(new JLabel("NET PAYABLE SALARY:"));
        previewPanel.add(lblPreviewNet);

        JPanel topGrid = new JPanel(new GridLayout(1, 2, 15, 15));
        topGrid.add(inputPanel);
        topGrid.add(previewPanel);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        styleButton(btnCalculatePreview, new Color(40, 100, 180));
        styleButton(btnSavePayroll, new Color(30, 140, 60));
        styleButton(btnClearCalc, new Color(110, 110, 110));

        btnCalculatePreview.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSavePayroll.setFont(new Font("Segoe UI", Font.BOLD, 13));

        actions.add(btnCalculatePreview);
        actions.add(btnSavePayroll);
        actions.add(btnClearCalc);

        // Explanatory Note Panel
        JPanel notePanel = new JPanel(new BorderLayout());
        notePanel.setBorder(BorderFactory.createTitledBorder("Salary Calculation Logic Reference"));
        JTextArea txtLogic = new JTextArea(
                "• Daily Rate = Basic Salary / Total Working Days\n"
              + "• Absent Days = Working Days - Days Present  ==>  Attendance Deduction = Absent Days × Daily Rate\n"
              + "• Gross Salary = (Basic Salary - Attendance Deduction) + Bonus\n"
              + "• Tax Deductions: Slab-based TDS (Up to ₹30k: 0%, ₹30k-60k: 5%, ₹60k-100k: 10%, >₹100k: 20%)\n"
              + "• Net Payable Salary = Gross Salary - Tax Deduction"
        );
        txtLogic.setEditable(false);
        txtLogic.setBackground(new Color(245, 248, 255));
        txtLogic.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtLogic.setBorder(new EmptyBorder(8, 8, 8, 8));
        notePanel.add(txtLogic, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(actions, BorderLayout.NORTH);
        centerPanel.add(notePanel, BorderLayout.CENTER);

        mainPanel.add(topGrid, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    // --- TAB 3: PAY SLIP GENERATOR UI ---
    private JPanel buildPaySlipTab() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topBar.add(new JLabel("Select Payroll Record:"));
        cmbSlipRecords.setPreferredSize(new Dimension(350, 28));
        topBar.add(cmbSlipRecords);

        styleButton(btnGenerateSlip, new Color(30, 110, 190));
        styleButton(btnExportSlip, new Color(40, 140, 60));
        styleButton(btnPrintSlip, new Color(80, 80, 80));

        topBar.add(btnGenerateSlip);
        topBar.add(btnExportSlip);
        topBar.add(btnPrintSlip);

        txtSlipArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtSlipArea.setEditable(false);
        txtSlipArea.setBackground(new Color(252, 252, 252));
        txtSlipArea.setText("\n   Please select a payroll record from the top dropdown and click 'View Pay Slip'.");

        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(txtSlipArea), BorderLayout.CENTER);
        return mainPanel;
    }

    // --- TAB 4: PAYROLL HISTORY & REPORTS UI ---
    private JPanel buildPayrollHistoryTab() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Filter Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topBar.add(new JLabel("Search Payrolls:"));
        topBar.add(txtSearchPayroll);
        topBar.add(btnSearchPayroll);
        topBar.add(btnRefreshPayroll);
        topBar.add(Box.createHorizontalStrut(20));

        styleButton(btnViewSelectedSlip, new Color(30, 110, 190));
        styleButton(btnDeletePayroll, new Color(190, 40, 40));

        topBar.add(btnViewSelectedSlip);
        topBar.add(btnDeletePayroll);

        // Bottom Summary Bar
        JPanel bottomBar = new JPanel(new GridLayout(1, 3, 10, 5));
        bottomBar.setBorder(new EmptyBorder(8, 8, 8, 8));
        bottomBar.setBackground(new Color(240, 245, 250));

        lblTotalPayrollCount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalNetDisbursed.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalNetDisbursed.setForeground(new Color(20, 120, 40));
        lblTotalTaxCollected.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalTaxCollected.setForeground(new Color(170, 40, 40));

        bottomBar.add(lblTotalPayrollCount);
        bottomBar.add(lblTotalNetDisbursed);
        bottomBar.add(lblTotalTaxCollected);

        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(payrollTable), BorderLayout.CENTER);
        mainPanel.add(bottomBar, BorderLayout.SOUTH);
        return mainPanel;
    }

    // ==========================================
    // UI HELPERS & TABLE REFRESH METHODS
    // ==========================================

    public void updateEmployeeTable(List<Employee> employees) {
        empTableModel.setRowCount(0);
        for (Employee e : employees) {
            empTableModel.addRow(new Object[]{
                    e.getEmpId(),
                    e.getName(),
                    e.getDepartment(),
                    e.getDesignation(),
                    String.format("%.2f", e.getBasicSalary()),
                    e.getEmail(),
                    e.getPhone()
            });
        }
    }

    public void updateEmployeeDropdown(List<Employee> employees) {
        cmbCalcEmployee.removeAllItems();
        for (Employee e : employees) {
            cmbCalcEmployee.addItem(e);
        }
    }

    public void updatePayrollTable(List<PayrollRecord> records) {
        payrollTableModel.setRowCount(0);
        double totalDisbursed = 0.0;
        double totalTax = 0.0;

        for (PayrollRecord r : records) {
            payrollTableModel.addRow(new Object[]{
                    "PAY-" + r.getPayrollId(),
                    r.getEmpId(),
                    r.getEmpName(),
                    r.getDepartment(),
                    r.getMonthYear(),
                    r.getPresentDays() + "/" + r.getWorkingDays(),
                    String.format("%.2f", r.getBasicSalary()),
                    String.format("%.2f", r.getBonus()),
                    String.format("%.2f", r.getAttendanceDeduction()),
                    String.format("%.2f", r.getGrossSalary()),
                    String.format("%.2f", r.getTaxDeduction()),
                    String.format("%.2f", r.getNetSalary()),
                    r.getGeneratedDate()
            });
            totalDisbursed += r.getNetSalary();
            totalTax += r.getTaxDeduction();
        }

        lblTotalPayrollCount.setText("Total Records: " + records.size());
        lblTotalNetDisbursed.setText(String.format("Total Disbursed: ₹ %,.2f", totalDisbursed));
        lblTotalTaxCollected.setText(String.format("Total Tax (TDS): ₹ %,.2f", totalTax));
    }

    public void updatePayrollDropdown(List<PayrollRecord> records) {
        cmbSlipRecords.removeAllItems();
        for (PayrollRecord r : records) {
            cmbSlipRecords.addItem(r);
        }
    }

    public void clearEmployeeInputs() {
        txtEmpId.setText("");
        txtEmpName.setText("");
        cmbDepartment.setSelectedIndex(0);
        txtDesignation.setText("");
        txtBasicSalary.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtEmpId.setEditable(true);
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public int showConfirm(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(230, 235, 245));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
    }
}

