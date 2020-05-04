package adventofcode2019

import adventofcode2019.intcode.IntCodeNumber

fun Iterable<Int>.intCodeNumber(): List<IntCodeNumber> = map { IntCodeNumber.of(it) }