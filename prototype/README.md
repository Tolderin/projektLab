# GonoszOnosz Prototype (#48)

A BME Szoftver projekt labor tantárgy (BMEVIIIAB02) Hókotrók
projektjének prototípus-fázisa. A 7. és 8. heti tervek alapján
készült CLI alkalmazás, amely az analízis modellt és a
játékmechanikát parancsalapú interakcióval tesztelhetővé teszi.

A program a **Standard Input**-ról olvassa a parancsokat, és
strukturált, prefixelt sorokat (`[SUCCESS]`, `[ERROR]`,
`[EVENT]`, `[STATE]`, `[LIST]`) ír a **Standard Output**-ra.

## Csapat (#48 — GonoszOnosz)

- Kálmán Patrik (XG5YQ1)
- Papp Bálint (HND2AH)
- dr. Gróf László Mihály (EAS7U0)
- Hája Gergő Zoltán (FEFSRB)
- Dobronyi Benedek Márk (IMQ1AR)

Konzulens: Marcsingó Ádám

## Fordítási és futtatási útmutató

A prototípus a kari felhőben (`niif.cloud.bme.hu`,
`Windows 10 20H2 - JDK-Eclipse-WSU` sablon) található
virtuális gépen tesztelendő. Csak a **JDK-ra** van szükség
(`javac`, `java`); semmilyen külső build-eszköz nem kell.

### Fordítás (Windows)

A projekt gyökérmappájából (ahol a `build.bat` van):

```cmd
build.bat
```

A szkript létrehozza a `bin/` mappát, összegyűjti a `src/`
alatti összes `.java` forrásfájlt egy ideiglenes
`sources.txt`-be, majd `javac -d bin @sources.txt` paranccsal
lefordítja őket. A `.class` fájlok a `bin/` alá kerülnek.

### Futtatás (Windows)

```cmd
run.bat < tests\test01_in.txt
```

vagy interaktívan (kézzel gépelt parancsokkal):

```cmd
java -cp bin main.ProtoApp
```

### Tesztek futtatása

Az automatizált tesztelő szkript (`RunAllTests.bat`)
végigiterál a `tests/test*_in.txt` fájlokon, futtatja a
prototípust az átirányított bemenettel és összehasonlítja a
kimenetet a megfelelő `tests/test*_expected.txt` fájllal a
Windows beépített `fc` parancsával.

```cmd
RunAllTests.bat
```

A szkript minden tesztre kiír egy `[PASS]` vagy `[FAIL]` sort,
és a végén összegzi a sikeres/sikertelen tesztek számát.
Sikertelen teszt esetén egy `tests/testNN_diff.txt` fájl
keletkezik a kimenet- és elvárt-fájl eltérésével.

A Linux/Git Bash változata: `RunAllTests.sh` (ugyanaz a logika
`diff`-fel `fc` helyett).

## Mappastruktúra

```
prototype/
├── README.md                <- ez a fájl
├── build.bat / build.sh     <- fordítás
├── run.bat / run.sh         <- futtatás
├── RunAllTests.bat          <- tesztsorozat (Windows)
├── RunAllTests.sh           <- tesztsorozat (Linux/Git Bash)
├── src/
│   ├── main/                <- belépési pont
│   ├── cli/                 <- parancsfeldolgozó keret
│   ├── io/                  <- kimeneti formázás
│   ├── commands/            <- konkrét ICommand implementációk
│   └── model/               <- domain osztályok
└── tests/                   <- 20 teszteset (input + expected)
```

## Fájllista

### Build- és futtatószkriptek

| Fájl | Méret | Szerep |
|---|---:|---|
| `build.bat`                 | < 1 KB  | Windows fordítás |
| `build.sh`                  | < 1 KB  | Linux/Git Bash fordítás |
| `run.bat`                   | < 1 KB  | Windows futtatás |
| `run.sh`                    | < 1 KB  | Linux/Git Bash futtatás |
| `RunAllTests.bat`           | ~2 KB   | Windows automatikus tesztelő |
| `RunAllTests.sh`            | ~1.5 KB | Linux/Git Bash tesztelő |

### Forráskód: `src/main/`

