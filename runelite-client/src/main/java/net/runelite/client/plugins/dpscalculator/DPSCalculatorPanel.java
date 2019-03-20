package net.runelite.client.plugins.dpscalculator;

import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DPSCalculatorPanel extends PluginPanel {
    private final DPSCalculator uiCalculator;
    private final SkillIconManager iconManager;
    //private final MaterialTabGroup tabGroup;

    DPSCalculatorPanel(SkillIconManager iconManager, Client client, SpriteManager spriteManager, ItemManager itemManager) {
        super();
        //getScrollPane().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        this.iconManager = iconManager;

        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new GridBagLayout());
        /*setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;

        tabGroup = new MaterialTabGroup();
        tabGroup.setLayout(new GridLayout(0, 6, 7, 7));

        //final UICalculatorInputArea uiInput = new UICalculatorInputArea();
        //uiInput.setBorder(new EmptyBorder(15, 0, 15, 0));
        //uiInput.setBackground(ColorScheme.DARK_GRAY_COLOR);


        add(tabGroup, c);
        c.gridy++;

        //add(uiInput, c);
        //c.gridy++;
        */

        uiCalculator = new DPSCalculator(client, spriteManager, itemManager);
        add(uiCalculator);
        //c.gridy++;
    }
}
