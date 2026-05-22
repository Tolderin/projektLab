#!/bin/bash
# ========================================================
# GonoszOnosz Snow Plows -- Grafikus valtozat fordito
# Csak JDK-ra van szukseg (javac).
# Hasznalat: ./build.sh
# ========================================================
set -e
echo "--- GonoszOnosz Grafikus valtozat forditasa ---"

mkdir -p bin

# Osszegyujti az osszes .java fajlt az src alol
find src -name "*.java" > sources.txt

# Fordit (kimenet: bin/)
javac -encoding UTF-8 -d bin @sources.txt

rm -f sources.txt
echo "Forditas kesz. A .class fajlok a bin mappaban talalhatok."
