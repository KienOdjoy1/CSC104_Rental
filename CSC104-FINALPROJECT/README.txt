PASSENGER VAN RENTAL SYSTEM (CSC 104)

HOW TO RUN
1. Compile:  javac -d bin src/*.java        (or open the folder in Eclipse and run Main.java)
2. Run:      java -cp bin Main
   Run it from this folder (the one with src), because the .txt data files are read from here.
   In Eclipse: do NOT create module-info.java.

EMPLOYEES (NO LOGIN)
There is no login or PIN. The program opens straight to the menu. Three employees already exist
the first time it runs (add more with "Add employee", see them with "View all employees"):
  E1  Store Manager   Manager
  E2  Counter Staff   Counter Staff
  E3  Van Mechanic    Mechanic

When an action needs an employee (rent, return, send van to maintenance), the program asks for
an existing Employee ID and checks that it exists and that the role is allowed:
  Rent / Return        : Manager or Counter Staff
  Send to maintenance  : Manager or Mechanic
Every rental saves the ID of the employee who rented it and who took it back.

VAN STATUS
Every van is Available, Rented, or Under maintenance. Only an Available van can be rented.
Option 5 lists vans grouped by status, option 6 checks one van, and renting shows the available
vans first and checks the chosen van before asking for the days.
A van under maintenance cannot be rented or deleted.

FILES CREATED BY THE PROGRAM
vans.txt, customers.txt, rentals.txt, employees.txt, maintenance.txt -> saved data (loaded again on next run)
notifications_log.txt -> record of every email receipt and SMS sent
customers.txt keeps address, valid ID type and ID number.
rentals.txt keeps return date, late fee (P250 per day after the end date), and the employees involved.
Older data files (fewer fields) still load.

NOTES
Email and SMS are simulated (printed on screen and saved in the log).
PINs are saved as plain text. This is fine for a school project, not for a real shop.
