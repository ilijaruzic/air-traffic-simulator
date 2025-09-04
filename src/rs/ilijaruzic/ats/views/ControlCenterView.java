package rs.ilijaruzic.ats.views;

import javax.swing.*;
import java.awt.*;

public class ControlCenterView extends JPanel
{
    private final JButton startResumeButton;
    private final JButton pauseButton;
    private final JButton resetButton;
    private final JLabel timeLabel;

    public ControlCenterView()
    {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        setBorder(BorderFactory.createTitledBorder("Simulation Control"));

        startResumeButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        resetButton = new JButton("Reset");
        timeLabel = new JLabel("Time: 00:00");
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 16));

        pauseButton.setEnabled(false);
        resetButton.setEnabled(false);

        add(startResumeButton);
        add(pauseButton);
        add(resetButton);
        add(new JSeparator(SwingConstants.VERTICAL));
        add(timeLabel);
    }

    public JButton getStartResumeButton()
    {
        return startResumeButton;
    }

    public JButton getPauseButton()
    {
        return pauseButton;
    }

    public JButton getResetButton()
    {
        return resetButton;
    }

    public JLabel getTimeLabel()
    {
        return timeLabel;
    }
}
