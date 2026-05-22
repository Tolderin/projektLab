@echo off
REM Inditja a grafikus valtozatot.
REM   run.bat            -> default mini-palya
REM   run.bat config.txt -> egy palya-konfigfajl bemenettel
java -cp bin main.MainApp %*
