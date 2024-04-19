package root.jobschedule;

import root.dto.BillDTO;
import root.dto.PageDTO;
import root.dto.SearchBillDTO;
import root.service.BillService;
import root.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

import static java.time.temporal.ChronoUnit.*;

@RequiredArgsConstructor
@Slf4j
@Component
public class JobScheduleBillEmail {

    private final BillService billService;
    private final EmailService emailService;

    private static final String ADMIN_EMAIL = "syhien85@hotmail.com";

    /**
     * Kiểm tra và gửi email đơn hàng mới cho quản trị viên (check 5 phút 1 lần)
     */
    @Scheduled(cron = "0 */5 * * * *") // 5 minutes
    public void cronSendNewBillToAdminEvery5Minus() {

        Date fiveMinuteBefore = Date.from(Instant.now().minus(5, MINUTES));
        Date today = Date.from(Instant.now());

        SearchBillDTO searchDTO = new SearchBillDTO();
        searchDTO.setStart(fiveMinuteBefore);
        searchDTO.setEnd(today);

        // tìm các đơn hàng start: 5 phút trước, end: now
        int page = 0;
        while (true) {
            searchDTO.setCurrentPage(page);
            PageDTO<BillDTO> billDTOs = billService.searchService(searchDTO);
            if (billDTOs != null) {
                for (BillDTO billDTO : billDTOs.getData()) {
//                    emailService.sendBillCreateEmail(ADMIN_EMAIL, "Shop Admin", billDTO);
                    log.warn("New bill #%s created".formatted(billDTO.getId()));
                }
                page++;
                if (page > billDTOs.getTotalPage()) break;
            }
        }
    }
}
