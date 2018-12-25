package com.x.general.assemble.control.jaxrs.office;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
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

public class ActionToHtml extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionToHtml.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Tika tika = new Tika();
		String type = tika.detect(bytes);
		Wo wo = new Wo();
		switch (type) {
		case ("application/msword"):
			wo.setValue(this.doc(bytes));
			break;
		case ("application/vnd.openxmlformats-officedocument.wordprocessingml.document"):
			wo.setValue(this.docx(bytes));
			break;
		default:
			throw new ExceptionUnsupportType(type);
		}
		result.setData(wo);
		return result;
	}

	private String doc(byte[] bytes) throws Exception {
		WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
				DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
		wordToHtmlConverter.setPicturesManager(new PicturesManager() {
			public String savePicture(byte[] content, PictureType pictureType, String suggestedName, float widthInches,
					float heightInches) {
				return suggestedName;
			}
		});
		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			HWPFDocument wordDocument = new HWPFDocument(in);
			wordToHtmlConverter.processDocument(wordDocument);
			Document htmlDocument = wordToHtmlConverter.getDocument();
			DOMSource domSource = new DOMSource(htmlDocument);
			StreamResult streamResult = new StreamResult(out);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "GB2312");// 编码格式
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");// 是否用空白分割
			serializer.setOutputProperty(OutputKeys.METHOD, "html");// 输出类型
			serializer.transform(domSource, streamResult);
			String content = new String(out.toByteArray());
			return content;
		}
	}

	private String docx(byte[] bytes) throws Exception {
		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			XWPFDocument document = new XWPFDocument(in);
			XHTMLOptions options = XHTMLOptions.create();
			XHTMLConverter.getInstance().convert(document, out, options);
			return new String(out.toByteArray());
		}
	}

	public static class Wo extends WrapString {

	}

}
