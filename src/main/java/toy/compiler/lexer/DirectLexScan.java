package toy.compiler.lexer;

import java.util.ArrayList;
import java.util.List;

public class DirectLexScan implements LexScan {


    @Override
    public List<Token> scan(String program) {
        List<Token> result = new ArrayList<>();
        String[] lines = program.split("\r?\n|\r");
        for (String line : lines) {
            result.addAll(scanProgramLine(line));
        }
        return result;
    }

    private List<Token> scanProgramLine(String lineCode) {
        List<Token> result = new ArrayList<>();
        int currentIndex = 0;
        while (currentIndex < lineCode.length()) {
            char currentChar = lineCode.charAt(currentIndex);
            if (Character.isWhitespace(currentChar)) {
                // FIXME manfred 2023/02/08 此处直接进入下轮循环，是否不太对？
                continue;
            }

            if (currentChar == '#') {
                break;
            }

            currentIndex++;
        }
        return result;
    }
}
