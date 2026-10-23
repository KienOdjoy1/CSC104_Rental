PASSENGER VAN RENTAL SYSTEM - PHILIPPINES (CSC 104 Final Project)
Console app in Java for a passenger van rental business in the Philippines.
Passenger vans only (no cargo vans). No outside libraries needed.

FOLDER LAYOUT
  src/       All 12 Java source files
  data/      Sample vans, customers, rentals (CSV). Delete this folder to start empty.
  run.bat    Compile and run on Windows (double click)
  run.sh     Compile and run on Mac / Linux
  out/       Created when you compile (the .class files)

HOW TO RUN
  Requirement: a JDK (Java 11 or newer).
  Windows      : double click run.bat
  Mac / Linux  : ./run.sh
  By hand      : javac -d out src/*.java      then      java -cp out Main
  Open the terminal in this folder (the one that has src and data), because the
  program reads and writes the "data" folder next to where you start it.

PHILIPPINE RULES USED
  Money         : Philippine peso, shown as PHP
  Plate number  : 3 letters + 3 or 4 digits, example ABC 1234 (ABC1234 also works)
  LTO license   : example N01-23-456789
  Mobile number : example 09171234567 (+63 917 123 4567 is also accepted)
  Fuel and toll are paid by the customer (shown on the receipt)

FILES IN src AND THE OOP CONCEPT EACH ONE SHOWS
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

RENTAL COST FORMULAS (calculateRentalCost), all in PHP
  StandardVan : rate x days, minus 10% if days >= 5
  TouristVan  : (rate + comfort fee) x days
  VIPVan      : (rate + premium fee + driver fee) x days
