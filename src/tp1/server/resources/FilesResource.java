package tp1.server.resources;

import tp1.api.service.rest.RestFiles;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class FilesResource implements RestFiles {

    private static final Logger Log = Logger.getLogger(UsersResource.class.getName());

    List<File> files = new ArrayList<>();

    FilesResource() {}

    @Override
    public void writeFile(String fileId, byte[] data, String token) throws IOException {
        Log.info("writeFile: " + fileId);
        URI serverUri = RestFiles.PATH
        File file = new File()
        files.add()
    }

    @Override
    public void deleteFile(String fileId, String token) {

    }

    @Override
    public byte[] getFile(String fileId, String token) {
        return new byte[0];
    }
}