| Fájl | Méret | Szerep |
|---|---:|---|
| `ProtoApp.java`             | ~3.6 KB | Belépési pont, parancsregisztráció |

### Forráskód: `src/cli/`

| Fájl | Méret | Szerep |
|---|---:|---|
| `ICommand.java`             | ~0.5 KB | Parancs interfész |
| `CommandParser.java`        | ~2 KB   | Stream-feldolgozó, `#` komment, ismeretlen parancs hibajelzés |
| `ObjectManager.java`        | ~3.4 KB | ID-alapú objektum-tár, reverse lookup |
| `Determinism.java`          | ~3.2 KB | `random` / `force_slip` állapot |
| `Context.java`              | ~1.2 KB | Globális statikus referenciatár |

### Forráskód: `src/io/`

| Fájl | Méret | Szerep |
|---|---:|---|
| `OutputFormatter.java`      | ~2.5 KB | `[SUCCESS]/[ERROR]/[EVENT]/[STATE]/[LIST]` formázók |

### Forráskód: `src/commands/`

| Fájl | Méret | Szerep |
|---|---:|---|
| `CreateCommand.java`        | ~3.7 KB | `create <type> <id>` |
| `AddToRoadCommand.java`     | ~2.2 KB | `add_to_road r1 lane l1 forward` |
| `ConnectRoadsCommand.java`  | ~1.5 KB | `connect_roads r1 end r2 start` |
| `ConnectFieldsCommand.java` | ~1.6 KB | `connect_fields l1 l2` |
| `SetRoadLengthCommand.java` | ~1.2 KB | `set_road_length r1 100.0` |
| `SetLaneStateCommand.java`  | ~1.6 KB | `set_lane_state l1 ...` |
| `SetMoneyCommand.java`      | ~1.3 KB | `set_money c1 1000` |
| `SetScoreCommand.java`      | ~1.1 KB | `set_score bd1 5` |
| `SetSaltEffectCommand.java` | ~1.4 KB | `set_salt_effect l1 2.0` |
| `SpawnCommand.java`         | ~5.9 KB | `spawn snowplow/car/bus ...` |
| `MoveBusCommand.java`       | ~1.8 KB | `move_bus bus1 t1` |
| `MovePlowCommand.java`      | ~1.8 KB | `move_plow sp1 l2` |
| `BuyCommand.java`           | ~6.8 KB | `buy <player> <plow> <type> [amount]` |
| `EquipCommand.java`         | ~2.7 KB | `equip sp1 salthead` (csak HomeBase-en) |
| `NextTurnCommand.java`      | ~2.2 KB | `next_turn [N]` -- force-slip, snow, move-fázis |
| `RandomCommand.java`        | ~1.1 KB | `random on|off` |
| `ForceSlipCommand.java`     | ~1.2 KB | `force_slip <id> <true|false>` |
| `StatCommand.java`          | ~6.2 KB | `stat <id>` (Lane/SnowPlow/Car/Bus/Cleaner/BusDriver) |
| `ListCommand.java`          | ~2 KB   | `list <type>` |
| `LoadCommand.java`          | ~1.9 KB | `load <filename>` |
| `SaveCommand.java`          | ~6.9 KB | `save [filename]` |
| `ExitCommand.java`          | < 1 KB  | `exit` |

### Forráskód: `src/model/`

