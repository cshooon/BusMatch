package hoon.capstone.llama;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Upload {
    public static void main(String[] args) throws IOException, SQLException {
        // Step 1: Read Data from Excel File
        List<BusStop> busStops = new ArrayList<>();
        FileInputStream file = new FileInputStream(new File("/Users/hoon/Documents/LlamaVista/spring/src/main/java/hoon/capstone/llama/busstop.xlsx"));

        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header row

            BusStop busStop = new BusStop();
            busStop.routeId = String.valueOf((int) row.getCell(0).getNumericCellValue());
            busStop.routeName = row.getCell(1).getStringCellValue();
            busStop.sequence = (int) row.getCell(2).getNumericCellValue();
            busStop.nodeId = String.valueOf((int) row.getCell(3).getNumericCellValue());
            busStop.arsId = row.getCell(4).getStringCellValue();
            busStop.stopName = row.getCell(5).getStringCellValue();

            busStops.add(busStop);
        }
        workbook.close();
        file.close();
        System.out.println(busStops);
        // Step 2: Establish JDBC Connection
        Connection conn = DriverManager.getConnection("");

        // Step 4: Insert Data into Database
        String insertSQL = "INSERT INTO BusStops (ROUTE_ID, ROUTE_NAME, SEQUENCE, NODE_ID, ARS_ID, STOP_NAME) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(insertSQL);

        for (BusStop busStop : busStops) {
            preparedStatement.setString(1, busStop.routeId);
            preparedStatement.setString(2, busStop.routeName);
            preparedStatement.setInt(3, busStop.sequence);
            preparedStatement.setString(4, busStop.nodeId);
            preparedStatement.setString(5, busStop.arsId);
            preparedStatement.setString(6, busStop.stopName);

            preparedStatement.executeUpdate();
        }

        // Step 5: Close Connections
        preparedStatement.close();
        conn.close();
    }

}

