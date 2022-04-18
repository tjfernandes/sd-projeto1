package tp1.clients;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tp1.api.FileInfo;
import tp1.api.service.rest.RestDirectory;
import tp1.api.service.rest.RestFiles;
import tp1.clients.RestClient;
import tp1.server.Discovery;

import java.net.URI;
import java.util.List;

public class RestDirectoryClient extends RestClient implements RestDirectory {


    final WebTarget target;

    RestDirectoryClient( URI serverURI ) {
        super( serverURI );
        target = client.target( serverURI ).path( RestDirectory.PATH );
    }


    @Override
    public FileInfo writeFile(String filename, byte[] data, String userId, String password) {
        return super.reTry( () -> clt_writeFile(filename, data, userId, password));
    }

    private FileInfo clt_writeFile(String filename, byte[] data, String userId, String password) {

        FileInfo fileInfo = new FileInfo();
        fileInfo.setOwner(userId);
        fileInfo.setFilename(filename);
        String fileId = filename + "-" + userId;
        fileInfo.setFileURL(RestFiles.PATH + "/" + fileId);

        Response r = target.path(userId).path(filename)
                    .queryParam("password", password).request()
                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                    .post(Entity.entity(fileInfo, MediaType.APPLICATION_JSON));

        if( r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity() )
            return r.readEntity(FileInfo.class);
        else
            System.out.println("Error, HTTP error status: " + r.getStatus() );

        return null;
    }

    @Override
    public void deleteFile(String filename, String userId, String password) {

    }

    @Override
    public void shareFile(String filename, String userId, String userIdShare, String password) {

    }

    @Override
    public void unshareFile(String filename, String userId, String userIdShare, String password) {

    }

    @Override
    public byte[] getFile(String filename, String userId, String accUserId, String password) {
        return new byte[0];
    }

    @Override
    public List<FileInfo> lsFile(String userId, String password) {
        return null;
    }
}
