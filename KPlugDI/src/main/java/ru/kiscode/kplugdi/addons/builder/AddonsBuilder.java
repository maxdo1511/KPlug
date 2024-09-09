package ru.kiscode.kplugdi.addons.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.bukkit.Bukkit;
import ru.kiscode.kplugdi.KPlugDI;
import ru.kiscode.kplugdi.addons.http_client.HttpClient;
import ru.kiscode.kplugdi.addons.http_client.HttpRequest;
import ru.kiscode.kplugdi.addons.http_client.HttpRequestBuilder;
import ru.kiscode.kplugdi.addons.http_client.models.FileInfo;
import ru.kiscode.kplugdi.addons.service.AddonService;
import ru.kiscode.kplugdi.annotations.Autowired;
import ru.kiscode.kplugdi.annotations.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class AddonsBuilder {

    private final String globalVersion;
    private final List<AddonBuildInfo> addons;
    private final List<String> downloadedAddons; // Имена аддонов в папке на время старта проекта
    private AddonService addonService;
    private HttpClient httpClient;

    public AddonsBuilder() {
        String version = Bukkit.getServer().getBukkitVersion().split("-")[0];

        globalVersion = version;
        addons = new ArrayList<>();
        downloadedAddons = new ArrayList<>();
        scanAddonsDir();
        KPlugDI.getInstance().getLogger().warning("Global version: " + globalVersion);
    }

    public AddonsBuilder add(String addon, String version) {
        if (!isValidName(addon)) {
            KPlugDI.getInstance().getLogger().warning("No addon with name " + addon);
            return this;
        }
        AddonBuildInfo addonBuildInfo = new AddonBuildInfo(addon, version);
        if (isDownloaded(addonBuildInfo)) {
            addonBuildInfo.setDownload(true);
            addonBuildInfo.setFile(new File(KPlugDI.getInstance().getDataFolder(), AddonService.ADDONS_DIR + "/" + addonBuildInfo.getName() + ".jar"));
        }
        addons.add(addonBuildInfo);
        return this;
    }

    public AddonsBuilder add(String addon) {
        String version = getValidVersion(addon);
        if (version == null) {
            return add(addon, null);
        }
        return add(addon, version);
    }

    public void build() {
        for (AddonBuildInfo addon : addons) {
            if (!addon.isDownload()) {
                download(addon);
            } else {
                if (addon.getFile() == null) {
                    KPlugDI.getInstance().getLogger().warning("Addon file is null: " + addon.getName());
                    continue;
                }
                addonService.loadAddon(addon.getFile());
            }
        }
    }

    private boolean isValidName(String name) {
        //request to server
        return true;
    }

    private String getValidVersion(String name) {
        try {
            Object resp = new HttpRequestBuilder(httpClient)
                    .setUrl("addon/version/" + name)
                    .setMethod(HttpRequestBuilder.RequestMethod.GET)
                    .sentSync();
            return resp.toString();
        } catch (Exception e) {
            KPlugDI.getInstance().getLogger().warning("Error to get versions of addon " + name);
            e.printStackTrace();
        }
        return null;
    }

    private boolean isDownloaded(AddonBuildInfo addonBuildInfo) {
        String name = addonBuildInfo.getName();
        return downloadedAddons.contains(name);
    }

    private boolean scanAddonsDir() {
        File dir = new File(KPlugDI.getInstance().getDataFolder(), AddonService.ADDONS_DIR);
        if (!dir.exists()) {
            return false;
        }
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) continue;
            String fileName = file.getName().replace(".jar", "");
            downloadedAddons.add(fileName);
        }
        return true;
    }

    private boolean isValidVersion(String version, String range) {
        if (!range.contains("-")) {
            return version.equals(range);
        }
        String[] data = range.split("-", 2);
        return isVersionBigger(data[0], version) && isVersionBigger(version, data[1]);
    }

    private boolean isVersionBigger(String v1, String v2) {
        int[] l_v1 = Arrays.stream(v1.split("\\.")).mapToInt(Integer::parseInt).toArray();
        int[] l_v2 = Arrays.stream(v2.split("\\.")).mapToInt(Integer::parseInt).toArray();
        if (l_v1[0] > l_v2[0]) return true;
        if (l_v1[0] == l_v2[0] && l_v1[1] > l_v2[1]) return true;
        return l_v1[0] == l_v2[0] && l_v1[1] == l_v2[1] && l_v1[2] < l_v2[2];
    }

    private FileInfo getFileInfoInfo(AddonBuildInfo addon) {
        Object resp = new HttpRequestBuilder(httpClient)
                .setUrl("addon/fileInfo/" + addon.getName() + "/" + addon.getVersion())
                .setMethod(HttpRequestBuilder.RequestMethod.GET)
                .sentSync();
        // TODO перенести в Bean
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(resp.toString(), FileInfo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void download(AddonBuildInfo addon) {
        KPlugDI.getInstance().getLogger().warning("Downloading addon: " + addon.getName() + " with version " + addon.getVersion());
        setFileInfo(addon);
        try {
            new HttpRequestBuilder(httpClient)
                            .setUrl("/addon/download/" + addon.getName() + "/" + addon.getVersion())
                            .setMethod(HttpRequestBuilder.RequestMethod.POST)
                            .setCallbackObject(this)
                            .setCallbackMethod(this.getClass().getDeclaredMethod("saveFile", byte[].class, Object.class))
                            .setPriority(1)
                            .setByteArrayLength(addon.getByteArrayLength())
                            .sentAsync();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void setFileInfo(AddonBuildInfo addon) {
        FileInfo fileInfo = getFileInfoInfo(addon);
        addon.setByteArrayLength(fileInfo.getFileSize());
    }

    private void saveFile(byte[] data, Object request) {
        String fileName = ((HttpRequest) request).getUrlString().split("/")[5];
        KPlugDI.getInstance().getLogger().warning("Addon downloaded: " + fileName);
        File file = new File(KPlugDI.getInstance().getDataFolder(), AddonService.ADDONS_DIR + "/" + fileName + ".jar");
        try {
            Files.write(file.toPath(), data);
            addonService.loadAddon(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void delete(File addon) {
        addon.delete();
    }

    @Autowired
    private void setAddonService(AddonService addonService) {
        this.addonService = addonService;
    }

    @Autowired
    private void setHttpClient(@NonNull HttpClient httpClient) {
        this.httpClient = httpClient;
    }

}
