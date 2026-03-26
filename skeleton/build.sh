#!/bin/bash
# Ez a szkript lefordítja a Java forráskódokat egy 'bin' nevű mappába.
echo "Fordítás indítása..."
mkdir -p bin
# Megkeresi az összes .java fájlt az src mappában és lefordítja őket
find src -name "*.java" > sources.txt
javac -d bin @sources.txt
rm sources.txt
echo "Fordítás sikeresen befejeződött!"