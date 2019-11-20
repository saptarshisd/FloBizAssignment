package in.flobizAPI.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Dummy extends ApiUtils {
//	public static String getImageInBase64Encoded(String url) throws IOException {
//		URL obj;
//
//		obj = new URL(url);
//
//		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//		con.setRequestMethod("GET");
//
//		String encoded = Base64.encodeBase64String((IOUtils.toByteArray(con.getInputStream())));
//		return encoded;
//
//	}

	public static void main(String[] args) throws IOException {

//		URL obj = new URL("https://randomuser.me/api/?results=49");
//		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//		con.setRequestMethod("GET");
//		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//		String inputLine;
//		StringBuffer response = new StringBuffer();
//
//		while ((inputLine = in.readLine()) != null) {
//			response.append(inputLine);
//		}
//		in.close();
//
//		// print result
//		System.out.println(response.toString());
//		JsonPath path = new JsonPath(response.toString());
//		List<HashMap<String, HashMap<String, String>>> result = path.get("results");
//		HashMap<String, String> nameAndImage = new HashMap<String, String>();
//		for (int i = 0; i < result.size(); i++) {
//			System.out.println(result.get(i).get("name").get("title") + " " + result.get(i).get("name").get("first")
//					+ " " + result.get(i).get("name").get("last"));
//			System.out.println(getImageInBase64Encoded(result.get(i).get("picture").get("medium")));
//		}

		// Map<String,String> headers = new HashMap<String,String>();
		// headers.put(, value)
		//
		//// Response response = given().header("cache-control",
		// "no-cache").header("postman-token",
		// "1db6d1bd-5f26-4b8c-934d-ec26449b1ddd").get("https://randomuser.me/api/?results=49");
		// Response response = when().get();

		 File file = new File("./test-data/jsons/dummy.vm");
		 try {
		 int i = 71;
		 int j = 0;
		 StringBuilder sb = new StringBuilder();
		 BufferedReader br = new BufferedReader(new FileReader(file));
		 while (br.ready()) {
		 String s = br.readLine();
		 if (s.contains("variable")) {
		
		 sb.append(s.replaceAll("variable", "variable" + i));
		 sb.append("\n");
		 j++;
		 if (j % 2 == 0) {
		 i++;
		 }
		 } else {
		 sb.append(s+"\n");
		 }
		
		 }
		 System.out.println(sb);
		 } catch (FileNotFoundException e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		 } catch (IOException e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		 }

		// Format formatter = new SimpleDateFormat("dd MM yyyy HH mm");
		// String todaysDate = formatter.format(new Date().getTime());
		// System.out.println(todaysDate);

	}

}
