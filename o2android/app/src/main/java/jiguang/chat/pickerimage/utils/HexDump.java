package jiguang.chat.pickerimage.utils;

import java.io.IOException;
import java.io.StringReader;

public class HexDump {
	class HexTablifier {
		private int m_row = 8;

		private String m_pre = "";

		private String m_post = "\n";

		public HexTablifier() {
		}

		public HexTablifier(int row) {
			this(row, "", "\n");
		}

		public HexTablifier(int row, String pre) {
			this(row, pre, "\n");
		}

		public HexTablifier(int row, String pre, String post) {
			m_row = row;
			m_pre = pre;
			m_post = post;
		}

		public String format(String hex) {
			StringReader reader = new StringReader(hex);
			StringBuilder builder = new StringBuilder(hex.length() * 2);

			try {
				while (getHexLine(builder, reader)) {
				}
			} catch (IOException e) {
				// 不应该有异常出现。
			}

			return builder.toString();
		}

		private boolean getHexLine(StringBuilder builder, StringReader reader)
				throws IOException {
			StringBuilder lineBuilder = new StringBuilder();
			boolean result = true;

			for (int i = 0; i < m_row; i++) {
				result = getHexByte(lineBuilder, reader);

				if (result == false)
					break;
			}

			if (lineBuilder.length() > 0) {
				builder.append(m_pre);
				builder.append(lineBuilder);
				builder.append(m_post);
			}

			return result;
		}

		private boolean getHexByte(StringBuilder builder, StringReader reader)
				throws IOException {
			char[] hexByte = new char[4];
			int bytesRead = reader.read(hexByte);

			if (bytesRead == -1)
				return false;

			builder.append(hexByte, 0, bytesRead);
			builder.append(" ");

			return bytesRead == 4;
		}
	}

	private static final char m_hexCodes[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static final int m_shifts[] = { 60, 56, 52, 48, 44, 40, 36, 32, 28,
			24, 20, 16, 12, 8, 4, 0 };

	public static String tablify(byte[] bytes) {
		return (new HexDump()).new HexTablifier().format(HexDump.toHex(bytes));
	}

	public static String tablify(byte[] bytes, int row) {
		return (new HexDump()).new HexTablifier(row).format(HexDump
				.toHex(bytes));
	}

	public static String tablify(byte[] bytes, int row, String pre) {
		return (new HexDump()).new HexTablifier(row, pre).format(HexDump
				.toHex(bytes));
	}

	public static String tablify(String hex, int row, String pre, String post) {
		return (new HexDump()).new HexTablifier(row, pre, post).format(hex);
	}

	private static String toHex(final long value, final int digitNum) {
		StringBuilder result = new StringBuilder(digitNum);

		for (int j = 0; j < digitNum; j++) {
			int index = (int) ((value >> m_shifts[j + (16 - digitNum)]) & 15);
			result.append(m_hexCodes[index]);
		}

		return result.toString();
	}

	public static String toHex(final byte value) {
		return toHex(value, 2);
	}

	public static String toHex(final short value) {
		return toHex(value, 4);
	}

	public static String toHex(final int value) {
		return toHex(value, 8);
	}

	public static String toHex(final long value) {
		return toHex(value, 16);
	}

	public static String toHex(final byte[] value) {
		return toHex(value, 0, value.length);
	}

	public static String toHex(final byte[] value, final int offset,
                               final int length) {
        StringBuilder retVal = new StringBuilder();

		int end = offset + length;
		for (int x = offset; x < end; x++)
			retVal.append(toHex(value[x]));

		return retVal.toString();
	}

    public static byte[] restoreBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; ++i) {
            int c1 = charToNumber(hex.charAt(2 * i));
            int c2 = charToNumber(hex.charAt(2 * i + 1));
            if (c1 == -1 || c2 == -1) {
                return null;
            }
            bytes[i] = (byte) ((c1 << 4) + c2);
        }

        return bytes;
    }

    private static int charToNumber(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else if (c >= 'a' && c <= 'f') {
            return c - 'a' + 0xa;
        } else if (c >= 'A' && c <= 'F') {
            return c - 'A' + 0xA;
        } else {
            return -1;
        }
    }
}
