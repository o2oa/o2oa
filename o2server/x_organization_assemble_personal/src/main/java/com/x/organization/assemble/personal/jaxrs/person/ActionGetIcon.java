package com.x.organization.assemble.personal.jaxrs.person;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

class ActionGetIcon extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			CacheKey cacheKey = new CacheKey(this.getClass(), effectivePerson.getDistinguishedName());
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((Wo) optional.get());
			} else {
				Wo wo = this.get(business, effectivePerson);
				CacheManager.put(business.cache(), cacheKey, wo);
				result.setData(wo);
			}
			return result;
		}
	}

	private Wo get(Business business, EffectivePerson effectivePerson) throws Exception {
		String base64;
		if (Config.token().isInitialManager(effectivePerson.getDistinguishedName())) {
			/* 如果是xadmin单独处理 */
			base64 = com.x.base.core.project.config.Person.ICON_MANAGER;
		} else {
			Person person = business.person().pick(effectivePerson.getDistinguishedName());
			if (null == person) {
				throw new ExceptionPersonNotExist(effectivePerson.getDistinguishedName());
			}
			base64 = person.getIcon();
			if (StringUtils.isEmpty(base64)) {
				base64 = this.generate(person.getName(), 20, new Color(0xE6E6E6),new Color(0x666666));
//				if (Objects.equals(GenderType.m, person.getGenderType())) {
//					base64 = com.x.base.core.project.config.Person.ICON_MALE;
//				} else if (Objects.equals(GenderType.f, person.getGenderType())) {
//					base64 = com.x.base.core.project.config.Person.ICON_FEMALE;
//				} else {
//					base64 = com.x.base.core.project.config.Person.ICON_UNKOWN;
//				}
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
			Font font = this.pickFont((int) (size * 0.5));
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

	private Font pickFont(int pixel) {
		// 尝试常见中文家族名，但不做任何 canDisplay 检测；失败也会映射到逻辑字体
		String[] families = { "微软雅黑", "宋体", "黑体", "SimHei", "Noto Sans CJK SC", "Source Han Sans SC", // 宿主机装了更好
				"Microsoft YaHei", "PingFang SC", "WenQuanYi Zen Hei", "SimHei", Font.SANS_SERIF, "Dialog" // 逻辑字体兜底
		};
		for (String fam : families) {
			try {
				return new Font(fam, Font.PLAIN, pixel);
			} catch (Exception ignore) {
				// 保守：new Font 基本不会抛，但这里仍然兜底
			}
		}
		return new Font(Font.SANS_SERIF, Font.PLAIN, pixel);
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}
