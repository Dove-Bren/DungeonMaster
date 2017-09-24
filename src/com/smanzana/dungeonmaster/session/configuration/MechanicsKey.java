package com.smanzana.dungeonmaster.session.configuration;

import java.util.LinkedList;
import java.util.List;

public enum MechanicsKey {
	// Interaction
	ALLOW_PC_CONTROL("Allows connected players to perform actions", "When disabled, connected players can only view data"),
	
	// Stats
	USE_ABILITY_SCORES("Enables ability scores", "Ability Scores are strength, constitution, etc.","When turned off, roll, relevant requirement, and bonus mechanics are turned off."),
	USE_HEALTH("Enables health tracking", "When disabled, all notion of 'health' and 'how dead' something is are maintained", "by the DM"),
	USE_MANA("Enables mana use and tracking"),
	USE_STAMINA("Enables stamina tracking"),
	USE_OFFDEF("Enables offense and defense mechanics.", "This could mean AC or attack bonus and reduction", "on your combat settings"),
	USE_AC("Offense and Defense follow traditional AC/HD rules", "In short, AC is how high someone must roll to land a hit. HD/Attack bonus", "is the bonus to their roll", "When off, attack/defense are bonus/penalty to damage dealt.", "Requires USE_OFFDEF"),
	
	// Progression
	USE_LEVELS("Turns on level tracking", "When turned off, leveling'up (including adjusting stats) must be done", "entirely by the DM"),
	USE_XP("Turns on XP tracking", "When disabled, DM is responsible for triggering 'level ups' (if enabled)", "Requires USE_LEVELS"),
	LEVEL_ABILITY_SCORES("Does leveling increase ability scores?", "Amounts are configured in rolltables.cfg", "Requires USE_LEVELS, USE_ABILITY_SCORES"),
	HP_FROM_CONSTITUTION("Is max health based off of constitution?", "Configure scale below", "Requires USE_ABILITY_SCORES, USE_HEALTH"),
	MP_FROM_WISDOM("Is max mana based off of wisdom?", "Configure scale below", "Requires USE_ABILITY_SCORES, USE_MANA"),
	STAMINA_FROM_DEXTERITY("Is max stamina based off of dexterity?", "Configure scale below", "Requires USE_ABILITY_SCORES, USE_STAMINA"),
	
	// Setting (place, location, etc)
	// TODO
	
	// Combat
	USE_COMBAT_ACTIONS("Enables combat actions", "Combat actions are actions you perform in combat, such as swinging your weapon", "When off, DM does all work of dealing damage, applying status effects, etc"),
	CONFIRM_PC_ACTION("Requires PC action be confirmed by DM", "Prompts the DM before combat actions are performed", "Requires ALLOW_PC_CONTROL, USE_COMBAT_ACTIONS"),
	USE_DAMAGE_CALC("Do combat actions use regular damage calculation?", "When disabled, DM inputs damage (or manually rolls)", "Requires USE_COMBAT_ACTIONS, USE_HEALTH"),
	ENABLE_COMBAT_BONUS("Enables combat bonuses.", "Bonuses are defined in CombatBonus.cfg.", "Players get a bonus based off their attributes and the weapon/attack they use", "Requires USE_COMBAT_ACTIONS, USE_HEALTH"),
	USE_ENEMY_ACTIONS("Enemies use preconfigured actions", "When off, DM does all parts of an enemy turn by hand", "Requires USE_COMBAT_ACTIONS"),
	ENEMY_TARGET_RANDOM("If set, enemies automatically target a random ally", "Takes precedence over ENEMY_TARGET_WEAKEST"),
	ENEMY_TARGET_WEAKEST("Enemies prefer to attack character with least hpXdefense hash", "If ENEMY_TARGET_RANDOM is set, this is not used", "Requires USE_HEALTH or USE_DEFENSE"),
	DROP_LOOT("Enemies drop their inventories on death"),
	AUTO_LOOT("Is loot dropped automatically picked up?", "If false, players are given a loot action (if USE_ACTIONS). DM can also trigger a loot", "Requires DROP_LOOT"),
	LOOT_SPLIT("When looting, equipment is displayed and divied up to all players", "Splitting rules are defined by LOOT_SPLIT_RANDOM and LOOT_SPLIT_ROLL", "When false, loot is given to person who looted (or DM picks)", "Requires DROP_LOOT"),
	LOOT_SPLIT_RANDOM("When looting, random players are picked as recipients.", "When LOOT_SPLIT_ROLL is true, random players are picked to roll for the items", "Requires DROP_LOOT"),
	LOOT_SPLIT_ROLL("When looting, players must roll to get loot", "Players roll against eachother. Highest roller gets the item", "Requires DROP_LOOT"),
	USE_INITIATIVE("Automatically roll for turn order when combat starts", "Will prompt DM for order if false"),
	
	// Actions
	USE_ACTIONS("Enables or disables all actions", "When false, DM is responsible for ALL modification of inventory, effects, etc."),
	AUTO_ACTIONS("Actions are pulled from relevant NPCs and objects when possible", "DM can still add manual actions", "Example: NPCs that sell will auto create a 'buy/sell' action", "Requires USE_ACTIONS"),
	
	// Magic
	USE_SPELL_SLOTS("Casting spells consume spell slots", "If USE_MANA is true, spells will also cost mana.", "Requires USE_ACTIONS"),
	USE_SPELL_SLOTS_LARGER("If the appropriate spell slot is not available to cast a spell, should", "a large one be used instead?", "Requires USE_SPELL_SLOTS"),
	
	// Items
	ITEMS_DROPABLE("Can PCs drop items?", "If false, items must be removed by DM", "Requires ALLOW_PC_CONTROL"),
	ITEMS_TRADING("Enables buy/sell of items", "Only NPCs marked as merchants will present this action", "If false, DM can remove item and add gold manually", "Requires ALLOW_PC_CONTROL"),
	ITEMS_SELLABLE("Can PCs sell items?", "If false, they can't. If ITEMS_TRADING is true, PCs can still buy", "Requires ALLOW_PC_CONTROL, ITEMS_TRADING"),
	DURABILITY_ENABLED("Enables all other durability options"),
	EQUIPMENT_USE_DURABILITY("In normal use, does equipment lose durability?", "This means when struck, when striking, etc", "If you want to hand-control durabilty, set to false"),
	EQUIP_RESTRICT_LEVEL("Equipment performs a level check before being able to be used"),
	EQUIP_RESTRICT_SCORE("Equipment performs an ability score check before being able to be used");
	
	private String[] comments;
	
	private MechanicsKey(String ...strings) {
		this.comments = strings;
	}
	
	public List<String> getComments() {
		List<String> list = new LinkedList<String>();
		
		for (String s : comments) {
			list.add(s);
		}
		
		return list;
	}
}
