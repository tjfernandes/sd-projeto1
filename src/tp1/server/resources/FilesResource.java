package tp1.server.resources;

import tp1.api.service.rest.RestFiles;
import tp1.server.Discovery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FilesResource implements RestFiles {

    private static final Logger Log = Logger.getLogger(FilesResource.class.getName());

    Map<String, File> files = new HashMap<String, File>();

    FilesResource() {}

    @Override
    public void writeFile(String fileId, byte[] data, String token) throws IOException {
        Log.info("writeFile: " + fileId);

        Discovery discovery = new Discovery();
        
        try {
            URI[] serversUri = discovery.knownUrisOf("files");

            while (serversUri == null)
                serversUri = discovery.knownUrisOf("files");
        
            URI serverUri = serversUri[0];


            for(URI uri : serversUri) {
                String[] segments = uri.getPath().split("/");
                String fId = segments[segments.length - 1];
                if(fileId.equals(fId))
                    serverUri = uri;
            }
            
            File file = new File(serverUri);
            FileOutputStream fileStream = new FileOutputStream(file);
            fileStream.write(data);
            
            files.put(fileId, file);
    
            
            
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // serversUri + /fileId
        // nhtoo://ds89123679:8080/rest/files
    }    

    @Override
    public void deleteFile(String fileId, String token) {

    }

    @Override
    public byte[] getFile(String fileId, String token) {
        return new byte[0];
    }
}
