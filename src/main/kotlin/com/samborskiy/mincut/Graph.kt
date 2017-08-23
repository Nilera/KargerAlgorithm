package com.samborskiy.mincut

import java.util.*

fun buildGraph(immutableEdges: List<Edge>): Graph {
    var count = 0

    val edges = ArrayList<Edge>(immutableEdges.size)
    val vertexTo = HashMap<Int, MutableMap<Int, Edge>>()
    for ((from, to, weight) in immutableEdges) {
        if (from == to) { // edge loop
            continue
        }

        val edge = vertexTo.getOrPut(from, { HashMap() })
                .getOrPut(to, {
                    val edge = Edge(from, to, 0, count++)
                    edges += edge
                    edge
                })
        edge.weight += weight
    }

    return Graph(edges, vertexTo, vertexTo.keys.max()!! + 1)
}

data class Edge(var from: Int, var to: Int, var weight: Int, var index: Int = -1) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Edge

        if (from != other.from) return false
        if (to != other.to) return false

        return true
    }

    override fun hashCode(): Int {
        var result = from
        result = 31 * result + to
        return result
    }
}

data class Graph(
        val edges: MutableList<Edge>,
        val vertexTo: MutableMap<Int, MutableMap<Int, Edge>>,
        var nextVertexNumber: Int
) {

    fun deepCopy(): Graph = buildGraph(edges)
}
