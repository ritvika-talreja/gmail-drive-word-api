package wordextract;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.Arrays;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

public class WordData {

    public void readWord(String path, int tableIndex) {
        try (FileInputStream fis = new FileInputStream(path);
             XWPFDocument document = new XWPFDocument(fis)) {

            XWPFTable table = document.getTables().get(tableIndex);

            // Determine the maximum width for each column based on the longest text
            int[] maxColumnWidths = new int[table.getRow(0).getTableCells().size()];
            for (XWPFTableRow row : table.getRows()) {
                for (int cellIndex = 0; cellIndex < row.getTableCells().size(); cellIndex++) {
                    String cellText = row.getCell(cellIndex).getText();
                    if (cellText.length() > maxColumnWidths[cellIndex]) {
                        maxColumnWidths[cellIndex] = cellText.length();
                    }
                }
            }

            // Print header row
            for (int i = 0; i < maxColumnWidths.length; i++) {
                System.out.print(String.format("%-" + (maxColumnWidths[i] + 2) + "s", table.getRow(0).getCell(i).getText()));
            }
            System.out.println();

            // Print data rows
            for (int rowIndex = 1; rowIndex < table.getRows().size(); rowIndex++) {
                XWPFTableRow row = table.getRow(rowIndex);
                for (int cellIndex = 0; cellIndex < row.getTableCells().size(); cellIndex++) {
                    String cellText = row.getCell(cellIndex).getText();
                    System.out.print(String.format("%-" + (maxColumnWidths[cellIndex] + 2) + "s", cellText));
                }
                System.out.println(); // Move to the next row
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getColumnIndex(XWPFTable table, String columnName) {
        // Iterate through the first row to find the column index
        for (int i = 0; i < table.getRow(0).getTableCells().size(); i++) {
            String cellText = table.getRow(0).getCell(i).getText().trim();
            if (cellText.equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }

    public void addNewRow(String path, int tableIndex, String[] rowData) {
        try (FileInputStream fis = new FileInputStream(path);
             XWPFDocument document = new XWPFDocument(fis)) {

            XWPFTable table = document.getTables().get(tableIndex);

            // Create a new row
            XWPFTableRow newRow = table.createRow();

            // Populate cells in the new row with specific data
            for (int cellIndex = 0; cellIndex < Math.min(rowData.length, newRow.getTableCells().size()); cellIndex++) {
                XWPFTableCell cell = newRow.getCell(cellIndex);
                cell.setText(rowData[cellIndex]);
            }

            // Save the changes to the document
            try (FileOutputStream fos = new FileOutputStream(path)) {
                document.write(fos);
                System.out.println("New Row Added Successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateCellValue(String path, int tableIndex, String columnName, int rowNumber, String newValue) {
        try (FileInputStream fis = new FileInputStream(path);
             XWPFDocument document = new XWPFDocument(fis)) {

            XWPFTable table = document.getTables().get(tableIndex);

            // Find the column index based on the column name
            int columnIndex = getColumnIndex(table, columnName);

            // Check if the column index is valid
            if (columnIndex != -1) {
                // Get the specified row
                XWPFTableRow row = table.getRow(rowNumber - 1);

                // Check if the row index is valid
                if (row != null) {
                    // Get the cell in the specified column and row
                    XWPFTableCell cell = row.getCell(columnIndex);

                    // Remove existing paragraphs from the cell
                    while (cell.getParagraphs().size() > 0) {
                        cell.removeParagraph(0);
                    }

                    // Create a new paragraph and run
                    XWPFParagraph newParagraph = cell.addParagraph();
                    XWPFRun run = newParagraph.createRun();

                    // Set the text for the run
                    run.setText(newValue);

                    // Save the changes to the document
                    try (FileOutputStream fos = new FileOutputStream(path)) {
                        document.write(fos);
                        System.out.println("Cell Value Updated Successfully.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Invalid row number");
                }
            } else {
                System.out.println("Column not found");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteRows(String path, int tableIndex, int[] rowNumbers) {
        try (FileInputStream fis = new FileInputStream(path);
             XWPFDocument document = new XWPFDocument(fis)) {

            XWPFTable table = document.getTables().get(tableIndex);

            // Remove the specified rows
            for (int i = rowNumbers.length - 1; i >= 0; i--) {
                int rowIndex = rowNumbers[i] - 1;
                if (rowIndex >= 0 && rowIndex < table.getRows().size()) {
                    table.removeRow(rowIndex);
                }
            }

            // Save the changes to the document
            try (FileOutputStream fos = new FileOutputStream(path)) {
                document.write(fos);
                System.out.println("Rows Deleted Successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String filePath = "/Users/ritvikatalreja/Documents/extract.docx";
        WordData word = new WordData();

        Scanner scanner = new Scanner(System.in);

        int tableIndex;
        int choice;

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis)) {

            while (true) {
                System.out.println("Enter the table number:");
                tableIndex = scanner.nextInt();

                if (tableIndex >= 0 && tableIndex < document.getTables().size()) {
                    break;
                } else {
                    System.out.println("Invalid table index. Please try again.");
                }
            }

            while (true) {
                System.out.println("Choose an operation:");
                System.out.println("1. Read Data from Word");
                System.out.println("2. Add New Row");
                System.out.println("3. Update Cell Value");
                System.out.println("4. Delete Rows");

                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        word.readWord(filePath, tableIndex);
                        break;
                    case 2:
                        System.out.println("Enter data for each column (separated by spaces):");
                        scanner.nextLine(); // Consume newline
                        String[] rowData = scanner.nextLine().split("\\s+");
                        word.addNewRow(filePath, tableIndex, rowData);
                        break;
                    case 3:
                        System.out.println("Enter column name:");
                        String columnName = scanner.next();
                        System.out.println("Enter row number:");
                        int rowNumber = scanner.nextInt();
                        System.out.println("Enter new cell value:");
                        String newValue = scanner.next();
                        word.updateCellValue(filePath, tableIndex, columnName, rowNumber, newValue);
                        break;
                    case 4:
                        System.out.println("Enter row numbers to delete (separated by spaces):");
                        scanner.nextLine(); // Consume newline
                        String[] rowNumbersStr = scanner.nextLine().split("\\s+");
                        int[] rowNumbers = Arrays.stream(rowNumbersStr)
                                .mapToInt(Integer::parseInt)
                                .toArray();
                        word.deleteRows(filePath, tableIndex, rowNumbers);
                        break;
                    default:
                        System.out.println("Invalid choice");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
