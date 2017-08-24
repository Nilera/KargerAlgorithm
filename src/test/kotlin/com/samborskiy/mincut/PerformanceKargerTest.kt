package com.samborskiy.mincut

import org.junit.Test
import java.util.*

private val RANDOM = Random()

class PerformanceKargerTest {

    private val FUNCTIONS = ArrayList<(Graph) -> Int>().apply {
        add({ g: Graph -> findMincut(g) })
        (10..100 step 10).forEach { i ->
            add({ g: Graph -> findMincutFast(g, i) })
        }
    }

    @Test
    fun uniformPerformanceTest1() {
        val lowerBound = 25
        val upperBound = 75
        val step = 5
        val iteration = 100

        val graphs = (lowerBound..upperBound step step)
                .map { verticesNumber -> generateRandomGraph(verticesNumber, 1500) }
        val answers = graphs.map { graph -> findMincut(graph) } // let's consider that Karger's algorithm always correct

        performanceTest(FUNCTIONS, graphs, answers, lowerBound, step, iteration)
    }

    @Test
    fun uniformPerformanceTest2() {
        val lowerBound = 5
        val upperBound = 20
        val step = 5
        val iteration = 100

        val answers = (lowerBound..upperBound step step)
                .map { verticesNumber -> RANDOM.nextInt(verticesNumber - 2) + 1 }
        val graphs = (lowerBound..upperBound step step)
                .mapIndexed { index, verticesNumber -> generateTwoComponentGraph(verticesNumber, answers[index]) }

        performanceTest(FUNCTIONS, graphs, answers, lowerBound, step, iteration)
    }

    private fun performanceTest(functions: List<(Graph) -> Int>, graphs: List<Graph>, answers: List<Int>, lowerBound: Int, step: Int, iteration: Int) {
        functions.forEachIndexed { index, mincut ->
            println("%d mincut function:".format(index))
            graphs.forEachIndexed { i, graph ->
                val (time, errors) = getAvrStatistics(answers[i], graph, mincut, iteration)
                println("n = %d: avr time = %d and errors = %d"
                        .format(i * step + lowerBound, time, errors))
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
}