| Fájl | Méret | Szerep |
|---|---:|---|
| `Field.java`                | < 1 KB  | Graf-csomópont interfész |
| `Lane.java`                 | ~6.8 KB | Forgalmi sáv, hó/jég/zúzalék/só logika |
| `Road.java`                 | ~3 KB   | Sávokat és épületeket összefogó konténer |
| `Building.java`             | ~1.3 KB | Épület absztrakt ős |
| `GeneralBuilding.java`      | < 1 KB  | Lakás/munkahely |
| `HomeBase.java`             | ~3.3 KB | Hókotrók telephelye, dockedPlows |
| `Terminal.java`             | ~1.5 KB | Buszvégállomás, registerArrival |
| `Vehicle.java`              | ~1.6 KB | Jármű absztrakt ős |
| `Car.java`                  | ~5.4 KB | NPC autó útvonalkereséssel |
| `Bus.java`                  | ~3.7 KB | Busz, fordulók, slip/collide |
| `SnowPlow.java`             | ~3.1 KB | Hókotró, attachments/activeHead |
| `CleanerHead.java`          | ~1.1 KB | Hókotró fej absztrakt ős |
| `SweepHead.java`            | ~1.4 KB | Söprő fej (hó+zúzalék toltatás) |
| `ThrowerHead.java`          | ~1.1 KB | Hányó fej (járdára) |
| `IcebreakerHead.java`       | < 1 KB  | Jégtörő fej |
| `SaltHead.java`             | ~1.6 KB | Sószóró fej, üzemanyag (só) |
| `DragonHead.java`           | ~1.4 KB | Sárkány fej, üzemanyag (biokerozin) |
| `GravelHead.java`           | ~1.6 KB | **Új (7. hét)** zúzalékszóró fej |
| `IPurchasable.java`         | < 1 KB  | Boltban vásárolható interfész |
| `IntegratedMarket.java`     | ~1.2 KB | Bolt a HomeBase-ben |
| `Player.java`               | < 1 KB  | Játékos absztrakt ős |
| `Cleaner.java`              | ~1.7 KB | Takarító játékos |
| `BusDriver.java`            | ~1.1 KB | Buszvezető játékos |
| `Map.java`                  | ~3 KB   | Pálya, BFS-útvonalkeresés, havazás |
| `GameLogic.java`            | ~3.5 KB | Körök, advanceTurn, manageTurn, endGame |

### Tesztesetek: `tests/`

20 teszteset, mindegyik egy `testNN_in.txt` (bemenet) és egy
`testNN_expected.txt` (elvárt kimenet) párral. Minden teszt
egy konkrét képességet ellenőriz a 8. heti dokumentum 9.1
fejezete szerint.

| Teszt | Cél |
|---|---|
| 01 | Pálya betöltés, list/stat |
| 02 | Save kimenet a load-dal kompatibilis |
| 03 | Busz végállomásra érkezés, fordulószám |
| 04 | Érvénytelen mozgás nem-szomszédra (`[ERROR]`) |
| 05 | NPC autó útvonaltervezése |
| 06 | NPC autó reakciója lezárt sávra |
| 07 | Determinisztikus megcsúszás jégen |
| 08 | `force_slip false` -- megcsúszás elkerülése |
| 09 | Söprő fej (hó-áthelyezés szomszédra) |
| 10 | Hányó fej (járdára szórás) |
| 11 | Jégtörő fej |
| 12 | Sószóró fej + saltEffect |
| 13 | Zúzalékszóró fej (új feature) |
| 14 | Sárkány fej (teljes olvasztás) |
| 15 | Kifogyott üzemanyag (Empty state) |
| 16 | Havazás minden körben |
| 17 | Sóhatás idejének lejárta körönként |
| 18 | Felszerelés vásárlása, money-csökkenés |
| 19 | Vásárlási kísérlet fedezet nélkül |
| 20 | Felszerelés-csere kívülről (`[ERROR]`) |

## Bemeneti nyelv

A bemeneti nyelv a 7. hét 7.1.2 fejezete szerint. A parancsok
egyetlen szóval kezdődnek, paraméterek szóközzel elválasztva.
A `#`-tal kezdődő sorok kommentek (átugorva). Üres sorok
átugorva. Az ID-k tetszőleges string azonosítók.

### Konfigurációs parancsok

```
create <type> <id>
add_to_road <road_id> <lane|building> <item_id> [direction]
connect_roads <r1> <pos1> <r2> <pos2>
connect_fields <field1> <field2>
set_road_length <road_id> <length>
set_lane_state <lane_id> <snowDepth> <isFrozen> <iceThickness> <gravelDepth>
set_money <player_id> <amount>
set_score <player_id> <amount>
set_salt_effect <lane_id> <duration>
spawn snowplow <id> <field_id> <owner_id>
spawn car <id> <field_id> <home_id> <work_id>
spawn bus <id> <field_id>
```

