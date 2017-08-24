package com.samborskiy.mincut

import org.junit.Test
import java.util.*
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
}
