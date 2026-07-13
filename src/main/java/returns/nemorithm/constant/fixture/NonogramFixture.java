package returns.nemorithm.constant.fixture;

import returns.nemorithm.domain.nonogram.Board;
import returns.nemorithm.domain.nonogram.Element;
import returns.nemorithm.domain.nonogram.Nonogram;

public class NonogramFixture {
    public static Nonogram create(int size, int testcase) {
        return switch (size) {
            case 5 -> fiveByFive(testcase);
            case 10 -> tenByTen(testcase);
            case 15 -> fifteenByFifteen(testcase);
            case 20 -> twentyByTwenty(testcase);
            case 30 -> thirtyByThirty(testcase);
            default -> throw new IllegalStateException("size is only up to 30 * 30 in 5 units");
        };
    }

    public static Nonogram fiveByFive(int testcase) {
        Board board = BoardFixture.empty(5, 5);
        Element element = ElementFixture.fiveByFiveElement(testcase);

        return new Nonogram(board, element);
    }

    public static Nonogram tenByTen(int testcase) {
        Board board = BoardFixture.empty(10, 10);
        Element element = ElementFixture.tenByTenElement(testcase);

        return new Nonogram(board, element);
    }

    public static Nonogram fifteenByFifteen(int testcase) {
        Board board = BoardFixture.empty(15, 15);
        Element element = ElementFixture.fifteenByFifteenElement(testcase);

        return new Nonogram(board, element);
    }

    public static Nonogram twentyByTwenty(int testcase) {
        Board board = BoardFixture.empty(20, 20);
        Element element = ElementFixture.twentyByTwentyElement(testcase);

        return new Nonogram(board, element);
    }

    public static Nonogram thirtyByThirty(int testcase) {
        Board board = BoardFixture.empty(30, 30);
        Element element = ElementFixture.thirtyByThirtyElement(testcase);

        return new Nonogram(board, element);
    }
}
