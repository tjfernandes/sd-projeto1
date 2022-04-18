package tp1.clients;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tp1.api.FileInfo;
import tp1.api.User;
import tp1.api.service.rest.RestFiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

public class RestFilesClient extends RestClient implements RestFiles {

    final WebTarget target;
    URI serverURI;

    RestFilesClient(URI serverURI) {
        super(serverURI);
        this.serverURI = serverURI;
        target = client.target(serverURI).path(RestFiles.PATH);
    }

    @Override
    public void writeFile(String fileId, byte[] data, String token) throws IOException {
        File file = new File(serverURI);
        FileOutputStream fileStream = new FileOutputStream(file);
        fileStream.write(data);

        Response r = target.path(fileId).request().accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(File.class, MediaType.APPLICATION_JSON));

        if( r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity() )
            System.out.println(r.readEntity(File.class));
        else
            System.out.println("Error, HTTP error status: " + r.getStatus() );
    }

    @Override
    public void deleteFile(String fileId, String token) {

    }

    @Override
    public byte[] getFile(String fileId, String token) {
        return new byte[0];
    }
}
