# ACADEMIC PROJECT REPORT

## Mini Project: Employee Payroll System
**Course:** Object-Oriented Programming with Java  
**Class Group:** A2  
**Team No:** 7  
**Submission Deadline:** 2-Nov-2026  

---

## 1. ABSTRACT
The **Employee Payroll System** is a standalone desktop application developed in Java following the **Model-View-Controller (MVC)** architectural pattern and utilizing **SQLite** as the relational database backend via Java Database Connectivity (JDBC). The primary goal of the system is to automate human resource and payroll operations, including maintaining employee demographic and compensation records, recording monthly attendance, computing earned wages based on working and present days, incorporating performance bonuses, calculating income tax deductions (TDS) based on progressive tax slabs, generating formatted salary payslips, and exporting/printing financial receipts. By decoupling database management (Model), graphical presentation (View), and event coordination (Controller), the system achieves modularity, maintainability, and data integrity.

---

## 2. PROBLEM STATEMENT & OBJECTIVES

### 2.1 Problem Statement
Traditional manual payroll calculations are prone to human computation errors, delay in generating salary receipts, lack of proper attendance reconciliation, and inconsistent tax deduction application. 

### 2.2 Objectives
1. Implement a desktop GUI using **Java Swing** for intuitive user interactions.
2. Follow the **MVC design pattern** strictly to decouple business logic from UI components.
3. Establish robust relational database persistence using **SQLite JDBC**.
4. Support full **CRUD operations** on employee records.
5. Accurately calculate monthly salaries incorporating:
   - Attendance loss deductions based on pro-rata daily wage rates.
   - Performance bonuses and incentives.
   - Automated income tax deductions (TDS) using standard tax slabs.
   - Net payable salary computation.
6. Provide an automated **Pay Slip Generator** with export-to-text and native printing capabilities.
7. Maintain comprehensive **historical payroll records** with total expenditure summaries.

---

## 3. SYSTEM ARCHITECTURE (MVC PATTERN)

The system is partitioned into three decoupled layers:

```
                            +-------------------+
                            |       USER        |
                            +-------------------+
                                      │
                               Interacts with UI
                                      ▼
                        +---------------------------+
                        |           VIEW            |
                        |    (PayrollView.java)     |
                        | - Swing Frames & Tabs     |
                        | - Input Forms & JTables   |
                        | - Payslip Text Display    |
                        +---------------------------+
                                      │
                         Events fired │ Updates UI
                                      ▼
                        +---------------------------+
                        |        CONTROLLER         |
                        |  (PayrollController.java) |
                        | - ActionListeners         |
                        | - Input Validation        |
                        | - Orchestrates Data Flow  |
                        +---------------------------+
                                      │
                          Invokes SQL │ Returns entities
                                      ▼
                        +---------------------------+
                        |           MODEL           |
                        |    (PayrollModel.java)    |
                        | - JDBC SQLite Connection  |
                        | - CRUD Database Queries   |
                        | - Salary / Tax Math Logic |
                        | - Payslip String Builder  |
                        +---------------------------+
                                      │
                                      ▼
                        +---------------------------+
                        |      SQLITE DATABASE      |
                        |   (payroll_system.db)     |
                        +---------------------------+
```

### 3.1 Model Layer (`PayrollModel.java`, `Employee.java`, `PayrollRecord.java`)
- **Responsibility:** Manages all application data, maintains the SQLite database connection, executes SQL queries with `PreparedStatement` to prevent SQL injection, and implements the payroll calculation algorithms.
- **Key Methods:**
  - `initDatabase()`: Creates `employees` and `payroll` tables if not present; seeds initial data.
  - `addEmployee()`, `updateEmployee()`, `deleteEmployee()`, `getAllEmployees()`, `searchEmployees()`.
  - `calculateTaxDeduction(double grossSalary)`: Applies tiered tax slabs.
  - `calculateAndSavePayroll(...)`: Computes daily rates, deductions, gross, tax, and net pay.
  - `generatePaySlipText(PayrollRecord record)`: Produces ASCII-formatted salary slips.

