package tp1.clients;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import javax.print.attribute.standard.Media;

import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tp1.api.service.rest.RestFiles;

public class RestFilesClient extends RestClient implements RestFiles {

    final WebTarget target;

	public RestFilesClient( URI serverURI ) {
		super( serverURI );
		target = client.target( serverURI ).path( RestFiles.PATH );
	}

    @Override
    public void writeFile(String fileId, byte[] data, String token) throws IOException {

        target.path(fileId).request()
            .accept(MediaType.APPLICATION_JSON)
            .post(Entity.entity(data, MediaType.APPLICATION_OCTET_STREAM));

    }

    @Override
    public void deleteFile(String fileId, String token) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public byte[] getFile(String fileId, String token) {
        
        Response r = target.path(fileId).request()
                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                    .get();

        if( r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity() )
            return r.readEntity(byte[].class);
        else
            System.out.println("Error, HTTP error status: " + r.getStatus() );
        return null;
    }

}