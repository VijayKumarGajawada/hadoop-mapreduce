package mapred.ipldata.programs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class SeasonLastThreeTeams {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration cobj = new Configuration();
		Job jobj = Job.getInstance(cobj," ");
		jobj.setJarByClass(SeasonLastThreeTeams.class);
		jobj.setMapperClass(MyMapper.class);
		jobj.setReducerClass(MyReducer.class);
		jobj.setMapOutputKeyClass(Text.class);
		jobj.setMapOutputValueClass(Text.class);
		jobj.setOutputKeyClass(Text.class);
		jobj.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(jobj, new Path(args[0]));
		FileSystem.get(cobj).delete(new Path(args[1]), true);
		FileOutputFormat.setOutputPath(jobj, new Path(args[1]));
		System.exit(jobj.waitForCompletion(true) ? 0 : 1);
	}

public static class MyMapper extends Mapper<LongWritable,Text,Text,Text>
{
	public void map(LongWritable key, Text value, Context contx) throws IOException, InterruptedException
	{
		String strValue = value.toString();
		String[] valueArr = strValue.split(",");
		String season = valueArr[1];
		String team = valueArr[10];
		String result = valueArr[8];
		if(!result.matches("no result"))
		{
			String  w_1 = team + "," + 1;
			contx.write(new Text(season), new Text(w_1));
		}
	}
}

public static class MyReducer extends Reducer<Text,Text,Text,Text>
{
	public void reduce(Text key,Iterable<Text> values, Context context) throws IOException, InterruptedException
	{
		TreeMap<Integer,String> tmap = new TreeMap<>();
		HashMap<String,Integer> hmap = new HashMap<>();
		int count = 0;
		for(Text val : values)
		{
			String valArr[] = val.toString().split(",");
			String team = valArr[0];
			count = Integer.parseInt(valArr[1]);
			if(hmap.containsKey(team))
			{
				int currentCount = hmap.get(team);
				hmap.put(team, currentCount+count);
			}
			else
			{
				hmap.put(team, count);
			}
		}
		for(Map.Entry<String,Integer> entry : hmap.entrySet())
		{
			String team = entry.getKey();
			int teamCount = entry.getValue();
			tmap.put(teamCount,team);
			if(tmap.size() > 3)
			{
				tmap.remove(tmap.lastKey());
			}
		}
		context.write(key, new Text(tmap.values().toString()));
	}
}
}
