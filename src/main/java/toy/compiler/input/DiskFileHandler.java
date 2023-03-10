package toy.compiler.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;

public class DiskFileHandler implements FileHandler {
    private StringBuffer sourceCode = new StringBuffer();
    private int curPos = 0;

    private String pathname;

    public DiskFileHandler(String pathname) {
        this.pathname = pathname;
    }

    public DiskFileHandler() {
        this.pathname = "testInput.c";
    }

    @Override
    public void open() {
        File file = new File(pathname);
        try {
            String encoding = "UTF-8";
            InputStreamReader read = new InputStreamReader(
                    Files.newInputStream(file.toPath()), encoding);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;

            while ((lineTxt = bufferedReader.readLine()) != null) {
                sourceCode.append(lineTxt);
                sourceCode.append('\n');
            }
            bufferedReader.close();
            read.close();
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }

    @Override
    public int read(byte[] buf, int begin, int len) {
        if (curPos >= sourceCode.length()) {
            return 0;
        }

        int readCount = 0;
        byte[] sourceBuf = sourceCode.toString().getBytes();
        while ((curPos + readCount < sourceCode.length()) && readCount < len) {
            buf[begin + readCount] = sourceBuf[curPos + readCount];
            readCount++;
        }
        curPos += readCount;

        return readCount;
    }

    @Override
    public int close() {
        return 0;
    }

    @Override
    public StringBuffer getSourceCode() {
        return sourceCode;
    }

    public static void main(String[] args) {
        DiskFileHandler d = new DiskFileHandler("testInput.c");
        d.open();
        System.out.println(d.sourceCode);
        byte bs[] = new byte[20];
        d.read(bs, 3, 13);
        for (int i = 0; i < bs.length; i++) {
            System.out.println(bs[i] + " " + (char) bs[i]);
        }
    }

}
