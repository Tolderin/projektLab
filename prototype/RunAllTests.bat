@echo off
REM ============================================================
REM  GonoszOnosz Prototype -- automatikus tesztfutto
REM
REM  Vegigiteral az osszes tests\test*_in.txt bemeneti fajlon,
REM  futtatja a prototipust I/O atiranyitassal, majd osszehasonlitja
REM  a kimeneti fajlt az elvart kimenettel (test*_expected.txt)
REM  a Windows beepitett fc parancsaval.
REM
REM  Hasznalat (a prototype mappabol):
REM    RunAllTests.bat
REM ============================================================

setlocal enabledelayedexpansion
chcp 65001 > nul

set PASS_COUNT=0
set FAIL_COUNT=0
set FAIL_LIST=

REM Bizonyosodjunk meg hogy a forditas eloszor lefutott
if not exist bin (
    echo Bin mappa hianyzik, eloszor build.bat-ot futtatok...
    call build.bat
)

echo --- GonoszOnosz Prototype Tesztelese ---
echo.

for %%F in (tests\test*_in.txt) do (
    set INFILE=%%F
    set BASENAME=%%~nF
    set BASENAME=!BASENAME:_in=!
    set OUTFILE=tests\!BASENAME!_actual.txt
    set EXPFILE=tests\!BASENAME!_expected.txt

    REM Prototipus futtatasa I/O atiranyitassal
    java -cp bin main.ProtoApp < "!INFILE!" > "!OUTFILE!"

    if exist "!EXPFILE!" (
        fc /N "!OUTFILE!" "!EXPFILE!" > nul
        if errorlevel 1 (
            echo [FAIL] !BASENAME!
            set /a FAIL_COUNT=!FAIL_COUNT!+1
            set FAIL_LIST=!FAIL_LIST! !BASENAME!
            REM Diff kimenet a debug-hoz
            fc /N "!OUTFILE!" "!EXPFILE!" > "tests\!BASENAME!_diff.txt"
        ) else (
            echo [PASS] !BASENAME!
            set /a PASS_COUNT=!PASS_COUNT!+1
            REM Sikerul -- a diff fajlt torolj uk ha letezne
            if exist "tests\!BASENAME!_diff.txt" del "tests\!BASENAME!_diff.txt"
        )
    ) else (
        echo [SKIP] !BASENAME! ^(nincs expected fajl^)
    )
)

echo.
echo ===== Osszesites =====
echo Sikerult:    !PASS_COUNT!
echo Sikertelen:  !FAIL_COUNT!
if not "!FAIL_LIST!"=="" (
    echo Hibas tesztek:!FAIL_LIST!
)

endlocal
