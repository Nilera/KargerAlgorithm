package com.samborskiy.mincut

import org.junit.Test
import java.util.*

private val RANDOM = Random()

class PerformanceKargerTest {

    @Test
    fun performanceTest() {
        val lowerBound = 25
        val upperBound = 75
        val step = 5
        val iteration = 100

        val functions = ArrayList<(Graph) -> Int>().apply {
            add({ g: Graph -> findMincut(g) })
            (10..100 step 10).forEach { i ->
                add({ g: Graph -> findMincutFast(g, i) })
            }
        }

        val graphs = (lowerBound..upperBound step step)
                .map { verticesNumber -> generateRandomGraph(verticesNumber, 1500) }
                .toList()
        val answers = graphs.map { graph -> findMincut(graph) } // let's consider that Karger's algorithm always correct

        functions.forEachIndexed { index, mincut ->
            println("%d mincut function:".format(index))
            graphs.forEachIndexed { i, graph ->
                val (time, errors) = getAvrStatistics(answers[i], graph, mincut, iteration)
                println("n = %d: avr time = %d and errors = %d"
                        .format((i + 1) * step + lowerBound, time, errors))
            }
            println()
        }
    }

    private fun getAvrStatistics(answer: Int, graph: Graph, mincut: (Graph) -> Int, iteration: Int): Pair<Long, Int> {
        var totalTime: Long = 0
        val errors = (1..iteration)
                .map {
                    val (time, ans) = getStatistics(graph, mincut)
                    totalTime += time
                    ans
                }.filter { ans -> ans != answer }
                .count()
        return Pair(totalTime.div(iteration), errors)
    }

    private fun getStatistics(graph: Graph, mincut: (Graph) -> Int): Pair<Long, Int> {
        val time = System.currentTimeMillis()
        val answer = mincut(graph)
        return Pair(System.currentTimeMillis() - time, answer)
    }


    private fun generateRandomGraph(verticesNumber: Int, edgesNumber: Int, maxWeight: Int = 10): Graph {
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
}
