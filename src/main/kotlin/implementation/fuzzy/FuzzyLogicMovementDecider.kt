package implementation.fuzzy

import api.Direction
import api.MovementDecider
import api.Point
import api.Tile
import kotlin.math.roundToInt
import kotlin.coroutines.experimental.*
import java.util.*

class FuzzyLogicMovementDecider: MovementDecider {

    val logicController = LogicController()

    override fun decide(
        board: List<List<Tile>>,
        snake: List<Point>,
        apple: Point,
        forbiddenDirection: Direction
    ): Direction {
        var planningBoard = createPlanningBoard(board,snake.last())
        var isVisible = if (isAppleVisible(planningBoard, snake.first(), apple)) 1; else 0
        var strategy = logicController.getStrategyType(snake.count(), isVisible).roundToInt()
        var direction = Direction.RIGHT
        println(strategy)
        

        if(strategy == 0 ){
            direction = gainMoreTime(board,snake,apple)
        }
        else if(strategy == 1){
           direction =  straightToTheApple(board,snake,apple,forbiddenDirection)
        }
        else if(strategy == 2){
           direction = fastestRoute(board,snake,apple)
           //direction = fastestBFS(board,snake,apple)
        }
        else{ // if(strategy == 3){
            //longestRoute()
            direction = fastestBFS(board,snake,apple)
        }
        return direction
    }

    fun isAppleVisible(
        planningBoard : Array<IntArray>,
        head: Point,
        apple: Point
    ): Boolean {
        if (head.x < 0 || head.x >= planningBoard.count()){
            return false
        }
        if (head.y < 0 || head.y >= planningBoard[0].count()){
            return false
        }

        var result = appleBFS(planningBoard,head,apple)

        return result
    }

    fun appleBFS(
        planningBoard : Array<IntArray>,
        head: Point,
        apple: Point
    ): Boolean {
        if (head == apple) {
            return true
        }

        planningBoard[head.x][head.y]=-1
        //val queue = Queue<Point>();
        //val queue = mutableListOf<Point>()
        val queue = LinkedList<Point>()
        
        queue.add(head)

        while (queue.count() != 0){

            var current = queue.poll()
            if (current == apple) {
                return true
            }
            val moves = getMoves(head,apple)

            for (move in moves){
                var nextHead = Point(current.x + move.x, current.y + move.y)
                if (nextHead.x < 0 || nextHead.x >= planningBoard.count()){
                    continue
                }
                if (nextHead.y < 0 || nextHead.y >= planningBoard[0].count()){
                    continue
                }

                if(planningBoard[nextHead.x][nextHead.y] == 0){
                    planningBoard[nextHead.x][nextHead.y] = -1
                    queue.add(nextHead)
                    //println("queue adding ${nextHead}")
                    //Thread.sleep(150)
                }
                else if (planningBoard[nextHead.x][nextHead.y] == 2){
                    return true
                }

            }
        }
    return false // to delete
    }

    fun straightToTheApple(
        board: List<List<Tile>>,
        snake: List<Point>,
        apple: Point,
        forbiddenDirection: Direction
    ): Direction {
        var head = snake.first()
        if (head.x > apple.x) {
            if (forbiddenDirection != Direction.LEFT){
                return Direction.LEFT
            }
        } else if (head.x < apple.x) {
            if (forbiddenDirection != Direction.RIGHT){
                return Direction.RIGHT
            }
        }
        if (head.y > apple.y) {
            if (forbiddenDirection != Direction.UP){
                return Direction.UP
            }
        } else if (head.y < apple.y) {
            if (forbiddenDirection != Direction.DOWN){
                return Direction.DOWN
            }
        }
        return Direction.UP // never happens
    }

    // wchodze do kazdej z dozwolonych kratek dookola. (rekursywnie)
    // jesli znajde jablko, to zwracam jakies true i nie robie pozostalych przeszukiwan.
    fun fastestRoute(
        board: List<List<Tile>>,
        snake: List<Point>,
        apple: Point
    ): Direction {
        var planningBoard = createPlanningBoard(board,snake.last())
        var head = snake.first()
        var maskedBoard = markUnreachableTiles(planningBoard,head,apple)

        //println(planningBoard.toList().map { it.toList() })
        println("HEAD: ${snake.first()}; TAIL: ${snake.last()}; APPLE: ${apple}")
        println(snake)

        var direction = guidedDFS(maskedBoard,head, snake.last(),apple)
        return direction
    }

