package net.dongliu.requests.struct;

import java.io.File;
import java.net.URLConnection;

/**
 * @author Dong Liu dongliu@live.cn
 */
public class MultiPart {
    // the filed name name
    private String name;
    // the file for multi part upload
    private File file;
    // the file content type
    private String mime;

    /**
     * get multipart from file path
     */
    public MultiPart(String fieldName, String path) {
        this.file = new File(path);
        this.name = fieldName;
        this.mime = URLConnection.guessContentTypeFromName(file.getName());
    }

    /**
     * get multipart from file path
     */
    public MultiPart(String fieldName, String path, String mime) {
        this.file = new File(path);
        this.name = fieldName;
        this.mime = mime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getFileName() {
        return this.file.getName();
    }
}
