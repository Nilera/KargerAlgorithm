package com.samborskiy.mincut

import java.io.BufferedReader
import java.io.FileReader
import java.util.*
import java.util.stream.Stream
import kotlin.streams.toList

private val RANDOM = Random()

fun readGraph(filename: String): Graph {
    BufferedReader(FileReader(filename)).use {
        val edges = it.lines()
                .flatMap { line ->
                    val (from, to, weight) = line.split(" ").map { value -> value.toInt() }
                    Stream.of(Edge(from, to, weight), Edge(to, from, weight))
                }.toList()

        return buildGraph(edges)
    }
}

fun generateTwoComponentGraph(componentSize: Int, linkedEdgesNumber: Int): Graph {
    val edges = ArrayList<Edge>(2 * (4 * componentSize + linkedEdgesNumber))

    for (from in 0 until componentSize) {
        for (to in from until componentSize) {
            edges += Edge(from, to, 1)
            edges += Edge(to, from, 1)
        }
    }
    for (from in componentSize until 2 * componentSize) {
        for (to in from until 2 * componentSize) {
            edges += Edge(from, to, 1)
            edges += Edge(to, from, 1)
        }
    }

    for (i in 0 until linkedEdgesNumber) {
        val from = RANDOM.nextInt(componentSize)
        val to = RANDOM.nextInt(componentSize) + componentSize
        edges += Edge(from, to, 1)
        edges += Edge(to, from, 1)
    }

    return buildGraph(edges)
}

fun generateRandomGraph(verticesNumber: Int, edgesNumber: Int, maxWeight: Int = 10): Graph {
    val edges = ArrayList<Edge>(2 * edgesNumber)

    var from = RANDOM.nextInt(verticesNumber)
    for (i in 0 until edgesNumber) {
        val to = RANDOM.nextInt(verticesNumber)
        val weight = RANDOM.nextInt(maxWeight)
        edges += Edge(from, to, weight)
        edges += Edge(to, from, weight)
        from = to
    }

    return buildGraph(edges)
}