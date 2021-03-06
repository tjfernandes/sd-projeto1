package tp1.server.resources;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.FileInfo;
import tp1.api.User;
import tp1.api.service.rest.RestDirectory;
import tp1.api.service.rest.RestFiles;
import tp1.clients.RestFilesClient;
import tp1.clients.RestUsersClient;
import tp1.server.Discovery;
import tp1.server.RESTDirServer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
public class DirectoryResource implements RestDirectory {

    private static Map<String, List<FileInfo>> userFiles = new HashMap<>();

    private static final Logger Log = Logger.getLogger(RESTDirServer.class.getName());

    public DirectoryResource() {}

    @Override
    public FileInfo writeFile(String filename, byte[] data, String userId, String password) {
        Log.info("Writing " + filename + " of user " + userId + "...");

        User user = null;

        URI[] filesUris = null;
    
        Discovery discovery = Discovery.getInstance();

        String fileId = String.format("%s-%s", filename, userId);

        try {

            var usersUri = discovery.knownUrisOf("users");
            while(usersUri == null)
                usersUri = discovery.knownUrisOf("users");
            
            for( URI uri: usersUri ) {
                if (user == null) {
                    user = ((new RestUsersClient(uri)).getUser(userId, password));
                }
            }
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        URI fileUri = null;

        try {

            filesUris = discovery.knownUrisOf("files");
            while(filesUris == null)
                filesUris = discovery.knownUrisOf("files");

            // posteriormente definir o server de files
            fileUri = filesUris[0];
            RestFilesClient fileClient = new RestFilesClient(fileUri);
            fileClient.writeFile(fileId, data, "token");
            
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        
        if (!userFiles.containsKey(userId))
            userFiles.put(userId, new ArrayList<FileInfo>());
        

        FileInfo fileInfo = new FileInfo();
        fileInfo.setOwner(userId);
        fileInfo.setFilename(filename);
        String path = String.format("%s%s/%s", fileUri, RestFiles.PATH, fileId);
        fileInfo.setFileURL(path);

        List<FileInfo> filesList = userFiles.get(userId);
        boolean fileExists = false;
        for(FileInfo fInfo: filesList) {
            if (fInfo.getFilename().equals(filename)) {
                fInfo = fileInfo;
                fileExists = true;
                break;
            }  
        }

        if (!fileExists) {
            filesList.add(fileInfo);
        }
         
        return fileInfo;
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
        Log.info("Getting file " + filename + " from user " + userId + "...");
        Discovery discovery = Discovery.getInstance();

        User user = null;
        
        try {

            var usersUri = discovery.knownUrisOf("users");
            while(usersUri == null)
                usersUri = discovery.knownUrisOf("users");
            
            for( URI uri: usersUri ) {
                if (user == null) {
                    user = (new RestUsersClient(uri).getUser(accUserId, password));
                }
            }

        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        List<FileInfo> files = userFiles.get(userId);

        String fileURL = "";
        boolean fileExists = false;
        boolean isShared = false;
        Response r = null;
        for (FileInfo fi : files) {
            if (fi.getFilename().equals(filename)) {
                fileExists = true;
                fileURL = fi.getFileURL();
                if (fi.getOwner().equals(accUserId)) {
                    isShared = true;
                    r = Response.temporaryRedirect(URI.create(fileURL)).build();
                }
                else if (fi.getSharedWith() != null) 
                    if (fi.getSharedWith().contains(accUserId) || fi.getOwner().equals(accUserId)) {
                        isShared = true;
                        r = Response.temporaryRedirect(URI.create(fileURL)).build();
                    }
                break;
            }      
        }
        

        if (!fileExists)
            throw new WebApplicationException(Status.NOT_FOUND);
        if (!isShared)
            throw new WebApplicationException(Status.FORBIDDEN);
        else
            throw new WebApplicationException(r);
    }

    @Override
    public List<FileInfo> lsFile(String userId, String password) {
        // TODO
        return null;
    }
}
