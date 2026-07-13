package returns.nemorithm.util;

import returns.nemorithm.domain.nonogram.Cell;
import returns.nemorithm.domain.nonogram.CellGroup;
import returns.nemorithm.domain.nonogram.CellGroupType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineValidator {

    public static boolean isMatched(Cell[] cells, int[] elements) {
        List<Integer> trueGroups = extractTrueGroups(cells);

        if (trueGroups.size() != elements.length) {
            return false;
        }

        for (int i = 0; i < elements.length; i++) {
            if (trueGroups.get(i) != elements[i]) {
                return false;
            }
        }

        return true;
    }

    public static boolean isCompleted(Cell[] cells, int[] elements) {
        if (containsNone(cells)) {
            return false;
        }

        return isMatched(cells, elements);
    }

    private static List<Integer> extractTrueGroups(Cell[] cells) {
        List<Integer> trueGroups = new ArrayList<>();

        int count = 0;
        for (Cell cell : cells) {
            if (cell == Cell.TRUE) {
                count++;
            } else {
                if (count > 0) {
                    trueGroups.add(count);
                    count = 0;
                }
            }
        }

        if (count > 0) {
            trueGroups.add(count);
        }

        return trueGroups;
    }

    public static List<CellGroup> divideLengthByFalse(Cell[] cells) {
        List<CellGroup> groups = new ArrayList<>();

        if (cells.length == 0) {
            return Collections.emptyList();
        }

        int count = 1;
        CellGroupType currentType = toGroupType(cells[0]);

        for (int i = 1; i < cells.length; i++) {
            CellGroupType type = toGroupType(cells[i]);

            if (type == currentType) {
                count++;
            } else {
                groups.add(new CellGroup(currentType, count));

                currentType = type;
                count = 1;
            }
        }

        groups.add(new CellGroup(currentType, count));

        return groups;
    }

    private static CellGroupType toGroupType(Cell cell) {
        if (cell == Cell.FALSE) {
            return CellGroupType.FALSE;
        }

        return CellGroupType.NOT_FALSE;
    }

    private static boolean containsNone(Cell[] cells) {
        return containsNone(cells, 0, cells.length);
    }

    private static boolean containsNone(Cell[] cells, int start, int end) {
        for (int i = start; i < end; i++) {
            if (cells[i] == Cell.NONE) {
                return true;
            }
        }

        return false;
    }

    public static boolean containsFalse(Cell[] cells) {
        return containsFalse(cells, 0, cells.length);
    }

    public static boolean containsFalse(Cell[] cells, int start, int end) {
        for (int i = start; i < end; i++) {
            if (cells[i] == Cell.FALSE) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsTrue(Cell[] cells) {
        return containsFalse(cells, 0, cells.length);
    }

    public static boolean containsTrue(Cell[] cells, int start, int end) {
        for (int i = start; i < end; i++) {
            if (cells[i] == Cell.TRUE) {
                return true;
            }
        }
        return false;
    }

    public static boolean allNone(Cell[] cells) {
        return allNone(cells, 0, cells.length);
    }

    public static boolean allNone(Cell[] cells, int start, int end) {
        for (int i = start; i < end; i++) {
            if (cells[i] != Cell.NONE) {
                return false;
            }
        }
        return true;
    }

    public static boolean allTrue(Cell[] cells) {
        return allTrue(cells, 0, cells.length);
    }

    public static boolean allTrue(Cell[] cells, int start, int end) {
        for (int i = start; i < end; i++) {
            if (cells[i] != Cell.TRUE) {
                return false;
            }
        }
        return true;
    }
}