package ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;



@AIName("totem")
public class TotemNpcAI2 extends GeneralNpcAI2 {

    @Override
    public void think() {
        // totems are not thinking
    }

    @Override
    public AttackIntention chooseAttackIntention() {
        if (skillId == 0) {
            skillId = getSkillList().getRandomSkill().getSkillId();
            skillLevel = 1;
        }
        return AttackIntention.SKILL_ATTACK;
    }

    @Override
    public boolean isMoveSupported() {
        return false;
    }

    @Override
    protected AIAnswer pollInstance(AIQuestion question) {
        switch (question) {
            case SHOULD_DECAY:
                return AIAnswers.NEGATIVE;
            case SHOULD_RESPAWN:
                return AIAnswers.NEGATIVE;
            case SHOULD_REWARD:
                return AIAnswers.NEGATIVE;
            default:
                return null;
        }
    }
}
