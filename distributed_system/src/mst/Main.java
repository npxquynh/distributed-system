package mst;

import java.util.List;

	public class Main {
		/**
		 * @param args
		 * @throws Exception 
		 */
		public static void main(String[] args) throws Exception {
			String fileName = "./input/input0.txt";
			Network network = new Network();
			
			MyFirstApp app = new MyFirstApp(fileName, network);
			
			network.simulate();
		}
}
