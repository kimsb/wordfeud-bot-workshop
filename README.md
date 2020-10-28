# Wordfeud-bot

I dag skal du få lage din helt egen Wordfeud-bot!

Algoritmen som vi skal implementere ble beskrevet i artikkelen [The World’s Fastest Scrabble Program](https://www.cs.cmu.edu/afs/cs/academic/class/15451-s06/www/lectures/scrabble.pdf) helt tilbake i 1988 🤯

Det er en ganske snedig algoritme som finner alle gyldige legg. Med noen enkle steg reduseres problemet vårt til én dimensjon, slik at vi bare trenger å finne alle gyldige legg for én enkelt rad om gangen. I Wordfeud kan man enten legge brikker horisontalt eller vertikalt, men dersom man tenker på et vertikalt legg som et horisontalt legg på et transponert brett, trenger vi bare å implementere algoritmen for å finne alle gyldige horisontale legg.

**Før kodingen starter kan du gå til fila `Bot` og sette variabelen `botName` til navnet på boten din (jeg har allerede opprettet Wordfeud-brukere til alle på formatet `<navn>bot` (uten ÆØÅ), dvs at f.eks Jørgen har en bruker som heter `jorgenbot`). Prøv å bygge og kjøre prosjektet for å se at innlogging funker!**

## Anchors

Det første vi skal gjøre er å finne ut hvilke felter som er såkalte `anchors`, dvs hvilke felter som kan være utgangspunkt for neste legg. Ethvert legg i Wordfeud må bygge videre på brikkene som allerede ligger på brettet, så et felt er klassifisert som `anchor` dersom
- ingen brikke er plassert på feltet
- det er plassert en brikke på et eller flere av feltene rundt brikken (over, under, venstre, høyre)

I `Board`-klassen sin konstruktør blir brettet fylt med brikkene fra Wordfeud-apiet. Gå til `Board` sin `init` og sett riktig `isAnchor` for hver `Square`.
Dersom det ikke er noen brikker på brettet, skal det midterste feltet `squares[7][7]` være `anchor`

For å teste koden kan vi kjøre `Anchors`-testene i `BoardTest`. (Testene blir ikke kjørt ved bygg.)

<details>
  <summary>Eksempel på implementasjon av init</summary>
    
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

For hvert horisontalt legg må vi også sørge for at brikkene vi legger former gyldige ord vertikalt når de legges inntil andre brikker. `Square` har en variabel `crossChecks` som er et `BitSet` som skal representere hvilke bokstaver vi kan legge for å forme gyldige ord vertikalt. `crossCheks` har størrelse 26, for de 26 bokstavene man kan legge i Wordfeud (disse er definert i `VALID_LETTERS` i fila `Constants`). Gå til klassen `Row` (Det kan være litt forvirrende at man skal finne cross-checks horisontalt, når jeg har nevnt at vi skal finne gyldige ord vertikalt, men det er fordi vi ser på kolonner som transponerte rader...). Her skal du implementere funksjonen `crossChecks()` og fylle ut riktig `BitSet` for hver `Square`. Her får du bruk for funksjonene `getPrefix()` og `getSuffix()`. 

For hver `Square` finnes det tre alternativer:
- Det finnes allerede en brikke på feltet (ingen bokstaver kan legges)
- Feltet har ingen prefix eller suffix (alle bokstaver kan legges)
- Feltet har prefix og/eller suffix (da må man for alle `VALID_LETTERS` L sjekke om `prefix + L + suffix` former gyldige ord)

For å sjekke om et ord er gyldig, brukes `Dictionary.contains()`

For å teste koden kan vi kjøre `Cross-checks`-testen i `RowTest`. (Testen blir ikke kjørt ved bygg.)

<details>
  <summary>Eksempel på implementasjon av crossChecks()</summary>
    
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

## Across moves

(Det som beskrives under her finnes i funksjonen `findAcrossMoves()` som allerede er implementert i klassen `Row`...)

For hver `anchor` skal vi finne alle `left parts` av ord som ligger til venstre for `anchor`, og deretter skal vi for hver `left part` finne alle matchende `right parts`. Den venstre delen av ordet består enten av brikker som allerede ligger på brettet, eller brikker som du har på racket ditt, aldri fra begge deler.
Dersom den venstre delen av ordet kommer fra brettet, kan vi bare bruke `getPrefix()` og finne alle matchende høyre deler for `prefix`, men hvis den venstre delen skal komme fra brettet må vi finne alle mulige `left parts`. En `left part` kan ikke krysse et `anchor`, så den er dermed begrenset av hvor mange felter den har til venstre, som ikke er `anchors`. Dette gir oss en `limit` som vi bruker når vi skal finne alle `left parts`

## Left part

Her skal vi implementere funksjonen `leftPart()` i klassen `Row`. Den finner alle gyldige `left parts` og kaller så på funskjonen `extendRight()` (som vi skal implementere etterpå) for hver `left part` den finner.

Her er pseudokode av funskjonen, som beskrevet i artikkelen nevnt over

```
LeftPart(PartialWord, node N in DAWG, limit) = 
  ExtendRight(PartialWord, N, Anchorsquare)
  if limit > 0 then
    for each edge E out of N
        if the letter L labeling edge E is in our rack then
          remove a tile labeled L from the rack
          let N' be the node reached by following egde E
          Leftpart(PartialWord + L, N', limit - 1)
          put the tile L back into the rack
```

I koden vår sender vi også med `anchorIndex` som lar hos holde styr på hvilken `Square` som er `anchor`, og `rack` som representerer racket vårt.
Siden `Rack` er immutable, trenger vi ikke fjerne/legge tilbake brikker på racket, men vi kan bruke `Rack` sin funksjon `without()` som returnerer et nytt `Rack` uten brikken som vi ønsker å fjerne, når vi kaller på neste `leftPart()`. Vi kan bruke `outgoingTransitions` for å finne edges ut av en `MDAGNode`.

<details>
  <summary>Eksempel på implementasjon av leftPart()</summary>
    
  ```kotlin
private fun leftPart(
        partialWord: String,
        node: MDAGNode,
        limit: Int,
        anchorIndex: Int,
        rack: Rack
    ) {
        extendRight(partialWord, node, anchorIndex, anchorIndex, rack)
        if (limit > 0) {
            node.outgoingTransitions.entries.forEach {
                if (rack.contains(it.key)) {
                    leftPart(partialWord + it.key, it.value, limit - 1, anchorIndex, rack.without(it.key))
                }
            }
        }
    }
  ```
  
</details>


## Extend right

For hver `left part` blir det gjort et kall på `extendRight()` for å fullføre ord, og det er den funksjonen vi skal implementere her.
I motsetning til en `left part` kan en høyre del av et ord bestå av både brikker fra racket og brikker som allerede ligger på brettet.

Her er pseudokode av funskjonen, som beskrevet i artikkelen nevnt over

```
ExtendRight(PartialWord, node N in DAWG, square)
if square is vacant then
    if N is a terminal node then
        LegalMove(PartialWord)
    for each edge E out of N
        if the letter L labeling edge E is in our rack AND L is in the cross-check set of square then
            remove a tile L from the rack
            let N' be the node reached by following edge E
            let next-square be the square to the right of square
            ExtendRight(PartialWord + L, N', next-square)
            put the tile L back into the rack
else
    let L be the letter occupying square
    if N has an edge labeled by L that leads to some node N' then
        let next-square be the square to the right of square
        ExtendRight(PartialWord + L, N', next-square)
```

Vi kan bruke `isAcceptNode` for å finne ut om en `MDAGNode` er en `terminal node`.

I koden vår tilsvarer `LegalMove(PartialWord)` å legge til en ny `RowMove` i listen `rowMoves`

For å få sjekket for `terminal node` også på siste felt kan det være lurt å legge til et tomt felt på enden av hver rad, ved f.eks å gjøre noe sånt som dette
```kotlin
val square = squares.getOrElse(index) { Square() }
```

For å finne ut om en bokstav er en del av `crossChecks` kan vi bruke `Square` sin `crossChecksContains()`

For å teste koden kan vi kjøre testen `Find all words` i `BoardTest`. (Testen blir ikke kjørt ved bygg)

<details>
  <summary>Eksempel på implementasjon av extendRight()</summary>
    
  ```kotlin
private fun extendRight(
        partialWord: String,
        node: MDAGNode,
        anchorIndex: Int,
        index: Int,
        rack: Rack
    ) {
        val square = squares.getOrElse(index) { Square() }
        if (!square.isOccupied()) {
            if (index != anchorIndex && node.isAcceptNode) {
                rowMoves.add(RowMove(partialWord,
                    index - partialWord.length,
                    calculateScore(partialWord, index - partialWord.length)))
            }
            node.outgoingTransitions.entries.forEach {
                if (rack.contains(it.key) && square.crossChecksContains(it.key)) {
                    extendRight(partialWord + it.key, it.value, anchorIndex, index + 1, rack.without(it.key))
                }
            }
        } else {
            square.getLetter()?.let {
                if (node.hasOutgoingTransition(it)) {
                    extendRight(partialWord + it, node.transition(it), anchorIndex, index + 1, rack)
                }
            }
        }
    }
  ```
  
</details>

**WOHOO!**
Nå har du faktisk en bot som kjører!
Meeen den har fortsatt en del forbedringspotensiale...
Den klarer ikke bruke blanke brikker (som er de beste brikkene) og den vet ikke hvor mange poeng hvert legg gir, så den legger bare det første ordet den finner...

## Blanke brikker

For å finne absolutt alle mulige legg, må boten lære seg å bruke blanke brikker.
Det fikser vi ved å legge til litt ekstra logikk i både `leftPart()` og `extendRight()`.
Etter vi har sjekket om racket vårt inneholder en gitt bokstav, sjekker vi også om racket inneholder en blank brikke (representert av `'*'`)
Dersom vi har en blank brikke bruker vi den som bokstaven vi har sjekket for (f.eks P), men vi setter den da til `lowerCase` for å differensiere bruk av blank brikke med bruk av vanlig bokstav.

For å teste koden kan vi kjøre testen `Find all words with blank` i `BoardTest`. (Testen blir ikke kjørt ved bygg)

Strålende! Nå finner vi ALLE mulige legg! Men det hadde vært fint å vite hvor mange poeng hvert legg gir...

<details>
  <summary>Eksempel på implementasjon av leftPart() med støtte for blank brikke</summary>
    
  ```kotlin
private fun leftPart(
        partialWord: String,
        node: MDAGNode,
        limit: Int,
        anchorIndex: Int,
        rack: Rack
    ) {
        extendRight(partialWord, node, anchorIndex, anchorIndex, rack)
        if (limit > 0) {
            node.outgoingTransitions.entries.forEach {
                if (rack.contains(it.key)) {
                    leftPart(partialWord + it.key, it.value, limit - 1, anchorIndex, rack.without(it.key))
                }
                if (rack.contains('*')) {
                    leftPart(partialWord + it.key.toLowerCase(), it.value, limit - 1, anchorIndex, rack.without('*'))
                }
            }
        }
    }
  ```
  
</details>

<details>
  <summary>Eksempel på implementasjon av extendRight() med støtte for blank brikke</summary>
    
  ```kotlin
private fun extendRight(
        partialWord: String,
        node: MDAGNode,
        anchorIndex: Int,
        index: Int,
        rack: Rack
    ) {
        val square = squares.getOrElse(index) { Square() }
        if (!square.isOccupied()) {
            if (index != anchorIndex && node.isAcceptNode) {
                rowMoves.add(RowMove(partialWord,
                    index - partialWord.length,
                    calculateScore(partialWord, index - partialWord.length)))
            }
            node.outgoingTransitions.entries.forEach {
                if (rack.contains(it.key) && square.crossChecksContains(it.key)) {
                    extendRight(partialWord + it.key, it.value, anchorIndex, index + 1, rack.without(it.key))
                }
                if (rack.contains('*') && square.crossChecksContains(it.key)) {
                    extendRight(partialWord + it.key.toLowerCase(), it.value, anchorIndex, index + 1, rack.without('*'))
                }
            }
        } else {
            square.getLetter()?.let {
                if (node.hasOutgoingTransition(it)) {
                    extendRight(partialWord + it, node.transition(it), anchorIndex, index + 1, rack)
                }
            }
        }
    }
  ```
  
</details>

## Poeng

For hvert horisontale legg må vi telle poeng for ordene som blir laget vertikalt også. Første steg for å regne ut poengsummen for hvert legg blir derfor å regne ut `crossSum` for hver `Square`. `crossSum` er poengsummen til alle bokstavene som allerede ligger på brettet over og under et gitt felt, og som vil bli til et vertikalt ord. `crossSum` kan vi finne ved å legge til noen linjer i funksjonen `crossChecks()` som vi implementerte tidligere.
`crossSum` skal settes til summen av alle bokstavene i `prefix` og `suffix`, eller `null` dersom feltet allerede er opptatt eller hvis feltet hverken har `prefix` eller `suffix`.

For å teste koden kan vi kjøre testene `Cross sums` i `RowTest`. (Testene blir ikke kjørt ved bygg)

<details>
  <summary>Eksempel på implementasjon av crossChecks() med crossSum</summary>
    
  ```kotlin
fun crossChecks(): List<Square> {
        return squares.mapIndexed { squareIndex, square ->
            val bitSet = BitSet(26)
            var crossSum: Int? = null
            if (!square.isOccupied()) {
                val prefix = getPrefix(squareIndex)
                val suffix = getSuffix(squareIndex)
                if (prefix.isEmpty() && suffix.isEmpty()) {
                    bitSet.flip(0, 26)
                } else {
                    VALID_LETTERS.forEachIndexed { bitSetIndex, letter ->
                        bitSet.set(bitSetIndex, contains(prefix + letter + suffix))
                    }
                    crossSum = if ((prefix + suffix).isEmpty()) null else (prefix + suffix).map(Constants::letterScore).sum()
                }
            }
            square.copy(crossChecks = bitSet, crossSum = crossSum)
        }
    }
  ```
  
</details>

YES! Da gjenstår det å regne ut poengsummen for hele legget, det gjør vi i funksjonen `calculateScore()` i `Row`. For hvert felt som er en del av det nye, vertikale legget gjelder:
- dersom feltet allerede har en brikke, ta med poengsummen til den brikken i den vertikale summen
- dersom en brikke fra racket blir brukt, ta med poengsummen til den brikken + evt `letterMultiplier`/`wordMultiplier` i både vertikal og horisontal sum

`letterMultiplier` gjelder for én brikke, dvs dersom du legger en B (som gir 4 poeng) på et felt med `letterMultiplier` = 2, så skal brikken gi 8 poeng (i begge retninger).

`wordMultiplier` gjelder for hele ordet (i begge retninger), dvs dersom du legger BÆ (som gir 4 + 8 = 12 poeng) over et felt med `wordMultiplier` = 2, så skal ordet gi 24 poeng.

Dersom alle 7 brikkene fra racket blir brukt (dette kalles en `bingo`), skal vi gi 40 poeng ekstra

For å teste koden kan vi kjøre testene `Calculate score` i `BoardTest`. (Testene blir ikke kjørt ved bygg)

<details>
  <summary>Eksempel på implementasjon av calculateScore()</summary>
    
  ```kotlin
private fun calculateScore(word: String, startIndex: Int): Int {
        var wordMultiplier = 1
        var crossSums = 0
        var addedLetters = 0
        return squares.subList(startIndex, startIndex + word.length).mapIndexed { index, square ->
            if (square.isOccupied()) {
                letterScore(square.getLetter()!!)
            } else {
                addedLetters++
                wordMultiplier *= square.wordMultiplier
                val squareScore = letterScore(word[index]) * square.letterMultiplier
                if (square.crossSum != null) {
                    crossSums += (squareScore + square.crossSum) * square.wordMultiplier
                }
                squareScore
            }
        }.sum() * wordMultiplier + crossSums + (if (addedLetters == 7) 40 else 0)
    }
  ```
  
</details>


## Strategi

WOW! 🔥
Nå har vi en bot som ALLTID finner det høyeste scorende legget!
Det må jo bli suksess, eller? I de fleste tilfeller vil en slik strategi være nok til å gjøre det veldig bra, men det finnes fortsatt forbedringspotensiale!
Her er noen få tips til forbedringer:
- Bytting av brikker: når bør man bytte og hvilke brikker bør man bytte? Nå bytter boten bare hvis den ikke finner et eneste gyldig legg.
- Spare fine brikker: noen ganger er det lønnsomt å kvitte seg med dårlige brikker, selv om man får litt mindre poeng.
- Bare bruke blank brikke når man får godt betalt for det
- Ikke åpne for mye: det hjelper ikke å få noen få ekstra poeng om man samtidig serverer motstanderen mye mer
- Sluttspill: mot slutten av spillet vet man hvilke brikker motstanderen sitter igjen med. Kanskje kan man blokke, slik at motstanderen ikke kan bli kvitt sine brikker?
