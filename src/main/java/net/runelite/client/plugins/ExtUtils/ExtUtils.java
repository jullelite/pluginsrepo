/*
 * Copyright (c) 2019-2020, ganom <https://github.com/Ganom>
 * All rights reserved.
 * Licensed under GPL3, see LICENSE for the full scope.
 */
package net.runelite.client.plugins.ExtUtils;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "ExtUtils",
        hidden = true
)
@Slf4j
@SuppressWarnings("unused")
@Singleton
public class ExtUtils extends Plugin
{
    @Inject
    private Client client;

    @Override
    protected void startUp()
    {

    }

    @Override
    protected void shutDown()
    {

    }

    public int[] stringToIntArray(String string)
    {
        return Arrays.stream(string.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
    }


    public List<Widget> getItems(int... itemIDs)
    {
        assert client.isClientThread();
        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

        if (inventoryWidget == null)
        {
            return new ArrayList<>();
        }

        return Arrays.stream(inventoryWidget.getDynamicChildren())
                .filter(child -> Arrays.stream(itemIDs)
                        .anyMatch(i -> i == child.getItemId()))
                .collect(Collectors.toList());
    }

    public List<Widget> getEquippedItems(int[] itemIds)
    {
        assert client.isClientThread();

        Widget equipmentWidget = client.getWidget(WidgetInfo.EQUIPMENT);

        List<Integer> equippedIds = new ArrayList<>();

        for (int i : itemIds)
        {
            equippedIds.add(i);
        }

        List<Widget> equipped = new ArrayList<>();

        if (equipmentWidget.getStaticChildren() != null)
        {
            for (Widget widgets : equipmentWidget.getStaticChildren())
            {
                for (Widget items : widgets.getDynamicChildren())
                {
                    if (equippedIds.contains(items.getItemId()))
                    {
                        equipped.add(items);
                    }
                }
            }
        }
        else
        {
            log.error("Children is Null!");
        }

        return equipped;
    }

    public int getTabHotkey(Tab tab)
    {
        assert client.isClientThread();

        final int var = client.getVarbitValue(client.getVarps(), tab.getVarbit());
        final int offset = 111;

        switch (var)
        {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                return var + offset;
            case 13:
                return 27;
            default:
                return -1;
        }
    }

    public WidgetInfo getPrayerWidgetInfo(String spell)
    {
        assert client.isClientThread();

        return null;
    }


    public Widget getPrayerWidget(String spell)
    {
        assert client.isClientThread();
        return null;
    }

    /**
     * This method must be called on a new
     * thread, if you try to call it on
     * {@link net.runelite.client.callback.ClientThread}
     * it will result in a crash/desynced thread.
     */
    public void typeString(String string)
    {
        assert !client.isClientThread();

        for (char c : string.toCharArray())
        {
            pressKey(c);
        }
    }

    public void pressKey(char key)
    {
        keyEvent(401, key);
        keyEvent(402, key);
        keyEvent(400, key);
    }

    private void keyEvent(int id, char key)
    {
        KeyEvent e = new KeyEvent(
                client.getCanvas(), id, System.currentTimeMillis(),
                0, KeyEvent.VK_UNDEFINED, key
        );

        client.getCanvas().dispatchEvent(e);
    }

    /**
     * This method must be called on a new
     * thread, if you try to call it on
     * {@link net.runelite.client.callback.ClientThread}
     * it will result in a crash/desynced thread.
     */
    public void click(Rectangle rectangle)
    {
        assert !client.isClientThread();
        Point point = getClickPoint(rectangle);
        click(point);
    }

    public void click(Point p)
    {
        assert !client.isClientThread();

        if (client.isStretchedEnabled())
        {
            final Dimension stretched = client.getStretchedDimensions();
            final Dimension real = client.getRealDimensions();
            final double width = (stretched.width / real.getWidth());
            final double height = (stretched.height / real.getHeight());
            final Point point = new Point((int) (p.getX() * width), (int) (p.getY() * height));
            mouseEvent(501, point);
            mouseEvent(502, point);
            mouseEvent(500, point);
            return;
        }
        mouseEvent(501, p);
        mouseEvent(502, p);
        mouseEvent(500, p);
    }

    public Point getClickPoint(Rectangle rect)
    {
        final int x = (int) (rect.getX() + getRandomIntBetweenRange((int) rect.getWidth() / 6 * -1, (int) rect.getWidth() / 6) + rect.getWidth() / 2);
        final int y = (int) (rect.getY() + getRandomIntBetweenRange((int) rect.getHeight() / 6 * -1, (int) rect.getHeight() / 6) + rect.getHeight() / 2);

        return new Point(x, y);
    }

    public int getRandomIntBetweenRange(int min, int max)
    {
        return (int) ((Math.random() * ((max - min) + 1)) + min);
    }

    private void mouseEvent(int id, Point point)
    {
        MouseEvent e = new MouseEvent(
                client.getCanvas(), id,
                System.currentTimeMillis(),
                0, point.getX(), point.getY(),
                1, false, 1
        );

        client.getCanvas().dispatchEvent(e);
    }
}