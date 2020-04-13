package adventofcode2019.day06

import adventofcode2019.linesFromResource
import java.lang.IllegalArgumentException
import java.nio.file.Paths
import java.util.stream.Collectors

fun main() {
    val universalOrbitMap = OrbitMap.fromRelationsMap(relationsFromInput())
    println(universalOrbitMap.checksum())
    println(universalOrbitMap.orbitalTransfersBetween(Object("YOU"), Object("SAN")) - 2)
}

private fun relationsFromInput(): Map<Object, Set<Object>> {
    return linesFromResource(Paths.get("adventofcode2019", "day06", "input.txt"))
        .map { line ->
            line.split(")").let { splitRes ->
                Object(splitRes[0]) to Object(splitRes[1])
            }
        }
        .collect(
            Collectors.toMap(
                Pair<Object, Object>::first,
                { setOf(it.second) }
            ) { set1, set2 ->
                set1.union(set2)
            }
        )
}

fun OrbitMap.checksum(): Int {
    class ChecksumVisitor : DepthUniversalOrbitMapVisitor {
        var checksum = 0
            private set

        override fun visit(obj: Object, depth: Int) {
            checksum += depth
        }
    }

    val visitor = ChecksumVisitor()
    visit(visitor)
    return visitor.checksum
}

private fun OrbitMap.orbitalTransfersBetween(obj1: Object, obj2: Object): Int {
    class PathFinderVisitor(val objects: Set<Object>) : PathUniversalOrbitMapVisitor {
        private val paths: MutableMap<Object, Path> = HashMap()

        fun pathFor(obj: Object): Path? = paths[obj]
        override fun visit(obj: Object, pathToObject: Path) {
            if (obj in objects) paths[obj] = pathToObject.add(obj)
        }
    }

    fun lastCommonObject(path1: Path, path2: Path): Object? {
        val iter1 = path1.iter()
        val iter2 = path2.iter()
        var lastCommonObject: Object? = null
        while (iter1.hasNext() && iter2.hasNext()) {
            val next1 = iter1.next()
            val next2 = iter2.next()
            if (next1 == next2) {
                lastCommonObject = next1
            } else {
                break
            }
        }
        return lastCommonObject
    }

    val pathFinderVisitor = PathFinderVisitor(setOf(obj1, obj2))
    visit(pathFinderVisitor)
    val path1 = pathFinderVisitor.pathFor(obj1) ?: throw IllegalArgumentException("Object $obj1 not found")
    val path2 = pathFinderVisitor.pathFor(obj2) ?: throw IllegalArgumentException("Object $obj2 not found")
    val lastCommonObject = lastCommonObject(path1, path2) ?: throw IllegalArgumentException("Objects $obj1, $obj2 has no common paths")
    val subPath1 = path1.subPath(lastCommonObject)
    val subPath2 = path2.subPath(lastCommonObject)
    return subPath1.orbits() + subPath2.orbits()
}
