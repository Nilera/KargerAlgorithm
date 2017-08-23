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
fun findMincut(graph: Graph): Int {
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
fun findMincutFast(graph: Graph, c: Int = 10): Int {
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
    var (edges, vertexTo, nextVertexNumber) = graph.deepCopy()
    while (vertexTo.size > 2) {
        val edge = edges[RANDOM.nextInt(edges.size)]
        contract(nextVertexNumber++, edge, edges, vertexTo)
    }
    return edges.first().weight // should remain only one edge (two with reverse one)
}

private fun getCutSlow(graph: Graph): Int {
    val copiedGraph = graph.deepCopy()
    var (edges, vertexTo, nextVertexNumber) = copiedGraph
    val lowerBound = Math.floor(graph.vertexTo.size / Math.sqrt(2.0)).toInt()
    while (vertexTo.size > lowerBound) {
        val edge = edges[RANDOM.nextInt(edges.size)]
        contract(nextVertexNumber++, edge, edges, vertexTo)
    }
    return Math.min(getCut(copiedGraph.deepCopy()), getCut(copiedGraph))
}

private fun contract(w: Int,
                     uv: Edge,
                     edges: MutableList<Edge>,
                     vertexTo: MutableMap<Int, MutableMap<Int, Edge>>) {
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
            removeEdge(edge, edges)
            removeEdge(reverseEdge, edges)
            vertexTo[edge.from]!![edge.to]!!.weight += edge.weight
            vertexTo[reverseEdge.from]!![reverseEdge.to]!!.weight += reverseEdge.weight
        } else {
            vertexTo[edge.from]!![edge.to] = edge
            vertexTo[reverseEdge.from]!![reverseEdge.to] = reverseEdge
        }
    }

    removeEdge(uv, edges)
    removeEdge(vu, edges)
}

private fun removeEdge(edge: Edge, edges: MutableList<Edge>) {
    val lastEdge = edges.last()
    lastEdge.index = edge.index
    edges[lastEdge.index] = lastEdge
    edges.removeAt(edges.size - 1)
}

