package tp1.server.resources;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.FileInfo;
import tp1.api.User;
import tp1.api.service.rest.RestDirectory;
import tp1.api.service.rest.RestFiles;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
public class DirectoryResource implements RestDirectory {

    private final UsersResource usersResource = new UsersResource();
    private final FilesResource filesResource = new FilesResource();

    private Map<User, List<FileInfo>> userFiles = new HashMap<>();

    private static final Logger Log = Logger.getLogger(DirectoryResource.class.getName());

    public DirectoryResource() {}

    @Override
    public FileInfo writeFile(String filename, byte[] data, String userId, String password) {
        Log.info("Writing " + filename + " of user " + userId + "...");

        User u = usersResource.getUser(userId, password);

        if (!userFiles.containsKey(u))
            userFiles.put(u, new ArrayList<FileInfo>());

        FileInfo fileInfo = new FileInfo();
        fileInfo.setOwner(userId);
        fileInfo.setFilename(filename);
        String fileId = filename + "-" + userId;
        fileInfo.setFileURL(RestFiles.PATH + "/" + fileId);

        List<FileInfo> filesList = userFiles.get(u);

        boolean fileExists = false;
        for(FileInfo fInfo: filesList) {
            if (fInfo.getFilename().equals(filename)) {
                fileInfo = fInfo;
                fileExists = true;
                break;
            }  
        }

        if (!fileExists)
            filesList.add(fileInfo);
        
        try {
            filesResource.writeFile(fileId, data, "token");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
            
        return null;
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
