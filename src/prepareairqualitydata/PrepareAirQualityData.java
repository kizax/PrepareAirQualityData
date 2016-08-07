/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prepareairqualitydata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kizax
 */
public class PrepareAirQualityData {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {

            String fileName = "./EPA_1996-2015.csv";
            String resultFileName = "./EPA_1996-2015_afterParse.csv";
            FileReader fileReader = new FileReader(fileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
//                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "Cp1252"));

            int lineCount = 0;
            ArrayList<String> airQualityDataList = new ArrayList();
            while (bufferedReader.ready()) {
                String recordStr = bufferedReader.readLine();
                lineCount++;

                String[] recordStrArray = recordStr.split(",");

                DateFormat dataFromat = new SimpleDateFormat("yyyy-MM-dd");

                Date monitorDate = null;
                try {
                    monitorDate = dataFromat.parse(recordStrArray[2]);
                } catch (ParseException ex) {
                    System.out.println(String.format("Line %1$d has ParseException", lineCount));
                    continue;
                }
                SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/M/d"); //2016/1/15
                String timeStr = timeFormat.format(monitorDate);

                String newRecordStr = String.format("%1$s, %2$s, %3$s, "
                        + "%4$s, %5$s, %6$s, %7$s, %8$s, "
                        + "%9$s, %10$s, %11$s, %12$s, %13$s, "
                        + "%14$s, %15$s, %16$s, %17$s, %18$s, "
                        + "%19$s, %20$s, %21$s, %22$s, %23$s, "
                        + "%24$s, %25$s, %26$s, %27$s", recordStrArray[0], timeStr, recordStrArray[3],
                        recordStrArray[4], recordStrArray[5], recordStrArray[6],
                        recordStrArray[7], recordStrArray[8], recordStrArray[9],
                        recordStrArray[10], recordStrArray[11], recordStrArray[12],
                        recordStrArray[13], recordStrArray[14], recordStrArray[15],
                        recordStrArray[16], recordStrArray[17], recordStrArray[18],
                        recordStrArray[19], recordStrArray[20], recordStrArray[21],
                        recordStrArray[22], recordStrArray[23], recordStrArray[24],
                        recordStrArray[25], recordStrArray[26], recordStrArray[27]);

                airQualityDataList.add(newRecordStr);
            }

            fileReader.close();

            System.out.println(String.format("Start writing result into file"));
            FileWriter resultFileWriter = createFileWriter(resultFileName, false);
            writeFile(resultFileWriter, airQualityDataList);

            System.out.println(String.format("Successfully writeresult into file"));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(PrepareAirQualityData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PrepareAirQualityData.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void writeFile(FileWriter resultFileWriter,
            ArrayList<String> airQualityDataList) {
        try {
            //寫入檔頭BOM，避免EXCEL開啟變成亂碼
            byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            resultFileWriter.write(new String(bom));

            //寫入紀錄檔
            int writingCount = 0;
            for (String airQualityData : airQualityDataList) {
                writeCsvFile(resultFileWriter, airQualityData);
                writingCount++;
            }

        } catch (IOException ex) {
            Logger.getLogger(PrepareAirQualityData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static FileWriter createFileWriter(String fileName, boolean append) {
        //建立log file
        File file = new File(fileName);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        FileWriter fileWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
                fileWriter = new FileWriter(file, append);
            } else {
                fileWriter = new FileWriter(file, append);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return fileWriter;
    }

    private static void writeCsvFile(FileWriter csvFileWriter, String record) {
        WriteThread writerThread = new WriteThread(csvFileWriter, record);
        writerThread.start();
    }

}
