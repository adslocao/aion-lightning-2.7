package com.aionemu.gameserver.command.admin;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.utils.PacketSendUtility;

//.speed <valeur>

public class CmdSpeed extends BaseCommand implements StatOwner {

	
    public void execute(Player admin, String... params) {
    	if (params.length < 1) {
			showHelp(admin);
			return;
		}

        int parameter = ParseInteger(params[0]);

        if (parameter < 0 || parameter > 5000) {
            PacketSendUtility.sendMessage(admin, "Valid values are in 0-5000 range");
            return;
        }

        admin.getGameStats().endEffect(this);
        if (parameter > 0) {
	        List<IStatFunction> functions = new ArrayList<IStatFunction>();
	        functions.add(new SpeedFunction(StatEnum.SPEED, parameter));
	        functions.add(new SpeedFunction(StatEnum.FLY_SPEED, parameter));
	        admin.getGameStats().addEffect(this, functions);
        }

        PacketSendUtility.broadcastPacket(admin, new SM_EMOTION(admin, EmotionType.START_EMOTE2, 0, 0), true);
    }

    class SpeedFunction extends StatFunction {

        static final int speed = 6000;
        static final int flyspeed = 9000;
        int modifier = 1;

        SpeedFunction(StatEnum stat, int modifier) {
            this.stat = stat;
            this.modifier = modifier;
        }

        @Override
        public void apply(Stat2 stat) {
            switch (this.stat) {
                case SPEED:
                    stat.setBase(speed + (speed * modifier) / 100);
                    break;
                case FLY_SPEED:
                    stat.setBase(flyspeed + (flyspeed * modifier) / 100);
                    break;
            }
        }

    }
}
