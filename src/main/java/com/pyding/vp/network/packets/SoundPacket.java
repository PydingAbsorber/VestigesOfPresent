package com.pyding.vp.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;
import java.util.function.Supplier;

public class SoundPacket {
    private final ResourceLocation soundLocation;
    private final float volume;
    private final float pitch;

    public SoundPacket(ResourceLocation soundLocation, float volume, float pitch) {
        this.soundLocation = soundLocation;
        this.volume = volume;
        this.pitch = pitch;
    }

    public static void encode(SoundPacket packet, FriendlyByteBuf buf) {
        buf.writeResourceLocation(packet.soundLocation);
        buf.writeFloat(packet.volume);
        buf.writeFloat(packet.pitch);
    }

    public static SoundPacket decode(FriendlyByteBuf buf) {
        return new SoundPacket(buf.readResourceLocation(), buf.readFloat(), buf.readFloat());
    }
    public static void handle(SoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            handle2(msg.soundLocation, msg.volume,msg.pitch);
        });

        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handle2(ResourceLocation soundLocation, float volume, float pitch) {
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(soundLocation);
        if (soundEvent != null) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.getCommandSenderWorld().playLocalSound(player.getX(), player.getY(), player.getZ(), soundEvent, SoundSource.MASTER, volume, pitch, false);
            }
        }
    }
}
