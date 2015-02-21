package storm.example1;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import storm.trident.TridentTopology;
import storm.trident.operation.BaseFilter;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;


public class TemplateTridentTopology {

    private static class Exclamation extends BaseFunction {
        @Override
        public void execute(TridentTuple tuple, TridentCollector collector) {
            collector.emit(new Values(tuple.getString(0) + "!"));
        }
    }

    private static class PrintFilter extends BaseFilter {
        @Override
        public boolean isKeep(TridentTuple tuple) {
            System.out.println(String.format("--- %s ---", tuple.getString(0)));
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        TridentTopology topology = new TridentTopology();
        topology.newStream("spout1", new TestWordSpout())
                .each(new Fields("word"), new Exclamation(), new Fields("first_exclaim"))
                .each(new Fields("first_exclaim"), new Exclamation(), new Fields("second_exclaim"))
                .each(new Fields("second_exclaim"), new PrintFilter());

        Config conf = new Config();
        conf.setDebug(true);

        if (args != null && args.length > 0) {
            conf.setNumWorkers(2);
            // Submit to remote Storm cluster
            StormSubmitter.submitTopology(args[0], conf, topology.build());
        } else {
            // Start a local Storm cluster for testing
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("trident-topo-exclaim", conf, topology.build());
            Utils.sleep(20000);
            cluster.killTopology("trident-topo-exclaim");
            cluster.shutdown();
        }
    }


}