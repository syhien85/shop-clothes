package root.pdf;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.RGBColor;
import lombok.RequiredArgsConstructor;

import java.awt.*;

public class PdfService {

    protected static Paragraph paragraph(String text, String tagClass) {

        int fontFamily = 0;
        int fontWeight = 0;
        if (tagClass.contains("bold")) {
            fontFamily = 1;
            fontWeight = 1;
        }

        int fontSize = 12;
        int spacingAfter = 1;
        if (tagClass.startsWith("h1")) {
            fontSize = 28;
            spacingAfter = 12;
        }
        if (tagClass.startsWith("h2")) {
            fontSize = 24;
            spacingAfter = 8;
        }
        if (tagClass.startsWith("h3")) {
            fontSize = 18;
            spacingAfter = 6;
        }
        if (tagClass.startsWith("h4")) {
            fontSize = 10;
            spacingAfter = 4;
        }
        if (tagClass.startsWith("p")) {
            fontSize = 12;
            spacingAfter = 4;
        }

        int align = Element.ALIGN_LEFT;
        if (tagClass.contains("center")) align = Element.ALIGN_CENTER;

        Font font2 = new Font(fontFamily, fontSize, fontWeight);
        Paragraph paragraph = new Paragraph(text, font2);
        paragraph.setAlignment(align);
        paragraph.setSpacingAfter(spacingAfter);

        return paragraph;
    }

    protected static PdfPTable tableFormat(int numColumns) {
        PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(100f);
        // table.setWidths(new int[]{3, 3});
        table.setSpacingBefore(4);
        return table;
    }

    protected static Font fontTableHead() {
        Font font = new Font(1, 10, 0);
        font.setColor(new RGBColor(0, 0, 0));
        font.setSize(11);
        return font;
    }

    protected static Font fontTableBody() {
        Font font = new Font(1, 10, 0);
        font.setColor(new RGBColor(0, 0, 0));
        font.setSize(10);
        return font;
    }

    protected static PdfPCell cellTableFormat(Color backgroundColor) {
        PdfPCell cell = new PdfPCell();
        cell.setBorderWidth(1);
        cell.setPadding(4);
        cell.setBackgroundColor(backgroundColor);
        return cell;
    }

    protected static PdfPCell cellFormatNoBorder() {
        PdfPCell cell = new PdfPCell();
        cell.setBorderWidth(0);
        cell.setPadding(4);
        cell.setBackgroundColor(new RGBColor(255, 255, 255));
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
}
