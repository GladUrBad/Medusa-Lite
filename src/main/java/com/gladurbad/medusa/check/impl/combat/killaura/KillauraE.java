package com.gladurbad.medusa.check.impl.combat.killaura;

import com.gladurbad.medusa.check.*;
import com.gladurbad.medusa.config.ConfigValue;
import com.gladurbad.medusa.network.Packet;
import com.gladurbad.medusa.playerdata.PlayerData;

import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;

import org.bukkit.entity.EntityType;


@CheckInfo(name = "Killaura", type = "E", dev = true)
public class KillauraE extends Check {

    private static final ConfigValue minSwings = new ConfigValue(ConfigValue.ValueType.INTEGER, "swings");
    private final static ConfigValue minFlag = new ConfigValue(ConfigValue.ValueType.INTEGER, "min-flag");

    private int swings, hits;

    public KillauraE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isReceiving()) {
            if(packet.getPacketId() == PacketType.Client.ARM_ANIMATION) {
                ++swings;
                if(swings >= minSwings.getInt()) {
                    if(hits > minFlag.getInt()) fail();
                    swings = 0;
                    hits = 0;
                }
            } else if(packet.getPacketId() == PacketType.Client.USE_ENTITY) {
                WrappedPacketInUseEntity wrappedPacketInUseEntity = new WrappedPacketInUseEntity(packet.getRawPacket());
                if(wrappedPacketInUseEntity.getEntity().getType() == EntityType.PLAYER) {
                    ++hits;
                }
            }
        }
    }
}
