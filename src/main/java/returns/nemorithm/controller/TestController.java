package returns.nemorithm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import returns.nemorithm.constant.fixture.NonogramFixture;
import returns.nemorithm.domain.nonogram.Nonogram;
import returns.nemorithm.solver.Core;
import returns.nemorithm.util.Printer;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestController {
    @GetMapping("/ping")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Nemorithm is live!");
    }

    @PostMapping("/nonogram")
    public ResponseEntity<?> testNonogram(@RequestParam int size, @RequestParam int testcase) {
        Nonogram nonogram = NonogramFixture.create(size, testcase);
        Core.start(nonogram);
        return ResponseEntity.ok(Printer.makeResult(nonogram));
    }
}
