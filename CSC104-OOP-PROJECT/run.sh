#!/bin/bash
# Compile and run the Passenger Van Rental System (Mac / Linux)
mkdir -p out
javac -d out src/*.java || { echo "Compile failed."; exit 1; }
java -cp out Main