### 3.2 View Layer (`PayrollView.java`)
- **Responsibility:** Renders the user interface using Java Swing.
- **Components:**
  - `JTabbedPane`: Segregates functionality into 4 organized tabs:
    1. *Employee Management*: Data form and employee table.
    2. *Attendance & Payroll*: Attendance inputs, bonus entry, and live salary preview.
    3. *Pay Slip Generator*: Monospaced display, file export, and print dialog.
    4. *Payroll History & Reports*: Searchable audit table with summary metrics.
  - Dialogs: `JOptionPane` for feedback and confirmation.

### 3.3 Controller Layer (`PayrollController.java`)
- **Responsibility:** Acts as the intermediary between View and Model.
- **Mechanism:**
  - Attaches `ActionListener` and `MouseListener` to UI components.
  - Validates user input types (e.g., ensuring employee IDs, basic salary, and days are positive numbers).
  - Handles SQLExceptions gracefully without crashing the GUI.
  - Refreshes views upon data changes.

---

## 4. DATABASE DESIGN & SCHEMA

The application uses an embedded relational SQLite database stored in `payroll_system.db`.

### 4.1 `employees` Table
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `emp_id` | `INTEGER` | `PRIMARY KEY` | Unique Employee Identifier |
| `name` | `TEXT` | `NOT NULL` | Full Name of Employee |
| `department` | `TEXT` | `NOT NULL` | Department Name |
| `designation` | `TEXT` | `NOT NULL` | Job Title |
| `basic_salary` | `REAL` | `NOT NULL` | Monthly Basic Pay (INR) |
| `email` | `TEXT` | - | Email Address |
| `phone` | `TEXT` | - | Contact Phone Number |

### 4.2 `payroll` Table
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `payroll_id` | `INTEGER` | `PRIMARY KEY AUTOINCREMENT` | Unique Salary Record ID |
| `emp_id` | `INTEGER` | `FOREIGN KEY REFERENCES employees(emp_id)` | Employee Identifier |
| `emp_name` | `TEXT` | `NOT NULL` | Denormalized Name for historical accuracy |
| `department` | `TEXT` | `NOT NULL` | Denormalized Department |
| `month_year` | `TEXT` | `NOT NULL` | Salary Period (e.g. "October 2026") |
| `working_days` | `INTEGER` | `NOT NULL` | Total working days in month |
| `present_days` | `INTEGER` | `NOT NULL` | Days attended by employee |
| `basic_salary` | `REAL` | `NOT NULL` | Base salary at computation time |
| `bonus` | `REAL` | `NOT NULL` | Performance incentive / bonus |
| `attendance_deduction` | `REAL` | `NOT NULL` | Pay cut for absent days |
| `gross_salary` | `REAL` | `NOT NULL` | Earned pay before tax |
| `tax_deduction` | `REAL` | `NOT NULL` | Income Tax / TDS deducted |
| `net_salary` | `REAL` | `NOT NULL` | Final disbursement amount |
| `generated_date` | `TEXT` | `NOT NULL` | Record generation timestamp |

---

## 5. MATHEMATICAL FORMULAS & BUSINESS LOGIC

### 5.1 Attendance Deduction Formula
1. **Daily Wage Rate:**
   $$\text{Daily Rate} = \frac{\text{Basic Salary}}{\text{Total Working Days}}$$
2. **Absent Days:**
   $$\text{Absent Days} = \max(0, \text{Total Working Days} - \text{Days Present})$$
3. **Attendance Loss Deduction:**
   $$\text{Attendance Deduction} = \text{Absent Days} \times \text{Daily Rate}$$

### 5.2 Gross Salary Calculation
$$\text{Gross Salary} = (\text{Basic Salary} - \text{Attendance Deduction}) + \text{Bonus}$$

### 5.3 Progressive Tax Deduction (TDS) Slabs
Tax is computed based on monthly gross income:
- **Bracket 1 (Gross $\le$ ₹30,000):**  
  $\text{Tax} = 0$
- **Bracket 2 (₹30,000 < Gross $\le$ ₹60,000):**  
  $\text{Tax} = (\text{Gross} - 30,000) \times 0.05$
- **Bracket 3 (₹60,000 < Gross $\le$ ₹100,000):**  
  $\text{Tax} = 1,500 + (\text{Gross} - 60,000) \times 0.10$
- **Bracket 4 (Gross > ₹100,000):**  
  $\text{Tax} = 5,500 + (\text{Gross} - 100,000) \times 0.20$

