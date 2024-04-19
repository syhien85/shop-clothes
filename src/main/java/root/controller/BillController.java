package root.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import root.dto.*;
import root.pdf.BillByDateBetweenPdfService;
import root.pdf.BillIdPdfService;
import root.service.BillService;
import root.service.EmailService;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bill")
public class BillController {

    private final BillService billService;
    private final EmailService emailService;
    private final BillIdPdfService billIdPdfService;
    private final BillByDateBetweenPdfService billByDateBetweenPdfService;

    @Value("${upload.folder}")
    private String UPLOAD_FOLDER;

    @CacheEvict(cacheNames = "bill-search", allEntries = true)
    @PostMapping
    public ResponseDTO<Void> create(@RequestBody @Valid BillDTO billDTO) {
        billService.create(billDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "bill-search", allEntries = true),
        @CacheEvict(cacheNames = "bill", key = "#billDTO.id") // khi update delete cache
    })
    @PutMapping
    public ResponseDTO<Void> update(@RequestBody BillDTO billDTO) {
        billService.update(billDTO);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @CacheEvict(cacheNames = "bill", key = "#id")
    @DeleteMapping
    public ResponseDTO<Void> delete(@RequestParam("id") Long id) {
        billService.delete(id);
        return ResponseDTO.<Void>builder().status(200).msg("OK").build();
    }

    @Cacheable(cacheNames = "bill", key = "#id", unless = "#result == null")
    @GetMapping
    public ResponseDTO<BillDTO> getById(@RequestParam("id") Long id) {
        return ResponseDTO.<BillDTO>builder()
            .status(200).msg("OK")
            .data(billService.getById(id))
            .build();
    }

    @Cacheable(cacheNames = "bill-search")
    @PostMapping("/search")
    public ResponseDTO<PageDTO<BillDTO>> search(@RequestBody @Valid SearchBillDTO searchDTO) {
        return ResponseDTO.<PageDTO<BillDTO>>builder().status(200).msg("OK")
            .data(billService.searchService(searchDTO))
            .build();
    }

    @GetMapping("/count-bill-by-date-time")
    public ResponseDTO<PageDTO<BillCountByMonthDTO[]>> countBillByDateTime() {
        return ResponseDTO.<PageDTO<BillCountByMonthDTO[]>>builder().status(200).msg("OK")
            .data(billService.countBillByMonthService())
            .build();
    }

    @GetMapping("/count-bill-by-coupon-code")
    public ResponseDTO<PageDTO<BillCountByCouponCodeDTO>> countBillByCouponCode() {
        return ResponseDTO.<PageDTO<BillCountByCouponCodeDTO>>builder().status(200).msg("OK")
            .data(billService.countBillByCouponCodeService())
            .build();
    }

    @GetMapping("/count-bill-by-user-total-invoice")
    public ResponseDTO<PageDTO<BillByUserTotalInvoiceDTO>> countBillByUser() {
        return ResponseDTO.<PageDTO<BillByUserTotalInvoiceDTO>>builder().status(200).msg("OK")
            .data(billService.billByUserTotalInvoiceService())
            .build();
    }

    @GetMapping("/count-bill-by-user-with-product")
    public ResponseDTO<PageDTO<BillByUserWithProductDTO>> billByUserWithProduct() {
        return ResponseDTO.<PageDTO<BillByUserWithProductDTO>>builder().status(200).msg("OK")
            .data(billService.billByUserWithProductService())
            .build();
    }

    @GetMapping("/count-bill-by-product-best-seller")
    public ResponseDTO<PageDTO<BillCountByProductBestSellerDTO>> billCountByProductBestSeller() {
        return ResponseDTO.<PageDTO<BillCountByProductBestSellerDTO>>builder().status(200).msg("OK")
            .data(billService.billCountByProductBestSellerService())
            .build();
    }

    @GetMapping("/pdf-bill")
    public void pdfBill(HttpServletResponse response, @RequestParam("billId") Long billId)
        throws DocumentException, IOException {

        response.setContentType("application/pdf");

        /*String currentTime = new SimpleDateFormat("yy-MM-dd_hh-mm-ss").format(new Date());
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=bill-id-" + billId + "_" + currentTime + ".pdf"
        );*/
        String filename = "bill-id-" + billId + ".pdf";
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=" + filename
        );
        billIdPdfService.billIdPdf(billId);

        File file = new File(UPLOAD_FOLDER + filename);
        Files.copy(file.toPath(), response.getOutputStream());

    }

    @PostMapping("/pdf-bill-by-date-between")
    public void pdfBillByDate(@RequestBody @Valid SearchBillDTO searchDTO,HttpServletResponse response)
        throws DocumentException, IOException {
        response.setContentType("application/pdf");

        String dateStartString = new SimpleDateFormat("dd-MM-yyyy").format(searchDTO.getStart());
        String dateEndString = new SimpleDateFormat("dd-MM-yyyy").format(searchDTO.getEnd());

        String filename = "bills-" + dateStartString + "_" + dateEndString + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        billByDateBetweenPdfService.billByDatePdf(searchDTO);

        File file = new File(UPLOAD_FOLDER + filename);
        Files.copy(file.toPath(), response.getOutputStream());

    }

}
