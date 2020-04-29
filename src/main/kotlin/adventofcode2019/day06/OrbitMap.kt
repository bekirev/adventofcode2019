package adventofcode2019.day06

import java.util.LinkedList

internal inline class Object(val id: String)

internal class OrbitMap private constructor(private val root: Node) {
    companion object {
        private val COM = Object("COM")

        fun fromRelationsMap(map: Map<Object, Set<Object>>): OrbitMap {
            fun createTree(map: Map<Object, Set<Object>>, root: Object): Node {
                val children = map[root] ?: emptySet()
                val nodes: Set<Node> = when {
                    children.isEmpty() -> emptySet<Node>()
                    else -> {
                        val nodes = HashSet<Node>()
                        for (child in children) {
                            nodes.add(createTree(map, child))
                        }
                        nodes
                    }
                }
                return Node(root, nodes)
            }
            return OrbitMap(createTree(map, COM))
        }
    }

    fun visit(visitor: DepthUniversalOrbitMapVisitor) {
        root.visit(visitor)
    }

    fun visit(visitor: PathUniversalOrbitMapVisitor) {
        root.visit(visitor)
    }
}

private data class Node(val obj: Object, val children: Set<Node>)

internal interface DepthUniversalOrbitMapVisitor {
    fun visit(obj: Object, depth: Int)
}

private fun Node.visit(visitor: DepthUniversalOrbitMapVisitor) {
    fun Node.visit(visitor: DepthUniversalOrbitMapVisitor, depth: Int) {
        visitor.visit(obj, depth)
        for (child in children) {
            child.visit(visitor, depth + 1)
        }
    }
    visit(visitor, 0)
}

internal interface PathUniversalOrbitMapVisitor {
    fun visit(obj: Object, pathToObject: Path)
}

private fun Node.visit(visitor: PathUniversalOrbitMapVisitor) {
    fun Node.visit(visitor: PathUniversalOrbitMapVisitor, pathToObject: Path) {
        visitor.visit(obj, pathToObject)
        val pathForChildren = pathToObject.add(obj)
        for (child in children) {
            child.visit(visitor, pathForChildren)
        }
    }
    visit(visitor, Path.EMPTY)
}

internal class Path private constructor(private val list: LinkedList<Object>) {
    companion object {
        val EMPTY = Path(LinkedList())

        private class PathIteratorImpl(private val iter: ListIterator<Object>) : PathIterator {
            override fun next(): Object = iter.next()
            override fun hasNext(): Boolean = iter.hasNext()
            override fun prev(): Object = iter.previous()
            override fun hasPrev(): Boolean = iter.hasPrevious()
        }
    }

    interface PathIterator {
        fun next(): Object
        fun hasNext(): Boolean
        fun prev(): Object
        fun hasPrev(): Boolean
    }

    fun iter(): PathIterator = PathIteratorImpl(list.listIterator())

    fun add(obj: Object): Path {
        return Path(
            LinkedList<Object>().apply {
                addAll(list)
                add(obj)
            }
        )
    }

    fun subPath(obj: Object): Path {
        var objFound = false
        val subPathList = LinkedList<Object>()
        for (objIt in list) {
            if (objIt == obj) {
                subPathList.add(objIt)
                objFound = true
            } else {
                if (objFound) {
                    subPathList.add(objIt)
                }
            }
        }
        return Path(subPathList)
    }

    fun orbits(): Int {
        val size = list.size
        return if (size > 1) size - 1 else 0
    }
}
