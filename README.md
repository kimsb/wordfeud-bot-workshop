# Wordfeud-bot

I dag skal du få lage din helt egen Wordfeud-bot!

Algoritmen som vi skal implementere ble beskrevet i artikkelen [The World’s Fastest Scrabble Program](https://www.cs.cmu.edu/afs/cs/academic/class/15451-s06/www/lectures/scrabble.pdf) helt tilbake i 1988 🤯

Det er en ganske snedig algoritme som finner alle gyldige legg. Med noen enkle steg reduseres problemet vårt til én dimensjon, slik at vi bare trenger å finne alle gyldige legg for én enkelt rad om gangen. I Wordfeud kan man enten legge brikker horisontalt eller vertikalt, men dersom man tenker på et vertikalt legg som et horisontalt legg på et transponert brett, trenger vi bare å implementere algoritmen for å finne alle gyldige horisontale legg.

## Anchors

Det første vi skal gjøre er å finne ut hvilke felter som er såkalte `anchors`, dvs hvilke felter som kan være utgangspunkt for neste legg. Ethvert legg i Wordfeud må bygge videre på brikkene som allerede ligger på brettet, så et felt er klassifisert som `anchor` dersom
- ingen brikke er plassert på feltet
- det er plassert en brikke på et eller flere av feltene rundt brikken (over, under, venstre, høyre)

I `Board`-klassen sin konstruktør blir brettet fylt med brikkene fra Wordfeud-apiet. Gå til `Board` sin `init` og sett riktig `isAnchor` for hver `Square`.
Dersom det ikke er noen brikker på brettet, skal det midterste feltet `squares[7][7]` være `anchor`

For å teste koden din kan du kjøre `Anchors`-testene i `BoardTest`. Testene blir ikke kjørt ved bygg.

<details>
  <summary>Eksempel på implementasjon av `init`</summary>
    
  ```kotlin
init {
        this.squares = squares.mapIndexed { i, row ->
            row.mapIndexed { j, square ->
                square.copy(isAnchor = !squares[i][j].isOccupied() &&
                    ((i == 7 && j == 7) ||
                        squares.getOrNull(i - 1)?.get(j)?.isOccupied() == true ||
                        squares[i].getOrNull(j - 1)?.isOccupied() == true ||
                        squares[i].getOrNull(j + 1)?.isOccupied() == true ||
                        squares.getOrNull(i + 1)?.get(j)?.isOccupied() == true))
            }
        }
    }
  ```
  
</details>

## Cross-Checks

For hvert horisontalt legg må vi også sørge for at brikkene vi legger former gyldige ord vertikalt når de legges inntil andre brikker. `Square` har en variabel `crossChecks` som er et `BitSet` som skal representere hvilke bokstaver vi kan legge for å forme gyldige ord vertikalt. `crossCheks` har størrelse 26, for de 26 bokstavene man kan legge i Wordfeud (disse er definert i `VALID_LETTERS` i fila `Constants`). Gå til klassen `Row` (Det kan være litt forvirrende at man skal finne cross-checks horisontalt, når jeg har nevnt at vi skal finne gyldige ord vertikalt, men det er fordi vi ser på kolonner som transponerte rader...). Her skal du implementere funksjonen `crossChecks` og fylle ut riktig `BitSet` for hver `Square`. Her får du bruk for funksjonene `getPrefix` og `getSuffix`. 

For hver `Square` finnes det tre alternativer:
- Det finnes allerede en brikke på feltet (ingen bokstaver kan legges)
- Feltet har ingen prefix eller suffix (alle bokstaver kan legges)
- Feltet har prefix og/eller suffix (da må man for alle `VALID_LETTERS` L sjekke om `prefix + L + suffix` former lovlige ord)

For å sjekke om et ord er gyldig, brukes `Dictionary.contains()`

For å teste koden din kan du kjøre `Cross-checks`-testen i `RowTest`. Testene blir ikke kjørt ved bygg.

<details>
  <summary>Eksempel på implementasjon av `crossChecks`</summary>
    
  ```kotlin
fun crossChecks(): List<Square> {
        return squares.mapIndexed { squareIndex, square ->
            val bitSet = BitSet(26)
            if (!square.isOccupied()) {
                val prefix = getPrefix(squareIndex)
                val suffix = getSuffix(squareIndex)
                if (prefix.isEmpty() && suffix.isEmpty()) {
                    bitSet.flip(0, 26)
                } else {
                    VALID_LETTERS.forEachIndexed { bitSetIndex, letter ->
                        bitSet.set(bitSetIndex, contains(prefix + letter + suffix))
                    }
                }
            }
            square.copy(crossChecks = bitSet)
        }
    }
  ```
  
</details>


//TODO CROSSUMS I CROSSCHECKS
