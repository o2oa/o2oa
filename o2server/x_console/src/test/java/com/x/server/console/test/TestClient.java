package com.x.server.console.test;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.StringTools;

public class TestClient {

	@Test
	public void test1() throws Exception {
		int width = 100;
		int height = 30;

		// BufferedImage image = ImageIO.read(new
		// File("/Users/mkyong/Desktop/logo.jpg"));
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		// g.setFont(new Font("SansSerif", Font.BOLD, 24));

		Graphics2D graphics = (Graphics2D) g;
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.drawString("JAVA", 10, 20);

		// save this image
		// ImageIO.write(image, "png", new File("/users/mkyong/ascii-art.png"));

		for (int y = 0; y < height; y++) {
			StringBuilder sb = new StringBuilder();
			for (int x = 0; x < width; x++) {

				sb.append(image.getRGB(x, y) == -16777216 ? " " : "$");

			}

			if (sb.toString().trim().isEmpty()) {
				continue;
			}

			System.out.println(sb);
		}

	}

	@Test
	public void test() {
		List<String> list = new ArrayList<>();
		list.add("com.x.base.core.project.x_attendance_assemble_control");
		list.add("com.x.base.core.project.x_bbs_assemble_control");
		list.add("com.x.base.core.project.x_calendar_assemble_control");
		list.add("com.x.base.core.project.x_cms_assemble_control");
		list.add("com.x.base.core.project.x_collaboration_assemble_websocket");
		list.add("com.x.base.core.project.x_collaboration_service_message");
		list.add("com.x.base.core.project.x_component_assemble_control");
		list.add("com.x.base.core.project.x_file_assemble_control");
		list.add("com.x.base.core.project.x_general_assemble_control");
		list.add("com.x.base.core.project.x_hotpic_assemble_control");
		list.add("com.x.base.core.project.x_meeting_assemble_control");
		list.add("com.x.base.core.project.x_message_assemble_communicate");
		list.add("com.x.base.core.project.x_mind_assemble_control");
		list.add("com.x.base.core.project.x_okr_assemble_control");
		list.add("com.x.base.core.project.x_organization_assemble_authentication");
		list.add("com.x.base.core.project.x_organization_assemble_control");
		list.add("com.x.base.core.project.x_organization_assemble_custom");
		list.add("com.x.base.core.project.x_organization_assemble_express");
		list.add("com.x.base.core.project.x_organization_assemble_personal");
		list.add("com.x.base.core.project.x_portal_assemble_designer");
		list.add("com.x.base.core.project.x_portal_assemble_surface");
		list.add("com.x.base.core.project.x_processplatform_assemble_bam");
		list.add("com.x.base.core.project.x_processplatform_assemble_designer");
		list.add("com.x.base.core.project.x_processplatform_assemble_surface");
		list.add("com.x.base.core.project.x_processplatform_service_processing");
		list.add("com.x.base.core.project.x_query_assemble_designer");
		list.add("com.x.base.core.project.x_query_assemble_surface");
		list.add("com.x.base.core.project.x_query_service_processing");
		List<String> in = new ArrayList<>();
		List<String> ex = new ArrayList<>();
		ex.add("com.x.base.core.project.x_mind_assemble_*");
		ex.add("com.x.base.core.project.x_okr_assemble_*");
		ex.add("com.x.base.core.project.x_hotpic_assemble_*");
		ex.add("com.x.base.core.project.x_calendar_*");
		ex.add("com.x.base.core.project.x_attendance_*");
		ex.add("com.x.base.core.project.x_meeting_*");
		ex.add("com.x.base.core.project.x_file_*");
		list = StringTools.includesExcludesWithWildcard(list, in, ex);
		System.out.println(XGsonBuilder.toJson(list));
		System.out.println(StringTools.matchWildcard("com.x.base.core.project.x_hotpic_assemble_control", "com.x.base.core.project.x_hotpic_assemble_*"));
	}

}
