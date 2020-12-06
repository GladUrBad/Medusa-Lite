package com.gladurbad.medusa.check.impl.combat.velocity;

import com.gladurbad.medusa.check.Check;
import com.gladurbad.api.check.CheckInfo;
import com.gladurbad.medusa.data.PlayerData;
import com.gladurbad.medusa.packet.Packet;
import java.util.ArrayDeque;

/**
 * Created on 11/23/2020 Package com.gladurbad.medusa.check.impl.combat.velocity by GladUrBad
 */
@CheckInfo(name = "Velocity (A)", experimental = true, description = "Checks for vertical velocity.")
public class VelocityA extends Check {

    private final ArrayDeque<Double> samples = new ArrayDeque<>();
    private int weirdTicks;

    public VelocityA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            if (data.getVelocityProcessor().getTicksSinceVelocity() < 5) {
                final double taken = data.getPositionProcessor().getDeltaY();
                final double expected = data.getVelocityProcessor().getVelocityY() * 0.99F;
                double percentage = (taken * 100) / expected;

                final boolean invalid = data.getPositionProcessor().isOnClimbable() ||
                        data.getPositionProcessor().isInLiquid() ||
                        data.getPositionProcessor().isBlockNearHead();

                if (invalid) weirdTicks = 0;
                else ++weirdTicks;

                samples.add(percentage);

                if (samples.size() >= 5 && weirdTicks > 20) {
                    final double max = samples.stream().mapToDouble(value -> value).max().orElse(0);
                    if (max < 98) {
                        fail(String.format("velocity=%.2f", max));
                    }
                    samples.clear();
                }
            }
        }
    }
}
