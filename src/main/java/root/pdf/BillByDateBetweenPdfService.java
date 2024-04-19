package root.pdf;

import root.dto.BillDTO;
import root.dto.PageDTO;
import root.dto.SearchBillDTO;
import root.repository.BillRepo;
import root.service.BillService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RGBColor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class BillByDateBetweenPdfService extends PdfService {

    private static final String VND = " VND";

    private final BillService billService;
    private final BillRepo billRepo;

    @Value("${upload.folder}")
    private String UPLOAD_FOLDER;

    public void billByDatePdf(SearchBillDTO searchDTO) throws DocumentException {

        Document document = new Document(PageSize.A4);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateStart = dateFormat.format(searchDTO.getStart());
        String dateEnd = dateFormat.format(searchDTO.getEnd());
        String filename = UPLOAD_FOLDER + "bills-" + dateStart + "_" + dateEnd + ".pdf";

        try {
            FileOutputStream writeFile = new FileOutputStream(filename);
            PdfWriter.getInstance(document, writeFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        document.open();

        // ======================================================
        Image image1 = null;
        try {
            image1 = Image.getInstance("classpath:/static/images/" + "icon.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PdfPTable table0 = tableFormat(4);
        table0.setSpacingAfter(16);
        PdfPCell cellBody0 = cellFormatNoBorder();
        cellBody0.setBorderColor(new RGBColor(255, 255, 255));

        cellBody0.setImage(image1);
        cellBody0.setFixedHeight(48);
        table0.addCell(cellBody0);

        cellBody0.setPhrase(null);
        table0.addCell(cellBody0);

        String now = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date.from(Instant.now()));
        cellBody0.setPhrase(new Phrase("Update: " + now));
        cellBody0.setColspan(2);
        cellBody0.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
        table0.addCell(cellBody0);
        document.add(table0);
        // ======================================================

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
        String dateStart2 = dateFormat2.format(searchDTO.getStart());
        String dateEnd2 = dateFormat2.format(searchDTO.getEnd());

        // add a paragraph
        String p1 = "Thống kê Đơn hàng từ: " + dateStart2 + " - " + dateEnd2;
        // document.add(paragraph(p1, "h1-bold"));
        document.add(paragraph(p1, "h3-center-bold"));

        // ======================================================
        // ======================================================
        PdfPTable table1 = tableFormat(4);
        table1.setSpacingBefore(16);
        table1.setSpacingAfter(16);
        PdfPCell cellHead1 = cellTableFormat(new RGBColor(249, 249, 249));
        PdfPCell cellBody1 = cellTableFormat(new RGBColor(255, 255, 255));

        // table thead
        cellHead1.setPhrase(new Phrase("#", fontTableHead()));
        table1.addCell(cellHead1);
        cellHead1.setPhrase(new Phrase("User Name", fontTableHead()));
        table1.addCell(cellHead1);
        cellHead1.setPhrase(new Phrase("Coupon Code", fontTableHead()));
        table1.addCell(cellHead1);
        cellHead1.setPhrase(new Phrase("Total Bill", fontTableHead()));
        table1.addCell(cellHead1);

        // ===
        int currentPage = 0;
        double doanhThu = 0.0;
        while (true) {
            searchDTO.setCurrentPage(currentPage);

            PageDTO<BillDTO> billDTOs = billService.searchService(searchDTO);

            for (BillDTO bill : billDTOs.getData()) {
                // #id, User Id,  Coupon, Bill Total
                Long billId = bill.getId();
                String userName = bill.getUser().getName();
                String couponCode = bill.getCouponCode();
                doanhThu += bill.getTotalBill();

                String billTotalFormat = new DecimalFormat("###,###,###")
                    .format(bill.getTotalBill()) + VND;

                // ==== table tbody tr start
                cellBody1.setPhrase(new Phrase("#" + billId, fontTableBody()));
                table1.addCell(cellBody1);
                cellBody1.setPhrase(new Phrase(userName, fontTableBody()));
                table1.addCell(cellBody1);
                cellBody1.setPhrase(new Phrase(couponCode, fontTableBody()));
                table1.addCell(cellBody1);
                cellBody1.setPhrase(new Phrase(billTotalFormat, fontTableBody()));
                table1.addCell(cellBody1);
                // ==== table tbody tr end
            }
            currentPage++;
            if (currentPage > billDTOs.getTotalPage()) break;
        }

        // table close
        document.add(table1);

        // ======================================================

        Long totalBill = billRepo.countBillByStartAndEnd(searchDTO.getStart(), searchDTO.getEnd());

        String doanhThuFormat = new DecimalFormat("###,###,###").format(doanhThu) + VND;

        String p3 = "Số lượng đơn hàng: " + totalBill + " | Doanh thu: " + doanhThuFormat;
        document.add(paragraph(p3, "p"));

        // ======================================================
        document.close();
    }
}
