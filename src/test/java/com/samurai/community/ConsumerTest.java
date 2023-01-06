package com.samurai.community;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ConsumerTest {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers","192.168.198.140:9092");
        props.setProperty("group.id", "test");

        props. setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props. setProperty( "value.deserializer","org.apache.kafka.common.serialization.StringDeserializer" ) ;


        KafkaConsumer<String,String> kafkaConsumer=new KafkaConsumer<String, String>(props);
        kafkaConsumer.subscribe(Arrays.asList("first"));
        while(true) {
            ConsumerRecords<String, String> consumerRecords =kafkaConsumer.poll(Duration.ofSeconds(5));
            for(ConsumerRecord<String, String> consumerRecord:consumerRecords){
                String topic=consumerRecord.topic();
                long offset=consumerRecord.offset();
                String key=consumerRecord.key();
                String value=consumerRecord.value();
                System.out.println( "topic:" + topic + " offset:" + offset + " key:" + key + " value:"+value);
            }
        }

    }
}
