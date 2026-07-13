package returns.nemorithm.solver;

import returns.nemorithm.domain.nonogram.*;
import returns.nemorithm.util.LineValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FindTrue {

    public static List<Position> find(Board board, LineSegment lineSegment) {
        List<Position> changedPositions = new ArrayList<>();

        changedPositions.addAll(findBasicIntersectionLength(board, lineSegment));
        changedPositions.addAll(findElementExpansion(board, lineSegment));
        changedPositions.addAll(findMinimumElementExpansion(board, lineSegment));

        return changedPositions;
    }

    /**
     * 기본 교차 판정
     * 기본적인 교차 확정 칸을 찾는 메서드
     * -> LineSegment 내부에 False 칸이 존재하면 안 됨
     * -> LineSegment의 길이는 꼭 전체일 필요없음
     * -> elements의 요소들은 반드시 하나 이상
     */
    public static List<Position> findBasicIntersectionLength(Board board, LineSegment lineSegment) {
        List<Position> changedPositions = new ArrayList<>();

        Cell[] cells = board.getLineSegment(lineSegment.direction(), lineSegment.idx(), lineSegment.start(), lineSegment.end());
        if (LineValidator.containsFalse(cells)) {
            return Collections.emptyList();
        }

        int length = lineSegment.length();
        int[] elements = lineSegment.elements();
        if (elements.length == 0) {
            return Collections.emptyList();
        }

        int sumWithoutGap = 0;
        for (int element : elements) {
            sumWithoutGap += element;
        }
        int sumWithGap = sumWithoutGap + elements.length - 1;

        if (sumWithGap > length) {
            return Collections.emptyList();
        }

        int offset = 0;
        int exclusionLength  = length - sumWithGap;
        for (int element : elements) {
            if (element > exclusionLength ) {
                int start = offset + exclusionLength;
                int end = offset + element;

                for (int i = start; i < end; i++) {
                    Position position = lineSegment.convert(i);
                    if (board.mark(position, Cell.TRUE, "findBasicIntersectionLength", lineSegment)) {
                        changedPositions.add(position);
                    }
                }
            }

            offset += element + 1;
        }

        return changedPositions;
    }

    /**
     * 요소 확장
     * 양끝에서부터 요소 길이만큼 확장 가능
     * -> 끝에 가능한 요소가 있는지 확인해야 함
     * -> LineSegment의 길이는 꼭 전체일 필요없음
     * -> elements의 요소들은 반드시 하나 이상
     */
    public static List<Position> findElementExpansion(Board board, LineSegment lineSegment) {
        List<Position> positions = new ArrayList<>();

        int length = lineSegment.length();
        int[] elements = lineSegment.elements();
        if (elements.length == 0) {
            return Collections.emptyList();
        }

        Cell[] cells = board.getLineSegment(lineSegment.direction(), lineSegment.idx(), lineSegment.start(), lineSegment.end());

        // find front
        int n = elements[0];
        int end = Math.min(n, length);
        if (LineValidator.containsTrue(cells, 0, end)) {
            boolean meet = false;

            for (int i = 0; i < end; i++) {
                if (cells[i] == Cell.TRUE) {
                    meet = true;
                    continue;
                }

                if (meet) {
                    Position position = lineSegment.convert(i);

                    if (board.mark(position, Cell.TRUE, "findElementExpansion", lineSegment)) {
                        positions.add(position);
                    }
                }
            }
        }

        // find rear
        n = elements[elements.length - 1];
        if (length >= n && LineValidator.containsTrue(cells, length - n, length)) {
            boolean meet = false;

            for (int i = length - 1; i >= length - n; i--) {
                if (cells[i] == Cell.TRUE) {
                    meet = true;
                    continue;
                }

                if (meet && cells[i] == Cell.NONE) {
                    Position position = lineSegment.convert(i);

                    if (board.mark(position, Cell.TRUE, "findElementExpansion", lineSegment)) {
                        positions.add(position);
                    }
                }
            }
        }

        return positions;
    }

    /**
     * 최소 요소 확장
     * 전체 요소들 중에 최소값을 알면 element expansion을 적용할 수 있음
     * -> LineSegment의 길이는 꼭 전체일 필요없음
     * -> elements의 요소들은 반드시 하나 이상
     */
    public static List<Position> findMinimumElementExpansion(Board board, LineSegment lineSegment) {
        List<Position> positions = new ArrayList<>();

        int length = lineSegment.length();
        int[] elements = lineSegment.elements();
        if (elements.length == 0) {
            return Collections.emptyList();
        }

        Cell[] cells = board.getLineSegment(lineSegment.direction(), lineSegment.idx(), lineSegment.start(), lineSegment.end());

        int minimumElementLength = elements[0];
        for(int e : elements) {
            if(e < minimumElementLength) {
                minimumElementLength = e;
            }
        }



        return positions;
    }
}