package returns.nemorithm.constant.fixture;

import returns.nemorithm.constant.fixture.testcase.*;
import returns.nemorithm.domain.nonogram.Element;

public class ElementFixture {
    public static Element fiveByFiveElement(int testcase) {
        return createElement(FiveByFiveTestCase.ROWS[testcase], FiveByFiveTestCase.COLS[testcase]);
    }

    public static Element tenByTenElement(int testcase) {
        return createElement(TenByTenTestCase.ROWS[testcase], TenByTenTestCase.COLS[testcase]);
    }

    public static Element fifteenByFifteenElement(int testcase) {
        return createElement(FifteenByFifteenTestCase.ROWS[testcase], FifteenByFifteenTestCase.COLS[testcase]);
    }

    public static Element twentyByTwentyElement(int testcase) {
        return createElement(TwentyByTwentyTestCase.ROWS[testcase], TwentyByTwentyTestCase.COLS[testcase]);
    }

    public static Element thirtyByThirtyElement(int testcase) {
        return createElement(ThirtyByThirtyTestCase.ROWS[testcase], ThirtyByThirtyTestCase.COLS[testcase] );
    }

    private static Element createElement(int[][] rows, int[][] cols) {
        Element element = new Element(rows.length, cols.length);

        for (int i = 0; i < rows.length; i++) {
            element.initRow(i, rows[i]);
        }

        for (int i = 0; i < cols.length; i++) {
            element.initCol(i, cols[i]);
        }

        return element;
    }
}
