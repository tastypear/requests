package net.dongliu.requests.converter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;

import java.io.*;

/**
 * save http response to file
 *
 * @author Dong Liu
 */
public class FileResponseConverter implements ResponseConverter {
    private final File file;

    /**
     * save http response to file
     *
     * @param filePath the file path to write to
     */
    public FileResponseConverter(String filePath) {
        this.file = new File(filePath);
    }

    /**
     * save http response to file
     *
     * @param file the file to write to
     */
    public FileResponseConverter(File file) {
        this.file = file;
    }

    /**
     * copy data into file output stream
     *
     * @param httpEntity the http response entity
     * @return true if success
     */
    @Override
    public Boolean convert(HttpEntity httpEntity) throws IOException {
        try (InputStream in = httpEntity.getContent()) {
            try (OutputStream out = new FileOutputStream(this.file)) {
                IOUtils.copy(in, out);
            }
        }
        return true;
    }
}
