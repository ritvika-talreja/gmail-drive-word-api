# Java API Integration

## Overview
This repository contains Java projects that integrate with various APIs and libraries:
- **Gmail API**: Code for sending emails and listing labels using Gmail's API.
- **Google Drive API**: Code for uploading and downloading files from Google Drive.
- **Apache POI**: Code for reading, updating, and deleting rows in Word documents.

## Files
1. **Gmail.java**: Demonstrates how to use the Gmail API for sending emails and listing labels.
2. **Word.java**: Demonstrates how to read, add, update, and delete rows in Word documents using Apache POI.
3. **DriveQuickstart.java**: Demonstrates how to use the Google Drive API for viewing, uploading, and downloading files.

## Prerequisites
- Java Development Kit (JDK) 8 or later.
- Apache POI library for Word document manipulation.
- Google API client libraries for Gmail and Google Drive.

## Setup
1. **Gmail API:**
   - Place your `credentials.json` in the `resources` folder.
   - Ensure you have the appropriate scopes and authentication setup.

2. **Google Drive API:**
   - Place your `credentials.json` in the appropriate location.
   - Ensure you have the required scopes and authentication setup.

3. **Apache POI:**
   - Add Apache POI dependency to your project.

## Usage
- For **Gmail API**: Run `Gmailer.java` to send emails and list labels.
- For **Google Drive API**: Run `DriveQuickstart.java` to interact with Google Drive.
- For **Apache POI**: Run `WordData.java` to modify Word documents.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

