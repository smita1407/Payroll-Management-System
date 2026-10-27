@echo off
title Employee Payroll System - Group A2 Team 7
echo ===================================================
echo   Compiling and Launching Employee Payroll System
echo   Class Group: A2 | Team No: 7
echo ===================================================
echo.
echo Compiling Java source files...
javac -cp ".;sqlite-jdbc-3.53.2.1.jar" *.java
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed! Please check your JDK installation.
    pause
    exit /b %errorlevel%
)
echo Compilation successful.
echo.
echo Launching Application GUI...
java -cp ".;sqlite-jdbc-3.53.2.1.jar" Main
pause

