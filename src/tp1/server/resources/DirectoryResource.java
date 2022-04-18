package tp1.server.resources;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import tp1.api.FileInfo;
import tp1.api.User;
import tp1.api.service.rest.RestDirectory;
import tp1.server.Discovery;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
public class DirectoryResource implements RestDirectory {

    private final FilesResource filesResource = new FilesResource();

    private final UsersResource usersResource = new UsersResource();

    private Map<String, List<FileInfo>> userFiles = new HashMap<>();

    private static final Logger Log = Logger.getLogger(DirectoryResource.class.getName());

    public DirectoryResource() {}

    @Override
    public FileInfo writeFile(String filename, byte[] data, String userId, String password) {

        User u = usersResource.getUser(userId, password);

        filesResource

        FileInfo fileInfo = new FileInfo(userId, filename, )


    }

    @Override
    public void deleteFile(String filename, String userId, String password) {
        // TODO
    }

    @Override
    public void shareFile(String filename, String userId, String userIdShare, String password) {
        // TODO
    }

    @Override
    public void unshareFile(String filename, String userId, String userIdShare, String password) {
        // TODO
    }

    @Override
    public byte[] getFile(String filename, String userId, String accUserId, String password) {
        // TODO
        return new byte[0];
    }

    @Override
    public List<FileInfo> lsFile(String userId, String password) {
        // TODO
        return null;
    }
}
