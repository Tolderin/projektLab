@echo off
REM ========================================================
REM  GonoszOnosz Snow Plows -- Grafikus valtozat fordito
REM  Csak JDK-ra van szukseg (javac).
REM  Hasznalat:  build.bat
REM ========================================================
echo --- GonoszOnosz Grafikus valtozat forditasa ---

if not exist bin mkdir bin

REM Osszegyujti az osszes .java fajlt az src alol egy szoveges fajlba
dir /s /B src\*.java > sources.txt

REM Fordit (kimenet: bin/)
javac -encoding UTF-8 -d bin @sources.txt
if errorlevel 1 (
    del sources.txt
    echo Forditasi hiba.
    exit /b 1
)

del sources.txt
echo Forditas kesz. A .class fajlok a bin mappaban talalhatok.
