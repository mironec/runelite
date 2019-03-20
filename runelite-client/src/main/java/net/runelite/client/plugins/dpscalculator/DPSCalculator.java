package net.runelite.client.plugins.dpscalculator;

import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.http.api.item.ItemEquipmentStats;
import net.runelite.http.api.item.ItemStats;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DPSCalculator extends JPanel {
    private final Client client;
    private final SpriteManager spriteManager;
    private final ItemManager itemManager;
    private final JButton uiRefreshButton;

    private final ArrayList<JLabel> uiMaxHitNumbers = new ArrayList<>();
    private final ArrayList<JLabel> uiAccuracyNumbers = new ArrayList<>();
    private final ArrayList<JLabel> uiDPMNumbers = new ArrayList<>();
    private final ArrayList<COMBAT_STYLE> uiStyles = new ArrayList<>();

    private final ArrayList<JTextField> uiEnemyStatFields = new ArrayList<>();
    private final ArrayList<MONSTER_STATS> uiEnemyStats = new ArrayList<>();

    private final JLabel uiAttackSpeedLabel;
    private final JLabel uiAttackSpeedNumber;
    private final JComboBox uiAttackStyleComboBox;
    private final JLabel uiMagicMaxHitLabel;
    private final JTextField uiMagicMaxHitField;
    private ATTACK_STYLE currentAttackStyle = ATTACK_STYLE.STAB; // For now a select, since I do not know how to get it dynamically

    DPSCalculator(Client client, SpriteManager spriteManager, ItemManager itemManager){
        this.client = client;
        this.spriteManager = spriteManager;
        this.itemManager = itemManager;

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;

        add(createMaxHitUI("melee"), c); c.gridy++;
        add(createAccuracyUI("melee"), c); c.gridy++;
        add(createDPMUI("melee"), c); c.gridy++;
        JPanel panel = new JPanel(); panel.setSize(0,10);
        add(panel);
        uiStyles.add(COMBAT_STYLE.MELEE);

        add(createMaxHitUI("ranged"), c); c.gridy++;
        add(createAccuracyUI("ranged"), c); c.gridy++;
        add(createDPMUI("ranged"), c); c.gridy++;
        panel = new JPanel(); panel.setSize(0,10);
        add(panel);
        uiStyles.add(COMBAT_STYLE.RANGED);

        uiMagicMaxHitLabel = new JLabel("Magic max hit: "); add(uiMagicMaxHitLabel, c); c.gridx++;
        uiMagicMaxHitField = new JTextField(); add(uiMagicMaxHitField, c); c.gridx--; c.gridy++;
        add(createAccuracyUI("magic"), c); c.gridy++;
        add(createDPMUI("magic"), c); c.gridy++;
        panel = new JPanel(); panel.setSize(0,10);
        add(panel);
        uiStyles.add(COMBAT_STYLE.MAGIC);

        uiRefreshButton = new JButton("Refresh");
        uiRefreshButton.addActionListener(e -> updateAll());
        add(uiRefreshButton, c);
        c.gridy++;

        add(createEnemyStatUI("defence level"), c); uiEnemyStats.add(MONSTER_STATS.DEFENCE_LEVEL); c.gridy++;
        add(createEnemyStatUI("magic level"), c); uiEnemyStats.add(MONSTER_STATS.MAGIC_LEVEL); c.gridy++;
        add(createEnemyStatUI("stab defence"), c); uiEnemyStats.add(MONSTER_STATS.STAB_DEFENCE); c.gridy++;
        add(createEnemyStatUI("slash defence"), c); uiEnemyStats.add(MONSTER_STATS.SLASH_DEFENCE); c.gridy++;
        add(createEnemyStatUI("crush defence"), c); uiEnemyStats.add(MONSTER_STATS.CRUSH_DEFENCE); c.gridy++;
        add(createEnemyStatUI("magic defence"), c); uiEnemyStats.add(MONSTER_STATS.MAGIC_DEFENCE); c.gridy++;
        add(createEnemyStatUI("ranged defence"), c); uiEnemyStats.add(MONSTER_STATS.RANGED_DEFENCE); c.gridy++;

        uiAttackSpeedLabel = new JLabel("Attack delay in ticks: ");
        add(uiAttackSpeedLabel, c);
        c.gridy++;
        uiAttackSpeedNumber = new JLabel();
        add(uiAttackSpeedNumber, c);
        c.gridy++;

        uiAttackStyleComboBox = new JComboBox();
        String[] styles = {"Stab", "Slash", "Crush"};
        for(String s : styles) uiAttackStyleComboBox.addItem(s);
        uiAttackStyleComboBox.addItemListener(e -> {
            if(e.getItem() == styles[0]) currentAttackStyle = ATTACK_STYLE.STAB;
            if(e.getItem() == styles[1]) currentAttackStyle = ATTACK_STYLE.SLASH;
            if(e.getItem() == styles[2]) currentAttackStyle = ATTACK_STYLE.CRUSH;
        });
        add(uiAttackStyleComboBox, c);
        c.gridy++;

        updateAll();
    }

    enum MONSTER_STATS{
        DEFENCE_LEVEL,
        MAGIC_LEVEL,
        STAB_DEFENCE,
        SLASH_DEFENCE,
        CRUSH_DEFENCE,
        MAGIC_DEFENCE,
        RANGED_DEFENCE
    }

    private JPanel createEnemyStatUI(String stat){
        JPanel panel;
        JLabel label;
        JTextField field;

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        label = new JLabel("Enemy " + stat + ": ");
        label.setFont(FontManager.getRunescapeSmallFont());

        field = new JTextField();
        uiEnemyStatFields.add(field);

        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.EAST);

        return panel;
    }

    private JPanel createAccuracyUI(String style){
        JPanel panel;
        JLabel label, number;

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        label = new JLabel("Accuracy " + style + ": ");
        label.setFont(FontManager.getRunescapeSmallFont());

        number = new JLabel("");
        number.setFont(FontManager.getRunescapeSmallFont());
        uiAccuracyNumbers.add(number);

        panel.add(label, BorderLayout.WEST);
        panel.add(number, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMaxHitUI(String style){
        JPanel panel;
        JLabel label, number;

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        label = new JLabel("Max hit " + style + ": ");
        label.setFont(FontManager.getRunescapeSmallFont());

        number = new JLabel("");
        number.setFont(FontManager.getRunescapeSmallFont());
        uiMaxHitNumbers.add(number);

        panel.add(label, BorderLayout.WEST);
        panel.add(number, BorderLayout.EAST);

        return panel;
    }

    private JPanel createDPMUI(String style){
        JPanel panel;
        JLabel label, number;

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        label = new JLabel("DPM " + style + ": ");
        label.setFont(FontManager.getRunescapeSmallFont());

        number = new JLabel("");
        number.setFont(FontManager.getRunescapeSmallFont());
        uiDPMNumbers.add(number);

        panel.add(label, BorderLayout.WEST);
        panel.add(number, BorderLayout.EAST);

        return panel;
    }

    private void updateAccuracies(){
        for(int i = 0; i < uiAccuracyNumbers.size(); i++){
            double number = 0.0;

            switch(uiStyles.get(i)){
                case MELEE:
                    number = calculateMeleeHitChance(currentAttackStyle);
                    break;
                case RANGED:
                    number = calculateRangedHitChance();
                    break;
                case MAGIC:
                    number = calculateMagicHitChance();
                    break;
            }

            uiAccuracyNumbers.get(i).setText(""+String.format("%.2f", number*100)+"%");
        }
    }

    private void updateMaxHits(){
        for(int i = 0; i < uiMaxHitNumbers.size(); i++){
            double number = 0.0;

            switch(uiStyles.get(i)){
                case MELEE:
                    number = calculateMeleeMaxHit();
                    break;
                case RANGED:
                    number = calculateRangedMaxHit();
                    break;
                case MAGIC:
                    break;
            }

            uiMaxHitNumbers.get(i).setText(""+String.format("%.4f", number));
        }
    }

    private void updateDPMs(){
        for(int i = 0; i < uiDPMNumbers.size(); i++){
            double number = 0.0;

            switch(uiStyles.get(i)){
                case MELEE:
                    number = calculateMeleeDPM(currentAttackStyle);
                    break;
                case RANGED:
                    number = calculateRangedDPM();
                    break;
                case MAGIC:
                    number = calculateMagicDPM();
                    break;
            }

            uiDPMNumbers.get(i).setText(""+String.format("%.2f", number));
        }
    }

    private void updateAll(){
        updateAccuracies();
        updateMaxHits();
        updateDPMs();
        uiAttackSpeedNumber.setText(""+ getAttackDelay());
    }

    enum COMBAT_STYLE {
        MELEE,
        RANGED,
        MAGIC
    }

    enum ATTACK_STYLE {
        SLASH,
        STAB,
        CRUSH,
        RANGED,
        MAGIC
    }

    enum EQUIPMENT_BONUS {
        MELEE_STR,
        RANGED_STR,
        MAGIC_DMG,

        SLASH_ATT,
        STAB_ATT,
        CRUSH_ATT,
        RANGE_ATT,
        MAGIC_ATT,

        SLASH_DEF,
        STAB_DEF,
        CRUSH_DEF,
        RANGE_DEF,
        MAGIC_DEF
    }

    private int getEquipmentBonus(EQUIPMENT_BONUS type){
        ItemContainer c = client.getItemContainer(InventoryID.EQUIPMENT);
        if (c == null) return -1;

        int bonus = 0;
        final Item[] items = c.getItems();
        for(Item item : items) {
            if (item == null) continue;
            ItemStats stats = itemManager.getItemStats(item.getId(), false);
            if(stats == null) continue;
            ItemEquipmentStats eqStats = stats.getEquipment();
            if(eqStats == null) continue;

            switch(type) {
                case MELEE_STR:
                    bonus += eqStats.getStr();
                    break;
                case RANGED_STR:
                    bonus += eqStats.getRstr();
                    break;
                case MAGIC_DMG:
                    bonus += eqStats.getMdmg();
                    break;
                case SLASH_ATT:
                    bonus += eqStats.getAslash();
                    break;
                case STAB_ATT:
                    bonus += eqStats.getAstab();
                    break;
                case CRUSH_ATT:
                    bonus += eqStats.getAcrush();
                    break;
                case RANGE_ATT:
                    bonus += eqStats.getArange();
                    break;
                case MAGIC_ATT:
                    bonus += eqStats.getAmagic();
                    break;
                case SLASH_DEF:
                    bonus += eqStats.getDslash();
                    break;
                case STAB_DEF:
                    bonus += eqStats.getDstab();
                    break;
                case CRUSH_DEF:
                    bonus += eqStats.getDcrush();
                    break;
                case RANGE_DEF:
                    bonus += eqStats.getDrange();
                    break;
                case MAGIC_DEF:
                    bonus += eqStats.getDmagic();
                    break;
            }
        }

        return bonus;
    }

    private int getAttackDelay(){
        ItemContainer c = client.getItemContainer(InventoryID.EQUIPMENT);
        if (c == null) return -1;

        final Item[] items = c.getItems();
        for(Item item : items) {
            if (item == null) continue;
            ItemStats stats = itemManager.getItemStats(item.getId(), false);
            if(stats == null) continue;
            ItemEquipmentStats eqStats = stats.getEquipment();
            if(eqStats == null) continue;

            if(eqStats.getSlot() != EquipmentInventorySlot.WEAPON.getSlotIdx()) continue;
            return eqStats.getAspeed();
        }

        return 4; // Unarmed assumed
    }

    // Taken partially from Bitterkoekje's spreadsheet, modified to be more in line with the strength calculation, but that may be wrong
    private int calculateBaseAttackRoll(int effLevel, double modifier, int styleBonus, double accuracyBonus){
        double effAtk = Math.floor(effLevel*modifier) + styleBonus;
        double baseAtk = 64.0 + effAtk * 64.0 + accuracyBonus * 8.0 + effAtk * accuracyBonus;
        int attackRoll = (int)Math.floor(baseAtk);

        return attackRoll;
    }

    private int calculateMeleeAttackRoll(ATTACK_STYLE attackStyle){
        int styleBonus = 0;         //TODO: get style bonus
        int potionBonus = 0;        //TODO: get potion bonus
        double prayerBonus = 1.0;   //TODO: get prayer bonus
        double otherBonus = 1.0;    //TODO: get other bonus
        int attackLevel = client.getBoostedSkillLevel(Skill.ATTACK);
        int attBonus = 0;
        switch(attackStyle){
            case STAB:
                attBonus = getEquipmentBonus(EQUIPMENT_BONUS.STAB_ATT);
                break;
            case SLASH:
                attBonus = getEquipmentBonus(EQUIPMENT_BONUS.SLASH_ATT);
                break;
            case CRUSH:
                attBonus = getEquipmentBonus(EQUIPMENT_BONUS.CRUSH_ATT);
                break;

        }

        return calculateBaseAttackRoll(attackLevel + potionBonus, prayerBonus * otherBonus, styleBonus, attBonus);
    }

    private int calculateRangedAttackRoll(){
        int styleBonus = 0;         //TODO: get style bonus
        int potionBonus = 0;        //TODO: get potion bonus
        double prayerBonus = 1.0;   //TODO: get prayer bonus
        double otherBonus = 1.0;    //TODO: get other bonus
        int rangedLevel = client.getBoostedSkillLevel(Skill.RANGED);
        int attBonus = getEquipmentBonus(EQUIPMENT_BONUS.RANGE_ATT);

        return calculateBaseAttackRoll(rangedLevel + potionBonus, prayerBonus * otherBonus, styleBonus, attBonus);
    }

    private int calculateMagicAttackRoll(){
        int styleBonus = 0;         //TODO: get style bonus
        int potionBonus = 0;        //TODO: get potion bonus
        double prayerBonus = 1.0;   //TODO: get prayer bonus
        double otherBonus = 1.0;    //TODO: get other bonus
        int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
        int attBonus = getEquipmentBonus(EQUIPMENT_BONUS.MAGIC_ATT);

        return calculateBaseAttackRoll(magicLevel + potionBonus, prayerBonus * otherBonus, styleBonus, attBonus);
    }

    // Taken partially from Bitterkoekje's spreadsheet, modified to be more in line with the strength calculation, but that may be wrong
    private int calculateBaseDefenceRoll(int effLevel, double modifier, int styleBonus, int defenceBonus){
        double effDef = Math.floor(effLevel*modifier) + styleBonus;
        double baseDef = 64.0 + effDef * 64.0 + defenceBonus * 8.0 + effDef * defenceBonus;
        int defenceRoll = (int)Math.floor(baseDef);

        return defenceRoll;
    }

    // TODO: Basically everything
    private int calculateNPCDefenceRoll(int defenceLevel, int defenceBonus){
        return calculateBaseDefenceRoll(defenceLevel, 1.0, 0, defenceBonus);
    }

    // Taken from Bitterkoekje's spreadsheet, maybe check - the 2 is suspicious, can the defence roll 0?
    private double calculateHitChance(int attackRoll, int defenceRoll){
        if(attackRoll > defenceRoll){
            return 1.0 - (defenceRoll+2.0)/(2.0*(attackRoll+1.0));
        }
        else{
            return attackRoll/(2.0*(defenceRoll+1.0));
        }
    }

    private double calculateBaseMaxHit(int effLevel, double modifier, int styleBonus, double strengthBonus, boolean precise){
        double effStr = Math.floor(effLevel*modifier) + styleBonus;
        double baseDamage = 1.3 + effStr / 10.0 + strengthBonus / 80.0 + effStr * strengthBonus / 640.0;
        double maxHit = baseDamage;
        if(!precise) maxHit = Math.floor(maxHit);

        return maxHit;
    }

    private double calculateMeleeMaxHit(boolean precise){
        int styleBonus = 0;         //TODO: get style bonus
        int potionBonus = 0;        //TODO: get potion bonus
        double prayerBonus = 1.0;   //TODO: get prayer bonus
        double otherBonus = 1.0;    //TODO: get other bonus
        int strengthLevel = client.getBoostedSkillLevel(Skill.STRENGTH);
        int strBonus = getEquipmentBonus(EQUIPMENT_BONUS.MELEE_STR);

        return calculateBaseMaxHit(strengthLevel + potionBonus, prayerBonus * otherBonus, styleBonus, strBonus, precise);
    }

    private double calculateRangedMaxHit(boolean precise){
        int styleBonus = 0;         //TODO: get style bonus
        int potionBonus = 0;        //TODO: get potion bonus
        double prayerBonus = 1.0;   //TODO: get prayer bonus
        double otherBonus = 1.0;    //TODO: get other bonus
        int rangedLevel = client.getBoostedSkillLevel(Skill.RANGED);
        int strBonus = getEquipmentBonus(EQUIPMENT_BONUS.RANGED_STR);

        return calculateBaseMaxHit(rangedLevel + potionBonus, prayerBonus * otherBonus, styleBonus, strBonus, precise);
    }

    private double calculateMeleeMaxHit() {
        return calculateMeleeMaxHit(true);
    }

    private double calculateRangedMaxHit() {
        return calculateRangedMaxHit(true);
    }

    private double calculateBaseDPM(double maxHit, double hitChance, int attackDelay){
        return hitChance * maxHit / 2.0 / attackDelay * 100.0;
    }

    private int getEnemyStat(MONSTER_STATS stat) throws NumberFormatException {
        return Integer.parseInt(uiEnemyStatFields.get(uiEnemyStats.indexOf(stat)).getText());
    }

    private int calculateEnemyDefenceRoll(ATTACK_STYLE attackStyle){
        int enemyDefenceLevel = 1;
        try{
            if(attackStyle == ATTACK_STYLE.MAGIC){
                enemyDefenceLevel = getEnemyStat(MONSTER_STATS.MAGIC_LEVEL);
            }
            else {
                enemyDefenceLevel = getEnemyStat(MONSTER_STATS.DEFENCE_LEVEL);
            }
        } catch(NumberFormatException e){}
        int enemyDefenceBonus = 0;
        try{
            switch(attackStyle) {
                case STAB:
                    enemyDefenceBonus = getEnemyStat(MONSTER_STATS.STAB_DEFENCE);
                    break;
                case SLASH:
                    enemyDefenceBonus = getEnemyStat(MONSTER_STATS.SLASH_DEFENCE);
                    break;
                case CRUSH:
                    enemyDefenceBonus = getEnemyStat(MONSTER_STATS.CRUSH_DEFENCE);
                case MAGIC:
                    enemyDefenceBonus = getEnemyStat(MONSTER_STATS.MAGIC_DEFENCE);
                case RANGED:
                    enemyDefenceBonus = getEnemyStat(MONSTER_STATS.RANGED_DEFENCE);
            }
        } catch(NumberFormatException e){}

        return calculateNPCDefenceRoll(enemyDefenceLevel, enemyDefenceBonus);
    }

    private double calculateMeleeHitChance(ATTACK_STYLE attackStyle){
        return calculateHitChance(calculateMeleeAttackRoll(attackStyle), calculateEnemyDefenceRoll(attackStyle));
    }

    private double calculateRangedHitChance(){
        return calculateHitChance(calculateRangedAttackRoll(), calculateEnemyDefenceRoll(ATTACK_STYLE.RANGED));
    }

    private double calculateMagicHitChance(){
        return calculateHitChance(calculateMagicAttackRoll(), calculateEnemyDefenceRoll(ATTACK_STYLE.MAGIC));
    }

    private double calculateMeleeDPM(ATTACK_STYLE attackStyle){
        return calculateBaseDPM(calculateMeleeMaxHit(), calculateMeleeHitChance(attackStyle), getAttackDelay());
    }

    private double calculateRangedDPM(){
        return calculateBaseDPM(calculateRangedMaxHit(), calculateRangedHitChance(), getAttackDelay());
    }

    private int getMagicAttackDelay(){
        return 5;
    }

    private int calculateMagicMaxHit(){
        try {
            return Integer.parseInt(uiMagicMaxHitField.getText());
        } catch (Exception e) {}
        return 0;
    }

    private double calculateMagicDPM(){
        return calculateBaseDPM(calculateMagicMaxHit(), calculateMagicHitChance(), getMagicAttackDelay());
    }
}
