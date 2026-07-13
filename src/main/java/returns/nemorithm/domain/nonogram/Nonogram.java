package returns.nemorithm.domain.nonogram;

import lombok.Getter;
import returns.nemorithm.util.Printer;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Nonogram {
    private final Board board;
    private final Element element;
    private final List<Board> snapshot = new ArrayList<>();

    public Nonogram(Board board, Element element) {
        this.board = board;
        this.element = element;
        saveSnapshot();
    }

    public void saveSnapshot() {
        this.snapshot.add(board.copy());
        System.out.println("======= Current count is: " + (this.snapshot.size()-1) + " =======");
        System.out.println(Printer.makeResult(this));
        System.out.println();
    }
}
