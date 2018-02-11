package textanalysis;;

public class TextParser {
	public static void main(String[] args){
		Parser parser = new Parser("What's the weather in Auburn on February 21st, 2018 at 5:23 pm");
		
		for(int i=0; i<parser.parts.length; i++){
			System.out.println(parser.parts[i]);
		}
	}
}
