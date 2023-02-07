package toy.compiler.input;


public interface FileHandler {

    void open();

    int close();

    int read(byte[] buf, int begin, int end);

    StringBuffer getSourceCode();
}
