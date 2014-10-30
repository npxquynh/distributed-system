package mst;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MyFirstApp {
	//public
	Network network;
	
	// private
	private final Charset ENCODING = StandardCharsets.US_ASCII;  
	
	public MyFirstApp(String fileName, Network network) throws Exception {
		this.network = network;
		
		List<String> lines = this.readTextFile(fileName);
		this.processInput(lines);
	}
	
	/**
	 * Process lines after lines of the input file
	 *
	 * @param  lines	a list of lines in input file
	 * @throws Exception
	 */
	private void processInput(List<String> lines) throws Exception {
		// 1st line: minimum budget
		double minBudget = Double.valueOf(lines.get(0));
		this.network.setMinBudget(minBudget);
		
		lines.remove(0);
		
		for(String line : lines) {
			String regex1 = "^(node).*";
			String regex2 = "^(bcst).*";
			if(line.matches(regex1)) {
				Pattern pattern = Pattern.compile("^node.(.*)");
				
				Matcher m = pattern.matcher(line);
				if (m.find()) {
					String foundString = m.group(1);
					String[] parts = foundString.split(", ");
					if (parts.length != 4) {
						throw new Exception();
					}
					else {
						int id = Integer.valueOf(parts[0]);
						double posX = Float.valueOf(parts[1]);
						double posY = Float.valueOf(parts[2]);
						double energy = Float.valueOf(parts[3]);
						
						Node node = new Node(id, posX, posY, energy, minBudget);
						network.addNode(node);
					}
					for (String s : parts) {
						System.out.println(s);
					}
					
				}
			}
			else if (line.matches(regex2)) {
				System.out.println(line);
			}			
		}
	}
	
	List<String> readTextFile(String fileName) throws IOException {
		Path path = Paths.get("./input/input0.txt");
		return Files.readAllLines(path, ENCODING);
	}
	
	
}
