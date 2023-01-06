package com.samurai.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaProducerTest {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Properties props = new Properties();
        props.load(new FileReader("src/main/resources/kafkaConn.properties"));

/*        props.put("bootstrap.servers","192.168.198.140:9092");
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        System.out.println(props.get("boostrap.servers"));
*/
        KafkaProducer<String,String> kafkaProducer = new KafkaProducer<>(props);
        for(int i=0;i<20000;i++){
            ProducerRecord<String,String> producerRecord=new ProducerRecord<String,String>("testJc",null,"1:Mercedes\n" +
                    "2:BMW\n" +
                    "3:Audi\n" +
                    "4:Cadillac\n" +
                    "5:Chevrolet\n" +
                    "6:Porsche\n" +
                    "7:Lamborghini\n" +
                    "8:Aston Martin\n" +
                    "9:Ferrari\n" +
                    "10:Jaguar\n" +
                    "11:Volvo\n" +
                    "12:Land Rover\n" +
                    "13:Volkswagen\n" +
                    "14:Bentley\n" +
                    "15:Aston Martin");
            Future<RecordMetadata> future=kafkaProducer.send(producerRecord);

            future.get();
            //System.out.println("the "+i+" th msg wrt successfully!");

/*            ProducerRecord<String,String> producerRecord=new ProducerRecord<String,String>("testJc",null,i+"");
            kafkaProducer.send(producerRecord,(RecordMetadata metadata, Exception exception)->{});*/
        }
        kafkaProducer.close();
    }
}
