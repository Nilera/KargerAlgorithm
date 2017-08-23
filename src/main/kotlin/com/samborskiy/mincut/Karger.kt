package com.samborskiy.mincut

import java.util.*
import java.util.stream.Stream
import kotlin.collections.HashMap

private val RANDOM = Random()

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

private fun getCut(graph: Graph): Int {
    var (edges, vertexTo, nextVertexNumber) = graph.deepCopy()
    while (vertexTo.size > 2) {
        val edge = edges[RANDOM.nextInt(edges.size)]
        contract(nextVertexNumber++, edge, edges, vertexTo)
    }
    return edges.first().weight // should remain only one edge (two with reverse one)
}

private fun contract(w: Int,
                     uv: Edge,
                     edges: MutableList<Edge>,
                     vertexTo: MutableMap<Int, MutableMap<Int, Edge>>) {
    val (u, v) = uv
    val vu = vertexTo[v]!![u]!!

    vertexTo[w] = HashMap<Int, Edge>()

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

