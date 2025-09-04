package rs.ilijaruzic.ats.views.utils;

import java.awt.*;

public class CoordinateConverter
{
    private CoordinateConverter()
    {
    }

    public static Point geoToScreen(double geoX, double geoY, int panelWidth, int panelHeight)
    {
        int padding = 20;
        int drawableWidth = panelWidth - 2 * padding;
        int drawableHeight = panelHeight - 2 * padding;

        double normX = (geoX + 90.0) / 180.0;
        double normY = (geoY + 90.0) / 180.0;

        int screenX = (int) (normX * drawableWidth) + padding;
        int screenY = (int) ((1 - normY) * drawableHeight) + padding;

        return new Point(screenX, screenY);
    }
}
