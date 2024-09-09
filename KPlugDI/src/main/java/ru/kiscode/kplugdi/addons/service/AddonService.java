package ru.kiscode.kplugdi.addons.service;

import ru.kiscode.kplugdi.KPlugDI;
import ru.kiscode.kplugdi.addons.AddonExtractor;
import ru.kiscode.kplugdi.addons.api.KAddon;
import ru.kiscode.kplugdi.annotations.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class AddonService {

    private final Map<String, KAddon> addons = new HashMap<>();
    public static final String ADDONS_DIR = "addons";

    public void loadAddon(File addon) {
        AddonExtractor<KAddon> extractor = new AddonExtractor<>(addon, KAddon.class);
        try {
            Class<? extends KAddon> addonClass = extractor.extract();
            try {
                KAddon addon_inst = (KAddon) addonClass.getConstructors()[0].newInstance();
                addon_inst.onLoad();
                addons.put(addon_inst.getName(), addon_inst);
            }catch (Exception e) {
                KPlugDI.getInstance().getLogger().warning("No argument constructor not found: " + addon.getName());
                return;
            }
            KPlugDI.getInstance().getLogger().info("Addon loaded: " + addon.getName());
        } catch (Exception e) {
            KPlugDI.getInstance().getLogger().warning("Failed to load addon: " + addon.getName());
        }
    }

    private void loadAddonsFromDirectory() {
        File main_dir = KPlugDI.getInstance().getDataFolder();
        File addons_dir = new File(main_dir, ADDONS_DIR);
        if (!addons_dir.exists()) {
            addons_dir.mkdir();
        }
        for (File addon : Objects.requireNonNull(addons_dir.listFiles())) {
            AddonExtractor<KAddon> extractor = new AddonExtractor<>(addon, KAddon.class);
            try {
                Class<? extends KAddon> addonClass = extractor.extract();
                try {
                    KAddon addon_inst = (KAddon) addonClass.getConstructors()[0].newInstance();
                    addons.put(addon_inst.getName(), addon_inst);
                }catch (Exception e) {
                    KPlugDI.getInstance().getLogger().warning("No argument constructor not found: " + addon.getName());
                    continue;
                }
                KPlugDI.getInstance().getLogger().info("Addon loaded: " + addon.getName());
            } catch (Exception e) {
                KPlugDI.getInstance().getLogger().warning("Failed to load addon: " + addon.getName());
            }
        }

        for (KAddon addon : addons.values()) {
            addon.onLoad();
        }
    }

}
