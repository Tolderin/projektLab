#!/bin/bash
# ============================================================
# GonoszOnosz Prototype -- automatikus tesztfutto (Linux/Git Bash)
# A Windows-os RunAllTests.bat masa, diff-fel osszehasonlit.
# ============================================================

set -u

cd "$(dirname "$0")"

if [ ! -d bin ]; then
    echo "Bin mappa hianyzik, eloszor build.sh-t futtatok..."
    ./build.sh
fi

PASS=0
FAIL=0
FAIL_LIST=""

echo "--- GonoszOnosz Prototype Tesztelese ---"
echo

for INFILE in tests/test*_in.txt; do
    BASENAME=$(basename "$INFILE" _in.txt)
    OUTFILE="tests/${BASENAME}_actual.txt"
    EXPFILE="tests/${BASENAME}_expected.txt"

    java -cp bin main.ProtoApp < "$INFILE" > "$OUTFILE" 2>&1

    if [ ! -f "$EXPFILE" ]; then
        echo "[SKIP] $BASENAME (nincs expected fajl)"
        continue
    fi

    if diff -q "$OUTFILE" "$EXPFILE" > /dev/null; then
        echo "[PASS] $BASENAME"
        PASS=$((PASS + 1))
        rm -f "tests/${BASENAME}_diff.txt"
    else
        echo "[FAIL] $BASENAME"
        FAIL=$((FAIL + 1))
        FAIL_LIST="$FAIL_LIST $BASENAME"
        diff "$OUTFILE" "$EXPFILE" > "tests/${BASENAME}_diff.txt"
    fi
done

echo
echo "===== Osszesites ====="
echo "Sikerult:    $PASS"
echo "Sikertelen:  $FAIL"
if [ -n "$FAIL_LIST" ]; then
    echo "Hibas tesztek:$FAIL_LIST"
fi
