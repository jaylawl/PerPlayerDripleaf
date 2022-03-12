package de.jaylawl.perplayerdripleaf.stuff;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.BigDripleaf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class DripleafManager implements Listener {

    private final HashMap<Voxel, DripleafAdapter> dripleafAdapters = new HashMap<>();

    public DripleafManager() {
    }

    //

    protected void unregisterDripleafAdapter(final @NotNull DripleafAdapter dripleafAdapter) {
        this.dripleafAdapters.remove(dripleafAdapter.getVoxel());
    }

    public DripleafAdapter[] getDripleafAdapters() {
        return this.dripleafAdapters.values().toArray(new DripleafAdapter[0]);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final @NotNull PlayerInteractEvent event) {

        if (!event.getAction().equals(Action.PHYSICAL)) {
            return;
        }
        final Block block = event.getClickedBlock();
        if (block == null || !block.getType().equals(Material.BIG_DRIPLEAF)) {
            return;
        }

        event.setCancelled(true);

        if (!((BigDripleaf) block.getBlockData()).getTilt().equals(BigDripleaf.Tilt.NONE)) {
            return;
        }

        final Voxel voxel = Voxel.fromBlock(block);
        DripleafAdapter dripleafAdapter = this.dripleafAdapters.get(voxel);
        if (dripleafAdapter == null) {
            dripleafAdapter = new DripleafAdapter(this, voxel);
            this.dripleafAdapters.put(voxel, dripleafAdapter);
        }
        dripleafAdapter.onStep(event.getPlayer());

    }

}
