package returns.nemorithm.solver;

import lombok.extern.slf4j.Slf4j;
import returns.nemorithm.domain.nonogram.*;
import returns.nemorithm.util.LineValidator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


@Slf4j
public class Core {
    public static void start(Nonogram nonogram) {
        log.info("Nonogram solving started");

        if(nonogram.getBoard().getRow() > 100 || nonogram.getBoard().getCol() > 100) {
            throw new IllegalArgumentException("Board size is too big");
        }

        Queue<LineSegment> q = new ArrayDeque<>();

        initialize(q, nonogram);

        while(!q.isEmpty()) {
            LineSegment poll = q.poll();

            List<Position> changedPositions = new ArrayList<>();

            changedPositions.addAll(FindTrue.find(nonogram.getBoard(), poll));
            changedPositions.addAll(FindFalse.find(nonogram.getBoard(), poll));
            changedPositions.addAll(FindDivision.findBasicIntersectionAfterConfirmedFrontOwner(nonogram.getBoard(), poll));

            if(!changedPositions.isEmpty()) {
                nonogram.saveSnapshot();

                for(Position p : changedPositions) {
                    addQueue(q, nonogram, p);
                }
            }

            List<LineSegment> dividedSegments = FindDivision.findRemainder(nonogram.getBoard(), poll);
            q.addAll(dividedSegments);
        }

        log.info("Nonogram solving algorithm finished");

        // all match 확인
        boolean completed = true;

        for (int row = 0; row < nonogram.getBoard().getRow(); row++) {
            Cell[] cells = nonogram.getBoard().getLineSegment(
                    Direction.ROW,
                    row,
                    0,
                    nonogram.getBoard().getCol()
            );

            if (!LineValidator.isCompleted(cells, nonogram.getElement().getRows()[row])) {
                completed = false;
                break;
            }
        }
        if (completed) {
            for (int col = 0; col < nonogram.getBoard().getCol(); col++) {
                Cell[] cells = nonogram.getBoard().getLineSegment(
                        Direction.COL,
                        col,
                        0,
                        nonogram.getBoard().getRow()
                );

                if (!LineValidator.isCompleted(cells, nonogram.getElement().getCols()[col])) {
                    completed = false;
                    break;
                }
            }
        }

        if(completed) {
            log.info("Nonogram solved perfectly with snapshot count - {}", nonogram.getSnapshot().size());
        } else {
            log.info("Nonogram solved partially with snapshot count - {}", nonogram.getSnapshot().size());
        }
    }

    private static void initialize(Queue<LineSegment> q, Nonogram nonogram) {
        for(int i = 0; i < nonogram.getBoard().getRow(); i++) {
            q.offer(
                new LineSegment(
                    Direction.ROW,
                    i,
                    0,
                    nonogram.getBoard().getCol(),
                    nonogram.getElement().getRows()[i]
                )
            );
        }
        for(int i = 0; i < nonogram.getBoard().getCol(); i++) {
            q.offer(
                new LineSegment(
                    Direction.COL,
                    i,
                    0,
                    nonogram.getBoard().getRow(),
                    nonogram.getElement().getCols()[i]
                )
            );
        }
    }

    private static void addQueue(Queue<LineSegment> q, Nonogram nonogram, Position p) {
        addRowQueue(q, nonogram, p.row());
        addColQueue(q, nonogram, p.col());
    }

    private static void addRowQueue(Queue<LineSegment> q, Nonogram nonogram, int row) {
        q.offer(
            new LineSegment(
                Direction.ROW,
                row,
                0,
                nonogram.getBoard().getCol(),
                nonogram.getElement().getRows()[row]
            )
        );
    }

    private static void addColQueue(Queue<LineSegment> q, Nonogram nonogram, int col) {
        q.offer(
            new LineSegment(
                Direction.COL,
                col,
                0,
                nonogram.getBoard().getRow(),
                nonogram.getElement().getCols()[col]
            )
        );
    }
}
