import javax.naming.Context;
import javax.xml.soap.Text;
import java.util.StringTokenizer;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    public void map(Object key, Text value, Context context) {
        StringTokenizer itr = new StringTokenizer(value.toString());
        while (itr.hasMoreTokens()) {
            word.set(itr.nextToken());
            context.write(word, one);
        }
    }

    public void reduce(Text key, Iterable values,Context context){
        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();
        }
        result.set(sum);
        context.write(key, result);
    }
}