package ru.kiscode.kplugdi.addons.http_client.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileInfo {

    private int fileSize;
    private String fileName;
    private String fileExtension;

}
