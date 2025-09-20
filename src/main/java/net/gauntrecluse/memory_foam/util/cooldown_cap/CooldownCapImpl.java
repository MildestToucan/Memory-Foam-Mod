package net.gauntrecluse.memory_foam.util.cooldown_cap;

public class CooldownCapImpl implements IPlayerEffectCDCap {
    private long lastTimeUsed = 0;

    @Override
    public long getLastTimeUsed() {
        return this.lastTimeUsed;
    }

    @Override
    public void setLastTimeUsed(long levelGameTime) {
        this.lastTimeUsed = levelGameTime;
    }
}
