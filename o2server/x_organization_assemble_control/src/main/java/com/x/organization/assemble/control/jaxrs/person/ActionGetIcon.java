package com.x.organization.assemble.control.jaxrs.person;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;

class ActionGetIcon extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetIcon.class);

	private static Font notoSc = null;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			CacheKey cacheKey = new CacheKey(this.getClass(), flag);
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((Wo) optional.get());
			} else {
				Wo wo = this.get(business, flag);
				CacheManager.put(business.cache(), cacheKey, wo);
				result.setData(wo);
			}
			return result;
		}
	}

	private Wo get(Business business, String flag) throws Exception {
		String base64;
		if (Config.token().isInitialManager(flag)) {
			/* 如果是xadmin单独处理 */
			base64 = com.x.base.core.project.config.Person.ICON_MANAGER;
		} else {
			Person person = business.person().pick(flag);
			if (null == person) {
				Identity identity = business.identity().pick(flag);
				if (identity != null) {
					person = business.person().pick(identity.getPerson());
				} else {
					throw new ExceptionPersonNotExist(flag);
				}
			}
			base64 = person.getIcon();
			if (StringUtils.isEmpty(base64)) {
				base64 = this.generate(person.getName(), 144, new Color(0xE6E6E6), new Color(0x666666));
			}
		}
		byte[] bs = Base64.decodeBase64(base64);
		return new Wo(bs, this.contentType(false, "icon.png"), this.contentDisposition(false, "icon.png"));
	}

	private String generate(String name, int size, Color bgColor, Color fontColor) throws IOException {
		// 只取姓
		String firstChar = name.substring(0, 1).toUpperCase();
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		// 开启多项抗锯齿和字体平滑
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setColor(bgColor);
		g.fillRect(0, 0, size, size);
		// 建议选用本地安装的中文字体，字体不存在时会降级
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			Font font = this.font((int) (size * 0.5));
			g.setFont(font);
			// 计算文字大小，居中
			FontMetrics fm = g.getFontMetrics();
			int x = (size - fm.stringWidth(firstChar)) / 2;
			int y = (size - fm.getHeight()) / 2 + fm.getAscent();
			g.setColor(fontColor);
			g.drawString(firstChar, x, y);
			g.dispose();
			ImageIO.write(image, "png", baos);
			return Base64.encodeBase64String(baos.toByteArray());
		} catch (Throwable ignore) {
			return com.x.base.core.project.config.Person.ICON_UNKOWN;
		}
	}

	private Font font(int pixel) throws Exception {
		if (notoSc == null) {
			synchronized (ActionGetIcon.class) {
				if (null == notoSc) {
					var path = Config.dir_commons_fonts().resolve("NotoSansCJKsc-Regular.otf");
					if (Files.exists(path)) {
						try (InputStream is = Files.newInputStream(path)) {
							notoSc = Font.createFont(Font.TRUETYPE_FONT, is);
						}
					}
				}
				if (null == notoSc) {
					notoSc = new Font(Font.SANS_SERIF, Font.PLAIN, pixel);
				}
			}
		}
		return notoSc.deriveFont(Font.PLAIN, pixel);
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}
