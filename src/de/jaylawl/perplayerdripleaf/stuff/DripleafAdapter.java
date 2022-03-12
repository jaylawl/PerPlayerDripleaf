package de.jaylawl.perplayerdripleaf.stuff;

import de.jaylawl.perplayerdripleaf.PerPlayerDripleaf;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class DripleafAdapter {

    public static final double EIGHTH_BLOCK_LENGTH = .125d;

    private final DripleafManager parentDripleafManager;
    private final Voxel voxel;
    private final HashMap<UUID, TiltProcess> tiltProcesses = new HashMap<>();

    public DripleafAdapter(final @NotNull DripleafManager parentDripleafManager, final @NotNull Voxel voxel) {
        this.parentDripleafManager = parentDripleafManager;
        this.voxel = voxel;
    }

    //

    public @NotNull DripleafManager getParentDripleafManager() {
        return this.parentDripleafManager;
    }

    public @NotNull Voxel getVoxel() {
        return this.voxel;
    }

    public void unregisterTiltProcess(final @NotNull TiltProcess tiltProcess) {
        this.tiltProcesses.remove(tiltProcess.getPlayerUniqueId());
    }

    public boolean hasActiveTiltProcesses() {
        return this.tiltProcesses.values().stream().noneMatch(BukkitRunnable::isCancelled);
    }

    public boolean isSolidForPlayer(final @NotNull UUID playerUniqueId) {
        return !this.tiltProcesses.containsKey(playerUniqueId) || this.tiltProcesses.get(playerUniqueId).isSolid();
    }

    public void terminate() {
        this.tiltProcesses.values().forEach(TiltProcess::cancel);
        this.tiltProcesses.clear();
        this.parentDripleafManager.unregisterDripleafAdapter(this);
    }

    //

    public void onStep(final @NotNull Player player) {
        final UUID playerUniqueId = player.getUniqueId();
        TiltProcess tiltProcess = this.tiltProcesses.get(playerUniqueId);
        if (tiltProcess == null || tiltProcess.isCancelled()) {
            tiltProcess = new TiltProcess(this, player);
            tiltProcess.runTaskTimer(PerPlayerDripleaf.getInstance(), 0L, 1L);
            this.tiltProcesses.put(playerUniqueId, tiltProcess);
        } else {
            if (!tiltProcess.isSolid()) {
                final Vector velocity = player.getVelocity();
                if (velocity.getY() <= 0) {
                    player.teleport(player.getLocation().subtract(0d, EIGHTH_BLOCK_LENGTH, 0d));
                    player.setVelocity(velocity);
                }
            }
        }
    }

}
