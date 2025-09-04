package rs.ilijaruzic.ats.views.utils;

import javax.swing.*;

public class AppMenuBar extends JMenuBar
{

    private JMenuItem loadCsvMenuItem;
    private JMenuItem saveCsvMenuItem;

    public AppMenuBar()
    {
        initialize();
    }

    private void initialize()
    {
        JMenu fileMenu = new JMenu("File");

        loadCsvMenuItem = new JMenuItem("Load from CSV...");
        saveCsvMenuItem = new JMenuItem("Save to CSV...");

        fileMenu.add(loadCsvMenuItem);
        fileMenu.add(saveCsvMenuItem);

        this.add(fileMenu);
    }

    public JMenuItem getLoadCsvMenuItem()
    {
        return loadCsvMenuItem;
    }

    public JMenuItem getSaveCsvMenuItem()
    {
        return saveCsvMenuItem;
    }
}
