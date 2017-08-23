package com.samborskiy.mincut

import org.junit.Test
import java.io.BufferedReader
import java.io.FileReader
import java.util.stream.Stream
import kotlin.streams.toList
import kotlin.test.assertEquals

class KargerTest {

    @Test
    fun simpleTest() {
        val filename = KargerTest::class.java.classLoader.getResource("simpleGraph").file
        val graph = readGraph(filename)
        assertEquals(8, findMincut(graph))
    }

    @Test
    fun wikiTest() {
        val filename = KargerTest::class.java.classLoader.getResource("wikiGraph").file
        val graph = readGraph(filename)
        assertEquals(3, findMincut(graph))
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
}
