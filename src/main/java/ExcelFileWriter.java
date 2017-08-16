import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class ExcelFileWriter
{
    private static final String TIME_HEADER = "Time (sec)";
    private static final String ABSORBANCE_HEADER = "Absorbance";

    public static void createFile(String fileName, Map<String, Collection<File>> tabDataSetMap)
    {
        XSSFWorkbook workbook = new XSSFWorkbook();

        for (Map.Entry<String, Collection<File>> entry : tabDataSetMap.entrySet())
        {
            int columnOffset = 0;

            XSSFSheet sheet = getSheet(workbook, entry.getKey());

            for (File file : entry.getValue())
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
                        }
                        catch (Exception ex)
                        {
                            cell.setCellValue((String) datatypes[columnIndex][rowIndex]);
                        }

                    }
                }

                columnOffset += maxColumns;
            }

            try
            {
                File toWrite = new File(fileName);
                toWrite.createNewFile();

                FileOutputStream outputStream = new FileOutputStream(fileName);
                workbook.write(outputStream);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
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

            while ((line = reader.readLine()) != null)
            {
                tableValues.add(Arrays.asList(line.split("\t")));
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
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

        return null;
    }
}