import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class ExcelFileWriter
{
    private static XSSFWorkbook workbook = new XSSFWorkbook();

    private static final String TIME_HEADER = "Time (sec)";
    private static final String ABSORBANCE_HEADER = "Absorbance";

    public static void createFile(String fileName, Map<String, Collection<File>> tabDataSetMap, Map<String, Double> normalizationOffset)
    {
        for (Map.Entry<String, Collection<File>> entry : tabDataSetMap.entrySet())
        {
            String sheetName = entry.getKey();
            Collection<File> files = entry.getValue();
            int columnOffset = copyDataValuesToExcelCells(sheetName, files);

            addNormalizedDataToExcelCells(sheetName, files, columnOffset, normalizationOffset);

            try
            {
                File toWrite = new File(fileName);
                toWrite.createNewFile();

                FileOutputStream outputStream = new FileOutputStream(fileName);
                workbook.write(outputStream);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    private static XSSFSheet getSheet(XSSFWorkbook workbook, String tabName)
    {
        if (workbook.getSheet(tabName) == null)
        {
            return workbook.createSheet(tabName);
        }

        return workbook.getSheet(tabName);
    }

    private static Row getRow(XSSFSheet sheet, int rowNumber)
    {
        if (sheet.getRow(rowNumber) == null)
        {
            return sheet.createRow(rowNumber);
        }
        return sheet.getRow(rowNumber);
    }

    private static Object[][] getExcelData(File file)
    {
        String fileName = file.getName().split("\\.")[0];

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            List<List<String>> tableValues = new ArrayList<>();

            String extension = "";

            int i = file.getPath().lastIndexOf('.');
            if (i > 0)
            {
                extension = file.getPath().substring(i + 1);
            }

            String separator = "\t";

            if (extension.equals("csv"))
            {
                separator = ",";
            }

            while ((line = reader.readLine()) != null)
            {
                List<String> split = Arrays.asList(line.split(separator));

                if(split.size() > 0 && isNumeric(split.get(0)))
                {
                    tableValues.add(split);
                }
            }

            Object[][] excelTableArray = new Object[2][tableValues.size() + 2];
            excelTableArray[0][0] = fileName;
            excelTableArray[0][1] = TIME_HEADER;
            excelTableArray[1][1] = ABSORBANCE_HEADER;

            for (int row = 0; row < tableValues.size(); row++)
            {
                List<String> rowData = tableValues.get(row);

                for (int column = 0; column < rowData.size(); column++)
                {
                    excelTableArray[column][row + 2] = rowData.get(column);
                }
            }

            return excelTableArray;
        } catch (Exception exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

    private static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    private static int copyDataValuesToExcelCells(String sheetName, Collection<File> files)
    {
        int columnOffset = 0;

        XSSFSheet sheet = getSheet(workbook, sheetName);

        for (File file : files)
        {
            Object[][] datatypes = getExcelData(file);

            int maxColumns = 2;

            for (int rowIndex = 0; rowIndex < datatypes[0].length; rowIndex++)
            {
                Row row = getRow(sheet, rowIndex);

                for (int columnIndex = 0; columnIndex < datatypes.length; columnIndex++)
                {
                    Cell cell = row.createCell(columnOffset + columnIndex);

                    try
                    {
                        double value = Double.parseDouble(datatypes[columnIndex][rowIndex].toString());
                        cell.setCellValue(value);
                    } catch (Exception ex)
                    {
                        cell.setCellValue((String) datatypes[columnIndex][rowIndex]);
                    }

                }
            }

            columnOffset += maxColumns;
        }

        return columnOffset;
    }

    private static void addNormalizedDataToExcelCells(String sheetName, Collection<File> files, int columnOffset, Map<String, Double> normalizationOffset)
    {
        int letterOffset = 0;
        XSSFSheet sheet = getSheet(workbook, sheetName);

        for (File file : files)
        {
            Object[][] datatypes = getExcelData(file);

            int maxColumns = 2;

            String fileName = file.getName().split("\\.")[0] + "Norm";
            Row manualRowData = getRow(sheet, 0);
            Cell manualDataCell = manualRowData.createCell(columnOffset);

            manualDataCell.setCellValue(fileName);

            manualRowData = getRow(sheet, 1);
            manualDataCell = manualRowData.createCell(columnOffset);
            manualDataCell.setCellValue(TIME_HEADER);
            manualDataCell = manualRowData.createCell(columnOffset + 1);
            manualDataCell.setCellValue(ABSORBANCE_HEADER);

            for (int rowIndex = 2; rowIndex < datatypes[0].length; rowIndex++)
            {
                Row row = getRow(sheet, rowIndex);

                for (int columnIndex = 0; columnIndex < datatypes.length; columnIndex++)
                {
                    Cell cell = row.createCell(columnOffset + columnIndex);
                    String rowVariable = Integer.toString(rowIndex + 1);
                    String columnCharacter = getCharForNumber(columnIndex + letterOffset);
                    String formula = columnCharacter + rowVariable;

                    if (columnIndex == 1 && !columnCharacter.isEmpty())
                    {
                        formula += "-(" + columnCharacter + "$3-" + normalizationOffset.get(sheetName) + ")";
                    }
                    cell.setCellFormula(formula);
                }

            }

            letterOffset += 2;
            columnOffset += maxColumns;
        }
    }

    private static String getCharForNumber(int number)
    {
        StringBuilder sb = new StringBuilder();

        if(number == 0)
        {
            return "A";
        }

        while (number-- > 0)
        {
            sb.append((char) ('A' + (number % 26)));
            number /= 26;
        }
        return sb.reverse().toString();
    }
}