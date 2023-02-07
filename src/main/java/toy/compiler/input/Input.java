package toy.compiler.input;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class Input {
    private final int MAX_LOOK = 16;
    private final int MAX_LEX = 200;
    private final int BUF_SIZE = (MAX_LEX * 3) + (2 * MAX_LOOK);
    private int bufferEndFlag = BUF_SIZE;
    private final int DANGER = bufferEndFlag - MAX_LOOK;
    private final int END = BUF_SIZE;
    private boolean readEof = false;
    private final byte[] inputBuf = new byte[BUF_SIZE];

    private int next = END;
    private int startCurCharPos = END;
    private int endCurCharPos = END;
    private int curCharLineno = 1;

    private FileHandler fileHandler = null;

    private boolean isReadEnd() {
        return (readEof && next >= bufferEndFlag);
    }

    private FileHandler getFileHandler() {
        return new DiskFileHandler();
    }

    public void newFile() {
        this.fileHandler = getFileHandler();
        fileHandler.open();

        readEof = false;
        next = END;
        startCurCharPos = END;
        endCurCharPos = END;
        curCharLineno = 1;
    }

    public String getCurText() {
        int len = endCurCharPos - startCurCharPos;
        byte[] str = Arrays.copyOfRange(inputBuf, startCurCharPos, endCurCharPos + len);
        return new String(str, StandardCharsets.UTF_8);
    }

    public byte inputAdvance() {
        char enter = '\n';

        if (isReadEnd()) {
            return 0;
        }

        if (!readEof && flush(false) < 0) {
            //缓冲区出错
            return -1;
        }

        if (inputBuf[next] == enter) {
            curCharLineno++;
        }
        endCurCharPos++;

        return inputBuf[next++];
    }

    private int flush(boolean force) {
        int noMoreCharToRead = 0;
        int flushOk = 1;

        int shiftPart, copyPart, leftEdge;
        if (isReadEnd()) {
            return noMoreCharToRead;
        }

        if (readEof) {
            return flushOk;
        }

        if (next > DANGER || force) {
            leftEdge = next;
            copyPart = bufferEndFlag - leftEdge;
            System.arraycopy(inputBuf, leftEdge, inputBuf, 0, copyPart);
            if (fillBuf(copyPart) == 0) {
                System.err.println("Internal Error, flush: Buffer full, can't read");
            }

            startCurCharPos -= leftEdge;
            endCurCharPos -= leftEdge;
            next -= leftEdge;
        }
        return flushOk;
    }

    private int fillBuf(int startPos) {
        int need;
        int got;
        need = END - startPos;
        if (need < 0) {
            System.err.println("Internal Error (fill buf): Bad read-request starting addr.");
        }

        if (need == 0) {
            return 0;
        }

        if ((got = fileHandler.read(inputBuf, startPos, need)) == -1) {
            System.err.println("Can't read input file");
        }

        bufferEndFlag = startPos + got;
        if (got < need) {
            //输入流已经到末尾
            readEof = true;
        }

        return got;
    }

    public String getWholeInput() {
        return fileHandler.getSourceCode().toString();
    }

    public static void main(String[] args) {
        Input input = new Input();
        input.newFile();
        for (int i = 0; i < 30; i++) {
            byte b = input.inputAdvance();
            System.out.println(b + "  " + (char) b);
        }
    }

}
