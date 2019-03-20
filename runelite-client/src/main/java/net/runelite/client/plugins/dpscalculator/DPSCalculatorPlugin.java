package net.runelite.client.plugins.dpscalculator;

import net.runelite.api.Client;
import net.runelite.api.SpriteID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

@PluginDescriptor(
        name = "DPS Calculator",
        description = "Enable the DPS Calculator panel",
        tags = {"panel", "combat"}
)

public class DPSCalculatorPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private SkillIconManager skillIconManager;

    @Inject
    private ItemManager itemManager;

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private ClientToolbar clientToolbar;

    private NavigationButton uiNavigationButton;

    @Override
    protected void startUp() throws Exception
    {
        final BufferedImage icon = spriteManager.getSprite(SpriteID.MULTI_COMBAT_ZONE_CROSSED_SWORDS, 0);
        final DPSCalculatorPanel uiPanel = new DPSCalculatorPanel(skillIconManager, client, spriteManager, itemManager);

        uiNavigationButton = NavigationButton.builder()
                .tooltip("DPS Calculator")
                .icon(icon)
                .priority(10)
                .panel(uiPanel)
                .build();

        clientToolbar.addNavigation(uiNavigationButton);
    }

    @Override
    protected void shutDown() throws Exception
    {
        clientToolbar.removeNavigation(uiNavigationButton);
    }
}
