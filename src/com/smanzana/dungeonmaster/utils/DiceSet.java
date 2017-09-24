package com.smanzana.dungeonmaster.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.smanzana.dungeonmaster.session.datums.data.DataNode;

/**
 * Multiple dice
 * @author Skyler
 *
 */
public class DiceSet implements ValueSpecifier {
	
	private List<Dice> dice;
	
	public DiceSet() {
		dice = new LinkedList<>();
	}
	
	public DiceSet(Dice ...dice) {
		this();
		for (Dice d : dice)
			this.dice.add(d);
	}
	
	public int roll() {
		return this.roll(new Random());
	}
	
	public int roll(Random rand) {
		int sum = 0;
		
		for (Dice d : this.dice)
			sum += d.roll(rand);
		
		return sum;
	}

	@Override
	public void load(DataNode root) {
		String serial = root.getValue();
		dice.clear();
		
		for (String sub : serial.split(",")) {
			Dice d = new Dice(0);
			d.load(new DataNode("dummy", sub.trim(), null));
			dice.add(d);
		}
	}

	@Override
	public DataNode write(String key) {
		// list of Dice, Dice, Dice
		String buf = "";
		
		for (Dice d : dice)
			buf += d.write("dummy").getValue() + ", ";
		
		return new DataNode(key, buf, null);
	}

	@Override
	public int fetchValue() {
		return roll();
	}
}
