package Gdrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DriveQuickstart {
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES =
            Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY, DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/Users/ritvikatalreja/eclipse-workspace/drive-quickstart/gdcredentials.json";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = DriveQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        // Get the authenticated user's email
        Oauth2 oauth2 = new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
        Userinfo userInfo = oauth2.userinfo().get().execute();
        System.out.println("Authenticated user email: " + userInfo.getEmail());

        return credential;
    }

    public void viewDrive(Drive service) throws IOException, GeneralSecurityException {
        FileList result = service.files().list()
                .setPageSize(20)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }
    }

    public String uploadFile(Drive service, String path, String fileName, String mimeType) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        java.io.File filePath = new java.io.File(path);
        FileContent mediaContent = new FileContent(mimeType, filePath);
        try {
            File file = service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            System.out.println("File ID: " + file.getId());
            return file.getId();
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to upload file: " + e.getDetails());
            throw e;
        }
    }

    public String downloadFile(Drive service, String realFileId) throws IOException {
        File fileMetadata = service.files().get(realFileId).execute();
        String fileName = fileMetadata.getName();
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            service.files().get(realFileId).executeMediaAndDownloadTo(outputStream);
            System.out.println("File downloaded successfully: " + fileName);
            return fileName;
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
            throw e;
        }
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        DriveQuickstart driveQuickstart = new DriveQuickstart();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Print the menu
            System.out.println("Enter choice:");
            System.out.println("1. View Drive");
            System.out.println("2. Upload File");
            System.out.println("3. Download File");
            System.out.println("4. Exit");

            // Read user choice
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character

            switch (choice) {
                case 1:
                    // View Drive
                    driveQuickstart.viewDrive(service);
                    break;
                case 2:
                    // Upload File
                    System.out.println("Enter file path to upload:");
                    String uploadFilePath = scanner.nextLine();
                    System.out.println("Enter file name for upload:");
                    String uploadFileName = scanner.nextLine();
                    System.out.println("Enter MIME type for upload:");
                    String uploadMimeType = scanner.nextLine();
                    driveQuickstart.uploadFile(service, uploadFilePath, uploadFileName, uploadMimeType);
                    break;
                case 3:
                    // Download File
                    System.out.println("Enter file ID to download:");
                    String downloadFileId = scanner.nextLine();
                    driveQuickstart.downloadFile(service, downloadFileId);
                    break;
                case 4:
                    // Exit
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }
}
