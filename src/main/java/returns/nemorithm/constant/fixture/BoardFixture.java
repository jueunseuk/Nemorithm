package returns.nemorithm.constant.fixture;

import returns.nemorithm.domain.nonogram.Board;

public class BoardFixture {
    public static Board empty(int row, int col) {
        return new Board(row, col);
    }
}
