package com.x.file.assemble.control.servlet.file;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.StorageMapping;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.File;
import com.x.file.core.entity.open.ReferenceType;

@WebServlet(urlPatterns = "/servlet/file/upload/referencetype/*")
@MultipartConfig
public class ActionFileUpload extends AbstractServletAction {

	private static Logger logger = LoggerFactory.getLogger(ActionFileUpload.class);

	private static final long serialVersionUID = 5628571943877405247L;

	private static final String PART_REFERENCETYPE = "referencetype";

	private static final String PART_REFERENCE = "reference";

	private static final String PART_SCALE = "scale";

	@HttpMethodDescribe(value = "创建File保存对象.url格式为:/servlet/file/upload/referencetype/{referencetype}/reference/{reference}/scale/{scale}", response = WrapOutId.class)
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			WrapOutId wrap = new WrapOutId();
			this.setCharacterEncoding(request, response);
			if (!this.isMultipartContent(request)) {
				throw new NotMultiPartRequestException();
			}
			String reference = this.getURIPart(request.getRequestURI(), PART_REFERENCE);
			String referenceTypeString = this.getURIPart(request.getRequestURI(), PART_REFERENCETYPE);
			ReferenceType referenceType = EnumUtils.getEnum(ReferenceType.class, referenceTypeString);
			String scaleString = this.getURIPart(request.getRequestURI(), PART_SCALE);
			Integer scale = NumberUtils.toInt(scaleString, 0);
			if (null == referenceType) {
				throw new InvalidReferenceTypeException(referenceTypeString);
			}
			if (StringUtils.isEmpty(reference)) {
				throw new EmptyReferenceException(reference);
			}
			FileItemIterator fileItemIterator = this.getItemIterator(request);
			while (fileItemIterator.hasNext()) {
				FileItemStream item = fileItemIterator.next();
				if (!item.isFormField()) {
					String id = this.save(effectivePerson, referenceType, reference, item, scale);
					wrap = new WrapOutId(id);
				}
			}
			result.setData(wrap);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		this.result(response, result);
	}

	private String save(EffectivePerson effectivePerson, ReferenceType referenceType, String reference,
			FileItemStream item, Integer scale) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				InputStream in = item.openStream()) {
			StorageMapping mapping = ThisApplication.context().storageMappings().random(File.class);
			if (null == mapping) {
				throw new AllocateStorageMaapingException();
			}
			/** 由于这里需要根据craeteTime创建path,先进行赋值,再进行校验,最后保存 */
			/** 禁止不带扩展名的文件上传 */
			if (StringUtils.isEmpty(FilenameUtils.getExtension(item.getName()))) {
				throw new EmptyExtensionException(item.getName());
			}
			File file = new File(mapping.getName(), item.getName(), effectivePerson.getName(), referenceType,
					reference);
			emc.check(file, CheckPersistType.all);
			if ((scale > 0) && ArrayUtils.contains(IMAGE_EXTENSIONS, file.getExtension())) {
				/** 如果是需要压缩的附件 */
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					BufferedImage image = ImageIO.read(in);
					BufferedImage scalrImage = Scalr.resize(image, Method.SPEED, Mode.FIT_TO_WIDTH, scale);
					ImageIO.write(scalrImage, file.getExtension(), baos);
					try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
						file.saveContent(mapping, bais, FilenameUtils.getName(item.getName()));
					}
				}
			} else {
				file.saveContent(mapping, in, FilenameUtils.getName(item.getName()));
			}
			emc.beginTransaction(File.class);
			emc.persist(file);
			emc.commit();
			return file.getId();
		}
	}
}