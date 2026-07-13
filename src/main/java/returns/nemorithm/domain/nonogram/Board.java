package returns.nemorithm.domain.nonogram;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Getter
@Slf4j
public class Board {
    private final int row;
    private final int col;
    private final Cell[][] cells;

    public Board(int row, int col) {
        this.row = row;
        this.col = col;
        this.cells = new Cell[row][col];
        for(int i = 0; i < row; i++) {
            Arrays.fill(this.cells[i], Cell.NONE);
        }
    }

    public Board copy() {
        Board board = new Board(row, col);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                board.cells[i][j] = cells[i][j];
            }
        }
        return board;
    }

    public Cell[] getLineSegment(Direction direction, int idx, int start, int end) {
        if(direction == Direction.ROW) {
            return Arrays.copyOfRange(cells[idx], start, end);
        } else {
            Cell[] result = new Cell[end - start];
            for (int r = start; r < end; r++) {
                result[r - start] = cells[r][idx];
            }
            return result;
        }
    }

    public boolean mark(Position position, Cell status, String message, LineSegment lineSegment) {
        Cell cell = cells[position.row()][position.col()];

        if (cell != Cell.NONE) {
            return false;
        }

        log.info(
            "mark for {} - position=({}, {}), status={}, lineSegment={}",
            message,
            position.row()+1,
            position.col()+1,
            status,
            lineSegment
        );

        cells[position.row()][position.col()] = status;
        return true;
    }
}
