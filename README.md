# Java API Integration

## Overview
This repository contains Java projects that integrate with various APIs and libraries:
- **Gmail API**: Code for sending emails and listing labels using Gmail's API.
- **Google Drive API**: Code for uploading and downloading files from Google Drive.
- **Apache POI**: Code for reading, updating, and deleting rows in Word documents.

## Prerequisites
- Java Development Kit (JDK) 8 or later.
- Apache POI library for Word document manipulation.
- Google API client libraries for Gmail and Google Drive.

## Setup

### Download json file from Google Cloud Console
1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project or select an existing project.
3. Enable the Gmail API and Google Drive API for your project.
4. Navigate to the **Credentials** page.
5. Click on **Create Credentials** and select **OAuth 2.0 Client IDs**.
6. Configure the OAuth consent screen if prompted.
7. Set the application type to **Desktop app**.
8. After configuring, click **Create**.
9. Download the 'json` file and save it to the `resources` folder of your project.

### Gmail API
1. Place your `credentials.json` in the `resources` folder.
2. Ensure you have the appropriate scopes and authentication setup.

### Google Drive API
1. Place your `credentials.json` in the appropriate location.
2. Ensure you have the required scopes and authentication setup.

### Apache POI
1. Add Apache POI dependency to your project. You can add it to your `pom.xml` if you are using Maven:
    ```xml
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>5.0.0</version>
    </dependency>
    ```

## Usage
1. **Gmail.java**: Run this file to send emails or list labels using the Gmail API.
2. **Word.java**: Run this file to perform operations on Word documents using Apache POI.
3. **DriveQuickstart.java**: Run this file to upload and download files from Google Drive using the Google Drive API.
