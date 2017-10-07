package com.pickdata.search;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapFile.Reader;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapFileOutputFormat;
import org.apache.hadoop.mapred.lib.HashPartitioner;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/*
 * hdfs에서 검색하는 프로그램 : 204페이지
 */
public class SearchValueListC11 extends Configured implements Tool {

	static Log log = LogFactory.getLog(SearchValueListC11.class);

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			ToolRunner.printGenericCommandUsage(System.out);

			args = new String[] { "-fs", "hdfs://bigdata01:9000", "-jt", "bigdata01:9001", };
			System.out.println(Arrays.toString(args));
			log.info(Arrays.toString(args));
		}
		ToolRunner.run(new SearchValueListC11(), args);
	}

	@Override
	public int run(String[] args) throws Exception {

		Path path = new Path("/home/java/pickdata/sample/mapfile/c11");
		
		//map file 조회
		// 배열을 리턴함 [STREAM] = 통의 수를 리턴
		Reader[] reader = MapFileOutputFormat.getReaders(FileSystem.get(getConf()), path, getConf());

		
		//id로 해당 값 검색 - 15(test)
//		String id = args[1];
		String id = "15";
		Text findKey = new Text();
		findKey.set(id);
		
		//검색값을 저장할 객체 선언
		Text value = new Text();

		// 파티셔너를 이용해 검색 키가 저장된 맵 파일을 조회 - bucket을 찾는 방법 2
		int bucket = new HashPartitioner<Text, Text>().getPartition(findKey, value, reader.length);
		System.out.println("bucket = " + bucket);
		Reader r = reader[bucket];

		//검색 결과 확인
		if (r.get(findKey, value) == null) {
			System.out.println("not found key =" + findKey.toString());
			log.info("not found...key =" + findKey.toString());

			// 찾은게 없으면 프로그램 종료
			System.exit(-1);
		}
		int cnt = 0;
		System.out.println(value);
		cnt++;

		IntWritable nextKey = new IntWritable();

		// 현재의 통에서 같은 key를 갖고 있는 데이터를 모두 출력해보자
		while (r.next(nextKey, value)) {
			if (findKey.equals(nextKey)) {
				System.out.println(value);
				cnt++;
			} else
				break;
		}
		System.out.println(findKey.toString() + ", cnt = " + cnt);
		return 0;
	}
}