    fun guidedDFS(
        planningBoard : Array<IntArray>,
        head: Point,
        tail: Point,
        apple: Point
    ): Direction {
        val moves = getMoves(head,apple)

        for (move in moves){
            var nextHead = Point(head.x + move.x, head.y + move.y)
            println("${nextHead} ${apple}")
            
            var newBoard = Array(planningBoard.count(), {IntArray(planningBoard[0].count()) {0} })
            for (i in planningBoard.indices){
                newBoard[i] = planningBoard[i].copyOf()
            }

            if (! isAppleVisible(newBoard, nextHead, apple)){
                continue
            }

            if ( DFSutil(planningBoard,nextHead, apple)){ // znaleziono sciezke do jablka
                when {
                    move.x == 1 -> return Direction.RIGHT
                    move.x == -1 -> return Direction.LEFT
                    move.y == 1 -> return Direction.DOWN
                    move.y == -1 -> return Direction.UP
                }
            }
        }
    println("Nie umialem dojsc")
    return Direction.DOWN // to delete
    }

    fun DFSutil(
        planningBoard : Array<IntArray>,
        head: Point,
        apple: Point
    ): Boolean {
           // println("dfs${head} ${apple}")
        // check if not out of array
        if (head.x < 0 || head.x >= planningBoard.count()){
            return false
        }
        if (head.y < 0 || head.y >= planningBoard[0].count()){
            return false
        }

        // if you landed on a snake-tile
        if (planningBoard[head.x][head.y] == -1){
            return false
        } // or apple tile
        else if (planningBoard[head.x][head.y] == 2){
            return true
        }
        
        var newBoard = Array(planningBoard.count(), {IntArray(planningBoard[0].count()) {0} })
        for (i in planningBoard.indices){
            newBoard[i] = planningBoard[i].copyOf()
        }
        newBoard[head.x][head.y]=-1
        
        val moves = getMoves(head,apple)

        for (move in moves){
            var nextHead = Point(head.x + move.x, head.y + move.y)
            if ( DFSutil(newBoard,nextHead, apple)){ // znaleziono sciezke do jablka
                return true
            }
        }
        return false
    }

    fun getMoves(
        head: Point,
        apple: Point
    ): List<Point> {
        val moves = mutableListOf<Point>()
        var result = Point(head.x - apple.x, head.y - apple.y)
        when {
            result.x > 0 -> moves.add(Point(-1,0))
            result.x < 0 -> moves.add(Point(1,0))
        }
        when {
            result.y > 0 -> moves.add(Point(0,-1))
            result.y < 0 -> moves.add(Point(0,1))
        }

        when {
            result.x > 0 -> moves.add(Point(1,0))
            result.x < 0 -> moves.add(Point(-1,0))
            else -> {
                moves.add(Point(1,0))
                moves.add(Point(-1,0))
            }
        }
        when {
            result.y > 0 -> moves.add(Point(0,1))
            result.y < 0 -> moves.add(Point(0,-1))
            else -> {
                moves.add(Point(0,1))
                moves.add(Point(0,-1))
            }
        }
    return moves
    }

    fun createPlanningBoard(
        board: List<List<Tile>>,
        tail: Point
    ): Array<IntArray> {
        var planningBoard = Array(board.count(), {IntArray(board[0].count()) {0} } )
        for (row in board.indices){
            for (col in board[row].indices){
                if (board[row][col] == Tile.SNAKE){
                    planningBoard[row][col] = -1
                }
                else if (board[row][col] == Tile.APPLE){
                    planningBoard[row][col] = 2
                }
            }
        }
        //planningBoard[tail.x][tail.y] = 0

        return planningBoard
    }

