package toy.compiler.lexer;

import java.util.List;

public interface LexScan {
    List<Token> scan(String program);
}
