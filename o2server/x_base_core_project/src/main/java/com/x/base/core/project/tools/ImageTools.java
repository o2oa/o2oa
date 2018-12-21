package com.x.base.core.project.tools;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class ImageTools {

	private final static int HUE_FACTOR = 16;

	public static String hue(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int r = 0;
		int g = 0;
		int b = 0;
		int rr = 0;
		int gg = 0;
		int bb = 0;
		List<String> list = new ArrayList<>();
		for (int w = 0; w < width; w++) {
			for (int h = 0; h < height; h++) {
				int pixel = image.getRGB(w, h);
				r = (pixel & 0xff0000) >> 16;
				// r = pixel >>> 16;
				g = (pixel & 0xff00) >> 8;
				b = (pixel & 0xff);
				rr = (((r + HUE_FACTOR) / HUE_FACTOR) * HUE_FACTOR) - 1;
				gg = (((g + HUE_FACTOR) / HUE_FACTOR) * HUE_FACTOR) - 1;
				bb = (((b + HUE_FACTOR) / HUE_FACTOR) * HUE_FACTOR) - 1;
				list.add(rr + "," + gg + "," + bb);
			}
		}
		Map<String, Long> map = list.stream().collect(Collectors.groupingBy(p -> p, Collectors.counting()));
		Optional<Entry<String, Long>> o = map.entrySet().stream().max(Comparator.comparing(Entry::getValue));
		String str = o.get().getKey();
		String[] rgb = str.split(",");
		String value = "#";
		value += Integer.toHexString(Integer.parseInt(rgb[0]));
		value += Integer.toHexString(Integer.parseInt(rgb[1]));
		value += Integer.toHexString(Integer.parseInt(rgb[2]));
		return value;
	}

}
