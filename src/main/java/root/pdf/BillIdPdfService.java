package root.pdf;

import root.entity.Bill;
import root.entity.BillItem;
import root.repository.BillRepo;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RGBColor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

@RequiredArgsConstructor
@Service
public class BillIdPdfService extends PdfService {

    private static final String VND = " VND";

    private final BillRepo billRepo;

    @Value("${upload.folder}")
    private String UPLOAD_FOLDER;

    public void billIdPdf(Long billId) throws DocumentException {

        Document document = new Document(PageSize.A4);

        String filename = UPLOAD_FOLDER + "bill-id-" + billId + ".pdf";

        try {
            FileOutputStream writeFile = new FileOutputStream(filename);
            PdfWriter.getInstance(document, writeFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        // ======================================================

        // add a paragraph
        String h1 = "Đơn hàng mới: #" + billId;
        document.add(paragraph(h1, "h1-bold-center"));

        String p1 = "Thông tin đơn hàng";
        document.add(paragraph(p1, "h3"));

        /*String p2 = "Phương thức thanh toán: ";
        document.add(paragraph(p2, "h3"));*/

        /*document.add(paragraph(titleEmail, "h2"));
        document.add(paragraph(titleEmail, "h3-bold"));
        document.add(paragraph(titleEmail, "h4"));
        document.add(paragraph(titleEmail, "p"));
        document.add(paragraph(titleEmail, "p-bold"));*/

        PdfPTable table = tableFormat(3);

        // Table Cells
        PdfPCell cellHead = cellTableFormat(new RGBColor(249, 249, 249));
        PdfPCell cellBody = cellTableFormat(new RGBColor(255, 255, 255));

        // table thead
        cellHead.setPhrase(new Phrase("Sản phẩm", fontTableHead()));
        table.addCell(cellHead);
        cellHead.setPhrase(new Phrase("Số lượng", fontTableHead()));
        table.addCell(cellHead);
        cellHead.setPhrase(new Phrase("Giá", fontTableHead()));
        table.addCell(cellHead);

        Bill bill = billRepo.findById(billId).orElseThrow();
        double billTotal = 0.0;
        // table tbody
        for (BillItem billItem : bill.getBillItems()) {
            String productName = String.valueOf(billItem.getProductColor().getProduct().getName());
            String colorName = String.valueOf(billItem.getProductColor().getColor().getName());
            String productColorName = productName + " - " + colorName;

            String productQuantity = String.valueOf(billItem.getQuantity());
            String productPrice = new DecimalFormat("###,###,###").format(billItem.getPrice()) + VND;

            cellBody.setPhrase(new Phrase(productColorName, fontTableBody()));
            table.addCell(cellBody);
            cellBody.setPhrase(new Phrase(productQuantity, fontTableBody()));
            table.addCell(cellBody);
            cellBody.setPhrase(new Phrase(productPrice, fontTableBody()));
            table.addCell(cellBody);

            billTotal += billItem.getQuantity() * billItem.getPrice();
        }

        String billTotalTempFormat = new DecimalFormat("###,###,###").format(billTotal) + VND;

        cellBody.setPhrase(new Phrase("Tạm tính:", fontTableBody()));
        cellBody.setColspan(2);
        table.addCell(cellBody);
        cellBody.setPhrase(new Phrase(billTotalTempFormat, fontTableBody()));
        table.addCell(cellBody);

        cellBody.setPhrase(new Phrase("Coupon Code:", fontTableBody()));
        cellBody.setColspan(2);
        table.addCell(cellBody);
        cellBody.setPhrase(new Phrase(bill.getCouponCode(), fontTableBody()));
        table.addCell(cellBody);

        cellBody.setPhrase(new Phrase("Discount amount:", fontTableBody()));
        cellBody.setColspan(2);
        table.addCell(cellBody);
        String discountAmountFormat = new DecimalFormat("###,###,###")
            .format(bill.getDiscountAmount()) + VND;
        cellBody.setPhrase(new Phrase("-" + discountAmountFormat, fontTableBody()));
        table.addCell(cellBody);

        cellBody.setPhrase(new Phrase("Phương thức thanh toán:", fontTableBody()));
        cellBody.setColspan(2);
        table.addCell(cellBody);
        cellBody.setPhrase(new Phrase(bill.getPayment().getPaymentMethod(), fontTableBody()));
        table.addCell(cellBody);

        String billTotalFormat = new DecimalFormat("###,###,###")
            .format(billTotal - bill.getDiscountAmount()) + VND;
        cellBody.setPhrase(new Phrase("Tổng cộng:", fontTableBody()));
        cellBody.setColspan(2);
        table.addCell(cellBody);
        cellBody.setPhrase(new Phrase(billTotalFormat, fontTableBody()));
        table.addCell(cellBody);

        document.add(table);

        PdfPTable table2 = tableFormat(3);
        table2.setSpacingBefore(32);
        PdfPCell cellHead2 = cellFormatNoBorder();
        // table thead

        cellHead2.setPhrase(new Phrase("Trạng thái đơn hàng: " + bill.getStatus(), fontTableBody()));
        table2.addCell(cellHead2);
        cellHead2.setPhrase(new Phrase("", fontTableBody()));
        table2.addCell(cellHead2); // cell no content

        String createdAt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(bill.getCreatedAt());
        cellHead2.setPhrase(new Phrase("Created at: " + createdAt, fontTableBody()));
        table2.addCell(cellHead2);

        document.add(table2);

        // ======================================================
        document.close();

    }
}
