package ai.instance.darkPoeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Npc;

import ai.AggressiveNpcAI2;

/**
 * @author Medzo and seita
 */
@AIName("balaurbarricade")
public class BalaurBarricadeAI2 extends AggressiveNpcAI2 {
    protected List<Integer> percents = new ArrayList<Integer>();

	private boolean isHome = false;
	
    @Override
    public int modifyDamage(int damage) {
        return 1;
    }

	
    private void synchro() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isAlreadyDead() && isHome) {
					checkPercentage(getLifeStats().getHpPercentage());
                }
            }
        }, 1500);

    }
	
	
    private synchronized void checkPercentage(int hpPercentage) {
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 60:
                    case 10:
                        sp();
                        break;
                }
                percents.remove(percent);
                break;
            }
        }
		synchro();
    }

    private void sp() {
        Npc npc = getOwner();
        float direction = Rnd.get(0, 199) / 100f;
        int distance = Rnd.get(1, 4);
        float x1 = (float) (Math.cos(Math.PI * direction) * distance);
        float y1 = (float) (Math.sin(Math.PI * direction) * distance);
        if (npc.getNpcId() == 700517 || npc.getNpcId() == 700556) {
            spawn(215262, npc.getX() + x1, npc.getY() + y1, npc.getZ(), (byte) 0);
            spawn(215262, npc.getX() + y1, npc.getY() + x1, npc.getZ(), (byte) 0);
        } else if (npc.getNpcId() == 700558) {
            spawn(215262, npc.getX() + x1, npc.getY() + y1, npc.getZ(), (byte) 0);
            spawn(214883, npc.getX() + y1, npc.getY() + x1, npc.getZ(), (byte) 0);
        }
    }

    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, new Integer[]{60, 10});
    }

    @Override
    protected void handleSpawned() {
        isHome = true;
		addPercent();
		synchro();
        super.handleDespawned();
    }

    @Override
    protected void handleBackHome() {
		isHome = true;
        addPercent();
        super.handleBackHome();
    }

    @Override
    protected void handleDespawned() {
        isHome = false;
		percents.clear();
        super.handleDespawned();
    }

    @Override
    protected void handleDied() {
		isHome = false;
        percents.clear();
        super.handleDied();
    }
}
