#!/bin/bash
# GonoszOnosz Prototype build script
set -e

echo "--- GonoszOnosz Prototype Forditasa ---"

mkdir -p bin

# Az osszes .java fajl osszegyujtese az src/ alol
find src -name "*.java" > sources.txt

javac -d bin @sources.txt

rm -f sources.txt

echo "Forditas kesz. A .class fajlok a bin mappaban talalhatok."
