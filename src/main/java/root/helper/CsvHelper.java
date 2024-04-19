package root.helper;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import root.entity.Permission;
import root.repository.PermissionRepo;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class CsvHelper {
    private final PermissionRepo permissionRepo;

    @Value("${upload.folder}")
    private String UPLOAD_FOLDER;

    public void saveCsvFile(MultipartFile file) {
        List<Permission> permissions = csvImportApacheCommons(file);
        permissionRepo.saveAll(permissions);
    }

    private List<Permission> csvImportApacheCommons(MultipartFile file) {
        List<Permission> permissionList = new ArrayList<>();
        Permission permission = null;
        try {
//			long start = System.currentTimeMillis();
            String csvFilePath = uploadFilePath(file);
//            String csvFilePath = "C:\\Users\\SH85\\Downloads\\Permission.csv";
            Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
            CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();
            CSVParser csvParser = csvFormat.parse(reader);
            List<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                permission = new Permission();
//				permission.setId(Long.parseLong(csvRecord.get(0)));
                permission.setPath(csvRecord.get(0));
                permission.setMethod(csvRecord.get(1));
                permission.setVisibility(Boolean.parseBoolean(csvRecord.get(2)));
                permission.setRoles(List.of(csvRecord.get(3).split(";")));

                permissionList.add(permission);
            }
//            System.out.println(permissionList);
//			long end = System.currentTimeMillis();
//            System.out.println("Import done in ms\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return permissionList;
    }

    private String uploadFilePath(MultipartFile fileUpload) throws IOException {
        String newFilename = null;

        if (fileUpload != null && !fileUpload.isEmpty()) {
            if (new File(UPLOAD_FOLDER).exists()) {
                new File(UPLOAD_FOLDER).mkdir();
            }

            String filename = fileUpload.getOriginalFilename();

            if (filename != null) {
                String extension = filename.substring((filename.lastIndexOf(".")));
                newFilename = UUID.randomUUID() + extension;
                File newFile = new File(UPLOAD_FOLDER + newFilename);
                try {
                    fileUpload.transferTo(newFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        File file = new File(UPLOAD_FOLDER + newFilename);
        Files.copy(
            file.toPath(),
            (new File(UPLOAD_FOLDER + file.getName())).toPath(),
            StandardCopyOption.REPLACE_EXISTING
        );

        return UPLOAD_FOLDER + newFilename;
    }
}