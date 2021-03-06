package mapreduce.imdb5000.progs;

import java.io.IOException;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Top10BWscore {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration cobj = new Configuration();
		Job jobj = Job.getInstance(cobj," ");
		jobj.setJarByClass(Top10BWscore.class);
		jobj.setMapperClass(MyMapper.class);
		jobj.setReducerClass(MyReducer.class);
		jobj.setMapOutputKeyClass(Text.class);
		jobj.setMapOutputValueClass(FloatWritable.class);
		jobj.setOutputKeyClass(NullWritable.class);
		jobj.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(jobj, new Path(args[0]));
		FileSystem.get(cobj).delete(new Path(args[1]), true);
		FileOutputFormat.setOutputPath(jobj, new Path(args[1]));
		System.exit(jobj.waitForCompletion(true) ? 0 : 1);
	}

public static class MyMapper extends Mapper<LongWritable,Text,Text,FloatWritable>
{
	public void map(LongWritable key, Text value, Context contx) throws IOException, InterruptedException
	{
		String strValue = value.toString();
		String[] valueArr = strValue.split(",");
		if(valueArr[0].matches(" Black and White"))
		{
			float score = Float.parseFloat(valueArr[2]);
			String movieTitle = valueArr[3];
			contx.write(new Text(movieTitle), new FloatWritable(score));
		}
	}
}

public static class MyReducer extends Reducer<Text,FloatWritable,NullWritable,Text>
{
	TreeMap<Float,String> tmap = new TreeMap<>();
	public void reduce(Text key,Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException
	{
		for(FloatWritable val : values)
		{
			float score=val.get();
			tmap.put(score, key.toString());
		}
	}
	public void cleanup(Context context) throws IOException, InterruptedException
	{
		while(tmap.size()>10)
		{
			tmap.remove(tmap.firstKey());
		}
		context.write(NullWritable.get(),new Text(tmap.descendingMap().toString()));
	}
}
}
