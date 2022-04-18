import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tp1.api.service.rest.RestFiles;

public class RestFilesClient extends RestClient implements RestFiles {

    final WebTarget target;

	RestFilesClient( URI serverURI ) {
		super( serverURI );
		target = client.target( serverURI ).path( RestFiles.PATH );
	}

    @Override
    public void writeFile(String fileId, byte[] data, String token) throws IOException {
	
        File file = new File(target.path(fileId).getUri());
		FileOutputStream fStream = new FileOutputStream(file);
        fStream.write(data);

        Response r = target.path(fileId).request()
					.accept(MediaType.APPLICATION_OCTET_STREAM)
					.post(Entity.entity(file, MediaType.APPLICATION_JSON));

		if( r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity() )
            System.out.println("File written in server: " + target.path(fileId).getUri());
        else
            System.out.println("Error, HTTP error status: " + r.getStatus() );
    }

    @Override
    public void deleteFile(String fileId, String token) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public byte[] getFile(String fileId, String token) {
        // TODO Auto-generated method stub
        return null;
    }

}