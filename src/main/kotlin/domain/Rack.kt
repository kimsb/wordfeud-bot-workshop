package domain

data class Rack(
    val tiles: List<Char>
) {
    fun contains(letter: Char): Boolean {
        return tiles.any { it == letter }
    }

    fun without(letter: Char): Rack {
        val index = tiles.indexOf(letter)
        return Rack(tiles.subList(0, index) + tiles.subList(index + 1, tiles.size))
    }
}
