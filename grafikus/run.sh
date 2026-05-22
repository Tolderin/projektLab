#!/bin/bash
# Inditja a grafikus valtozatot.
#   ./run.sh             -> default mini-palya
#   ./run.sh config.txt  -> palya-konfigfajl bemenettel
java -cp bin main.MainApp "$@"
