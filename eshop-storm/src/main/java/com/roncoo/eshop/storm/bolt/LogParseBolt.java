package com.roncoo.eshop.storm.bolt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.storm.spout.AccessLogKafkaSpout;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 日志解析bolt
 */
public class LogParseBolt extends BaseRichBolt {

    private static final Logger logger = LoggerFactory.getLogger(LogParseBolt.class);

    private OutputCollector collector;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.collector=collector;
    }

    @Override
    public void execute(Tuple tuple) {
        String message = tuple.getStringByField("message");
        logger.info("[LogParseBolt接收到一条日志]message="+message);
        JSONObject messageJson = JSONObject.parseObject(message);
        JSONObject uriArsJson = messageJson.getJSONObject("uri_args");
        Long productId = uriArsJson.getLong("productId");

        if(productId!=null){
            collector.emit(new Values(productId));
            logger.info("[LogParseBolt发射出去一个商品id]productId="+productId);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("productId"));
    }
}
