package tp1.server.resources;

import tp1.api.service.rest.RestFiles;
import tp1.server.Discovery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

@Singleton
public class FilesResource implements RestFiles {

    private static final Logger Log = Logger.getLogger(FilesResource.class.getName());

    Map<String, File> files = new HashMap<String, File>();

    FilesResource() {}

    @Override
    public void writeFile(String fileId, byte[] data, String token) throws IOException {
        Log.info("writeFile: " + fileId);

        File file = new File(fileId);
        FileOutputStream fO = new FileOutputStream(file);

        fO.write(data);
        fO.close();

        files.put(fileId, file);
    }    

    @Override
    public void deleteFile(String fileId, String token) {

    }

    @Override
    public byte[] getFile(String fileId, String token) {
        Log.info("getFile: " + fileId);


        File file = files.get(fileId);

        if (file == null)
            throw new WebApplicationException(Status.NOT_FOUND);

        FileInputStream fI;
        byte[] data = new byte[(int)file.length()];;
        try {
            fI = new FileInputStream(file);

            fI.read(data);
            fI.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return data;
    }
}
