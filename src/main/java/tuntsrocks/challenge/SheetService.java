package tuntsrocks.challenge;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SheetService {
    private static final String APPLICATION_NAME = "Tunts Rocks challenge app";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Arrays.asList(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE);
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
        InputStream in = SheetService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
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
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * https://docs.google.com/spreadsheets/d/1yTs7bLgOEPCuSP7YYrvUBPl3Sl7qQZDlCDHlaFQZLQE/edit
     */
    public void readSheet() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1yTs7bLgOEPCuSP7YYrvUBPl3Sl7qQZDlCDHlaFQZLQE";
        final String range = "engenharia_de_software!A4:H";
        Sheets service =
                new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            // Print the spreadsheet content
            List<RowData> rowsData = new ArrayList<>();
            List<List<Object>> rowsUpdate = new ArrayList<>();
            for (List row : values) {

                RowData rowData = new RowData();
                rowData.setRegistration((Integer.parseInt(row.get(0).toString())));
                rowData.setStudent((String) row.get(1));
                rowData.setAbsence((Integer.parseInt(row.get(2).toString())));
                rowData.setTestScore1((Integer.parseInt(row.get(3).toString())));
                rowData.setTestScore2((Integer.parseInt(row.get(4).toString())));
                rowData.setTestScore3((Integer.parseInt(row.get(5).toString())));
                rowsData.add(rowData);
            }
            for (RowData rowData : rowsData) {
                List<Object> valuesUpdate = new ArrayList<>();
                System.out.println(rowData.toString());
                double average = (rowData.getTestScore1() + rowData.getTestScore2() + rowData.getTestScore3()) / 3;
                double maxAbsences = 60 * 25 / 100;
                double passingGrade = 50;
                BigDecimal finalGrade = BigDecimal.ZERO;
                String status = "";
                finalGrade.setScale(0, RoundingMode.HALF_UP);

                // Content of the column G - "Situação"
                if (rowData.getAbsence() > maxAbsences) {
                    status = "Reprovado por Falta";
                    System.out.println(status);
                } else if (average < 50) {
                    status = "Reprovado por Nota";
                    System.out.println(status);
                } else if (average >= 50 && average < 70) {
                    status = "Exame Final";
                    System.out.println(status);
                } else if (average >= 70) {
                    status = "Aprovado";
                    System.out.println(status);
                }

                // Content of the column H - "Nota para Aprovação Final"
                if (average >= 50 && average < 70 && rowData.getAbsence() <= maxAbsences) {
                    finalGrade = finalGrade.valueOf((passingGrade * 2) - average);
                    System.out.println(("Nota para Aprovação Final: " + finalGrade));
                } else {
                    System.out.println(0);
                }

                //Add values to list
                valuesUpdate.add(status);
                valuesUpdate.add(finalGrade);

                rowsUpdate.add(valuesUpdate);
            }

            ValueRange body = new ValueRange().setValues(rowsUpdate);
            // Execute the data insertion into the spreadsheet
            service.spreadsheets().values().append(spreadsheetId, "engenharia_de_software!G4:H", body).setValueInputOption("RAW").execute();

        }
    }
}
