# Wordfeud-bot

I dag skal du få lage din helt egen Wordfeud-bot!

Algoritmen som vi skal implementere ble beskrevet i artikkelen [The World’s Fastest Scrabble Program](https://www.cs.cmu.edu/afs/cs/academic/class/15451-s06/www/lectures/scrabble.pdf) helt tilbake i 1988 🤯

Det er en ganske snedig algoritme som finner alle gyldige legg. Med noen enkle steg reduseres problemet vårt til én dimensjon, slik at vi bare trenger å finne alle gyldige legg for én enkelt rad om gangen. I Wordfeud kan man enten legge brikker bortover eller nedover, men dersom man tenker på et legg nedover som et legg bortover på et transponert brett, trenger vi bare å implementere algoritmen for å finne alle gyldige legg bortover.

## Anchors

Det første vi skal gjøre er å finne ut hvilke felter som er såkalte `anchors`, dvs hvilke felter som kan være utgangspunkt for neste legg. Ethvert legg i Wordfeud må bygge videre på brikkene som allerede ligger på brettet, så et felt er klassifisert som `anchor` dersom
- ingen brikke er plassert på feltet
- det er plassert en brikke på et eller flere av feltene rundt brikken (over, under, venstre, høyre)

I `Board`-klassen sin konstruktør blir brettet fylt med brikkene fra Wordfeud-apiet. Gå til `Board` sin `init` og sett riktig `isAnchor` for hver `Square`.
Dersom det ikke er noen brikker på brettet, skal det midterste feltet `squares[7][7]` være `anchor`

For å teste koden din kan du kjøre `Anchors`-testene i `BoardTest`. Disse testene blir ikke kjørt ved bygg.

<details>
  <summary>Eksempel på implementasjon</summary>
    
  ```kotlin
init {
        this.squares = squares.mapIndexed { i, row ->
            row.mapIndexed { j, square ->
                val isAnchor =
                    !squares[i][j].isOccupied() &&
                        ((i == 7 && j == 7) ||
                            squares.getOrNull(i - 1)?.get(j)?.isOccupied() == true ||
                            squares[i].getOrNull(j - 1)?.isOccupied() == true ||
                            squares[i].getOrNull(j + 1)?.isOccupied() == true ||
                            squares.getOrNull(i + 1)?.get(j)?.isOccupied() == true)
                square.copy(isAnchor = isAnchor)
            }
        }
    }
  ```
  
</details>
