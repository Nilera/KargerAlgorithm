package com.samborskiy.mincut

import org.junit.Test
import java.io.BufferedReader
import java.io.FileReader
import java.util.*
import java.util.stream.Stream
import kotlin.collections.ArrayList
import kotlin.streams.toList
import kotlin.test.assertEquals

private val RANDOM = Random()

class KargerTest {

    @Test
    fun simpleTest() {
        val graph = readGraphFromResources("simpleGraph")
        assertEquals(8, findMincut(graph))
        assertEquals(8, findMincutFast(graph))
    }

    @Test
    fun wikiTest() {
        val graph = readGraphFromResources("wikiGraph")
        assertEquals(3, findMincut(graph))
        assertEquals(3, findMincutFast(graph))
    }

    @Test
    fun uniformTest() {
        for (i in 0..10) {
            val answer = RANDOM.nextInt(5) + 1
            val graph = generateTwoComponentGraph(20, answer)
            assertEquals(answer, findMincut(graph))
            assertEquals(answer, findMincutFast(graph))
            println()
        }
    }

    private fun readGraphFromResources(filename: String): Graph {
        val resourceFilename = KargerTest::class.java.classLoader.getResource(filename).file
        return readGraph(resourceFilename)
    }

    private fun readGraph(filename: String): Graph {
        BufferedReader(FileReader(filename)).use {
            val edges = it.lines()
                    .flatMap { line ->
                        val (from, to, weight) = line.split(" ").map { value -> value.toInt() }
                        Stream.of(Edge(from, to, weight), Edge(to, from, weight))
                    }.toList()

            return buildGraph(edges)
        }
    }

    private fun generateTwoComponentGraph(componentSize: Int, linkedEdgesNumber: Int): Graph {
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
}
