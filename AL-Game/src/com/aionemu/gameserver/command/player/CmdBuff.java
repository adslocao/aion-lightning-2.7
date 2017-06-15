package com.aionemu.gameserver.command.player;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.actions.AbstractItemAction;
import com.aionemu.gameserver.model.templates.item.actions.SkillUseAction;
import com.aionemu.gameserver.services.TranslationService;

public class CmdBuff extends BaseCommand {
	
	private static Map<String, List<Integer>> scrollsList = new HashMap<String, List<Integer>>();
	private static String elemParam = "elem";
	private static String vitAtkParam = "va";
	private static String vitIncParam = "vi";
	private static String critPhysParam = "cp";
	private static String critMagParam = "cm";
	
	static {
		// Scroll Elem
		List<Integer> elemList = new ArrayList<Integer>();
		elemList.add(164000114); // Fine Fireproof Scroll
		elemList.add(164000115); // Fine Earthproof Scroll
		elemList.add(164000116); // Fine Waterproof Scroll
		elemList.add(164000117); // Fine Windproof Scroll
		scrollsList.put(elemParam, elemList);

		// Scroll Vit Atk
		List<Integer> vitAtkList = new ArrayList<Integer>();
		vitAtkList.add(164000073); // Greater Courage Scroll
		scrollsList.put(vitAtkParam, vitAtkList);
		
		// Scroll Vit Incant
		List<Integer> vitIncList = new ArrayList<Integer>();
		vitIncList.add(164000134); // Greater Awakening Scroll
		scrollsList.put(vitIncParam, vitIncList);
		
		// Scroll Crit Phys
		List<Integer> critPhysList = new ArrayList<Integer>();
		critPhysList.add(164000118); // Major Crit Strike Scroll
		scrollsList.put(critPhysParam, critPhysList);
		
		// Scroll Crit Mag
		List<Integer> critMagList = new ArrayList<Integer>();
		critMagList.add(164000122); // Major Crit Spell Scroll
		scrollsList.put(critMagParam, critMagList);
	}
	
	public void execute(Player player, String... params) {
		
		List<Integer> allparcho = new ArrayList<Integer>();
		Set<String> paramsClean = new HashSet<String>();
		for (String param : params) {
			paramsClean.add(param);
		}
		
		int error = 0;
		for (String string : paramsClean) {
			List<Integer> parchos = scrollsList.get(string);
			if(parchos == null){
				String message = TranslationService.BUFF_INVALID_PARAMETER.toString(player, string);
				sendCommandMessage(player, message);
				error++;
			}
			else {
				allparcho.addAll(parchos);
			}
		}
		
		allparcho.add(164000076); // Greater Running Scroll

		for (Integer scroll : allparcho) {
			Item targetItem = player.getInventory().getFirstItemByItemId(scroll);
			if (targetItem == null) {
				String message = TranslationService.BUFF_SCROLL_MISSING.toString(player, String.valueOf(scroll));
				sendCommandMessage(player, message);
				continue;
			}
			for (AbstractItemAction action : targetItem.getItemTemplate().getActions().getItemActions()) {
				if (action instanceof SkillUseAction) {
					if (!action.canAct(player, targetItem, null)) {
						return;
					}
					action.act(player, targetItem, null);
					break;
				}
			}
		}
		String message = TranslationService.BUFF_APPLIED_SUCCESS.toString(player);
		sendCommandMessage(player, message);
		if (error > 0) {
			showHelp(player);
		}
	}
}