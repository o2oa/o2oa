package test.tika;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

public class TestClient {

	public static void main(String[] args) throws IOException, TikaException {

		Tika tika = new Tika();

		var bytes = Files.readAllBytes(Paths.get("/data/Temp/我的世界观.pdf"));

		try (InputStream input = new ByteArrayInputStream(bytes)) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!" + input.available());
			System.out.println(tika.parseToString(input));
		}

	}

}
