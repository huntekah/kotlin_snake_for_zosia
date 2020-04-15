package implementation.fuzzy

import api.Direction
import api.MovementDecider
import api.Point
import api.Tile
import kotlin.math.roundToInt
import kotlin.coroutines.experimental.*

class FuzzyLogicMovementDecider: MovementDecider {

    val logicController = LogicController()

    override fun decide(
        board: List<List<Tile>>,
        snake: List<Point>,
        apple: Point,
        forbiddenDirection: Direction
    ): Direction {
        var strategy = logicController.getStrategyType(snake.count()).roundToInt()
        var direction = Direction.RIGHT
        println(strategy)
        

        if(strategy == 0 ){
            //gainMoreTime()
            direction = Direction.UP //TODO
        }
        else if(strategy == 1){
           direction =  straightToTheApple(board,snake,apple,forbiddenDirection)
        }
        else if(strategy == 2){
           direction = fastestRoute(board,snake,apple)
        }
        else{ // if(strategy == 3){
            //longestRoute()
            direction = Direction.UP //TODO
        }
        return direction
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
        var planningBoard = Array(board.count(), {IntArray(board[0].count()) {0} } )
        //inicjalizowanko
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
        println(planningBoard.toList().map { it.toList() })
        var head = snake.first()
        var direction = guidedDFS(planningBoard,head,apple)

        return direction
    }

    fun guidedDFS(
        planningBoard : Array<IntArray>,
        head: Point,
        apple: Point
    ): Direction {
        val moves = getMoves(head,apple)

        for (move in moves){
            var nextHead = Point(head.x + move.x, head.y + move.y)
            println("${head} ${apple}")
            if ( DFSutil(planningBoard,nextHead, apple)){ // znaleziono sciezke do jablka
                when {
                    move.x == 1 -> return Direction.RIGHT
                    move.x == -1 -> return Direction.LEFT
                    move.y == 1 -> return Direction.DOWN
                    move.y == -1 -> return Direction.UP
                }
            }
        }
    return Direction.DOWN // to delete
    }

    fun DFSutil(
        planningBoard : Array<IntArray>,
        head: Point,
        apple: Point
    ): Boolean {

        // check if not out of array
        if (head.x < 0 || head.x >= planningBoard.count()){
            return false
        }
        if (head.y < 0 || head.y >= planningBoard[0].count()){
            return false
        }

        if (planningBoard[head.x][head.y] == -1){
            return false
        }
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
 }
