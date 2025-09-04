package rs.ilijaruzic.ats.controllers;

import rs.ilijaruzic.ats.models.SimulationModel;
import rs.ilijaruzic.ats.utils.CSVHandler;
import rs.ilijaruzic.ats.views.MainView;
import rs.ilijaruzic.ats.views.utils.ErrorHandler;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class FileController
{
    private final SimulationModel model;
    private final MainView view;
    private final JFileChooser fileChooser;

    public FileController(SimulationModel model, MainView view)
    {
        this.model = model;
        this.view = view;
        this.fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        view.getAppMenuBar().getLoadCsvMenuItem().addActionListener(e -> loadFile());
        view.getAppMenuBar().getSaveCsvMenuItem().addActionListener(e -> saveFile());
    }

    private void loadFile()
    {
        int result = fileChooser.showOpenDialog(view);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = fileChooser.getSelectedFile();
            try
            {
                CSVHandler.DataContainer data = CSVHandler.loadData(selectedFile);
                model.loadData(data.airports(), data.flights());
            } catch (Exception ex)
            {
                ErrorHandler.showErrorMessage(view, ex.getMessage(), "Failed to load file");
            }
        }
    }

    private void saveFile()
    {
        int result = fileChooser.showSaveDialog(view);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".csv"))
            {
                selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".csv");
            }

            try
            {
                CSVHandler.saveData(selectedFile, model);
                JOptionPane.showMessageDialog(view, "Data saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex)
            {
                ErrorHandler.showErrorMessage(view, ex.getMessage(), "Failed to save file");
            }
        }
    }
}
