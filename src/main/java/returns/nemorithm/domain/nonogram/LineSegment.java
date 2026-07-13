package returns.nemorithm.domain.nonogram;

import java.util.Arrays;

/**
 * @param end not contains
 */
public record LineSegment(Direction direction, int idx, int start, int end, int[] elements) {
    public int length() {
        return end - start;
    }

    public Position convert(int offset) {
        if (direction == Direction.ROW) {
            return new Position(idx, start + offset);
        } else {
            return new Position(start + offset, idx);
        }
    }

    @Override
    public String toString() {
        return "LineSegment{" +
                "direction=" + direction +
                ", idx=" + idx +
                ", start=" + start +
                ", end=" + end +
                ", elements=" + Arrays.toString(elements) +
                '}';
    }
}
