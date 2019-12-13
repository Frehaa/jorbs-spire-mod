package stsjorbsmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import stsjorbsmod.actions.ExhumeCardsAction;

public class SelfExhumePatch {
    // Note: it's very important this happen as a prefix to die() rather than as an insert before the die() call in
    // damage(); this is because isHalfDead gets set by subclasses (Darkling, AwakenedOne) overriding damage().
    @SpirePatch(
            clz = AbstractMonster.class,
            method = "die",
            paramtypez = { boolean.class }
    )
    public static class ExhumeOnKillPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractMonster __this) {
            if (!__this.halfDead &&
                !__this.isDying &&
                __this.currentHealth <= 0 &&
                !MonsterSuicideTrackingPatch.IsMonsterSuicidingField.isSuiciding.get(__this) &&
                !IsMonsterFriendlyField.isFriendly.get(__this)
            ) {
                AbstractDungeon.actionManager.addToBottom(new ExhumeCardsAction(SelfExhumeFields.selfExhumeOnKill::get));
            }
        }
    }

    @SpirePatch(
            clz = AbstractCreature.class,
            method = "applyStartOfTurnPowers"
    )
    public static class ExhumeAtStartOfTurn7Patch {
        public static final int TURN_TO_EXHUME_ON = 7;

        @SpirePostfixPatch
        public static void patch(AbstractCreature __this) {
            // Note, the turn counter appears off by one because it isn't incremented til after start-of-turn powers are applied
            if (__this.isPlayer && AbstractDungeon.actionManager.turn == TURN_TO_EXHUME_ON - 1) {
                AbstractDungeon.actionManager.addToBottom(new ExhumeCardsAction(SelfExhumeFields.selfExhumeAtStartOfTurn7::get));
            }
        }
    }
}


