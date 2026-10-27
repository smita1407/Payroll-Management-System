# Employee Payroll System (Java Swing GUI + JDBC SQLite)

**Class Group:** A2  
**Team No:** 7  
**Mini Project Title:** Employee Payroll System  
**Architecture:** Model-View-Controller (MVC) Pattern  
**Database:** SQLite Relational Database via JDBC (`sqlite-jdbc-3.53.2.1.jar`)  
**GUI Framework:** Java Swing (Desktop GUI)

---

## 📌 Project Overview
The **Employee Payroll System** is a complete desktop application developed using Java Swing and SQLite following the **MVC (Model-View-Controller)** architectural pattern. 

### Key Features Implemented:
1. **Employee Management:** Complete CRUD operations (Add, View, Update, Delete, Search) for employee profiles with ID, Name, Department, Designation, Basic Salary, Email, and Phone.
2. **Attendance Management:** Records monthly total working days, days present, and automatically calculates unpaid absent days.
3. **Bonus & Allowances:** Flexible input for bonuses and performance incentives.
4. **Automated Salary Calculation:**
   - **Per Day Rate:** `Basic Salary / Total Working Days`
   - **Attendance Deduction:** `Absent Days × Per Day Rate`
   - **Gross Salary:** `(Basic Salary - Attendance Deduction) + Bonus`
5. **Tax Deductions (TDS):** Automatic progressive tax slab calculation:
   - Up to ₹30,000/month: `0%`
   - ₹30,001 to ₹60,000: `5%`
   - ₹60,001 to ₹100,000: `₹1,500 + 10%`
   - Above ₹100,000: `₹5,500 + 20%`
6. **Net Payable Salary:** `Gross Salary - Tax Deduction`
7. **Pay Slip Generator & Exporter:**
   - Generates formatted, printable salary payslips.
   - One-click **Export to .TXT** file.
   - Native **Print** support.
8. **Payroll History & Financial Analytics:** Searchable history log with summary stats (Total Disbursed, Total TDS Collected).

---

## 🏗️ MVC Architecture Breakdown

```
+-----------------------------------------------------------------+
|                       USER INTERACTION                          |
+-----------------------------------------------------------------+
                                │
                                ▼
+-----------------------------------------------------------------+
|                   CONTROLLER (PayrollController.java)           |
|  - Listens to GUI button clicks & user input events             |
|  - Validates numbers and fields                                 |
|  - Calls Model methods for calculations & database updates      |
|  - Passes fresh data back to View                               |
+-----------------------------------------------------------------+
         │                                               │
         ▼                                               ▼
+----------------------------------+   +----------------------------------+
|      MODEL (PayrollModel.java)   |   |       VIEW (PayrollView.java)    |
|  - SQLite JDBC Driver connection |   |  - Java Swing JFrame & Tabs      |
|  - Tables: employees, payroll    |   |  - Forms, TextFields, JComboBox  |
|  - Salary & Tax Math Logic       |   |  - JTable (Employees & Records)  |
|  - Payslip ASCII generator       |   |  - Payslip Preview JTextArea     |
+----------------------------------+   +----------------------------------+
         │
         ▼
+-----------------------------------------------------------------+
|                 DATABASE (payroll_system.db)                    |
|  - Local relational SQLite database file                        |
+-----------------------------------------------------------------+
```

---

## 💻 Prerequisites & Setup

1. **Java Development Kit (JDK 8 or higher):**
   Verify installation:
   ```cmd
   javac -version
   java -version
   ```
2. **SQLite JDBC Driver:**
   Ensure `sqlite-jdbc-3.53.2.1.jar` is located in the project root folder.

---

## 🚀 How to Compile and Run (Windows Command Line / PowerShell)

### Step 1: Open Terminal in Project Folder
Navigate to the project directory:
```cmd
cd "D:\college_work\java mini project"
```

### Step 2: Compile the Java Files
Run `javac` with the SQLite JAR in the classpath:
```cmd
javac -cp ".;sqlite-jdbc-3.53.2.1.jar" *.java
```

### Step 3: Run the Application
Launch the GUI application:
```cmd
java -cp ".;sqlite-jdbc-3.53.2.1.jar" Main
```

*(Note for Linux/macOS: Replace semicolon `;` with colon `:` in the classpath `-cp ".:sqlite-jdbc-3.53.2.1.jar"`)*

---

## 📁 Project Structure

```
java mini project/
├── sqlite-jdbc-3.53.2.1.jar     # SQLite JDBC driver library
├── Employee.java                # Employee entity model class
├── PayrollRecord.java           # Payroll record entity class
├── PayrollModel.java            # Model: SQLite DB operations & salary logic
├── PayrollView.java             # View: Java Swing GUI interface
├── PayrollController.java       # Controller: Event listeners & coordination
├── Main.java                    # Entry point class
├── payroll_system.db            # SQLite database file (created automatically)
├── README.md                    # Project documentation & run guide
├── PROJECT_REPORT.md            # Detailed academic project report
└── run.bat                      # One-click Windows launcher
```

---

## 🧮 Mathematical Formulas Used

| Component | Formula |
|---|---|
| **Daily Wage Rate** | $\text{Daily Rate} = \frac{\text{Basic Salary}}{\text{Total Working Days}}$ |
| **Absent Days** | $\text{Absent Days} = \text{Total Working Days} - \text{Days Present}$ |
| **Attendance Deduction** | $\text{Attendance Deduction} = \text{Absent Days} \times \text{Daily Rate}$ |
| **Gross Salary** | $\text{Gross Salary} = (\text{Basic Salary} - \text{Attendance Deduction}) + \text{Bonus}$ |
| **Tax Deduction (TDS)** | Calculated progressively using the defined monthly tax brackets |
| **Net Payable Salary** | $\text{Net Salary} = \text{Gross Salary} - \text{Tax Deduction}$ |

---

## 👥 Authors
- **Class Group:** A2
- **Team No:** 7
- **Submission Date:** November 2026

