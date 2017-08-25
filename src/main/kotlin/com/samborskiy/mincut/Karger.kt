package com.samborskiy.mincut

import java.util.*
import java.util.stream.Stream
import kotlin.collections.HashMap

private val RANDOM = Random()

/**
 * Simple Karger's algorithm implementation.
 *
 * @see <a href="http://www.leonidzhukov.net/hse/2012/socialnetworks/papers/mincut.pdf">Karger's algorithm</a>
 */
fun karger(graph: Graph): Int {
    var mincut = Int.MAX_VALUE
    val n = graph.vertexTo.size

    val count = Math.floor(n * n * Math.log(n.toDouble()) / Math.log(Math.E)).toInt()
    for (i in 0..count) {
        val cut = getCut(graph)
        if (cut < mincut) {
            mincut = cut
        }
    }

    return mincut
}

/**
 * Karger-Stein algorithm implementation.
 *
 * @see <a href="http://people.csail.mit.edu/karger/Papers/contract.pdf">Karger-Stein algorithm</a>
 */
fun kargerStein(graph: Graph, c: Int = 10): Int {
    var mincut = Int.MAX_VALUE
    val n = graph.vertexTo.size

    val count = Math.floor(c * Math.pow(Math.log(n.toDouble()), 2.0)).toInt()
    for (i in 0..count) {
        val cut = getCutSlow(graph)
        if (cut < mincut) {
            mincut = cut
        }
    }

    return mincut
}

private fun getCut(graph: Graph): Int {
    val contractedGraph = multiContract(graph, 2)
    return contractedGraph.edges.first().weight // should remain only one edge (two with reverse one)
}

private fun getCutSlow(graph: Graph): Int {
    return if (graph.vertexTo.size <= 6) {
        getCut(graph)
    } else {
        val lowerBound = Math.ceil(1 + graph.vertexTo.size / Math.sqrt(2.0)).toInt()
        val graph1 = multiContract(graph, lowerBound)
        val graph2 = multiContract(graph, lowerBound)
        Math.min(getCutSlow(graph1), getCutSlow(graph2))
    }
}

private fun multiContract(graph: Graph, t: Int): Graph {
    val copiedGraph = graph.deepCopy()
    while (copiedGraph.vertexTo.size > t) {
        val edge = copiedGraph.edges[RANDOM.nextInt(copiedGraph.edges.size)]
        contract(copiedGraph.nextVertexNumber++, edge, copiedGraph.edges, copiedGraph.vertexTo)
    }
    return copiedGraph
}

private fun contract(
        w: Int,
        uv: Edge,
        edges: MutableList<Edge>,
        vertexTo: MutableMap<Int, MutableMap<Int, Edge>>
) {
    val (u, v) = uv
    val vu = vertexTo[v]!![u]!!

    vertexTo[w] = HashMap()

    Stream.concat(
            vertexTo.remove(u)!!.entries.stream().filter { (to) -> to != v },
            vertexTo.remove(v)!!.entries.stream().filter { (to) -> to != u }
    ).forEach { (_, edge) ->
        val reverseEdge = vertexTo[edge.to]!!.remove(edge.from)!!
        edge.from = w
        reverseEdge.to = w

        if (vertexTo[reverseEdge.from]!!.contains(reverseEdge.to)) {
            edges.removeEdge(edge)
            edges.removeEdge(reverseEdge)
            vertexTo[edge.from]!![edge.to]!!.weight += edge.weight
            vertexTo[reverseEdge.from]!![reverseEdge.to]!!.weight += reverseEdge.weight
        } else {
            vertexTo[edge.from]!![edge.to] = edge
            vertexTo[reverseEdge.from]!![reverseEdge.to] = reverseEdge
        }
    }

    edges.removeEdge(uv)
    edges.removeEdge(vu)
}

private fun MutableList<Edge>.removeEdge(edge: Edge) {
    val lastEdge = last()
    lastEdge.index = edge.index
    set(lastEdge.index, lastEdge)
    removeAt(lastIndex)
}
