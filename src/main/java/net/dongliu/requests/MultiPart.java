package net.dongliu.requests;

import java.io.File;

/**
 * @author Dong Liu dongliu@live.cn
 */
public class MultiPart {
    private String fileName;
    private File file;
    private String meta;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }
}