### 5.4 Net Payable Salary
$$\text{Net Salary} = \text{Gross Salary} - \text{Tax Deduction}$$

---

## 6. SAMPLE CALCULATION WALKTHROUGH

- **Employee:** Amit Verma (Finance)
- **Basic Monthly Salary:** ₹50,000.00
- **Total Working Days:** 30
- **Days Present:** 27  $\implies$ **Absent Days:** 3
- **Bonus:** ₹1,500.00

1. $\text{Daily Rate} = \frac{50,000}{30} = ₹1,666.67$
2. $\text{Attendance Deduction} = 3 \times 1,666.67 = ₹5,000.00$
3. $\text{Earned Basic} = 50,000 - 5,000 = ₹45,000.00$
4. $\text{Gross Salary} = 45,000 + 1,500 = ₹46,500.00$
5. $\text{Tax Deduction (Bracket 2)} = (46,500 - 30,000) \times 0.05 = 16,500 \times 0.05 = ₹825.00$
6. $\text{Net Payable Salary} = 46,500 - 825 = \mathbf{₹45,675.00}$

---

## 7. SYSTEM MODULES & USER INTERFACE

### Module 1: Employee Management
Allows HR administrators to register new employees, modify existing employee titles and salaries, delete outdated profiles, and search by keywords. Selecting an employee from the table immediately loads details into input fields.

### Module 2: Attendance & Salary Calculation
Facilitates monthly payroll processing. Allows selection of employee from a synchronized dropdown, setting working days, present days, and bonus. Provides **Live Preview** showing instant calculations before saving to the SQLite database.

### Module 3: Pay Slip Generator
Displays a formal, printable payslip receipt within a monospace text viewer. Includes:
- Corporate header and contact info.
- Period and Employee metadata.
- Attendance summary metrics.
- Itemized earnings vs. deductions table.
- Net payable salary.
- Actions: Save as `.txt` file and Native Print.

### Module 4: Payroll History & Financial Summary
Maintains an audit ledger of all generated payrolls. Displays real-time aggregate statistics:
- Total Payrolls Processed.
- Cumulative Net Disbursed Funds.
- Cumulative Tax (TDS) Collected.

---

## 8. COMPILATION AND EXECUTION INSTRUCTIONS

### Compilation
```cmd
javac -cp ".;sqlite-jdbc-3.53.2.1.jar" *.java
```

### Execution
```cmd
java -cp ".;sqlite-jdbc-3.53.2.1.jar" Main
```

---

## 9. VIVA VOCE PREPARATION (QUESTIONS & ANSWERS)

1. **Q: What is the MVC pattern and why is it used?**  
   *A:* MVC separates an application into Model (data and business logic), View (UI representation), and Controller (event handling). This decouples presentation from data management, ensuring that UI redesigns do not affect SQL logic and vice versa.

2. **Q: What is JDBC and what role does the JAR file play?**  
   *A:* Java Database Connectivity (JDBC) is a Java API for connecting to databases. `sqlite-jdbc-3.53.2.1.jar` is the Type-4 SQLite JDBC driver providing the SQLite implementation of JDBC interfaces (`Connection`, `Statement`, `PreparedStatement`, `ResultSet`).

3. **Q: Why use `PreparedStatement` instead of `Statement`?**  
   *A:* `PreparedStatement` precompiles SQL statements, offers higher execution performance for repeated queries, and parameterizes input values (`?`), preventing SQL injection attacks.

4. **Q: How does SQLite differ from MySQL?**  
   *A:* SQLite is a serverless, zero-configuration, self-contained relational database stored as a single disk file (`payroll_system.db`), making it portable and ideal for desktop applications. MySQL is a client-server database requiring a separate running server process.

5. **Q: How does the application handle absent employees?**  
   *A:* It calculates a daily wage rate from the basic salary and total working days, multiplies it by the unpaid absent days, and deducts it from the basic salary to arrive at earned gross salary.

---

## 10. CONCLUSION
The **Employee Payroll System** demonstrates the effective integration of Java GUI programming (Swing), relational database persistence (SQLite via JDBC), and software architectural principles (MVC). The application successfully implements all required features—salary calculation, tax deductions, bonuses, attendance tracking, and payslip generation—providing a reliable, user-friendly, and maintainable software solution.

