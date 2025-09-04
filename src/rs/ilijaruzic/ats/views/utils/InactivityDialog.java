package rs.ilijaruzic.ats.views.utils;


import javax.swing.*;
import java.awt.*;

public class InactivityDialog extends JDialog
{
    private JLabel countdownLabel;
    private boolean userContinued = false;

    public InactivityDialog(Frame owner)
    {
        super(owner, "Inactivity Warning", true);
        initialize();
    }

    private void initialize()
    {
        setLayout(new BorderLayout(10, 10));

        countdownLabel = new JLabel("Application will close due to inactivity...", SwingConstants.CENTER);
        countdownLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JButton continueButton = new JButton("Continue");
        continueButton.addActionListener(e ->
        {
            userContinued = true;
            dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(continueButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        add(countdownLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getOwner());
        setResizable(false);
    }

    public void updateCountdown(int secondsLeft)
    {
        countdownLabel.setText("Closing in " + secondsLeft + " seconds...");
    }

    public boolean didUserContinue()
    {
        return userContinued;
    }
}