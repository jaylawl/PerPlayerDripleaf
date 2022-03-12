package de.jaylawl.perplayerdripleaf;

import de.jaylawl.perplayerdripleaf.stuff.DripleafAdapter;
import de.jaylawl.perplayerdripleaf.stuff.DripleafManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class PerPlayerDripleaf extends JavaPlugin {

    private static PerPlayerDripleaf INSTANCE;

    private DripleafManager dripleafManager;

    public PerPlayerDripleaf() {
    }

    //

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.dripleafManager = new DripleafManager();
        getServer().getPluginManager().registerEvents(this.dripleafManager, this);
    }

    @Override
    public void onDisable() {
        Arrays.stream(this.dripleafManager.getDripleafAdapters()).forEach(DripleafAdapter::terminate);
    }

    //

    public static PerPlayerDripleaf getInstance() {
        return INSTANCE;
    }

    public DripleafManager getDripleafManager() {
        return this.dripleafManager;
    }

}