Megjegyzés: a `set_lane_state` `iceThickness` paramétere a
8. heti spec szerint kompatibilitásból megmaradt, de a
modell már nem tárolja külön — a beolvasott érték eldobódik.

### Akciók

```
move_bus <bus_id> <target_field>
move_plow <plow_id> <target_field>
buy <player_id> <plow_id> <head_type>            (fej-vásárlás)
buy <player_id> <plow_id> <fuel_type> <amount>   (üzemanyag-vásárlás)
equip <plow_id> <head_type>                      (csak HomeBase-en)
```

### Szimuláció és lekérdezés

```
next_turn [N]
random on|off
force_slip <vehicle_id> <true|false>
stat <id>
list <type>
load <filename>
save [filename]
exit
```

## Kimeneti nyelv

```
[SUCCESS] <id> <action> <result>
[ERROR] <reason>
[EVENT] <event description>
[LIST] <type>: <id1>, <id2>, ...
[STATE] <id> (<class>)
 <attr1>: <value1>
 <attr2>: <value2>
 ...
```

A `save` parancs a memóriaképet a bemeneti nyelv konfigurációs
parancsai formájában írja ki — a kimenet közvetlenül vissza-
adható egy `load` parancs bemenetének (round-trip).

## Determinisztikusság

A csúszás és más véletlen elemek determinisztikussá tehetők:

```
random off            -- minden véletlen elem letiltva
force_slip car_1 true -- car_1 garantáltan megcsúszik (jeges sávon)
force_slip bus_1 false -- bus_1 garantáltan NEM csúszik meg
```

`random off` módban, ha egy járműre nincs explicit
`force_slip` beállítás, alapértelmezetten **megcsúszik** (a 7.
heti spec szerint). A `force_slip ... true` egy második,
`next_turn`-elején-aktiválódó mechanizmust is bekapcsol: a
jeges sávon **álló** járművek azonnal megcsúsznak — ez teszi
lehetővé a 6. teszteset (`spawn` a jeges sávra, force_slip
true) viselkedését.

## Megjegyzések a 8. heti specifikációhoz

A specifikáció és a tesztkimenetek között néhány
inkonzisztenciát találtunk. Ezek mindegyikét a tesztkimenet
javára döntöttük el (a tesztek szerint implementálva):

1. **`connect_fields` kimenet**: a `[SUCCESS] l1 connected to l2`
   alakot választottuk (a doksi két alakot kever).
2. **`force_slip` kimenet**: `[SUCCESS] force_slip <id> set to <value>`
   alak (illeszkedik az `<ID> <Akció> <Eredmény>` mintához).
3. **`Map snowed` event**: minden `next_turn`-ben generálódik.
4. **Söprő fej viselkedése**: az ELSŐ Lane-szomszédra tolja
   az összes havat (a 8. heti test 9 expected `snowDepth=20`
   értéke alapján), nem szétoszt egyenletesen.
5. **`applySaltEffect`**: csak a `saltEffect`-et állítja be
   (a doksi-pszeudokódból kihagytuk a `removeSnow`/`removeIce`
   hívást, mivel a 8. heti test 12 expected `isFrozen: true`
   értéke ezt cáfolja — a só "egy idő alatt" olvaszt).
6. **`activeHead` mezőnév**: a stat kimenetben kis betűvel
   (`sweephead`, `salthead`) mert a `getName()` lowercase-t
   ad vissza, és a buy/equip parancsok is lowercase típusneveket
   várnak — egységes nevezéktan.
7. **A SnowPlow.attachments**: az `equip` áthelyezi az
   activeHead-be (kivesz az attachments-ből), a stat csak a
   tartalék fejeket listázza.

## Fordítási és futtatási környezet

- **Operációs rendszer**: Windows 10 20H2 (kari VM-sablon)
- **JDK**: a VM-sablonban előre telepített JDK
- **Build-rendszer**: külső build-rendszer NINCS, csak `javac`/`java`

A fordítás `build.bat`-tal egy lépésben elvégezhető.
A `bin/` mappa a fordított `.class` fájlokat tartalmazza,
és nem kerül be a beadott csomagba (a build script
újragenerálja).
