package adventofcode2019.day04

import adventofcode2019.digitsCount
import adventofcode2019.getDigitAt
import java.util.stream.IntStream

fun main() {
    println(
        IntStream.rangeClosed(240298, 784956)
            .filter(::hasTwoEqualAdjacentDigits)
            .filter(::digitsNeverIncrease)
            .count()
    )
    println(
        IntStream.rangeClosed(240298, 784956)
            .filter(::hasAGroupOfExactlyTwoEqualAdjacentDigits)
            .filter(::digitsNeverIncrease)
            .count()
    )
}

fun hasTwoEqualAdjacentDigits(password: Int): Boolean {
    for (i: Int in 0 until password.digitsCount()) {
        if (password.getDigitAt(i) == password.getDigitAt(i + 1))
            return true
    }
    return false
}

fun hasAGroupOfExactlyTwoEqualAdjacentDigits(password: Int): Boolean {
    for (i: Int in 0..password.digitsCount() - 2) {
        if (password.getDigitAt(i) == password.getDigitAt(i + 1)
            && (i == 0 || i != 0 && password.getDigitAt(i - 1) != password.getDigitAt(i))
            && (i + 2 == password.digitsCount() || i + 2 != password.digitsCount() && password.getDigitAt(i + 2) != password.getDigitAt(i + 1))
        )
            return true
    }
    return false
}

fun digitsNeverIncrease(password: Int): Boolean {
    for (i: Int in 0 until password.digitsCount()) {
        if (password.getDigitAt(i) < password.getDigitAt(i + 1))
            return false
    }
    return true
}
