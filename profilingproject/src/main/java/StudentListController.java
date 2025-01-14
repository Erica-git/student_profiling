import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class StudentListController {

    private Main main;
    ConfirmWindow confirmWindow = new ConfirmWindow();

    public void setMain(Main main) {
        this.main = main;
        studentTableView.setItems(main.getStudentList());
    }

    @FXML
    private void handleNewFile() {
        if (!main.getStudentList().isEmpty()) {
            boolean answer = confirmWindow.showConfirmWindow("Confirm create a new file",
                    "Are you sure you want to create a new list without saving?");
            if (answer) {
                main.getStudentList().removeAll(main.getStudentList());
                errorLabel.setText("");
                statusLabel.setText("Elements in table: " + studentTableView.getItems().size());
            }
        }
    }

    @FXML
    private void handleOpenFile() {
        boolean answer = true;
        if (!main.getStudentList().isEmpty()) {
            answer = confirmWindow.showConfirmWindow("Confirm open file",
                    "Are you sure you want to open file without saving?");
        }
        if (answer) {
            main.getStudentList().removeAll(main.getStudentList());

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX files", "*.xlsx"));

            List<File> files = fileChooser.showOpenMultipleDialog(null);
            if (files != null) {
                errorLabel.setText("");
                try {
                    readFromExcel(files);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void readFromExcel(List<File> files) throws IOException{
        String fullname = "";
        String group = "";
        double averageGrade = 0;

        for(File file : files) {
            XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(file));
            XSSFSheet sheet = myExcelBook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            Row row = rowIterator.next();

            while(rowIterator.hasNext()) {

                row = rowIterator.next();

                if (row.getCell(1).getCellType() == XSSFCell.CELL_TYPE_STRING && row.getCell(1) != null && !row.getCell(1).equals("")) {
                    fullname = row.getCell(1).getStringCellValue();
                } else {
                    continue;
                }

                if (row.getCell(2).getCellType() == XSSFCell.CELL_TYPE_STRING && row.getCell(2) != null && !row.getCell(2).equals("")) {
                    group = row.getCell(2).getStringCellValue();
                } else {
                    group = "Неизвестно";
                }

                if (row.getCell(3).getCellType() == XSSFCell.CELL_TYPE_NUMERIC && row.getCell(3) != null && !row.getCell(3).equals("")) {
                    averageGrade = row.getCell(3).getNumericCellValue();
                } else {
                    averageGrade = 0;
                }
                String name[] = fullname.split(" ");
                main.getStudentList().add(new Student(name[0], name[1], name[2], group, averageGrade));
            }

            myExcelBook.close();
        }
    }
    @FXML
    private void handleAboutProgram() {
        showAboutWindow();
    }

    private void showAboutWindow() {
        Stage stage = new Stage();
        stage.setTitle("About Program");
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        Label lbl1 = new Label("Developed by");
        Label lbl2 = new Label("group IKPI-61");
        Label lbl3 = new Label("in 2019");
        Button btnOk = new Button("OK");
        btnOk.setOnAction(event -> stage.close());
        vbox.getChildren().addAll(lbl1, lbl2, lbl3, btnOk);
        Scene scene = new Scene(vbox, 250, 140);
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(main.getPrimaryStage());
        stage.showAndWait();
    }

    @FXML
    private void handleExitProgram() {
        showConfirmExitWindow();
    }

    public void showConfirmExitWindow() {
        boolean answer = confirmWindow.showConfirmWindow("Confirm exit",
                "Are you sure you want to exit the program without saving?");
        if (answer) {
            System.exit(1);
        }
    }
}
