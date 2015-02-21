package storm.example5

import backtype.storm.Config
import backtype.storm.LocalCluster
import backtype.storm.StormSubmitter
import backtype.storm.generated.StormTopology
import backtype.storm.topology.TopologyBuilder
import backtype.storm.utils.Utils
import groovy.util.logging.Slf4j

@Slf4j
class TwitterTopology {
    public static void main(String[] args) {
        Config config = getConfig(args)
        String topologyName = config.get("topology-name", "local")
        if (topologyName == "local") {
            log.debug config.get(TwitterStreamingSpout.TWITTER_USERNAME_KEY)
            log.debug config.get(TwitterStreamingSpout.TWITTER_FILTER_KEY)
            def cluster = new LocalCluster()
            cluster.submitTopology(topologyName, config, createTopology(config))
            Utils.sleep(5 * 60 * 1000);
            cluster.shutdown()
        } else {
            StormSubmitter.submitTopology(topologyName, config, createTopology(config));
        }
    }

    static StormTopology createTopology(Config config) {

        TopologyBuilder builder = new TopologyBuilder()
        builder.setSpout("twitter-spout", new TwitterStreamingSpout(config), 1)
        builder.setBolt("printer-bolt", new PrinterBolt(), 1).shuffleGrouping("twitter-spout")

        return builder.createTopology()
    }

    static Config getConfig(String[] args) {
        Config config = new Config()

        config.put(TwitterStreamingSpout.TWITTER_USERNAME_KEY, System.getenv("TWITTER_USERNAME"))
        config.put(TwitterStreamingSpout.TWITTER_PASSWORD_KEY, System.getenv("TWITTER_PASSWORD"))

        for (int i=0; i<args.length; i++) {

            if ("--name".equals(args[i]) && args.length > i+1)
                config.put("topology-name", args[i+1])
            if ("--filter".equals(args[i]) && args.length > i+1)
                config.put(TwitterStreamingSpout.TWITTER_FILTER_KEY, args[i+1])
        }

        return config
    }
}