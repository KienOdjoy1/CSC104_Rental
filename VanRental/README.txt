PASSENGER VAN RENTAL SYSTEM (CSC 104 Final Project)
Console app in Java. No outside libraries needed.

HOW TO RUN
  1. Install a JDK (Java 11 or newer).
  2. Open a terminal in this folder.
  3. Compile:   javac *.java
  4. Run:       java Main
  Data is saved automatically in a "data" folder (vans.csv, customers.csv, rentals.csv)
  and loaded again the next time you start the program.

FILES AND THE OOP CONCEPT EACH ONE SHOWS
  Rentable.java                 Interface (abstraction)
  PassengerVan.java             Abstract class, encapsulation (private fields, validated setters)
  StandardVan.java              Inheritance + method overriding (10% off for 5+ days)
  TouristVan.java               Inheritance + method overriding (adds comfort fee per day)
  VIPVan.java                   Inheritance + method overriding (adds premium and driver fee per day)
  Customer.java                 Encapsulation + input validation
  Rental.java                   Association (links one Customer and one PassengerVan)
  RentalService.java            Collections (ArrayList), aggregation, CRUD, search, rent and return
  FileManager.java              File persistence (read and write CSV files)
  InvalidInputException.java    Custom exception
  VanUnavailableException.java  Custom exception
  Main.java                     Console menu, catches exceptions and shows messages

RENTAL COST FORMULAS (calculateRentalCost)
  StandardVan : rate x days, minus 10% if days >= 5
  TouristVan  : (rate + comfort fee) x days
  VIPVan      : (rate + premium fee + driver fee) x days
