package com.smanzana.dungeonmaster.pawn;

import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.session.datums.data.DataCompatible;
import com.smanzana.dungeonmaster.session.datums.data.DataNode;
import com.smanzana.dungeonmaster.utils.Notable;
import com.smanzana.dungeonmaster.utils.NoteUtil;
import com.smanzana.dungeonmaster.utils.StatSet;

/**
 * Base entity class.
 * A Pawn can be a player character, an NPC, and enemy, etc
 * @author Skyler
 *
 */
public abstract class Pawn implements Notable, DataCompatible {
	
	/**
	 * Packages all set-able stats.
	 * When applied, only changes values with new values present
	 * @author Skyler
	 *
	 */
	public static class PawnOverlay {
		private Boolean killable;
		private Boolean dead;
		private Integer hp;
		private Integer maxhp;
		private Integer mp;
		private Integer maxmp;
		private Integer stamina;
		private Integer maxstamina;
		private Map<Attributes, Integer> scores;
		
		public PawnOverlay() {
			scores = new EnumMap<>(Attributes.class);
		}

		public PawnOverlay(Boolean killable, Boolean dead, Integer hp, Integer maxhp, Integer mp, Integer maxmp,
				Integer stamina, Integer maxstamina, Map<Attributes, Integer> scores) {
			this.killable = killable;
			this.dead = dead;
			this.hp = hp;
			this.maxhp = maxhp;
			this.mp = mp;
			this.maxmp = maxmp;
			this.stamina = stamina;
			this.maxstamina = maxstamina;
			this.scores = scores;
		}
		
		public PawnOverlay killable(boolean killable) {
			this.killable = killable;
			return this;
		}

		public PawnOverlay dead(boolean dead) {
			this.dead = dead;
			return this;
		}
		
		public PawnOverlay hp(int hp) {
			this.hp = hp;
			return this;
		}
		
		public PawnOverlay maxhp(int maxhp) {
			this.maxhp = maxhp;
			return this;
		}
		
		public PawnOverlay mp(int mp) {
			this.mp = mp;
			return this;
		}
		
		public PawnOverlay maxmp(int maxmp) {
			this.maxmp = maxmp;
			return this;
		}
		
		public PawnOverlay stamina(int stamina) {
			this.stamina = stamina;
			return this;
		}
		
		public PawnOverlay maxstamina(int maxstamina) {
			this.maxstamina = maxstamina;
			return this;
		}
		
		public PawnOverlay score(Attributes attr, int score) {
			this.scores.put(attr, score);
			return this;
		}
		
	}
	
	protected boolean canDie;
	protected boolean dead;
	protected StatSet stats;
	
	private List<String> notes;
		
	/**
	 * Creates generic Pawn that cannot die.
	 */
	public Pawn()
	{
		dead = false;
		canDie = false;
		notes = new LinkedList<>();
		this.stats = new StatSet();
	}
	
	/**
	 * Creates a pawn with the given max attributes.
	 * @param maxHealth
	 * @param maxMana
	 */
	public Pawn(int maxHealth, int maxMana)
	{
		this();
		canDie = true;
		stats.setHealth(maxHealth);
		stats.setMaxHealth(maxHealth);
		stats.setMana(maxMana);
		stats.setMaxMana(maxMana);
	}
	
	public int getHealth() {
		return stats.getHealth();
	}
	
	public int getMaxHealth() {
		return stats.getMaxHealth();
	}
	
	public int getMana() {
		return stats.getMana();
	}
	
	public int getMaxMana() {
		return stats.getMaxMana();
	}
	
	public int getStamina() {
		return stats.getStamina();
	}
	
	public int getMaxStamina() {
		return stats.getMaxStamina();
	}
	
	public int getAbilityScore(Attributes attrib) {
		return stats.getAbilityScore(attrib);
	}
	
	public boolean getCanDie()
	{
		return canDie;
	}
	
	/**
	 * Damages the pawn by the given amount.
	 * Checks health and, if they can die and did, returns true.
	 * @param amount
	 * @return true when the entity dies.
	 */
	public abstract boolean damage(Pawn source, int amount);
	
	/**
	 * Heals conventually. This could be interpreted as damage to undead.
	 * @param amount
	 * @return true if the pawn dies
	 */
	public abstract boolean heal(Pawn source, int amount);
	
	public void restoreMana(Pawn source, int amount) {
		stats.addMana(amount);
	}
	
	public void restoreStamina(Pawn source, int amount) {
		stats.addStamina(amount);
	}
	
	@Override
	public void addNote(String note) {
		notes.add(note);
	}
	
	@Override
	public void clearNotes() {
		notes.clear();
	}
	
	@Override
	public Collection<String> getNotes() {
		return notes;
	}
	
	public void applyOverlay(PawnOverlay overlay) {
		if (overlay.killable != null)
			this.canDie = overlay.killable;
		
		if (overlay.dead != null)
			this.dead = overlay.dead;
		
		if (overlay.hp != null)
			this.stats.setHealth(overlay.hp);
		
		if (overlay.maxhp != null)
			this.stats.setMaxHealth(overlay.maxhp);
		
		if (overlay.mp != null)
			this.stats.setMana(overlay.mp);
		
		if (overlay.maxmp != null)
			this.stats.setMaxMana(overlay.maxmp);
		
		if (overlay.stamina != null)
			this.stats.setStamina(overlay.stamina);
		
		if (overlay.maxstamina != null)
			this.stats.setMaxStamina(overlay.maxstamina);
		
		if (overlay.scores != null && !overlay.scores.isEmpty()) {
			for (Attributes attr : overlay.scores.keySet())
				if (overlay.scores.get(attr) != null)
					stats.setAbilityScore(attr, overlay.scores.get(attr));
		}
	}
	
	/**
	 * Creates a DataNode and writes in base objects.
	 * Suggested do this before adding subclass objects
	 * @param key
	 * @return
	 */
	protected DataNode writeBase(String key) {
		List<DataNode> list = new LinkedList<>();
		
		list.add(new DataNode("killable", this.canDie + "", null));
		list.add(new DataNode("dead", this.dead + "", null));
		list.add(this.stats.write("stats"));
		list.add(new DataNode("notes", NoteUtil.serializeNotes(notes), null));
		
		return new DataNode(key, null, list);
	}
	
	protected void readBase(DataNode root) {
		DataNode node;
		this.canDie = false;
		this.dead = false;
		
		if (null != (node = root.getChild("killable"))) {
			this.canDie = DataNode.parseBool(node);
		}
		
		if (null != (node = root.getChild("dead"))) {
			this.canDie = DataNode.parseBool(node);
		}
		
		if (null != (node = root.getChild("stats"))) {
			this.stats.load(node);
		}
		
		if (null != (node = root.getChild("notes"))) {
			this.notes = NoteUtil.deserializeNotes(node.getValue());
		}
	}
}
