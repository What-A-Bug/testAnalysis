package textanalysis;;

public class TextParser {
	public static void main(String[] args){
		Parser parser = new Parser("What's the weather in Auburn at 5:23 pm yesterday?");
		System.out.println(parser.getlat());
		System.out.println(parser.getlon());
		System.out.println(parser.getdate());
		System.out.println(parser.gettime());
	}
}
