package com.voronoi.voronoiworkspace.Services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.voronoi.voronoiworkspace.Entities.Images;
import com.voronoi.voronoiworkspace.Entities.User;
import com.voronoi.voronoiworkspace.Repositories.ImageRepository;
import com.voronoi.voronoiworkspace.Repositories.UserRepository;
import com.voronoi.voronoiworkspace.RequestDTO.DownloadFile;
import com.voronoi.voronoiworkspace.ResponseDTO.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GoogleDriveService {
    
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;
    
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "VoronoiApp";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = new ArrayList<>(Arrays.asList(DriveScopes.DRIVE,DriveScopes.DRIVE_FILE,DriveScopes.DRIVE_PHOTOS_READONLY));
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = GoogleDriveService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //returns an authorized Credential object.
        return credential;
    }

    public Drive getInstance() throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        return service;
    }

    public String getfiles() throws IOException, GeneralSecurityException {

        Drive service = getInstance();

        // Print the names and IDs for up to 10 files.
        FileList result = service.files().list()
                .setPageSize(10)
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
            return "No files found.";
        } else {
            return files.toString();
        }
    }
//uploadFile()

    @Transactional
    public BaseResponse uploadFiles(MultipartFile file, String id) throws GeneralSecurityException, IOException {
        BaseResponse response = new BaseResponse();
        if (null != file) {
            File fileMetadata = new File();
            fileMetadata.setName(file.getOriginalFilename());
            File uploadFile = getInstance()
                    .files()
                    .create(fileMetadata, new InputStreamContent(
                            file.getContentType(),
                            new ByteArrayInputStream(file.getBytes()))
                    )
                    .setFields("id").execute();
            System.out.println(uploadFile.getId());
            User user = userRepository.findById(Long.valueOf(id)).get();
            Images images = new Images();
            images.setFileId(uploadFile.getId());
            images.setName(fileMetadata.getName());
            images.setFileType(file.getContentType());
            images.setAction("Imported");
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            // Format the current date and time using the formatter
            String formattedDateTime = now.format(formatter);
            images.setActionDate(formattedDateTime);
            Images savedImage = imageRepository.save(images);
            User updatedUser = user.addSet(savedImage);
            userRepository.save(updatedUser);
            response.setResponseMessage("File Uploaded!");
            response.setResponseCode(1);

        }
        return response;
    }

    public byte[] downloadFile(String fileId) throws GeneralSecurityException, IOException {
        Drive service = getInstance();

        // Use the Drive API to get the file metadata
        File fileMetadata = service.files().get(fileId).execute();

        // Get the input stream for the file content
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            service.files().get(fileId).executeMediaAndDownloadTo(outputStream);
            byte[] fileContent = outputStream.toByteArray();
            return fileContent;
//            String dataUri = "data:image/png;base64," + Base64.getEncoder().encodeToString(fileContent);

            // Return the file content in the response entity
//            return new ResponseEntity<>(dataUri, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
