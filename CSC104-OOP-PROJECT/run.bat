@echo off
REM Compile and run the Passenger Van Rental System (Windows)
if not exist out mkdir out
javac -d out src\*.java
if errorlevel 1 (
  echo Compile failed.
  pause
  exit /b 1
)
java -cp out Main
pause
