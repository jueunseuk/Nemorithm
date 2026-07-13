package returns.nemorithm.util;

import returns.nemorithm.domain.nonogram.Cell;
import returns.nemorithm.domain.nonogram.Nonogram;

public class Printer {
    public static String makeResult(Nonogram nonogram) {
        StringBuilder sb = new StringBuilder();

        int totalCnt = nonogram.getBoard().getRow() * nonogram.getBoard().getCol();
        int trueCnt = 0;
        int falseCnt = 0;
        int noneCnt = 0;

        Cell[][] cells = nonogram.getBoard().getCells();
        for(int i = 0; i < nonogram.getBoard().getRow(); i++) {
            for(int j = 0; j < nonogram.getBoard().getCol(); j++) {
                switch (cells[i][j]) {
                    case NONE: sb.append("□"); noneCnt++; break;
                    case TRUE: sb.append("■"); trueCnt++; break;
                    case FALSE: sb.append("X"); falseCnt++; break;
                }
            }
            sb.append("\n");
        }

        sb.append("\n");
        sb.append("snapshot counts : ").append(nonogram.getSnapshot().size()-1).append("\n");
        sb.append("total cells : ").append(totalCnt).append("\n");
        sb.append("true cells : ").append(trueCnt).append("\n");
        sb.append("false cells : ").append(falseCnt).append("\n");
        sb.append("none cells : ").append(noneCnt).append("\n");
        sb.append("completion rate : ").append(String.format("%.3f", (double) (trueCnt+falseCnt)/totalCnt*100)).append("%\n");

        return sb.toString().trim();
    }
}
