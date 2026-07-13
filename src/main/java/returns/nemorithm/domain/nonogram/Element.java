package returns.nemorithm.domain.nonogram;

import lombok.Getter;

@Getter
public class Element {
    private final int[][] rows;
    private final int[][] cols;

    public Element(int row, int col) {
        this.rows = new int[row][];
        this.cols = new int[col][];
    }

    public void initRow(int idx, int[] element) {
        this.rows[idx] = element;
    }

    public void initCol(int idx, int[] element) {
        this.cols[idx] = element;
    }
}
