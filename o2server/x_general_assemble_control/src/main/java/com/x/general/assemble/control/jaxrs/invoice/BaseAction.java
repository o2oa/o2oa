package com.x.general.assemble.control.jaxrs.invoice;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.general.assemble.control.tools.PdfElectronicInvoiceTools;
import com.x.general.assemble.control.tools.PdfRailwayInvoiceTools;
import com.x.general.assemble.control.tools.PdfRegularInvoiceTools;
import com.x.general.core.entity.Invoice;
import com.x.general.core.entity.InvoiceDetail;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

abstract class BaseAction extends StandardJaxrsAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);

    protected void extractInvoice(Invoice invoice, byte[] bytes) throws Exception {
        try {
            PDDocument doc = PDDocument.load(bytes);
            PDPage firstPage = doc.getPage(0);
            int pageWidth = Math.round(firstPage.getCropBox().getWidth());
            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.setSortByPosition(true);
            String fullText = textStripper.getText(doc);
            if (firstPage.getRotation() != 0) {
                pageWidth = Math.round(firstPage.getCropBox().getHeight());
            }
            String allText = PdfRegularInvoiceTools.replace(fullText).replace("（", "(")
                    .replace("）", ")").replace("￥", "¥");
            invoice.setContent(fullText);
            LOGGER.info("{}-文件解析的发票内容:{}", invoice.getName(), allText);
            if (allText.contains(PdfRailwayInvoiceTools.KEY)) {
                LOGGER.info("解析铁路电子发票各项信息");
                PdfRailwayInvoiceTools.getInvoice(allText, invoice);
            } else if (allText.contains("电子发票") || allText.contains("电⼦发票")) {
                LOGGER.info("解析电子发票各项信息");
                PdfElectronicInvoiceTools.getFullElectronicInvoice(allText, pageWidth, doc,
                        firstPage, invoice);
            } else {
                LOGGER.info("解析包含密码区发票各项信息");
                PdfRegularInvoiceTools.getRegularInvoice(allText, pageWidth, doc, firstPage,
                        invoice);
            }
            if (StringUtils.isNotBlank(invoice.getDate())) {
                try {
                    invoice.setInvoiceDate(DateTools.parse(invoice.getDate(), "yyyy年MM月dd日"));
                } catch (Exception e) {
                    LOGGER.warn("发票日期：{}，转换错误：{}", invoice.getDate(), e.getMessage());
                }
            }
            if (CollectionUtils.isNotEmpty(invoice.getProperties().getDetailList())) {
                invoice.setDetail(
                        invoice.getProperties().getDetailList().stream().map(InvoiceDetail::getName)
                                .collect(Collectors.joining(",")));
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new ExceptionErrorExtract(e.getMessage());
        }
    }
}