    fun markUnreachableTiles(
        planningBoard : Array<IntArray>,
        head: Point,
        apple: Point
    ): Array<IntArray> {
        if (head.x < 0 || head.x >= planningBoard.count()){
            throw IllegalStateException()
        }
        if (head.y < 0 || head.y >= planningBoard[0].count()){
            throw IllegalStateException()
        }
        //planningBoard[head.x][head.y]=-1
        val queue = LinkedList<Point>()
        
        queue.add(apple)

        while (queue.count() != 0){

            var current = queue.poll()
            val moves = getMoves(apple,head)

            for (move in moves){
                var nextHead = Point(current.x + move.x, current.y + move.y)
                if (nextHead.x < 0 || nextHead.x >= planningBoard.count()){
                    continue
                }
                if (nextHead.y < 0 || nextHead.y >= planningBoard[0].count()){
                    continue
                }

                if(planningBoard[nextHead.x][nextHead.y] == 0){
                    planningBoard[nextHead.x][nextHead.y] = 1
                    queue.add(nextHead)
                }
            }
        }
    // first mark all unnecesary states as if it was a snake tile (-1)
    for (row in planningBoard.indices){
        for (col in planningBoard[row].indices){
            if (planningBoard[row][col] == 0){
                planningBoard[row][col] = -1
            }
        }
    }

    // then mark all good states as empty (0)
    for (row in planningBoard.indices){
        for (col in planningBoard[row].indices){
            if (planningBoard[row][col] == 1){
                planningBoard[row][col] = 0
            }
        }
    }

    return planningBoard
    }

fun fastestBFS(
        board: List<List<Tile>>,
        snake: List<Point>,
        apple: Point
    ): Direction {
        var planningBoard = createPlanningBoard(board,snake.last())
        var head = snake.first()
        var maskedBoard = markUnreachableTiles(planningBoard,head,apple)

        //println(planningBoard.toList().map { it.toList() })
        println("BFS HEAD: ${snake.first()}; TAIL: ${snake.last()}; APPLE: ${apple}")
        //println(snake)

        var direction = guidedBFS(maskedBoard,snake,apple)
        return direction
    }

    fun guidedBFS(
        planningBoard : Array<IntArray>,
        snake: List<Point>,
        apple: Point
    ): Direction {
        var head = snake.first()
        if (head == apple) {
            throw IllegalStateException()
        }
        val neighbours = getMoves(head,apple)
        for (neighbour in neighbours){
            var possible_apple = Point(head.x + neighbour.x, head.y + neighbour.y)
            if(apple == possible_apple){
                return head.dirTo(apple)
            }
        }
        planningBoard[head.x][head.y]=-1
        //val queue = Queue<Point>();
        //val queue = mutableListOf<Point>()
        val queue = LinkedList<Point>()
        val prev = mutableMapOf<Point,Point>()        
        queue.add(head)

        loop@ while (queue.count() != 0){

            var current = queue.poll()
            if (current == apple) {
                break
            }
            val moves = getMoves(head,apple)

            for (move in moves){
                var nextHead = Point(current.x + move.x, current.y + move.y)
                if (nextHead.x < 0 || nextHead.x >= planningBoard.count()){
                    continue
                }
                if (nextHead.y < 0 || nextHead.y >= planningBoard[0].count()){
                    continue
                }

                if(planningBoard[nextHead.x][nextHead.y] == 0){
                    planningBoard[nextHead.x][nextHead.y] = -1
                    queue.add(nextHead)
                    prev.put(nextHead, current)
                    //println("queue adding ${nextHead}")
                    //Thread.sleep(150)
                }
                else if (planningBoard[nextHead.x][nextHead.y] == 2){
                    prev.put(nextHead, current)
                    break@loop
                }

            }
        }
        // we found some path, now we need to reconstruct it.
        if(prev[apple] == null){
            println("random response1")
            return Direction.random(snake[0].dirTo(snake[1]))
        }
        var i: Point = prev[apple] as Point // unsafe. how to fix it?
        val path = LinkedList<Point>()
        while(prev.containsKey(i)){
            path.add(i)

            if(prev[i] == null){
                println("random response2")
                return Direction.random(snake[0].dirTo(snake[1]))
            }
            i=prev[i] as Point
        }

    return head.dirTo(path.last())
    }

    fun gainMoreTime(
        board: List<List<Tile>>,
        snake: List<Point>,
        apple: Point
    ): Direction {

        for( tile in snake.reversed()){
            var planningBoard = createPlanningBoard(board,snake.last())
            var visibilityBoard = createPlanningBoard(board,snake.last())
            planningBoard[apple.x][apple.y] = 0
            planningBoard[tile.x][tile.y] = 2
            visibilityBoard[apple.x][apple.y] = 0
            visibilityBoard[tile.x][tile.y] = 2
            
            if (isAppleVisible(visibilityBoard, snake.first(), tile)){
                return guidedBFS(planningBoard,snake, tile)
            }
            planningBoard[tile.x][tile.y] = -1
        }
        return Direction.random(snake[0].dirTo(snake[1]))
    }

 }
