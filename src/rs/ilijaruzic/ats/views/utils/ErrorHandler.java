package rs.ilijaruzic.ats.views.utils;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ErrorHandler
{
    private static final Logger LOGGER = Logger.getLogger(ErrorHandler.class.getName());

    private ErrorHandler()
    {
    }

    public static void showErrorMessage(Component parent, String message, String title)
    {
        LOGGER.log(Level.WARNING, "Displaying error to user: [{0}] - {1}", new Object[]{title, message});
        JOptionPane.showMessageDialog(
                parent,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void showErrorMessage(Component parent, String message)
    {
        showErrorMessage(parent, message, "Error");
    }

    public static void showErrorMessages(Component parent, java.util.List<String> messages)
    {
        if (messages == null || messages.isEmpty())
        {
            return;
        }

        StringBuilder message = new StringBuilder();
        for (String error : messages)
        {
            message.append(error).append("\n");
        }

        showErrorMessage(parent, message.toString(), messages.size() > 1 ? "Errors" : "Error");
    }
}
