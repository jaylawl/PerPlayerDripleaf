package de.jaylawl.perplayerdripleaf.stuff;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.BigDripleaf;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TiltProcess extends BukkitRunnable {

    private final DripleafAdapter parentDripleafAdapter;
    private final UUID playerUniqueId;
    private int tick = -1;

    public TiltProcess(final @NotNull DripleafAdapter parentDripleafAdapter, final @NotNull Player player) {
        this.parentDripleafAdapter = parentDripleafAdapter;
        this.playerUniqueId = player.getUniqueId();
    }

    //

    public @NotNull UUID getPlayerUniqueId() {
        return this.playerUniqueId;
    }

    public int getTick() {
        return this.tick;
    }

    public boolean isSolid() {
        return this.tick < 20 || this.tick >= 80;
    }

    @Override
    public void run() {
        this.tick++;
        if (this.tick > 80) {
            cancel();
            this.parentDripleafAdapter.unregisterTiltProcess(this);
            if (!this.parentDripleafAdapter.hasActiveTiltProcesses()) {
                this.parentDripleafAdapter.terminate();
            }
            return;
        }

        final BigDripleaf.Tilt newTilt = switch (this.tick) {
            case 0 -> BigDripleaf.Tilt.UNSTABLE;
            // case 10 -> BigDripleaf.Tilt.PARTIAL; // This causes weird behaviour, but isn't vital
            case 20 -> BigDripleaf.Tilt.FULL;
            case 80 -> BigDripleaf.Tilt.NONE;
            default -> null;
        };
        if (newTilt == null) {
            return;
        }

        final Player player = Bukkit.getPlayer(this.playerUniqueId);
        if (player == null || !player.isOnline()) {
            cancel();
            return;
        }
        final Location location = this.parentDripleafAdapter.getVoxel().toLocation();
        if (location == null) {
            cancel();
            this.parentDripleafAdapter.terminate();
            return;
        }
        final Block block = location.getBlock();
        if (!block.getType().equals(Material.BIG_DRIPLEAF)) {
            cancel();
            this.parentDripleafAdapter.terminate();
            return;
        }
        final BigDripleaf bigDripleaf = ((BigDripleaf) block.getBlockData().clone());
        if (!bigDripleaf.getTilt().equals(BigDripleaf.Tilt.NONE)) {
            player.sendBlockChange(location, bigDripleaf);
            cancel();
            this.parentDripleafAdapter.terminate();
            return;
        }
        bigDripleaf.setTilt(newTilt);
        player.sendBlockChange(location, bigDripleaf);
        player.playSound(
                location,
                (newTilt.equals(BigDripleaf.Tilt.NONE) ? Sound.BLOCK_BIG_DRIPLEAF_TILT_UP : Sound.BLOCK_BIG_DRIPLEAF_TILT_DOWN),
                SoundCategory.BLOCKS,
                1f, // volume
                1f // pitch
        );

    }

}
