package returns.nemorithm.solver;

import returns.nemorithm.domain.nonogram.*;
import returns.nemorithm.util.LineValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FindFalse {
    public static List<Position> find(Board board, LineSegment lineSegment) {
        List<Position> changedPositions = new ArrayList<>();

        changedPositions.addAll(findNPlus2(board, lineSegment));
        changedPositions.addAll(findNPlus1(board, lineSegment));
        changedPositions.addAll(findLeadingCell(board, lineSegment));
        changedPositions.addAll(findSameElementLength(board, lineSegment));
        changedPositions.addAll(findFinalization(board, lineSegment));

        return changedPositions;
    }

    /**
     * N + 2
     * 첫번째 요소의 크기가 n일 때 n+2부터 n만큼 색칠되어 있으면 n+1 칸은 항상 X
     * -> 시작 또는 끝쪽이 전부 비어있다는 전제 조건 필요
     * -> LineSegment의 길이는 꼭 전체일 필요없음
     * -> elements의 요소들은 반드시 하나 이상
     */
    public static List<Position> findNPlus2(Board board, LineSegment lineSegment) {
        List<Position> changedPositions = new ArrayList<>();

        int length = lineSegment.length();
        int[] elements = lineSegment.elements();
        if (elements.length == 0) {
            return Collections.emptyList();
        }

        Cell[] cells = board.getLineSegment(lineSegment.direction(), lineSegment.idx(), lineSegment.start(), lineSegment.end());

        // find front
        int n = elements[0];
        if (length >= n * 2 + 1
                && LineValidator.allNone(cells, 0, n + 1)
                && LineValidator.allTrue(cells, n + 1, n * 2 + 1)) {
            Position position = lineSegment.convert(n);

            if (board.mark(position, Cell.FALSE, "findNPlus2", lineSegment)) {
                changedPositions.add(position);
            }
        }

        // find rear
        n = elements[elements.length - 1];
        if (length >= n * 2 + 1
                && LineValidator.allNone(cells, length - n - 1, length)
                && LineValidator.allTrue(cells, length - n * 2 - 1, length - n - 1)) {

            Position position = lineSegment.convert(length - n - 1);

            if (board.mark(position, Cell.FALSE, "findNPlus2", lineSegment)) {
                changedPositions.add(position);
            }
        }

        return changedPositions;
    }

    /**
     * N + 1
     * 첫번째 요소의 크기가 n일 때 처음부터 n 사이에 확정 O 시작점과 길이가 n이면 처음부터 +1 위치까지는 전부 X
     * -> 시작 또는 끝쪽이 전부 비어있다는 전제 조건 필요
     * -> LineSegment의 길이는 꼭 전체일 필요없음
     * -> elements의 요소들은 반드시 하나 이상
     */
    public static List<Position> findNPlus1(Board board, LineSegment lineSegment) {
        List<Position> changedPositions = new ArrayList<>();

        int length = lineSegment.length();
        int[] elements = lineSegment.elements();
        if (elements.length == 0) {
            return Collections.emptyList();
        }

        Cell[] cells = board.getLineSegment(lineSegment.direction(), lineSegment.idx(), lineSegment.start(), lineSegment.end());

        // find front
        int n = elements[0];
        int frontEnd = Math.min(n + 1, length);

        if (LineValidator.containsTrue(cells, 0, frontEnd)) {
            int trueStart = -1;
            int trueEnd = -1;

            for (int i = 0; i < frontEnd; i++) {
                if (cells[i] != Cell.TRUE) {
                    continue;
                }
                trueStart = i;
                while (i < length && cells[i] == Cell.TRUE) {
                    i++;
                }
                trueEnd = i;
                break;
            }

            if (trueStart != -1) {
                int trueLength = trueEnd - trueStart;

                if (trueStart <= n && trueLength == n) {
                    int left = trueStart - 1;

                    if (left >= 0) {
                        Position position = lineSegment.convert(left);

                        if (board.mark(position, Cell.FALSE, "findNPlus1-front-left", lineSegment)) {
                            changedPositions.add(position);
                        }
                    }

                    if (trueEnd < length) {
                        Position position = lineSegment.convert(trueEnd);

                        if (board.mark(position, Cell.FALSE, "findNPlus1-front-right", lineSegment)) {
                            changedPositions.add(position);
                        }
                    }
                }
            }
        }

        // find rear
        n = elements[elements.length - 1];
        int rearStart = Math.max(length - n - 1, 0);
        if (LineValidator.containsTrue(cells, rearStart, length)) {
            int cnt = 0;
            int leftIndex = -1;

            for (int i = length - 1; i >= rearStart; i--) {
                if (cells[i] == Cell.TRUE) {
                    cnt++;
                } else if (cnt > 0) {
                    leftIndex = i;
                    break;
                }
            }

            if (cnt == n && leftIndex != -1) {
                for (int i = leftIndex; i < length; i++) {
                    Position position = lineSegment.convert(i);

                    if (board.mark(position, Cell.FALSE, "findNPlus1", lineSegment)) {
                        changedPositions.add(position);
                    }
                }
            }
        }

        return changedPositions;
    }

    /**
     * X 끌어오기
     * 첫 번째/마지막 element가 포함해야 하는 TRUE 그룹이 있을 때,
     * 그 element가 도달할 수 없는 바깥쪽 칸을 FALSE로 확정한다.
     */
    public static List<Position> findLeadingCell(Board board, LineSegment lineSegment) {
        List<Position> changedPositions = new ArrayList<>();

        int length = lineSegment.length();
        int[] elements = lineSegment.elements();

        if (elements.length == 0) {
            return changedPositions;
        }

        Cell[] cells = board.getLineSegment(
                lineSegment.direction(),
                lineSegment.idx(),
                lineSegment.start(),
                lineSegment.end()
        );

        // find front
        int n = elements[0];
        int frontLimit = Math.min(n + 1, length);

        int trueStart = -1;
        int trueEnd = -1;

        for (int i = 0; i < frontLimit; i++) {
            if (cells[i] != Cell.TRUE) {
                continue;
            }

            trueStart = i;

            while (i < length && cells[i] == Cell.TRUE) {
                i++;
            }

            trueEnd = i; // exclusive
            break;
        }

        if (trueStart != -1) {
            int trueLength = trueEnd - trueStart;

            if (trueLength < n) {
                int falseEnd = trueEnd - n;

                for (int i = 0; i < falseEnd; i++) {
                    Position position = lineSegment.convert(i);

                    if (board.mark(position, Cell.FALSE, "findLeadingCell-front", lineSegment)) {
                        changedPositions.add(position);
                    }
                }
            }
        }

        // find rear
        n = elements[elements.length - 1];
        int rearLimit = Math.max(length - n - 1, 0);

        trueStart = -1;
        trueEnd = -1;

        for (int i = length - 1; i >= rearLimit; i--) {
            if (cells[i] != Cell.TRUE) {
                continue;
            }

            trueEnd = i + 1;

            while (i >= 0 && cells[i] == Cell.TRUE) {
                i--;
            }

            trueStart = i + 1;
            break;
        }

        if (trueStart != -1) {
            int trueLength = trueEnd - trueStart;

            if (trueLength < n) {
                int falseStart = trueStart + n;

                for (int i = falseStart; i < length; i++) {
                    Position position = lineSegment.convert(i);

                    if (board.mark(position, Cell.FALSE, "findLeadingCell-rear", lineSegment)) {
                        changedPositions.add(position);
                    }
                }
            }
        }

        return changedPositions;
    }

    /**
     *
     */
    public static List<Position> findElementCount(Board board, LineSegment lineSegment) {
        return null;
    }

    /**
     * 동일한 길이 확정
     * 요소의 길이가 모두 n으로 동일한 경우 n 양 옆에 X 가능
     * -> 반드시 전체 길이의 LineSegment만 가능
     */
    public static List<Position> findSameElementLength(Board board, LineSegment lineSegment) {
        List<Position> changedPositions = new ArrayList<>();

        int length = lineSegment.length();
        int[] elements = lineSegment.elements();
        if (elements.length == 0) {
            return Collections.emptyList();
        }

        if(lineSegment.direction() == Direction.ROW) {
            if(lineSegment.end() - lineSegment.start() != board.getCol()) {
                return Collections.emptyList();
            }
        } else {
            if(lineSegment.end() - lineSegment.start() != board.getRow()) {
                return Collections.emptyList();
            }
        }

        Cell[] cells = board.getLineSegment(
                lineSegment.direction(),
                lineSegment.idx(),
                lineSegment.start(),
                lineSegment.end()
        );

        int n = elements[0];
        for(int element: elements) {
            if(element != n) {
                return Collections.emptyList();
            }
        }

        int i = 0;

        while (i < length) {
            if (cells[i] != Cell.TRUE) {
                i++;
                continue;
            }

            int trueStart = i;

            while (i < length && cells[i] == Cell.TRUE) {
                i++;
            }

            int trueEnd = i;
            int trueLength = trueEnd - trueStart;

            if (trueLength != n) {
                continue;
            }

            // left
            if (trueStart - 1 >= 0 && cells[trueStart - 1] == Cell.NONE) {
                Position position = lineSegment.convert(trueStart - 1);

                if (board.mark(position, Cell.FALSE, "findSameElementLength", lineSegment)) {
                    changedPositions.add(position);
                }
            }

            // right
            if (trueEnd < length && cells[trueEnd] == Cell.NONE) {
                Position position = lineSegment.convert(trueEnd);

                if (board.mark(position, Cell.FALSE, "findSameElementLength", lineSegment)) {
                    changedPositions.add(position);
                }
            }
        }

        return changedPositions;
    }

    /**
     *
     */
    public static List<Position> findElementInterval(Board board, LineSegment lineSegment) {
        return null;
    }

    /**
     * 마무리 유형
     * 1. 필요한 TRUE 개수를 이미 모두 채운 경우
     *    -> 나머지는 FALSE
     * 2. TRUE + NONE 개수가 필요한 TRUE 개수와 같은 경우
     *    -> 남은 NONE은 TRUE
     * 3. TRUE 그룹이 이미 elements와 매칭되는 경우
     *    -> 나머지는 FALSE
     */
    public static List<Position> findFinalization(Board board, LineSegment lineSegment) {
        List<Position> changedPositions = new ArrayList<>();

        int[] elements = lineSegment.elements();

        Cell[] cells = board.getLineSegment(
                lineSegment.direction(),
                lineSegment.idx(),
                lineSegment.start(),
                lineSegment.end()
        );

        int requiredTrueCount = 0;
        for (int element : elements) {
            requiredTrueCount += element;
        }

        int trueCount = 0;
        int noneCount = 0;

        for (Cell cell : cells) {
            if (cell == Cell.TRUE) {
                trueCount++;
            } else if (cell == Cell.NONE) {
                noneCount++;
            }
        }

        // 필요한 TRUE를 이미 다 채움 -> 나머지는 FALSE
        if (trueCount == requiredTrueCount) {
            markAll(board, lineSegment, cells.length, Cell.FALSE, changedPositions);
            return changedPositions;
        }

        // 남은 NONE을 전부 TRUE로 해야 필요한 TRUE 개수를 채움
        if (trueCount + noneCount == requiredTrueCount) {
            markAll(board, lineSegment, cells.length, Cell.TRUE, changedPositions);
            return changedPositions;
        }

        // TRUE 그룹이 이미 elements와 매칭됨 -> 나머지는 FALSE
        if (LineValidator.isMatched(cells, elements)) {
            markAll(board, lineSegment, cells.length, Cell.FALSE, changedPositions);
            return changedPositions;
        }

        return changedPositions;
    }

    private static void markAll(
            Board board,
            LineSegment lineSegment,
            int length,
            Cell status,
            List<Position> changedPositions
    ) {
        for (int i = 0; i < length; i++) {
            Position position = lineSegment.convert(i);

            if (board.mark(position, status, "findFinalization", lineSegment)) {
                changedPositions.add(position);
            }
        }
    }
}
