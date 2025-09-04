package rs.ilijaruzic.ats.controllers;

import rs.ilijaruzic.ats.views.MainView;
import rs.ilijaruzic.ats.views.utils.InactivityDialog;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class InactivityController
{
    private static final int INACTIVITY_TIMEOUT_MS = 60 * 1000;
    private static final int COUNTDOWN_SECONDS = 5;

    private final MainView view;
    private final Timer inactivityTimer;
    private boolean isPaused = false;

    public InactivityController(MainView view)
    {
        this.view = view;

        inactivityTimer = new Timer(INACTIVITY_TIMEOUT_MS, e -> showInactivityDialog());
        inactivityTimer.setRepeats(false);

        Toolkit.getDefaultToolkit().addAWTEventListener(event ->
        {
            if (isPaused)
            {
                inactivityTimer.restart();
            }
        }, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);

        inactivityTimer.start();
    }

    public void pause()
    {
        isPaused = true;
        inactivityTimer.stop();
    }

    public void resume()
    {
        isPaused = false;
        inactivityTimer.restart();
    }

    private void showInactivityDialog()
    {
        InactivityDialog dialog = new InactivityDialog(view);
        AtomicInteger countdownValue = new AtomicInteger(COUNTDOWN_SECONDS);

        Timer countdownTimer = new Timer(1000, e ->
        {
            int remaining = countdownValue.decrementAndGet();
            dialog.updateCountdown(remaining);
            if (remaining <= 0)
            {
                System.exit(0);
            }
        });

        dialog.updateCountdown(COUNTDOWN_SECONDS);
        countdownTimer.start();
        dialog.setVisible(true);

        countdownTimer.stop();
        if (dialog.didUserContinue())
        {
            inactivityTimer.restart();
        } else
        {
            System.exit(0);
        }
    }
}
