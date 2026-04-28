@echo off
echo --- GonoszOnosz Prototype Forditasa ---

REM Letrehozzuk a bin mappat, ha meg nem letezik
if not exist bin mkdir bin

REM Osszegyujtjuk az osszes .java fajlt az src mappabol egy szoveges fajlba
dir /s /B src\*.java > sources.txt

REM Leforditjuk a forrasfajlokat a bin mappaba
javac -d bin @sources.txt

REM Letoroljuk az ideiglenes fajlt
del sources.txt

echo Forditas kesz. A .class fajlok a bin mappaban talalhatok.
