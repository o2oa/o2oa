package com.x.general.assemble.control.jaxrs.office;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tika.Tika;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.w3c.dom.Document;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.converter.docx.poi.xhtml.XWPF2XHTMLConverter;
import io.swagger.v3.oas.annotations.media.Schema;

public class ActionToHtml extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionToHtml.class);

	private static final String TYPE_DOC = "application/msword";
	private static final String TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

	/**
	 * 只能使用 xdocreport 2.0.2 版本,2.0.3版本报错 Caused by: java.lang.NoSuchFieldError:
	 * Factory at
	 * fr.opensagres.poi.xwpf.converter.core.styles.XWPFStylesDocument$FontsDocumentVisitor.visitDocumentPart(XWPFStylesDocument.java:1600)
	 * 
	 * @param effectivePerson
	 * @param bytes
	 * @param disposition
	 * @return
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws ExceptionUnsupportType
	 */
	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition)
			throws ParserConfigurationException, IOException, TransformerException, ExceptionUnsupportType {
		LOGGER.debug("execute:{}, fileName:{}.", effectivePerson::getDistinguishedName, disposition::getFileName);
		ActionResult<Wo> result = new ActionResult<>();
		Tika tika = new Tika();
		String type = tika.detect(bytes);
		Wo wo = new Wo();
		switch (type) {
		case (TYPE_DOC):
			wo.setValue(this.doc(bytes));
			break;
		case (TYPE_DOCX):
			wo.setValue(this.docx(bytes));
			break;
		default:
			throw new ExceptionUnsupportType(type);
		}
		result.setData(wo);
		return result;
	}

	private String doc(byte[] bytes) throws ParserConfigurationException, IOException, TransformerException {
		WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
				DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
		wordToHtmlConverter.setPicturesManager((byte[] content, PictureType pictureType, String suggestedName,
				float widthInches, float heightInches) -> suggestedName);
		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			HWPFDocument wordDocument = new HWPFDocument(in);
			wordToHtmlConverter.processDocument(wordDocument);
			Document htmlDocument = wordToHtmlConverter.getDocument();
			DOMSource domSource = new DOMSource(htmlDocument);
			StreamResult streamResult = new StreamResult(out);
			TransformerFactory tf = TransformerFactory.newDefaultInstance();
			Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());// 编码格式
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");// 是否用空白分割
			serializer.setOutputProperty(OutputKeys.METHOD, "html");// 输出类型
			serializer.transform(domSource, streamResult);
			return new String(out.toByteArray(), StandardCharsets.UTF_8);
		}
	}

	private String docx(byte[] bytes) throws IOException {
		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			XWPFDocument document = new XWPFDocument(in);
			Options options = Options.getFrom("DOCX");
			XHTMLOptions xhtmlOptions = XWPF2XHTMLConverter.getInstance().toXHTMLOptions(options);
			XHTMLConverter.getInstance().convert(document, out, xhtmlOptions);
			return new String(out.toByteArray(), StandardCharsets.UTF_8);
		}
	}

	@Schema(name = "com.x.general.assemble.control.jaxrs.office.ActionToHtml$Wo")
	public static class Wo extends WrapString {
		private static final long serialVersionUID = 6581539366197332222L;
	}

}