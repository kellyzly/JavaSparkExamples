import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

public class SimpleApp {
    //Some example about how to use JavaRDD

    public static void main2(String[] args) {
        String logFile = "input.txt";
        JavaSparkContext sc = new JavaSparkContext("local", "Simple App");
        JavaRDD<String> logData = sc.textFile(logFile).cache();

        long numAs = logData.filter(new Function<String, Boolean>() {
            public Boolean call(String s) {
                return s.contains("a");
            }
        }).count();

        long numBs = logData.filter(new Function<String, Boolean>() {
            public Boolean call(String s) {
                return s.contains("b");
            }
        }).count();

        if (numAs != 2 || numBs != 2) {
            System.out.println("Failed to parse log files with Spark");
            System.exit(-1);
        }
        System.out.println("Test succeeded");
        sc.stop();
    }


    public static void main1(String[] args) {
//        Tuple2<Integer,String> t0 = new Tuple2<Integer, String>(100, "banana");
//        Tuple2<Integer,String> t1 = new Tuple2<Integer, String>(100, "apple");
//        Tuple2<Integer,String> t2 = new Tuple2<Integer, String>(200, "orange");
//        List<Tuple2<Integer,String>> data = Arrays.asList(t0,t1,t2);
        List<String> data0
                = Arrays.asList("100,banana", "100,apple", "200,orange");
        JavaSparkContext sc = new JavaSparkContext("local", "Simple App");
        JavaRDD<String> data1 = sc.parallelize(data0);
//        JavaRDD<Tuple2<Integer,String>> data2 = data1.map(new Function<String, Tuple2<Integer,
//                String>>(){
//              public Tuple2<Integer,String> call(String s){
//                 String[] tmp = s.split(",");
//                 return new Tuple2<Integer, String>(Integer.parseInt(tmp[0]),tmp[1]);
//              }
//        });
        JavaPairRDD<Integer, String> data3 = data1.mapToPair(new PairFunction<String, Integer,
                String>() {
            public Tuple2<Integer, String> call(String s) {
                String[] tmp = s.split(",");
                return new Tuple2<Integer, String>(Integer.parseInt(tmp[0]), tmp[1]);
            }
        });
        JavaPairRDD<Integer, String> data4 = data3.sortByKey();
        data4.map(new Function<Tuple2<Integer, String>, String>() {
            public String call(Tuple2<Integer, String> t) {
                System.out.println("t._1:" + t._1());
                System.out.println("t._2:" + t._2());

                return t._2();
            }
        });

        long num = data4.count();
        System.out.println("num:" + num);

    }


    public static void main(String[] args) {
        List<String> data0
                = Arrays.asList("100,banana", "100,apple", "200,orange");
        JavaSparkContext sc = new JavaSparkContext("local", "Simple App");
        JavaRDD<String> data1 = sc.parallelize(data0);
        JavaRDD<Integer> data3 = data1.map(new Function<String,
                Integer>() {
            @Override
            public Integer call(String s) throws Exception {
                String[] tmp = s.split(",");
                return Integer.parseInt(tmp[0]);

            }
        });

        int res = data3.reduce(new Function2<Integer, Integer, Integer>() {

            @Override
            public Integer call(Integer a, Integer b) throws Exception {
                return a + b;
            }
        });
        System.out.println("res:" + res);
    }
}
