package com.aionemu.gameserver.services;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;

public enum TranslationService {

	COMMAND_ERROR_GENERAL (
			"[GiveMe] Une erreur s'est produite. Veuillez contacter un administrateur.",
			"[GiveMe] An error has occured. Please contact an administrator."
			),
	GENERAL_ERROR_DB (
			"[Shop][Error] Une erreur de base de donn\u00E9es est survenue. Veuillez contacter un adminitrateur.",
			"[Shop][Error] A database error has occurred. Please contact an Administrator"
			),
	SHOP_MAILBOX_FULL (
			"[Shop] Votre boite aux lettres est pleine, impossible de recevoir les objets command\u00E9s.", 
			"[Shop] Your mailbox is full, unable to receive WebShop items."),
	SHOP_NO_PENDING_ORDER (
			"[Shop] Aucun achat boutique n'est en attente pour %s.",
			"[Shop] No WebShop order found for player %s."
			),
	SHOP_ORDER_RECEIVED (
			"[Shop] Votre achat effectu\u00E9 pour %s a bien \u00E9t\u00E9 r\u00E9cup\u00E9r\u00E9.",
			"[Shop] Your WebShop order for %s has been correctly sent."
			),
	SHOP_ORDERS_RECEIVED (
			"[Shop] Les %s achats effectu\u00E9s pour %s ont bien \u00E9te r\u00E9cup\u00E9r\u00E9s.",
			"[Shop] The %s WebShop orders for %s have been correctly sent."
			),
	SHOP_MAIL_TITLE (
			"Voici votre commande",
			"Here is your command"
			),
	SHOP_THANKYOU_ORDER (
			"\nNous vous remercions pour votre achat.",
			"\nThank you for your order."
			),
	SHOP_MULTIPLE_MAIL (
			" (un seul courrier ne suffisait pas pour cette commande ; veuillez v\u00E9rifier vos autres courriers).",
			" (one mail was not enough, please check your mails)."
			),
	LOCALE_SHOW_VALUE (
			"[Locale] Votre locale est actuellement \"%s\"",
			"[Locale] Your locale is currently set to \"%s\""
			),
	LOCALE_UPDATED (
			"[Locale] Votre locale a \u00E9t\u00E9 mise \u00E0 jour sur votre compte",
			"[Locale] Your account locale has been successfully updated"
			),
	LOCALE_ERR_UNKNOWN_TYPE (
			"[Locale] Seules les valeurs \"fr\" et \"en\" sont valides",
			"[Locale] You can only change your locale with \"fr\" and \"en\" value"
			),
	// DSP = DropShopPoint service
	DSP_ERROR_LEVEL_GROUP (
			"[Loot] Le niveau d'au moins un membre de votre groupe est trop \u00E9lev\u00E9 pour remporter des %toll.",
			"[Loot] Level of at least one of your teammates is too high for winning %toll."
			),
	DSP_ERROR_LEVEL_PLAYER (
			"[Loot] Votre niveau est trop \u00E9lev\u00E9 pour remporter des %toll.",
			"[Loot] Your level is too high for winning %toll."
			),
	DSP_ERROR_ADD_ERROR (
			"[Loot] Une erreur est survenue lors de l'ajout de vos %toll. Veuillez contacter un administrateur.",
			"[Loot] An error has occured when adding your %toll. Please contact administrator."
			),
	DSP_NO_LUCK_NO_WIN (
			"[Loot] Dommage ! Vous n'avez pas eu la chance d'obtenir des %toll sur ce NPC.",
			"[Loot] Too bad! You didn't have chance to win %toll on this NPC."
			),
	DSP_YOU_WIN (
			"[Loot] F\u00E9licitations ! En tuant ce NPC, vous avez remport\u00E9 %s %toll !",
			"[Loot] Congratulations! By killing this NPC, you win %s %toll!"
			),
	// Player command .giveme (for crafting)
	GIVE_ME_ERROR_ID (
			"[GiveMe] L'ID indiqu\u00E9 n'est pas un ID d'objet valide. Veuillez r\u00E9essayer.",
			"[GiveMe] This ID is not a valid item ID. Please try again."
			),
	GIVE_ME_ERROR_QTY (
			"[GiveMe] La quantit\u00E9 souhait\u00E9e n'est pas valide. Veuillez r\u00E9essayer.",
			"[GiveMe] Required quantity is not valid. Please try again."
			),
	GIVE_ME_NOT_ALLOWED (
			"[GiveMe] Cet objet ne peut \u00EAtre obtenu avec cette commande. V\u00E9rifiez si vous pouvez l'acheter ou le crafter.",
			"[GiveMe] This item cannot be given with this command. Check if you can buy or craft it."
			),
	GIVE_ME_SUCCESS (
			"[GiveMe] Vous avez obtenu %s [item:%s]",
			"[GiveMe] You received %s [item:%s]"
			),
	// Dominated faction bonus
	DFB_LOGIN_ANNOUNCE (
			"[Bonus] Votre faction b\u00E9n\u00E9ficie des statistiques bonus suivantes :",
			"[Bonus] Your faction has these bonus statistics:"
			),
	DFB_LOGIN_HUNTING (
			"Bonus d'XP de chasse : +%s",
			"Hunting XP Bonus: +%s"
			),
	DFB_LOGIN_QUEST (
			"Bonus d'XP de qu\u00EA : +%s",
			"Quest XP Bonus: +%s"
			),
	DFB_LOGIN_CRAFT (
			"Bonus d'XP de craft : +%s",
			"Craft XP Bonus: +%s"
			),
	DFB_LOGIN_GATHER (
			"Bonus d'XP de r\u00E9colte : +%s",
			"Gathering XP Bonus: +%s"
			),
	DFB_LOGIN_AP (
			"Bonus de PA PvP : +%s",
			"PvP AP Bonus: +%s"
			),
	DFB_LOGIN_ATTACK (
			"D\u00E9g\u00E2ts PvP inflig\u00E9s : +%s",
			"PvP attack Bonus: +%s"
			),
	DFB_LOGIN_DEFENSE (
			"D\u00E9fense PvP : +%s",
			"PvP defence Bonus: +%s"
			),
	// Get tolls with Kinah
	TOLL_ERROR_KINAH (
			"[TOLL] Le montant en Kinah n'est pas valide",
			"[TOLL] Kinah amount is not correct"
			),
	TOLL_ERROR_NOTENOUGHKINAH (
			"[TOLL] Vous n'avez pas assez de Kinah. Un peu de farm, que diable !",
			"[TOLL] You don't have enough Kinah. Do more farm, omg !"
			),
	TOLL_ERROR_TOLL (
			"[TOLL] Le montant en %toll n'est pas valide",
			"[TOLL] %toll amount is not correct"
			),
	TOLL_ERROR_COST (
			"[TOLL] Le montant en Kinah ne correspond pas au montant en %toll",
			"[TOLL] Kinah amount does not match with %toll amount"
			),
	TOLL_SUCCESS (
			"[TOLL] Vous avez d\u00E9pens\u00E9 %s Kinahs pour obtenir %s %toll",
			"[TOLL] You paid %s Kinahs to get %s %toll"
			),
	// Buff player
	BUFF_SCROLL_MISSING (
			"[Buff] Le [item:%s] n'a pas \u00E9t\u00E9 trouv\u00E9 dans votre inventaire.",
			"[Buff] [item:%s] was not found in your inventory."
			),
	BUFF_INVALID_PARAMETER (
			"[Buff] Le param\u00E8tre \"%s\" n'a pas \u00E9t\u00E9 reconnu.",
			"[Buff] Parameter \"%s\" was not recognized."
			),
	BUFF_APPLIED_SUCCESS (
			"[Buff] Tous les buffs demand\u00E9s ont \u00E9t\u00E9 appliqu\u00E9s avec succ\u00E8s.",
			"[Buff] All requested buffs have been applied."
			),
	// Recharger AI
	RECHARGER_LIFE (
			"[Recharger] Vos PV et vos PM ont \u00E9t\u00E9 restaur\u00E9s.",
			"[Recharger] Your HP and MP have been restored."
			),
	RECHARGER_DOTS (
			"[Recharger] Vos effets ont \u00E9t\u00E9 supprim\u00E9s.",
			"[Recharger] Your dots and abnormal effects have been removed."
			),
	RECHARGER_DP (
			"[Recharger] Votre Energie Divine a \u00E9t\u00E9 recharg\u00E9e.",
			"[Recharger] Your Divine Power has been recharged."
			),
	RECHARGER_FLY (
			"[Recharger] Votre Temps de vol a \u00E9t\u00E9 restaur\u00E9.",
			"[Recharger] Your fly time has been restored."
			),
	RECHARGER_SKILL (
			"[Recharger] Vos CD de skills ont \u00E9t\u00E9 supprim\u00E9s.",
			"[Recharger] Your skill cooldown have been removed."
			),
	RECHARGER_FIGHT (
			"C'est fait, %s. Vous pouvez retourner au combat.",
			"Done %s. You can now go back to fight."
			),
	RECURSIVEADD_MESSAGE (
			"Votre cube se met \u00E0 trembler. Un objet myst\u00E9rieux y est apparu...",
			"Your cube start to shake. A mysterious object has appeared..."
			),
	COIN_FOUNTAIN_START (
			"La fontaine a bien re\u00E7u votre pr\u00E9sent...",
			"Fountain get your gift..."
			),
	COIN_FOUNTAIN_PLATINUM (
			"Bien jou\u00E9 ! Vous avez gagn\u00E9 une m\u00E9daille de platine !",
			"Nice job! You won a platinum medal!"
			),
	COIN_FOUNTAIN_GOLD (
			"Bien jou\u00E9 ! Vous avez gagn\u00E9 deux m\u00E9dailles en or !",
			"Nice job! You won two gold medals!"
			),
	COIN_FOUNTAIN_RUSTED (
			"Quel dommage ! Vous avez obtenu une m\u00E9daille rouill\u00E9e :(",
			"Too bad! You won a rusted medal :("
			),
	NEW_PLAYER_BONUS_AP (
			"Vous \u00EAtes nouveau sur le serveur, vous b\u00E9n\u00E9ficiez d'un bonus AP x%s pendant encore %s jour(s)",
			"You're a newbie on this server, you get an AP bonus x%s for another %s day(s)"
			),
	;
	
	private String fr = "";
	private String en = "";
	
	TranslationService(String fr, String en) {
		this.fr = fr;
		this.en = en;
	}
	
	public String toString(Player player, String... params){
		String enMessage = en;
		String frMessage = fr;
		
		String enTollName = CustomConfig.TOLL_NAME_EN;
		String frTollName = CustomConfig.TOLL_NAME_FR;
		
		for (String param : params) {
			enMessage = enMessage.replaceFirst("%s", param);
			frMessage = frMessage.replaceFirst("%s", param);
		}
		
		enMessage = enMessage.replaceFirst("%toll", enTollName);
		frMessage = frMessage.replaceFirst("%toll", frTollName);
		
		String locale = player.getCommonData().getLocale();
		
		if(locale.contains("en"))
			return enMessage;
		else if(locale.contains("fr"))
			return frMessage;
		else
			return enMessage;
	}
	
}