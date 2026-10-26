PASSENGER VAN RENTAL SYSTEM (CSC 104)

HOW TO RUN
1. Compile:  javac -d bin src/*.java        (or open the folder in Eclipse and run Main.java)
2. Run:      java -cp bin Main
   Run it from this folder (the one with src), because the .txt data files are read from here.
   In Eclipse: do NOT create module-info.java.

RENTAL STAFF (NO LOGIN)
There is no login or PIN. The program opens straight to the menu. The only kind of employee is
Rental Staff. Three rental staff exist in employees.txt:
  E1  Angela Torres
  E2  Benjie Dizon
  E3  Camille Perez

When an action needs a staff member (rent, return, send van to maintenance), the program asks for
an existing Rental staff ID and checks that it exists.
Every rental saves the ID of the staff who rented it and who took it back.
Every maintenance record saves the ID of the staff who logged it.

VAN STATUS
Every van is Available, Rented, or Under maintenance. Only an Available van can be rented.
Option 6 lists vans grouped by status, option 7 checks one van, and renting shows the available
vans first and checks the chosen van before asking for the days.
A van under maintenance cannot be rented or deleted.

DELETE CUSTOMER
Option 2 deletes a customer after a Y/N confirmation. A customer who has any rental record
(active or returned) cannot be deleted, so the rental history stays complete.
A deleted customer ID is reused: if C1 is deleted, the next customer added becomes C1.

FILES CREATED BY THE PROGRAM
vans.txt, customers.txt, rentals.txt, employees.txt (rental staff), maintenance.txt -> saved data (loaded again on next run)
notifications_log.txt -> record of every email receipt and SMS sent
customers.txt keeps address, valid ID type and ID number.
rentals.txt keeps return date, late fee (P250 per day after the end date), and the rental staff involved.
Older data files (fewer fields) still load.

NOTES
Email and SMS are simulated (printed on screen and saved in the log).
