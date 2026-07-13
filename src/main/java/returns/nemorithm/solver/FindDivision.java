package returns.nemorithm.solver;

import lombok.extern.slf4j.Slf4j;
import returns.nemorithm.domain.nonogram.*;
import returns.nemorithm.util.LineValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class FindDivision{
    public static List<LineSegment> find(Board board, LineSegment lineSegment) {
        List<LineSegment> newLineSegments = new ArrayList<>();

        newLineSegments.addAll(findRemainder(board, lineSegment));

        return newLineSegments;
    }

    /**
     * 나머지 분리
     * 이미 끝난 끝부분들을 최대한 잘라내서 나머지 진행
     * -> 라인의 한 부분을 추출해서 다시 큐에 넣음
     */
    public static List<LineSegment> findRemainder(Board board, LineSegment lineSegment) {
        List<LineSegment> newLineSegments = new ArrayList<>();

        int length = lineSegment.length();
        int[] elements = lineSegment.elements();

        if (elements.length == 0) {
            return newLineSegments;
        }

        Cell[] cells = board.getLineSegment(
                lineSegment.direction(),
                lineSegment.idx(),
                lineSegment.start(),
                lineSegment.end()
        );

        int start = 0;
        int end = length;

        int elementStart = 0;
        int elementEnd = elements.length;

        boolean changed = true;

        while (changed) {
            changed = false;

            // 앞쪽에 이미 확정된 X가 있으면 제거
            while (start < end && cells[start] == Cell.FALSE) {
                start++;
                changed = true;
            }

            // 뒤쪽에 이미 확정된 X가 있으면 제거
            while (start < end && cells[end - 1] == Cell.FALSE) {
                end--;
                changed = true;
            }

            // front completed element 제거
            while (elementStart < elementEnd) {
                int n = elements[elementStart];

                if (end - start < n + 1) {
                    break;
                }

                boolean completedFront =
                        LineValidator.allTrue(cells, start, start + n)
                                && cells[start + n] == Cell.FALSE;

                if (!completedFront) {
                    break;
                }

                start += n + 1;
                elementStart++;
                changed = true;

                // OOOX 제거 후 바로 다음이 X면 다시 제거할 수 있게 위 while로 돌아감
                break;
            }

            if (changed) {
                continue;
            }

            // rear completed element 제거
            while (elementStart < elementEnd) {
                int n = elements[elementEnd - 1];

                if (end - start < n + 1) {
                    break;
                }

                boolean completedRear =
                        cells[end - n - 1] == Cell.FALSE
                                && LineValidator.allTrue(cells, end - n, end);

                if (!completedRear) {
                    break;
                }

                end -= n + 1;
                elementEnd--;
                changed = true;

                break;
            }
        }

        if (elementStart >= elementEnd) {
            return newLineSegments;
        }

        if (start >= end) {
            return newLineSegments;
        }

        int[] newElements = Arrays.copyOfRange(elements, elementStart, elementEnd);

        LineSegment newLineSegment = new LineSegment(
                lineSegment.direction(),
                lineSegment.idx(),
                lineSegment.start() + start,
                lineSegment.start() + end,
                newElements
        );

        if (!isSameSegment(lineSegment, newLineSegment)) {
            newLineSegments.add(newLineSegment);
        }

        return newLineSegments;
    }

    private static boolean isSameSegment(LineSegment a, LineSegment b) {
        return a.direction() == b.direction()
                && a.idx() == b.idx()
                && a.start() == b.start()
                && a.end() == b.end()
                && Arrays.equals(a.elements(), b.elements());
    }

    /**
     * 앞쪽 TRUE 덩어리가 첫 번째 element 소속으로 확정됐지만 아직 완성되지는 않은 경우 남은 부분을 나머지 원소들에게 배정 가능
     * -> 첫 번째 element가 침범할 수 없는 안전 구간에 대해 나머지 elements의 basic intersection을 적용
     * -> LineSegment의 길이는 꼭 전체일 필요없음
     * -> elements의 요소들은 반드시 둘 이상
     */
    public static List<Position> findBasicIntersectionAfterConfirmedFrontOwner(
            Board board,
            LineSegment lineSegment
    ) {
        List<Position> changedPositions = new ArrayList<>();

        int[] elements = lineSegment.elements();

        if (elements.length <= 1) {
            return changedPositions;
        }

        int length = lineSegment.length();
        int firstElement = elements[0];

        Cell[] cells = board.getLineSegment(
            lineSegment.direction(),
            lineSegment.idx(),
            lineSegment.start(),
            lineSegment.end()
        );

        int trueStart = findFirstTrueGroupStart(cells);
        int trueEnd = findFirstTrueGroupEnd(cells, trueStart);

        if (trueStart == -1) {
            return changedPositions;
        }

        int trueLength = trueEnd - trueStart;

        if (trueLength > firstElement) {
            return changedPositions;
        }

        if (trueStart > firstElement) {
            return changedPositions;
        }

        int safeStartOffset = trueStart  + 1;

        if (safeStartOffset >= length) {
            return changedPositions;
        }

        int[] remainingElements = Arrays.copyOfRange(elements, 1, elements.length);

        if (lineSegment.length() - safeStartOffset < requiredLength(remainingElements)) {
            return changedPositions;
        }

        LineSegment safeSegment = new LineSegment(
            lineSegment.direction(),
            lineSegment.idx(),
            lineSegment.start() + safeStartOffset,
            lineSegment.end(),
            remainingElements
        );

        changedPositions.addAll(
            FindTrue.findBasicIntersectionLength(board, safeSegment)
        );

        return changedPositions;
    }

    private static int findFirstTrueGroupStart(Cell[] cells) {
        for (int i = 0; i < cells.length; i++) {
            if (cells[i] == Cell.TRUE) {
                return i;
            }
        }

        return -1;
    }

    private static int findFirstTrueGroupEnd(Cell[] cells, int start) {
        if (start == -1) {
            return -1;
        }

        int i = start;

        while (i < cells.length && cells[i] == Cell.TRUE) {
            i++;
        }

        return i;
    }

    private static int requiredLength(int[] elements) {
        if (elements.length == 0) {
            return 0;
        }

        int sum = 0;

        for (int element : elements) {
            sum += element;
        }

        return sum + elements.length - 1;
    }
}
